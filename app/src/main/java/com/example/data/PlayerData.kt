package com.example.data

enum class OfficerRank(
    val title: String,
    val levelRequired: Int,
    val xpRequired: Int,
    val description: String
) {
    CONSTABLE("Constable", 1, 0, "Patrol officer handling street disputes and petty crime."),
    SENIOR_CONSTABLE("Senior Constable", 3, 500, "Experienced street patrol officer."),
    ASI("Assistant Sub-Inspector (ASI)", 6, 1500, "Authorized to lead investigation teams."),
    SI("Sub-Inspector (SI)", 10, 3500, "Station investigation head commanding local squads."),
    INSPECTOR("Inspector", 15, 7000, "Police station in-charge managing major crime operations."),
    DSP("Deputy Superintendent of Police (DSP)", 20, 12000, "High-ranking officer commanding citywide special operations.");

    companion object {
        fun fromXp(xp: Int): OfficerRank {
            var current = CONSTABLE
            for (rank in entries) {
                if (xp >= rank.xpRequired) {
                    current = rank
                }
            }
            return current
        }
    }

    val requiredXp: Int
        get() = xpRequired

    fun nextRank(): OfficerRank? {
        val nextIdx = ordinal + 1
        return if (nextIdx < entries.size) entries[nextIdx] else null
    }
}

data class PlayerProfile(
    var officerName: String = "Officer Hamza",
    var officerId: String = "PK-4072",
    var rank: OfficerRank = OfficerRank.CONSTABLE,
    var level: Int = 1,
    var xp: Int = 0,
    var cash: Int = 1000,
    val completedMissions: MutableSet<Int> = mutableSetOf(),
    var totalArrests: Int = 0,
    var shotsFired: Int = 0,
    var shotsHit: Int = 0,
    var patrolDistanceMeters: Float = 0f,
    var currentWeaponId: String = "pistol",
    var currentVehicleId: String = "moto"
) {
    val accuracyPercentage: Int
        get() = if (shotsFired > 0) ((shotsHit.toFloat() / shotsFired) * 100).toInt().coerceIn(0, 100) else 100

    fun addXp(amount: Int): Boolean {
        val oldRank = rank
        xp += amount
        level = (xp / 400) + 1
        rank = OfficerRank.fromXp(xp)
        return rank != oldRank
    }
}
