package com.example.data

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject

data class GameSettings(
    var isMusicOn: Boolean = true,
    var isSoundOn: Boolean = true,
    var isVibrationOn: Boolean = true,
    var graphicsQuality: String = "High", // Low, Medium, High
    var controlSensitivity: Float = 1.0f  // 0.5 to 1.5
)

class SaveManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("police_hero_pakistan_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_PROFILE = "player_profile"
        private const val KEY_WEAPONS = "weapons_catalog"
        private const val KEY_VEHICLES = "vehicles_catalog"
        private const val KEY_CRIMINALS = "criminals_database"
        private const val KEY_MISSIONS = "missions_progress"
        private const val KEY_DUTIES = "daily_duties"
        private const val KEY_SETTINGS = "game_settings"
    }

    fun saveGame(
        profile: PlayerProfile,
        weapons: Map<String, Weapon>,
        vehicles: Map<String, VehicleConfig>,
        criminals: List<CriminalProfile>,
        missions: List<MissionData>,
        duties: List<DailyDuty>,
        settings: GameSettings
    ) {
        val editor = prefs.edit()

        // 1. Profile
        val profJson = JSONObject().apply {
            put("name", profile.officerName)
            put("id", profile.officerId)
            put("rank", profile.rank.name)
            put("level", profile.level)
            put("xp", profile.xp)
            put("cash", profile.cash)
            put("totalArrests", profile.totalArrests)
            put("shotsFired", profile.shotsFired)
            put("shotsHit", profile.shotsHit)
            put("patrolDistance", profile.patrolDistanceMeters.toDouble())
            put("weaponId", profile.currentWeaponId)
            put("vehicleId", profile.currentVehicleId)

            val compArray = JSONArray()
            profile.completedMissions.forEach { compArray.put(it) }
            put("completedMissions", compArray)
        }
        editor.putString(KEY_PROFILE, profJson.toString())

        // 2. Weapons
        val wepJson = JSONObject()
        weapons.forEach { (id, wep) ->
            val item = JSONObject().apply {
                put("unlocked", wep.isUnlocked)
                put("currentAmmo", wep.currentAmmoInMag)
                put("reserveAmmo", wep.reserveAmmo)
            }
            wepJson.put(id, item)
        }
        editor.putString(KEY_WEAPONS, wepJson.toString())

        // 3. Vehicles
        val vehJson = JSONObject()
        vehicles.forEach { (id, veh) ->
            val item = JSONObject().apply {
                put("unlocked", veh.isUnlocked)
                put("health", veh.currentHealth.toDouble())
                put("engine", veh.upgrades.engineLevel)
                put("handling", veh.upgrades.handlingLevel)
                put("durability", veh.upgrades.durabilityLevel)
            }
            vehJson.put(id, item)
        }
        editor.putString(KEY_VEHICLES, vehJson.toString())

        // 4. Criminals
        val crimArray = JSONArray()
        criminals.forEach { crim ->
            val cObj = JSONObject().apply {
                put("id", crim.id)
                put("status", crim.status.name)
                put("previousArrests", crim.previousArrests)
                put("evidence", crim.evidenceFound)
                put("history", crim.arrestHistoryNote)
            }
            crimArray.put(cObj)
        }
        editor.putString(KEY_CRIMINALS, crimArray.toString())

        // 5. Missions progress
        val misArray = JSONArray()
        missions.forEach { m ->
            val mObj = JSONObject().apply {
                put("id", m.id)
                put("completed", m.isCompleted)
                put("stars", m.bestStars)
            }
            misArray.put(mObj)
        }
        editor.putString(KEY_MISSIONS, misArray.toString())

        // 6. Daily Duties
        val dutyArray = JSONArray()
        duties.forEach { d ->
            val dObj = JSONObject().apply {
                put("id", d.id)
                put("count", d.currentCount)
                put("claimed", d.isClaimed)
            }
            dutyArray.put(dObj)
        }
        editor.putString(KEY_DUTIES, dutyArray.toString())

        // 7. Settings
        val setObj = JSONObject().apply {
            put("music", settings.isMusicOn)
            put("sound", settings.isSoundOn)
            put("vibrate", settings.isVibrationOn)
            put("quality", settings.graphicsQuality)
            put("sensitivity", settings.controlSensitivity.toDouble())
        }
        editor.putString(KEY_SETTINGS, setObj.toString())

        editor.apply()
    }

    fun loadProfile(): PlayerProfile {
        val str = prefs.getString(KEY_PROFILE, null) ?: return PlayerProfile()
        return try {
            val json = JSONObject(str)
            val prof = PlayerProfile(
                officerName = json.optString("name", "Officer Hamza"),
                officerId = json.optString("id", "PK-4072"),
                rank = runCatching { OfficerRank.valueOf(json.optString("rank", "CONSTABLE")) }.getOrDefault(OfficerRank.CONSTABLE),
                level = json.optInt("level", 1),
                xp = json.optInt("xp", 0),
                cash = json.optInt("cash", 1000),
                totalArrests = json.optInt("totalArrests", 0),
                shotsFired = json.optInt("shotsFired", 0),
                shotsHit = json.optInt("shotsHit", 0),
                patrolDistanceMeters = json.optDouble("patrolDistance", 0.0).toFloat(),
                currentWeaponId = json.optString("weaponId", "pistol"),
                currentVehicleId = json.optString("vehicleId", "moto")
            )
            val compArr = json.optJSONArray("completedMissions")
            if (compArr != null) {
                for (i in 0 until compArr.length()) {
                    prof.completedMissions.add(compArr.getInt(i))
                }
            }
            prof
        } catch (_: Exception) {
            PlayerProfile()
        }
    }

    fun loadWeapons(): Map<String, Weapon> {
        val defaults = WeaponCatalog.getDefaultWeapons().toMutableMap()
        val str = prefs.getString(KEY_WEAPONS, null) ?: return defaults
        return try {
            val json = JSONObject(str)
            defaults.forEach { (id, wep) ->
                if (json.has(id)) {
                    val item = json.getJSONObject(id)
                    wep.isUnlocked = item.optBoolean("unlocked", wep.isUnlocked)
                    wep.currentAmmoInMag = item.optInt("currentAmmo", wep.magazineCapacity)
                    wep.reserveAmmo = item.optInt("reserveAmmo", wep.reserveAmmo)
                }
            }
            defaults
        } catch (_: Exception) {
            defaults
        }
    }

    fun loadVehicles(): Map<String, VehicleConfig> {
        val defaults = VehicleCatalog.getDefaultVehicles().toMutableMap()
        val str = prefs.getString(KEY_VEHICLES, null) ?: return defaults
        return try {
            val json = JSONObject(str)
            defaults.forEach { (id, veh) ->
                if (json.has(id)) {
                    val item = json.getJSONObject(id)
                    veh.isUnlocked = item.optBoolean("unlocked", veh.isUnlocked)
                    veh.currentHealth = item.optDouble("health", veh.baseDurability.toDouble()).toFloat()
                    veh.upgrades.engineLevel = item.optInt("engine", 1)
                    veh.upgrades.handlingLevel = item.optInt("handling", 1)
                    veh.upgrades.durabilityLevel = item.optInt("durability", 1)
                }
            }
            defaults
        } catch (_: Exception) {
            defaults
        }
    }

    fun loadCriminals(): List<CriminalProfile> {
        val defaults = CriminalDatabase.getDefaultCriminals()
        val str = prefs.getString(KEY_CRIMINALS, null) ?: return defaults
        return try {
            val arr = JSONArray(str)
            val map = mutableMapOf<String, JSONObject>()
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                map[obj.getString("id")] = obj
            }
            defaults.forEach { crim ->
                map[crim.id]?.let { obj ->
                    crim.status = runCatching { CriminalStatus.valueOf(obj.optString("status", "WANTED")) }.getOrDefault(CriminalStatus.WANTED)
                    crim.previousArrests = obj.optInt("previousArrests", crim.previousArrests)
                    crim.evidenceFound = obj.optString("evidence", crim.evidenceFound)
                    crim.arrestHistoryNote = obj.optString("history", crim.arrestHistoryNote)
                }
            }
            defaults
        } catch (_: Exception) {
            defaults
        }
    }

    fun loadMissions(): List<MissionData> {
        val defaults = MissionCatalog.ALL_MISSIONS
        val str = prefs.getString(KEY_MISSIONS, null) ?: return defaults
        return try {
            val arr = JSONArray(str)
            val map = mutableMapOf<Int, JSONObject>()
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                map[obj.getInt("id")] = obj
            }
            defaults.forEach { m ->
                map[m.id]?.let { obj ->
                    m.isCompleted = obj.optBoolean("completed", false)
                    m.bestStars = obj.optInt("stars", 0)
                }
            }
            defaults
        } catch (_: Exception) {
            defaults
        }
    }

    fun loadDuties(): List<DailyDuty> {
        val defaults = DailyDutyCatalog.getDailyDuties()
        val str = prefs.getString(KEY_DUTIES, null) ?: return defaults
        return try {
            val arr = JSONArray(str)
            val map = mutableMapOf<String, JSONObject>()
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                map[obj.getString("id")] = obj
            }
            defaults.forEach { d ->
                map[d.id]?.let { obj ->
                    d.currentCount = obj.optInt("count", 0)
                    d.isClaimed = obj.optBoolean("claimed", false)
                }
            }
            defaults
        } catch (_: Exception) {
            defaults
        }
    }

    fun loadSettings(): GameSettings {
        val str = prefs.getString(KEY_SETTINGS, null) ?: return GameSettings()
        return try {
            val json = JSONObject(str)
            GameSettings(
                isMusicOn = json.optBoolean("music", true),
                isSoundOn = json.optBoolean("sound", true),
                isVibrationOn = json.optBoolean("vibrate", true),
                graphicsQuality = json.optString("quality", "High"),
                controlSensitivity = json.optDouble("sensitivity", 1.0).toFloat()
            )
        } catch (_: Exception) {
            GameSettings()
        }
    }

    fun hasSaveData(): Boolean {
        return prefs.contains(KEY_PROFILE)
    }

    fun resetAllProgress() {
        prefs.edit().clear().apply()
    }
}
