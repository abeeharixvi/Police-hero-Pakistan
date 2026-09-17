package com.example.game

import android.graphics.RectF

data class CityZone(
    val id: Int,
    val name: String,
    val bounds: RectF,
    val description: String,
    val accentColorHex: Long
)

data class BuildingObstacle(
    val bounds: RectF,
    val type: String, // "house", "shop", "stall", "station", "warehouse", "dhaba", "wall", "tree"
    val colorHex: Long
)

data class RoadSegment(
    val bounds: RectF,
    val isHighway: Boolean = false,
    val isAlley: Boolean = false
)

object CityMap {
    val ZONES: List<CityZone> = listOf(
        CityZone(1, "Old Bazaar", RectF(1000f, 800f, 2000f, 1700f), "Narrow spice lanes, vibrant textile shops, and bustling street stalls.", 0xFFD87D4A),
        CityZone(2, "Residential Mohalla", RectF(700f, 1800f, 1800f, 2900f), "Dense brick houses, tight corners, courtyards, and local neighbourhood streets.", 0xFF8D6E63),
        CityZone(3, "Commercial Area", RectF(2200f, 900f, 3300f, 1900f), "Plazas, banks, electronics showrooms, and busy commercial avenues.", 0xFF546E7A),
        CityZone(4, "Bus Stand", RectF(2000f, 2200f, 2900f, 3100f), "Inter-city bus terminal, colourful Bedford minibuses, tea stalls, and travelers.", 0xFF7E57C2),
        CityZone(5, "Railway Area", RectF(3200f, 1800f, 4400f, 2900f), "Freight depot, rail tracks, storage yards, and cargo sheds.", 0xFF455A64),
        CityZone(6, "Industrial Area", RectF(3300f, 3000f, 4600f, 4100f), "Heavy machinery mills, manufacturing units, and gated compounds.", 0xFF37474F),
        CityZone(7, "Village Outskirts", RectF(400f, 3100f, 1600f, 4600f), "Clay-brick kilns, fields, palm trees, unpaved village roads, and old wells.", 0xFF689F38),
        CityZone(8, "Highway", RectF(1500f, 3500f, 4600f, 4150f), "High-speed multi-lane dual carriageway connecting city and cargo routes.", 0xFF263238),
        CityZone(9, "Police Station", RectF(2200f, 2200f, 2800f, 2800f), "Central Headquarters, motor pool garage, lockup yard, and briefing halls.", 0xFF1565C0),
        CityZone(10, "Criminal Hideout Area", RectF(3700f, 4100f, 4900f, 4900f), "Abandoned factory complex, barricaded alleyways, and syndicate safehouses.", 0xFFB71C1C)
    )

    // Roads layout
    val ROADS: List<RoadSegment> = listOf(
        // Central avenues
        RoadSegment(RectF(2350f, 0f, 2650f, 5000f)), // Main North-South Spine
        RoadSegment(RectF(0f, 2350f, 5000f, 2650f)), // Main East-West Spine

        // Bazaar roads & alleys
        RoadSegment(RectF(1000f, 1200f, 2350f, 1350f)),
        RoadSegment(RectF(1400f, 800f, 1550f, 2350f), isAlley = true),
        RoadSegment(RectF(1750f, 800f, 1880f, 1800f), isAlley = true),

        // Commercial Avenues
        RoadSegment(RectF(2650f, 1250f, 3400f, 1400f)),
        RoadSegment(RectF(2900f, 900f, 3050f, 2350f)),

        // Mohalla streets
        RoadSegment(RectF(800f, 2100f, 2350f, 2220f), isAlley = true),
        RoadSegment(RectF(1100f, 1900f, 1220f, 2900f), isAlley = true),

        // Railway & Industrial routes
        RoadSegment(RectF(2650f, 2650f, 4500f, 2800f)),
        RoadSegment(RectF(3600f, 1800f, 3750f, 4200f)),
        RoadSegment(RectF(4000f, 2800f, 4150f, 4600f)),

        // High-Speed Southern Highway
        RoadSegment(RectF(800f, 3700f, 4800f, 3950f), isHighway = true),

        // Village outskirts trail
        RoadSegment(RectF(700f, 2900f, 850f, 4700f)),
        RoadSegment(RectF(600f, 4200f, 2400f, 4320f)),

        // Hideout compound access
        RoadSegment(RectF(3800f, 4150f, 4600f, 4250f), isAlley = true)
    )

