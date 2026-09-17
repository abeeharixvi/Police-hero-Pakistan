package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.example.data.GameSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

class AudioManager(private val context: Context, var settings: GameSettings) {
    private val scope = CoroutineScope(Dispatchers.Default)
    private val pcmCache = ConcurrentHashMap<String, ShortArray>()
    private var sirenJob: Job? = null
    private var engineJob: Job? = null

    private val vibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vm?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    init {
        // Pre-generate common procedural sound waveforms
        scope.launch {
            pcmCache["btn"] = generateTone(880.0, 0.05, 0.4)
            pcmCache["shot_pistol"] = generateNoiseShot(0.12, 0.8)
            pcmCache["shot_shotgun"] = generateNoiseShot(0.22, 1.0)
            pcmCache["shot_rifle"] = generateNoiseShot(0.09, 0.9)
            pcmCache["reload"] = generateReloadClick()
            pcmCache["arrest"] = generateArrestClick()
            pcmCache["footstep"] = generateFootstepThud()
            pcmCache["radio"] = generateRadioBeep()
            pcmCache["complete"] = generateVictoryChime()
            pcmCache["failed"] = generateFailureTone()
        }
    }

    fun playButtonClick() {
        if (!settings.isSoundOn) return
        vibrateShort(20)
        playSound("btn")
    }

    fun playFootstep() {
        if (!settings.isSoundOn) return
        playSound("footstep")
    }

    fun playGunshot(weaponId: String) {
        if (!settings.isSoundOn) return
        vibrateShort(45)
        when (weaponId) {
            "shotgun" -> playSound("shot_shotgun")
            "rifle" -> playSound("shot_rifle")
            else -> playSound("shot_pistol")
        }
    }

    fun playReload() {
        if (!settings.isSoundOn) return
        playSound("reload")
    }

    fun playArrest() {
        if (!settings.isSoundOn) return
        vibrateShort(70)
        playSound("arrest")
    }

    fun playRadioChatter() {
        if (!settings.isSoundOn) return
        playSound("radio")
    }

    fun playMissionComplete() {
        if (!settings.isMusicOn && !settings.isSoundOn) return
        vibrateShort(100)
        playSound("complete")
    }

    fun playMissionFailed() {
        if (!settings.isMusicOn && !settings.isSoundOn) return
        vibrateShort(120)
        playSound("failed")
    }

    fun setSirenActive(active: Boolean) {
        if (!active || !settings.isSoundOn) {
            sirenJob?.cancel()
            sirenJob = null
            return
        }
        if (sirenJob != null) return

        sirenJob = scope.launch {
            val sampleRate = 22050
            while (isActive) {
                // Alternating European/Pakistani police two-tone wail
                playRawTone(750.0, 0.28, sampleRate, 0.6)
                delay(280)
                if (!isActive) break
                playRawTone(960.0, 0.28, sampleRate, 0.6)
                delay(280)
            }
        }
    }

    fun setEngineActive(active: Boolean, speedRatio: Float) {
        if (!active || !settings.isSoundOn) {
            engineJob?.cancel()
            engineJob = null
            return
        }
        if (engineJob != null) return

        engineJob = scope.launch {
            val sampleRate = 16000
            while (isActive) {
                val freq = 55.0 + (speedRatio * 85.0)
                playRawTone(freq, 0.15, sampleRate, 0.25)
                delay(150)
            }
        }
    }

    private fun playSound(key: String) {
        val samples = pcmCache[key] ?: return
        scope.launch {
            playBuffer(samples, 22050)
        }
    }

