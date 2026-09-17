package com.example.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import com.example.audio.AudioManager
import com.example.data.Weapon
import com.example.game.GameEngine
import com.example.game.GamePlayStatus
import com.example.game.GameRenderer
import com.example.game.GameSession
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Composable
fun GameScreen(
    session: GameSession,
    audioManager: AudioManager,
    weapons: Map<String, Weapon>,
    onReturnToStation: (cash: Int, xp: Int, stars: Int) -> Unit,
    onRetryMission: () -> Unit,
    onExitToStation: () -> Unit,
    onWatchRewardedAd: (onSuccess: () -> Unit) -> Unit
) {
    val engine = remember(session) { GameEngine(session, audioManager) }
    val renderer = remember { GameRenderer() }
    var frameTick by remember { mutableLongStateOf(0L) }
    var isPaused by remember { mutableStateOf(false) }

    // High performance continuous 60fps game loop
    LaunchedEffect(session, isPaused) {
        while (isActive) {
            if (!isPaused && session.status == GamePlayStatus.PLAYING) {
                engine.update()
                frameTick++
            }
            delay(16) // ~60 FPS
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
            .testTag("game_screen_root")
    ) {
        // 2D Game Canvas
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .testTag("game_canvas")
        ) {
            // Read frameTick to trigger recomposition per frame
            @Suppress("UNUSED_VARIABLE")
            val tick = frameTick

            drawIntoCanvas { canvas ->
                renderer.drawWorld(canvas.nativeCanvas, session, size.width, size.height)
            }
        }

        // Overlay Interactive HUD
        GameHud(
            session = session,
            onJoystickMoved = { x, y ->
                engine.inputX = x
                engine.inputY = y
            },
            onShoot = { engine.onShootPressed() },
            onReload = { engine.onReloadPressed() },
            onArrest = { engine.onArrestPressed() },
            onInvestigate = { engine.onInvestigatePressed() },
            onVehicleToggle = { engine.onVehicleTogglePressed() },
            onSwitchWeapon = {
                // Cycle weapon
                val unlocked = weapons.values.filter { it.isUnlocked }
                val curIdx = unlocked.indexOfFirst { it.id == session.player.activeWeapon?.id }
                val nextWep = unlocked[(curIdx + 1) % unlocked.size]
                engine.onSwitchWeaponPressed(nextWep)
            },
            onSirenToggle = {
                val v = session.player.inVehicle
                if (v != null) {
                    v.isSirenOn = !v.isSirenOn
                    audioManager.setSirenActive(v.isSirenOn)
                }
            },
            onPause = { isPaused = true }
        )

        // Pause Menu Dialog
        if (isPaused) {
            AlertDialog(
                onDismissRequest = { isPaused = false },
                title = { Text(text = "MISSION PAUSED", color = Color(0xFFFFD700), fontWeight = FontWeight.Black) },
                text = { Text(text = "Police dispatch on standby. Resume operations or return to headquarters?", color = Color.White) },
                confirmButton = {
                    Button(
                        onClick = { isPaused = false },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5)),
                        modifier = Modifier.testTag("resume_mission_button")
                    ) {
                        Text("RESUME DUTY")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            isPaused = false
                            onExitToStation()
                        },
                        modifier = Modifier.testTag("abandon_mission_button")
                    ) {
                        Text("RETURN TO STATION", color = Color(0xFFEF5350))
                    }
                },
                containerColor = Color(0xFF0F172A)
            )
        }

        // Mission Success or Failure Debrief Dialog
        if (session.status == GamePlayStatus.MISSION_SUCCESS || session.status == GamePlayStatus.MISSION_FAILED) {
            MissionDebriefDialog(
                session = session,
                isSuccess = session.status == GamePlayStatus.MISSION_SUCCESS,
                onReturnToStation = onReturnToStation,
                onRetryMission = onRetryMission,
                onWatchRewardedAd = onWatchRewardedAd
            )
        }
    }
}
