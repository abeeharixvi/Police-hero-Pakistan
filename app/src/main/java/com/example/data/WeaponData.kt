package com.example.data

data class Weapon(
    val id: String,
    val name: String,
    val damage: Int,
    val range: Float,
    val magazineCapacity: Int,
    var currentAmmoInMag: Int,
    var reserveAmmo: Int,
    val reloadTimeMs: Long,
    val accuracy: Float, // 0.0 to 1.0 (1.0 = perfect laser)
    val price: Int,
    var isUnlocked: Boolean = false,
    val fireRateMs: Long = 300L,
    val ammoPrice: Int = 150
) {
    fun copyWeapon(): Weapon {
        return copy(
            currentAmmoInMag = currentAmmoInMag,
            reserveAmmo = reserveAmmo,
            isUnlocked = isUnlocked
        )
    }
}

object WeaponCatalog {
    fun getDefaultWeapons(): Map<String, Weapon> {
        return mapOf(
            "pistol" to Weapon(
                id = "pistol",
                name = "Service Pistol 9mm",
                damage = 25,
                range = 380f,
                magazineCapacity = 12,
                currentAmmoInMag = 12,
                reserveAmmo = 72,
                reloadTimeMs = 1200L,
                accuracy = 0.88f,
                price = 0,
                isUnlocked = true,
                fireRateMs = 280L
            ),
            "shotgun" to Weapon(
                id = "shotgun",
                name = "Police 12-Gauge Shotgun",
                damage = 65,
                range = 280f,
                magazineCapacity = 6,
                currentAmmoInMag = 6,
                reserveAmmo = 36,
                reloadTimeMs = 1800L,
                accuracy = 0.72f,
                price = 1500,
                isUnlocked = false,
                fireRateMs = 600L
            ),
            "rifle" to Weapon(
                id = "rifle",
                name = "Tactical Patrol Rifle",
                damage = 42,
                range = 500f,
                magazineCapacity = 30,
                currentAmmoInMag = 30,
                reserveAmmo = 120,
                reloadTimeMs = 2000L,
                accuracy = 0.94f,
                price = 3200,
                isUnlocked = false,
                fireRateMs = 150L
            )
        )
    }
}
