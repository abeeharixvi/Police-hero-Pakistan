package com.example.data

data class VehicleUpgrade(
    var engineLevel: Int = 1,     // 1 to 5: boosts max speed & acceleration
    var handlingLevel: Int = 1,   // 1 to 5: boosts turn rate & responsiveness
    var durabilityLevel: Int = 1  // 1 to 5: boosts armor & health
)

data class VehicleConfig(
    val id: String,
    val name: String,
    val description: String,
    val baseSpeed: Float,
    val baseAcceleration: Float,
    val baseHandling: Float,
    val baseDurability: Float,
    val price: Int,
    var isUnlocked: Boolean = false,
    var currentHealth: Float = baseDurability,
    var upgrades: VehicleUpgrade = VehicleUpgrade()
) {
    val maxSpeed: Float
        get() = baseSpeed + (upgrades.engineLevel - 1) * 25f

    val acceleration: Float
        get() = baseAcceleration + (upgrades.engineLevel - 1) * 20f

    val handling: Float
        get() = baseHandling + (upgrades.handlingLevel - 1) * 0.15f

    val maxDurability: Float
        get() = baseDurability + (upgrades.durabilityLevel - 1) * 60f

    val upgradeCostEngine: Int
        get() = upgrades.engineLevel * 500

    val upgradeCostHandling: Int
        get() = upgrades.handlingLevel * 450

    val upgradeCostDurability: Int
        get() = upgrades.durabilityLevel * 400
}

object VehicleCatalog {
    fun getDefaultVehicles(): Map<String, VehicleConfig> {
        return mapOf(
            "moto" to VehicleConfig(
                id = "moto",
                name = "Police Patrol Motorcycle",
                description = "Nimble and fast through narrow bazaar alleys. Quick acceleration but fragile.",
                baseSpeed = 230f,
                baseAcceleration = 130f,
                baseHandling = 1.35f,
                baseDurability = 110f,
                price = 0,
                isUnlocked = true
            ),
            "car" to VehicleConfig(
                id = "car",
                name = "Police Pursuit Sedan",
                description = "Standard city cruiser with balanced speed, pursuit stability, and sirens.",
                baseSpeed = 260f,
                baseAcceleration = 140f,
                baseHandling = 1.15f,
                baseDurability = 220f,
                price = 2000,
                isUnlocked = false
            ),
            "jeep" to VehicleConfig(
                id = "jeep",
                name = "Police 4x4 Tactical Jeep",
                description = "Rugged all-terrain enforcer for village outskirts, ramming roadblocks, and rough roads.",
                baseSpeed = 240f,
                baseAcceleration = 150f,
                baseHandling = 1.05f,
                baseDurability = 340f,
                price = 4500,
                isUnlocked = false
            ),
            "van" to VehicleConfig(
                id = "van",
                name = "Police Heavy Prisoner Van",
                description = "Armored tactical transport. Heavy armor and ramming power to stop fleeing convoys.",
                baseSpeed = 200f,
                baseAcceleration = 100f,
                baseHandling = 0.85f,
                baseDurability = 500f,
                price = 6500,
                isUnlocked = false
            )
        )
    }
}