    // Solid Buildings & Obstacles for realistic city collision
    val OBSTACLES: List<BuildingObstacle> = listOf(
        // Police Station HQ enclosure
        BuildingObstacle(RectF(2380f, 2380f, 2620f, 2520f), "station", 0xFF1A365D), // Main HQ Building
        BuildingObstacle(RectF(2250f, 2380f, 2340f, 2500f), "station", 0xFF2A4365), // Motor Pool Garage
        BuildingObstacle(RectF(2660f, 2380f, 2750f, 2500f), "station", 0xFF2D3748), // Armory & Lockup Annex

        // Old Bazaar Stalls and Shops
        BuildingObstacle(RectF(1100f, 900f, 1350f, 1100f), "stall", 0xFF8D6E63),
        BuildingObstacle(RectF(1150f, 1400f, 1350f, 1600f), "shop", 0xFF795548),
        BuildingObstacle(RectF(1600f, 900f, 1720f, 1150f), "stall", 0xFFD84315),
        BuildingObstacle(RectF(1600f, 1400f, 1720f, 1650f), "shop", 0xFF6D4C41),
        BuildingObstacle(RectF(1900f, 950f, 2150f, 1200f), "shop", 0xFF8D6E63),
        BuildingObstacle(RectF(1900f, 1350f, 2150f, 1600f), "shop", 0xFF5D4037),

        // Commercial Plaza Buildings
        BuildingObstacle(RectF(2700f, 950f, 2880f, 1200f), "house", 0xFF37474F),
        BuildingObstacle(RectF(3100f, 950f, 3350f, 1200f), "house", 0xFF455A64),
        BuildingObstacle(RectF(2700f, 1450f, 2880f, 1750f), "house", 0xFF263238),
        BuildingObstacle(RectF(3100f, 1450f, 3350f, 1750f), "house", 0xFF37474F),

        // Residential Mohalla Houses
        BuildingObstacle(RectF(850f, 1950f, 1050f, 2150f), "house", 0xFFA1887F),
        BuildingObstacle(RectF(1250f, 1950f, 1450f, 2150f), "house", 0xFF8D6E63),
        BuildingObstacle(RectF(850f, 2350f, 1050f, 2600f), "house", 0xFF795548),
        BuildingObstacle(RectF(1250f, 2350f, 1450f, 2600f), "house", 0xFF6D4C41),
        BuildingObstacle(RectF(850f, 2700f, 1350f, 2850f), "wall", 0xFF5D4037),

        // Bus Stand Passenger Sheds & Dhaba
        BuildingObstacle(RectF(2050f, 2700f, 2300f, 2850f), "dhaba", 0xFFE65100),
        BuildingObstacle(RectF(2050f, 2900f, 2300f, 3050f), "stall", 0xFF4E342E),
        BuildingObstacle(RectF(2450f, 2750f, 2650f, 2900f), "shop", 0xFF3E2723),

        // Railway Freight Depot Sheds
        BuildingObstacle(RectF(3300f, 1900f, 3550f, 2150f), "warehouse", 0xFF37474F),
        BuildingObstacle(RectF(3800f, 1900f, 4100f, 2200f), "warehouse", 0xFF455A64),
        BuildingObstacle(RectF(3300f, 2350f, 3550f, 2550f), "warehouse", 0xFF263238),

        // Industrial Mill Compounds
        BuildingObstacle(RectF(3400f, 3100f, 3580f, 3400f), "warehouse", 0xFF212121),
        BuildingObstacle(RectF(3800f, 3100f, 4100f, 3400f), "warehouse", 0xFF424242),
        BuildingObstacle(RectF(4200f, 3100f, 4500f, 3500f), "warehouse", 0xFF263238),
        BuildingObstacle(RectF(3400f, 3600f, 3580f, 3900f), "warehouse", 0xFF303030),

        // Village Kilns, Mud Walls & Outskirts Houses
        BuildingObstacle(RectF(500f, 3300f, 700f, 3550f), "house", 0xFF8D6E63),
        BuildingObstacle(RectF(900f, 3300f, 1150f, 3600f), "dhaba", 0xFF795548),
        BuildingObstacle(RectF(500f, 3800f, 750f, 4100f), "wall", 0xFF5D4037),
        BuildingObstacle(RectF(900f, 3950f, 1150f, 4200f), "house", 0xFF6D4C41),

        // Highway Petrol Station & Toll Plaza
        BuildingObstacle(RectF(2100f, 3550f, 2300f, 3680f), "shop", 0xFFD32F2F), // Petrol Station Canopy
        BuildingObstacle(RectF(3200f, 3550f, 3400f, 3680f), "shop", 0xFF1976D2), // Highway Diner

        // Criminal Hideout Heavy Barricades & Fortified Mill
        BuildingObstacle(RectF(3900f, 4300f, 4200f, 4600f), "warehouse", 0xFF1A1A1A),
        BuildingObstacle(RectF(4350f, 4300f, 4700f, 4650f), "warehouse", 0xFF0D0D0D),
        BuildingObstacle(RectF(3900f, 4700f, 4400f, 4850f), "wall", 0xFF262626)
    )

    fun getZoneAt(x: Float, y: Float): CityZone {
        for (z in ZONES) {
            if (z.bounds.contains(x, y)) {
                return z
            }
        }
        return ZONES[8] // Default Police Station
    }

    fun isCollidingWithObstacle(x: Float, y: Float, radius: Float): Boolean {
        // Map boundary check
        if (x - radius < 50f || x + radius > GameConstants.WORLD_WIDTH - 50f ||
            y - radius < 50f || y + radius > GameConstants.WORLD_HEIGHT - 50f
        ) {
            return true
        }

        for (obs in OBSTACLES) {
            // Expand bounds by radius
            if (x >= obs.bounds.left - radius && x <= obs.bounds.right + radius &&
                y >= obs.bounds.top - radius && y <= obs.bounds.bottom + radius
            ) {
                return true
            }
        }
        return false
    }
}
