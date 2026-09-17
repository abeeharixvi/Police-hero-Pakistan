package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.ads.AdManager
import com.example.ads.AdRewardType
import com.example.audio.AudioManager
import com.example.data.DailyDuty
import com.example.data.MissionData
import com.example.data.PlayerProfile
import com.example.data.SaveManager
import com.example.data.VehicleConfig
import com.example.data.Weapon
import com.example.game.GameSession
import com.example.ui.CriminalRecordsScreen
import com.example.ui.DailyDutiesDialog
import com.example.ui.GameScreen
import com.example.ui.GarageScreen
import com.example.ui.LockupScreen
import com.example.ui.MainMenuScreen
import com.example.ui.MissionBoardScreen
import com.example.ui.PoliceStationScreen
import com.example.ui.ProfileScreen
import com.example.ui.SettingsScreen
import com.example.ui.WeaponShopScreen

enum class AppScreen {
    MAIN_MENU,
    GAME_PLAY,
    POLICE_STATION,
    MISSION_BOARD,
    CRIMINAL_RECORDS,
    LOCKUP,
    GARAGE,
    WEAPONS,
    PROFILE,
    SETTINGS
}

class MainActivity : ComponentActivity() {

    private lateinit var saveManager: SaveManager
    private lateinit var audioManager: AudioManager
    private lateinit var adManager: AdManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        saveManager = SaveManager(this)
        val initialSettings = saveManager.loadSettings()
        audioManager = AudioManager(this, initialSettings)
        adManager = AdManager(this).apply { initialize() }

