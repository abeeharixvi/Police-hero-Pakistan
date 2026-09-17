package com.example.data

data class DailyDuty(
    val id: String,
    val title: String,
    val description: String,
    val targetCount: Int,
    var currentCount: Int = 0,
    val rewardCash: Int,
    val rewardXp: Int,
    var isClaimed: Boolean = false
) {
    val isCompleted: Boolean
        get() = currentCount >= targetCount
}

object DailyDutyCatalog {
    fun getDailyDuties(): List<DailyDuty> {
        return listOf(
            DailyDuty(
                id = "duty_arrests",
                title = "Law & Order Patrol",
                description = "Arrest at least 2 wanted suspects in the city.",
                targetCount = 2,
                currentCount = 0,
                rewardCash = 600,
                rewardXp = 350
            ),
            DailyDuty(
                id = "duty_missions",
                title = "Active Duty Officer",
                description = "Successfully complete 2 active investigation missions.",
                targetCount = 2,
                currentCount = 0,
                rewardCash = 800,
                rewardXp = 450
            ),
            DailyDuty(
                id = "duty_pursuit",
                title = "Rapid Response",
                description = "Successfully intercept and stop 1 fleeing vehicle pursuit.",
                targetCount = 1,
                currentCount = 0,
                rewardCash = 1000,
                rewardXp = 500
            )
        )
    }
}