    private fun playBuffer(samples: ShortArray, sampleRate: Int) {
        try {
            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(samples.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            track.write(samples, 0, samples.size)
            track.play()
            scope.launch {
                delay((samples.size * 1000L / sampleRate) + 50)
                try {
                    track.stop()
                    track.release()
                } catch (_: Exception) {}
            }
        } catch (_: Exception) {}
    }

    private fun playRawTone(freq: Double, durationSec: Double, sampleRate: Int, volume: Double) {
        val samples = generateTone(freq, durationSec, volume, sampleRate)
        playBuffer(samples, sampleRate)
    }

    private fun generateTone(freq: Double, durationSec: Double, volume: Double, sampleRate: Int = 22050): ShortArray {
        val count = (sampleRate * durationSec).toInt()
        val buffer = ShortArray(count)
        for (i in 0 until count) {
            val time = i.toDouble() / sampleRate
            val envelope = 1.0 - (i.toDouble() / count) // Linear fade out
            val sample = sin(2.0 * PI * freq * time) * volume * envelope
            buffer[i] = (sample * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return buffer
    }

    private fun generateNoiseShot(durationSec: Double, volume: Double): ShortArray {
        val sampleRate = 22050
        val count = (sampleRate * durationSec).toInt()
        val buffer = ShortArray(count)
        val rand = Random(42)
        for (i in 0 until count) {
            val progress = i.toDouble() / count
            val envelope = (1.0 - progress) * (1.0 - progress)
            val noise = (rand.nextDouble() * 2.0 - 1.0) * volume * envelope
            buffer[i] = (noise * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return buffer
    }

    private fun generateReloadClick(): ShortArray {
        val sampleRate = 22050
        val count = (sampleRate * 0.25).toInt()
        val buffer = ShortArray(count)
        val click1 = (count * 0.2).toInt()
        val click2 = (count * 0.7).toInt()
        val rand = Random(99)
        for (i in 0 until count) {
            var v = 0.0
            if (i in click1..(click1 + 180)) {
                v = (rand.nextDouble() * 2 - 1) * 0.7
            } else if (i in click2..(click2 + 250)) {
                v = (rand.nextDouble() * 2 - 1) * 0.9
            }
            buffer[i] = (v * Short.MAX_VALUE).toInt().toShort()
        }
        return buffer
    }

    private fun generateArrestClick(): ShortArray {
        val sampleRate = 22050
        val count = (sampleRate * 0.3).toInt()
        val buffer = ShortArray(count)
        for (i in 0 until count) {
            val time = i.toDouble() / sampleRate
            val decay = 1.0 - (i.toDouble() / count)
            val sample = (sin(2.0 * PI * 1200.0 * time) + sin(2.0 * PI * 2400.0 * time) * 0.5) * decay * 0.8
            buffer[i] = (sample * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return buffer
    }

    private fun generateFootstepThud(): ShortArray {
        val sampleRate = 22050
        val count = (sampleRate * 0.08).toInt()
        val buffer = ShortArray(count)
        for (i in 0 until count) {
            val time = i.toDouble() / sampleRate
            val decay = (1.0 - (i.toDouble() / count))
            val sample = sin(2.0 * PI * 85.0 * time) * 0.4 * decay
            buffer[i] = (sample * Short.MAX_VALUE).toInt().toShort()
        }
        return buffer
    }

    private fun generateRadioBeep(): ShortArray {
        val sampleRate = 22050
        val count = (sampleRate * 0.18).toInt()
        val buffer = ShortArray(count)
        for (i in 0 until count) {
            val time = i.toDouble() / sampleRate
            val sample = (sin(2.0 * PI * 1800.0 * time) * 0.35)
            buffer[i] = (sample * Short.MAX_VALUE).toInt().toShort()
        }
        return buffer
    }

    private fun generateVictoryChime(): ShortArray {
        val sampleRate = 22050
        val count = (sampleRate * 0.7).toInt()
        val buffer = ShortArray(count)
        for (i in 0 until count) {
            val time = i.toDouble() / sampleRate
            val progress = i.toDouble() / count
            val freq = when {
                progress < 0.25 -> 523.25 // C5
                progress < 0.50 -> 659.25 // E5
                progress < 0.75 -> 783.99 // G5
                else -> 1046.50           // C6
            }
            val decay = 1.0 - (progress % 0.25) * 4.0 * 0.5
            val sample = sin(2.0 * PI * freq * time) * 0.5 * decay
            buffer[i] = (sample * Short.MAX_VALUE).toInt().toShort()
        }
        return buffer
    }

    private fun generateFailureTone(): ShortArray {
        val sampleRate = 22050
        val count = (sampleRate * 0.6).toInt()
        val buffer = ShortArray(count)
        for (i in 0 until count) {
            val time = i.toDouble() / sampleRate
            val progress = i.toDouble() / count
            val freq = 320.0 - (progress * 140.0) // Descending pitch
            val decay = 1.0 - progress
            val sample = sin(2.0 * PI * freq * time) * 0.5 * decay
            buffer[i] = (sample * Short.MAX_VALUE).toInt().toShort()
        }
        return buffer
    }

    private fun vibrateShort(ms: Long) {
        if (!settings.isVibrationOn) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(ms, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(ms)
            }
        } catch (_: Exception) {}
    }

    fun release() {
        sirenJob?.cancel()
        engineJob?.cancel()
    }
}
