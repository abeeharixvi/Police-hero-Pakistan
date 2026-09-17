package com.example.game

import android.os.SystemClock
import com.example.audio.AudioManager
import com.example.data.CriminalStatus
import com.example.data.MissionObjectiveState
import com.example.data.Weapon
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class GameEngine(
    val session: GameSession,
    private val audioManager: AudioManager
) {
    private var lastFrameTime: Long = SystemClock.elapsedRealtime()

    // Joystick input [-1.0f .. 1.0f]
    var inputX: Float = 0f
    var inputY: Float = 0f

    fun update() {
        val now = SystemClock.elapsedRealtime()
        val dt = ((now - lastFrameTime) / 1000f).coerceIn(0.001f, 0.05f)
        lastFrameTime = now

        if (session.status != GamePlayStatus.PLAYING) {
            return
        }

        session.missionTimeSeconds += dt
        if (session.notificationTimer > 0f) {
            session.notificationTimer -= dt
        }

        val player = session.player
        val crim = session.criminal

        // 1. Move Player or Drive Vehicle
        player.move(inputX, inputY, dt, CityMap)

        // Accumulate patrol distance
        val moveSpeed = sqrt(inputX * inputX + inputY * inputY)
        if (moveSpeed > 0.1f) {
            session.profile.patrolDistanceMeters += (moveSpeed * 10f * dt)
        }

        // Engine sound modulation if in vehicle
        if (player.inVehicle != null) {
            val ratio = player.inVehicle!!.speed / player.inVehicle!!.config.maxSpeed
            audioManager.setEngineActive(true, ratio)
            audioManager.setSirenActive(player.inVehicle!!.isSirenOn)
        } else {
            audioManager.setEngineActive(false, 0f)
            audioManager.setSirenActive(false)
        }

        // 2. Update Weapons & Reloading
        val wep = player.activeWeapon
        if (wep != null && player.isReloading) {
            if (now >= player.reloadTimer) {
                player.isReloading = false
                val needed = wep.magazineCapacity - wep.currentAmmoInMag
                val toLoad = needed.coerceAtMost(wep.reserveAmmo)
                wep.currentAmmoInMag += toLoad
                wep.reserveAmmo -= toLoad
                session.showNotification("Weapon Reloaded (${wep.currentAmmoInMag}/${wep.reserveAmmo})")
            }
        }

        // 3. Update Criminal AI
        crim?.let { c ->
            c.updateAI(dt, player) { bullet ->
                session.bullets.add(bullet)
                audioManager.playGunshot("pistol")
            }
        }

        // 4. Update Friendly Police Backup AI
        for (backup in session.backupUnits) {
            backup.update(dt, crim)
        }

        // 5. Update Ambient Civilians
        for (civ in session.civilians) {
            civ.update(dt)
        }

        // 6. Update Bullets & Hit Detection
        val bulletIter = session.bullets.iterator()
        while (bulletIter.hasNext()) {
            val b = bulletIter.next()
            b.update(dt)

            // Hit wall?
            if (CityMap.isCollidingWithObstacle(b.x, b.y, 4f)) {
                b.isAlive = false
            }

            // Hit Criminal?
            if (b.isAlive && b.isFromPlayer && crim != null && crim.aiState != CriminalAIState.ARRESTED) {
                val dx = b.x - crim.x
                val dy = b.y - crim.y
                if (dx * dx + dy * dy < (GameConstants.CRIMINAL_RADIUS + 8f) * (GameConstants.CRIMINAL_RADIUS + 8f)) {
                    b.isAlive = false
                    crim.health = (crim.health - b.damage).coerceAtLeast(15f) // Keep suspect alive for arrest
                    session.shotsHitSession++
                    session.profile.shotsHit++
                    session.showNotification("Suspect subdued! Health: ${crim.health.toInt()}%")
                }
            }

            // Hit Player?
            if (b.isAlive && !b.isFromPlayer) {
                val dx = b.x - player.x
                val dy = b.y - player.y
                if (dx * dx + dy * dy < (GameConstants.PLAYER_RADIUS + 6f) * (GameConstants.PLAYER_RADIUS + 6f)) {
                    b.isAlive = false
                    player.takeDamage(b.damage.toFloat())
                    session.damageTakenSession += b.damage
                    session.showNotification("Officer under fire! Health: ${player.health.toInt()}%")
                    if (player.health <= 0f) {
                        session.status = GamePlayStatus.MISSION_FAILED
                        session.failureReason = "Officer Down in the Line of Duty"
                        audioManager.playMissionFailed()
                    }
                }
            }

            if (!b.isAlive) {
                bulletIter.remove()
            }
        }

        // 7. Contextual Proximity Checks
        updateInteractionProximities(player, crim)

        // 8. Progress Mission Objectives Automatically
        checkObjectiveProgress(player, crim)
    }

    private fun updateInteractionProximities(player: PlayerEntity, crim: CriminalEntity?) {
        // Nearest clue
        var bestClue: com.example.data.ClueItem? = null
        var bestClueDist = Float.MAX_VALUE
        for (clue in session.clues) {
            if (!clue.isInvestigated) {
                val dx = player.x - clue.worldX
                val dy = player.y - clue.worldY
                val dist = sqrt(dx * dx + dy * dy)
                if (dist < GameConstants.INVESTIGATE_RANGE && dist < bestClueDist) {
                    bestClueDist = dist
                    bestClue = clue
                }
            }
        }
        session.nearestClue = bestClue

        // Nearest vehicle
        var bestVeh: VehicleEntity? = null
        var bestVehDist = Float.MAX_VALUE
        for (veh in session.vehicles) {
            val dx = player.x - veh.x
            val dy = player.y - veh.y
            val dist = sqrt(dx * dx + dy * dy)
            if (dist < GameConstants.VEHICLE_ENTER_RANGE && dist < bestVehDist) {
                bestVehDist = dist
                bestVeh = veh
            }
        }
        session.nearestVehicle = bestVeh

        // Can arrest suspect?
        if (crim != null && crim.aiState != CriminalAIState.ARRESTED) {
            val dx = player.x - crim.x
            val dy = player.y - crim.y
            val dist = sqrt(dx * dx + dy * dy)
            session.canArrestSuspect = dist <= GameConstants.ARREST_RANGE
        } else {
            session.canArrestSuspect = false
        }
    }

    private fun checkObjectiveProgress(player: PlayerEntity, crim: CriminalEntity?) {
        val obj = session.currentObjective ?: return
        when (obj.state) {
            MissionObjectiveState.REACH_LOCATION -> {
                val dx = player.x - session.mission.targetX
                val dy = player.y - session.mission.targetY
                if (sqrt(dx * dx + dy * dy) < 280f) {
                    audioManager.playRadioChatter()
                    session.advanceObjective()
                }
            }
            MissionObjectiveState.SPOTTED_SUSPECT -> {
                if (crim != null) {
                    val dx = player.x - crim.x
                    val dy = player.y - crim.y
                    if (sqrt(dx * dx + dy * dy) < 320f) {
                        audioManager.playRadioChatter()
                        session.advanceObjective()
                    }
                }
            }
            MissionObjectiveState.CHASE_SUSPECT -> {
                if (crim != null && crim.aiState == CriminalAIState.ARRESTED) {
                    session.advanceObjective()
                } else if (crim != null && session.canArrestSuspect) {
                    session.showNotification("SUSPECT CORNERED! PRESS ARREST NOW!")
                }
            }
            MissionObjectiveState.RETURN_TO_STATION -> {
                val dx = player.x - GameConstants.POLICE_STATION_X
                val dy = player.y - GameConstants.POLICE_STATION_Y
                if (sqrt(dx * dx + dy * dy) < 200f) {
                    session.advanceObjective()
                    audioManager.playMissionComplete()
                }
            }
            else -> {}
        }
    }

    fun onShootPressed() {
        val player = session.player
        val wep = player.activeWeapon ?: return
        if (player.isReloading) return

        val now = SystemClock.elapsedRealtime()
        if (now - player.lastShotTime < wep.fireRateMs) return

        if (wep.currentAmmoInMag <= 0) {
            onReloadPressed()
            return
        }

        wep.currentAmmoInMag--
        player.lastShotTime = now
        session.shotsFiredSession++
        session.profile.shotsFired++

        audioManager.playGunshot(wep.id)

        // Bullet trajectory with accuracy spread
        val spread = (1.0f - wep.accuracy) * 0.25f
        val angle = player.angle + (kotlin.random.Random.nextFloat() * spread * 2f - spread)

        session.bullets.add(
            BulletEntity(
                x = player.x + cos(player.angle) * 25f,
                y = player.y + sin(player.angle) * 25f,
                dirX = cos(angle),
                dirY = sin(angle),
                damage = wep.damage,
                isFromPlayer = true,
                maxRange = wep.range
            )
        )
    }

    fun onReloadPressed() {
        val player = session.player
        val wep = player.activeWeapon ?: return
        if (player.isReloading || wep.currentAmmoInMag == wep.magazineCapacity || wep.reserveAmmo <= 0) return

        player.isReloading = true
        player.reloadTimer = SystemClock.elapsedRealtime() + wep.reloadTimeMs
        audioManager.playReload()
        session.showNotification("Reloading...")
    }

    fun onArrestPressed(): Boolean {
        val crim = session.criminal
        if (crim == null || crim.aiState == CriminalAIState.ARRESTED) {
            return false
        }

        if (session.canArrestSuspect) {
            crim.aiState = CriminalAIState.ARRESTED
            crim.profile.status = CriminalStatus.ARRESTED
            crim.profile.previousArrests++
            crim.profile.arrestHistoryNote = "Apprehended in Mission: ${session.mission.title}"
            session.profile.totalArrests++
            audioManager.playArrest()
            session.showNotification("★ SUSPECT IN HANDCUFFS! Return to Police Station.")

            if (session.currentObjective?.state == MissionObjectiveState.ARREST_SUSPECT ||
                session.currentObjective?.state == MissionObjectiveState.CHASE_SUSPECT
            ) {
                session.advanceObjective()
            }
            return true
        } else {
            session.showNotification("SUSPECT OUT OF RANGE - GET CLOSER!")
            return false
        }
    }

    fun onInvestigatePressed(): Boolean {
        val clue = session.nearestClue
        if (clue != null && !clue.isInvestigated) {
            clue.isInvestigated = true
            session.suspectProfile.evidenceFound = "${clue.title}: ${clue.description}"
            audioManager.playRadioChatter()
            session.showNotification("Examined: ${clue.title}")

            if (session.currentObjective?.state == MissionObjectiveState.INVESTIGATE_CLUE ||
                session.currentObjective?.state == MissionObjectiveState.QUESTION_WITNESS
            ) {
                session.advanceObjective()
            }
            return true
        }
        return false
    }

    fun onVehicleTogglePressed() {
        val player = session.player
        if (player.inVehicle != null) {
            // Exit vehicle
            val veh = player.inVehicle!!
            player.x = veh.x + sin(veh.angle) * 30f
            player.y = veh.y - cos(veh.angle) * 30f
            player.inVehicle = null
            audioManager.setEngineActive(false, 0f)
            audioManager.setSirenActive(false)
            session.showNotification("Exited Police Vehicle")
        } else {
            // Enter nearest vehicle
            val veh = session.nearestVehicle
            if (veh != null) {
                player.inVehicle = veh
                player.x = veh.x
                player.y = veh.y
                player.angle = veh.angle
                audioManager.setEngineActive(true, 0.2f)
                session.showNotification("Entered ${veh.config.name}")
            }
        }
    }

    fun onSwitchWeaponPressed(newWeapon: Weapon) {
        session.player.activeWeapon = newWeapon.copyWeapon()
        session.showNotification("Equipped ${newWeapon.name}")
    }
}