        setContent {
            val darkColors = darkColorScheme(
                primary = Color(0xFF1E88E5),
                secondary = Color(0xFFFFD700),
                background = Color(0xFF070D1E),
                surface = Color(0xFF0F1B38)
            )

            MaterialTheme(colorScheme = darkColors) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF070D1E)
                ) {
                    PoliceHeroApp()
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        audioManager.setSirenActive(false)
        audioManager.setEngineActive(false, 0f)
    }

    override fun onDestroy() {
        super.onDestroy()
        audioManager.release()
    }

    @Composable
    private fun PoliceHeroApp() {
        var currentScreen by remember { mutableStateOf(AppScreen.MAIN_MENU) }
        var showDailyDuties by remember { mutableStateOf(false) }

        // Local game state loaded from persistent storage
        var profile by remember { mutableStateOf(saveManager.loadProfile()) }
        val weapons = remember { mutableStateMapOf<String, Weapon>().apply { putAll(saveManager.loadWeapons()) } }
        val vehicles = remember { mutableStateMapOf<String, VehicleConfig>().apply { putAll(saveManager.loadVehicles()) } }
        val criminals = remember { mutableStateListOf(*saveManager.loadCriminals().toTypedArray()) }
        val missions = remember { mutableStateListOf(*saveManager.loadMissions().toTypedArray()) }
        val duties = remember { mutableStateListOf(*saveManager.loadDuties().toTypedArray()) }
        var settings by remember { mutableStateOf(saveManager.loadSettings()) }

        var activeSession by remember { mutableStateOf<GameSession?>(null) }

        // Helper to persist whenever state changes
        fun saveCurrentState() {
            saveManager.saveGame(profile, weapons, vehicles, criminals, missions, duties, settings)
        }

        // Auto-save on exit or change
        DisposableEffect(Unit) {
            onDispose {
                saveCurrentState()
            }
        }

        fun launchMission(mission: MissionData) {
            val suspect = criminals.firstOrNull { it.id == mission.suspectId } ?: criminals.first()
            val vehicle = vehicles[profile.currentVehicleId]
            val weapon = weapons[profile.currentWeaponId] ?: weapons.values.first()

            activeSession = GameSession(
                profile = profile,
                mission = mission,
                suspectProfile = suspect,
                vehicleConfig = vehicle,
                activeWeapon = weapon
            )
            currentScreen = AppScreen.GAME_PLAY
            audioManager.playButtonClick()
        }

        // Handle hardware back press gracefully
        BackHandler(enabled = currentScreen != AppScreen.MAIN_MENU) {
            when (currentScreen) {
                AppScreen.GAME_PLAY -> {
                    // Let in-game pause dialog handle return to station
                }
                AppScreen.POLICE_STATION -> currentScreen = AppScreen.MAIN_MENU
                else -> currentScreen = AppScreen.POLICE_STATION
            }
            audioManager.playButtonClick()
        }

        Box(modifier = Modifier.fillMaxSize()) {
            when (currentScreen) {
                AppScreen.MAIN_MENU -> {
                    MainMenuScreen(
                        profile = profile,
                        onNavigate = { route ->
                            audioManager.playButtonClick()
                            when (route) {
                                "continue_game" -> {
                                    val nextMission = missions.firstOrNull { !it.isCompleted } ?: missions.first()
                                    launchMission(nextMission)
                                }
                                "police_station" -> currentScreen = AppScreen.POLICE_STATION
                                "mission_board" -> currentScreen = AppScreen.MISSION_BOARD
                                "garage" -> currentScreen = AppScreen.GARAGE
                                "weapons" -> currentScreen = AppScreen.WEAPONS
                                "criminals" -> currentScreen = AppScreen.CRIMINAL_RECORDS
                                "lockup" -> currentScreen = AppScreen.LOCKUP
                                "daily_duties" -> showDailyDuties = true
                                "settings" -> currentScreen = AppScreen.SETTINGS
                            }
                        }
                    )
                }

                AppScreen.GAME_PLAY -> {
                    activeSession?.let { session ->
                        GameScreen(
                            session = session,
                            audioManager = audioManager,
                            weapons = weapons,
                            onReturnToStation = { cashEarned, xpEarned, stars ->
                                profile.cash += cashEarned
                                val promoted = profile.addXp(xpEarned)
                                if (promoted) {
                                    Toast.makeText(
                                        this@MainActivity,
                                        "PROMOTED! You are now ${profile.rank.title}!",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }

                                session.mission.isCompleted = true
                                session.mission.bestStars = maxOf(session.mission.bestStars, stars)
                                if (!profile.completedMissions.contains(session.mission.id)) {
                                    profile.completedMissions.add(session.mission.id)
                                }

                                // Advance Daily Duties
                                duties.find { it.id == "duty_patrol" }?.let { it.currentCount = (it.currentCount + 100).coerceAtMost(it.targetCount) }
                                duties.find { it.id == "duty_search" }?.let { it.currentCount = (it.currentCount + 1).coerceAtMost(it.targetCount) }
                                duties.find { it.id == "duty_chase" }?.let { it.currentCount = (it.currentCount + 1).coerceAtMost(it.targetCount) }

                                saveCurrentState()
                                adManager.showInterstitial(this@MainActivity) {
                                    currentScreen = AppScreen.POLICE_STATION
                                }
                            },
                            onRetryMission = {
                                launchMission(session.mission)
                            },
                            onExitToStation = {
                                audioManager.playButtonClick()
                                currentScreen = AppScreen.POLICE_STATION
                            },
                            onWatchRewardedAd = { onSuccess ->
                                adManager.showRewardedAd(
                                    activity = this@MainActivity,
                                    rewardType = AdRewardType.DOUBLE_MISSION_REWARD,
                                    onUserEarnedReward = {
                                        onSuccess()
                                    },
                                    onAdFailedOrUnavailable = {
                                        onSuccess()
                                    }
                                )
                            }
                        )
                    }
                }

                AppScreen.POLICE_STATION -> {
                    PoliceStationScreen(
                        profile = profile,
                        onNavigateDepartment = { deptId ->
                            audioManager.playButtonClick()
                            when (deptId) {
                                "mission_board" -> currentScreen = AppScreen.MISSION_BOARD
                                "officer_desk" -> currentScreen = AppScreen.PROFILE
                                "criminal_records" -> currentScreen = AppScreen.CRIMINAL_RECORDS
                                "weapon_locker" -> currentScreen = AppScreen.WEAPONS
                                "garage" -> currentScreen = AppScreen.GARAGE
                                "lockup" -> currentScreen = AppScreen.LOCKUP
                                "dispatch_map" -> {
                                    val nextMission = missions.firstOrNull { !it.isCompleted } ?: missions.first()
                                    launchMission(nextMission)
                                }
                            }
                        },
                        onBackToMenu = {
                            audioManager.playButtonClick()
                            currentScreen = AppScreen.MAIN_MENU
                        }
                    )
                }

                AppScreen.MISSION_BOARD -> {
                    MissionBoardScreen(
                        profile = profile,
                        missions = missions,
                        onLaunchMission = { mission ->
                            launchMission(mission)
                        },
                        onBack = {
                            audioManager.playButtonClick()
                            currentScreen = AppScreen.POLICE_STATION
                        }
                    )
                }

                AppScreen.CRIMINAL_RECORDS -> {
                    CriminalRecordsScreen(
                        criminals = criminals,
                        onBack = {
                            audioManager.playButtonClick()
                            currentScreen = AppScreen.POLICE_STATION
                        }
                    )
                }

                AppScreen.LOCKUP -> {
                    LockupScreen(
                        criminals = criminals,
                        onBack = {
                            audioManager.playButtonClick()
                            currentScreen = AppScreen.POLICE_STATION
                        }
                    )
                }

                AppScreen.GARAGE -> {
                    GarageScreen(
                        profile = profile,
                        vehicles = vehicles,
                        onSelectVehicle = { vehId ->
                            profile.currentVehicleId = vehId
                            saveCurrentState()
                            Toast.makeText(this@MainActivity, "Vehicle deployed to service fleet.", Toast.LENGTH_SHORT).show()
                            audioManager.playButtonClick()
                        },
                        onUpgradeVehicle = { vehId, upgradeType ->
                            val veh = vehicles[vehId] ?: return@GarageScreen
                            val cost = 600
                            if (profile.cash >= cost) {
                                profile.cash -= cost
                                when (upgradeType) {
                                    "engine" -> veh.upgrades.engineLevel = (veh.upgrades.engineLevel + 1).coerceAtMost(5)
                                    "handling" -> veh.upgrades.handlingLevel = (veh.upgrades.handlingLevel + 1).coerceAtMost(5)
                                    "durability" -> veh.upgrades.durabilityLevel = (veh.upgrades.durabilityLevel + 1).coerceAtMost(5)
                                }
                                saveCurrentState()
                                Toast.makeText(this@MainActivity, "Upgraded $upgradeType to Level ${veh.upgrades.engineLevel}!", Toast.LENGTH_SHORT).show()
                                audioManager.playButtonClick()
                            } else {
                                Toast.makeText(this@MainActivity, "Insufficient funds! Rs $cost required.", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onBack = {
                            audioManager.playButtonClick()
                            currentScreen = AppScreen.POLICE_STATION
                        }
                    )
                }

                AppScreen.WEAPONS -> {
                    WeaponShopScreen(
                        profile = profile,
                        weapons = weapons,
                        onSelectWeapon = { wepId ->
                            profile.currentWeaponId = wepId
                            saveCurrentState()
                            Toast.makeText(this@MainActivity, "Weapon assigned to service holster.", Toast.LENGTH_SHORT).show()
                            audioManager.playButtonClick()
                        },
                        onBuyWeapon = { wepId ->
                            val wep = weapons[wepId] ?: return@WeaponShopScreen
                            if (profile.cash >= wep.price) {
                                profile.cash -= wep.price
                                wep.isUnlocked = true
                                profile.currentWeaponId = wepId
                                saveCurrentState()
                                Toast.makeText(this@MainActivity, "Purchased and equipped ${wep.name}!", Toast.LENGTH_SHORT).show()
                                audioManager.playButtonClick()
                            } else {
                                Toast.makeText(this@MainActivity, "Insufficient department funds!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onBuyAmmo = { wepId ->
                            val wep = weapons[wepId] ?: return@WeaponShopScreen
                            if (profile.cash >= wep.ammoPrice) {
                                profile.cash -= wep.ammoPrice
                                wep.reserveAmmo += 50
                                saveCurrentState()
                                Toast.makeText(this@MainActivity, "Requisitioned 50 rounds for ${wep.name}!", Toast.LENGTH_SHORT).show()
                                audioManager.playReload()
                            } else {
                                Toast.makeText(this@MainActivity, "Insufficient funds for ammo crate!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onBack = {
                            audioManager.playButtonClick()
                            currentScreen = AppScreen.POLICE_STATION
                        }
                    )
                }

                AppScreen.PROFILE -> {
                    ProfileScreen(
                        profile = profile,
                        onBack = {
                            audioManager.playButtonClick()
                            currentScreen = AppScreen.POLICE_STATION
                        }
                    )
                }

                AppScreen.SETTINGS -> {
                    SettingsScreen(
                        settings = settings,
                        onSettingsChanged = { updated ->
                            settings = updated
                            audioManager.settings = updated
                            saveCurrentState()
                        },
                        onResetProgress = {
                            saveManager.resetAllProgress()
                            profile = saveManager.loadProfile()
                            weapons.clear()
                            weapons.putAll(saveManager.loadWeapons())
                            vehicles.clear()
                            vehicles.putAll(saveManager.loadVehicles())
                            criminals.clear()
                            criminals.addAll(saveManager.loadCriminals())
                            missions.clear()
                            missions.addAll(saveManager.loadMissions())
                            duties.clear()
                            duties.addAll(saveManager.loadDuties())
                            Toast.makeText(this@MainActivity, "Progress reset successfully.", Toast.LENGTH_SHORT).show()
                            currentScreen = AppScreen.MAIN_MENU
                        },
                        onBack = {
                            audioManager.playButtonClick()
                            currentScreen = AppScreen.MAIN_MENU
                        }
                    )
                }
            }

            // Daily Duties Dialog Overlay
            if (showDailyDuties) {
                DailyDutiesDialog(
                    duties = duties,
                    onClaimDuty = { duty ->
                        duty.isClaimed = true
                        profile.cash += duty.rewardCash
                        saveCurrentState()
                        Toast.makeText(this@MainActivity, "Claimed Rs ${duty.rewardCash} for ${duty.title}!", Toast.LENGTH_SHORT).show()
                        audioManager.playButtonClick()
                    },
                    onDismiss = { showDailyDuties = false }
                )
            }
        }
    }
}
