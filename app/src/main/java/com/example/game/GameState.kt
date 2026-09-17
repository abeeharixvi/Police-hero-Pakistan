package com.example.game

import com.example.data.ClueItem
import com.example.data.CriminalProfile
import com.example.data.EvidenceType
import com.example.data.MissionData
import com.example.data.MissionObjectiveState
import com.example.data.MissionObjectiveStep
import com.example.data.OfficerRank
import com.example.data.PlayerProfile
import com.example.data.VehicleConfig
import com.example.data.Weapon
import kotlin.math.sqrt

enum class GamePlayStatus {
    PLAYING,
    MISSION_SUCCESS,
    MISSION_FAILED,
    PAUSED
}

class GameSession(
    val profile: PlayerProfile,
    val mission: MissionData,
    val suspectProfile: CriminalProfile,
    val vehicleConfig: VehicleConfig?,
    val activeWeapon: Weapon
) {
    var status: GamePlayStatus = GamePlayStatus.PLAYING
    val player: PlayerEntity = PlayerEntity()
    var criminal: CriminalEntity? = null
    val clues: MutableList<ClueItem> = mutableListOf()
    val bullets: MutableList<BulletEntity> = mutableListOf()
    val backupUnits: MutableList<PoliceBackupEntity> = mutableListOf()
    val civilians: MutableList<CivilianEntity> = mutableListOf()
    val vehicles: MutableList<VehicleEntity> = mutableListOf()

    val objectiveSteps: List<MissionObjectiveStep> = mission.createObjectiveSteps()
    var currentStepIndex: Int = 0

    var missionTimeSeconds: Float = 0f
    var shotsFiredSession: Int = 0
    var shotsHitSession: Int = 0
    var damageTakenSession: Float = 0f
    var failureReason: String = ""

    // Contextual interaction prompts
    var nearestClue: ClueItem? = null
    var nearestVehicle: VehicleEntity? = null
    var canArrestSuspect: Boolean = false
    var notificationMessage: String = "Proceed to ${mission.locationZone}"
    var notificationTimer: Float = 4f

    init {
        player.activeWeapon = activeWeapon.copyWeapon()

        // Place player's selected vehicle in Police Station Motor Pool
        if (vehicleConfig != null) {
            val pVeh = VehicleEntity(
                x = GameConstants.POLICE_STATION_X - 120f,
                y = GameConstants.POLICE_STATION_Y + 80f,
                config = vehicleConfig,
                isPolice = true
            )
            vehicles.add(pVeh)
        }

        // Spawn Clues around mission target area
        clues.add(
            ClueItem(
                id = "clue_1",
                type = EvidenceType.STOLEN_PHONE,
                title = "Primary Evidence",
                description = "Dropped contraband recovered at the scene.",
                worldX = mission.targetX - 40f,
                worldY = mission.targetY - 30f
            )
        )
        clues.add(
            ClueItem(
                id = "clue_2",
                type = EvidenceType.FOOTPRINT,
                title = "Fleeing Footprints",
                description = "Muddy boot prints heading into the alleyways.",
                worldX = mission.targetX + 60f,
                worldY = mission.targetY + 50f
            )
        )

        // Spawn Suspect
        val crim = CriminalEntity(
            x = mission.targetX + 180f,
            y = mission.targetY + 140f,
            profile = suspectProfile,
            hasGetawayVehicle = mission.isVehicleChase
        )
        criminal = crim
        if (crim.vehicle != null) {
            vehicles.add(crim.vehicle!!)
        }

        // Spawn Friendly Police Support based on Officer Rank
        if (profile.rank.ordinal >= OfficerRank.ASI.ordinal) {
            backupUnits.add(PoliceBackupEntity(GameConstants.POLICE_STATION_X + 150f, GameConstants.POLICE_STATION_Y))
        }
        if (profile.rank.ordinal >= OfficerRank.INSPECTOR.ordinal) {
            backupUnits.add(PoliceBackupEntity(GameConstants.POLICE_STATION_X - 150f, GameConstants.POLICE_STATION_Y))
        }

        // Spawn Ambient Civilians in zone
        civilians.add(CivilianEntity(mission.targetX - 100f, mission.targetY + 80f, "vendor"))
        civilians.add(CivilianEntity(mission.targetX + 120f, mission.targetY - 90f, "citizen"))
        civilians.add(CivilianEntity(mission.targetX - 80f, mission.targetY - 100f, "citizen"))
    }

    val currentObjective: MissionObjectiveStep?
        get() = objectiveSteps.getOrNull(currentStepIndex)

    fun advanceObjective() {
        currentObjective?.isDone = true
        currentStepIndex++
        if (currentStepIndex >= objectiveSteps.size) {
            status = GamePlayStatus.MISSION_SUCCESS
            showNotification("★ MISSION ACCOMPLISHED! Return completed.")
        } else {
            showNotification(objectiveSteps[currentStepIndex].description)
        }
    }

    fun showNotification(msg: String) {
        notificationMessage = msg
        notificationTimer = 3.5f
    }
}
