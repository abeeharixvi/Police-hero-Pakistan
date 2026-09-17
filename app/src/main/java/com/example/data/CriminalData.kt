package com.example.data

enum class CriminalStatus {
    WANTED,
    ARRESTED
}

data class CriminalProfile(
    val id: String,
    val name: String,
    val alias: String,
    var status: CriminalStatus = CriminalStatus.WANTED,
    val wantedLevel: Int, // 1 to 5 stars
    val crimes: List<String>,
    var previousArrests: Int,
    val knownLocationZone: String,
    val avatarColorHex: Long, // Color for mugshot badge
    var evidenceFound: String = "Under ongoing surveillance",
    var arrestHistoryNote: String = "No prior conviction on file",
    val cellNumber: Int
) {
    val mugshotColorHex: Long get() = avatarColorHex
    val crimeType: String get() = crimes.firstOrNull() ?: "Criminal Offense"
    val zoneLocation: String get() = knownLocationZone
}

object CriminalDatabase {
    fun getDefaultCriminals(): List<CriminalProfile> {
        return listOf(
            CriminalProfile(
                id = "CR-101",
                name = "Rashid Chohan",
                alias = "Billa Snatcher",
                wantedLevel = 1,
                crimes = listOf("Mobile Phone Snatching", "Fleeing on Motorbike"),
                previousArrests = 1,
                knownLocationZone = "Old Bazaar",
                avatarColorHex = 0xFFD32F2F,
                cellNumber = 1
            ),
            CriminalProfile(
                id = "CR-102",
                name = "Kamran Malik",
                alias = "Tikka Pickpocket",
                wantedLevel = 1,
                crimes = listOf("Purse Snatching", "Crowd Disturbance"),
                previousArrests = 0,
                knownLocationZone = "Commercial Area",
                avatarColorHex = 0xFFE64A19,
                cellNumber = 2
            ),
            CriminalProfile(
                id = "CR-103",
                name = "Tariq Butt",
                alias = "Kabari Tariq",
                wantedLevel = 1,
                crimes = listOf("Bicycle Theft", "Illegal Scrap Parts Trading"),
                previousArrests = 2,
                knownLocationZone = "Residential Mohalla",
                avatarColorHex = 0xFFF57C00,
                cellNumber = 3
            ),
            CriminalProfile(
                id = "CR-104",
                name = "Zubair Ansari",
                alias = "Chor Zubair",
                wantedLevel = 2,
                crimes = listOf("Commercial Shop Burglary", "Breaking Shutter Locks"),
                previousArrests = 1,
                knownLocationZone = "Commercial Area",
                avatarColorHex = 0xFF7B1FA2,
                cellNumber = 4
            ),
            CriminalProfile(
                id = "CR-105",
                name = "Farooq Qureshi",
                alias = "Lala Armed",
                wantedLevel = 2,
                crimes = listOf("Street Armed Robbery", "Weapon Brandishing"),
                previousArrests = 3,
                knownLocationZone = "Old Bazaar",
                avatarColorHex = 0xFF512DA8,
                cellNumber = 5
            ),
            CriminalProfile(
                id = "CR-106",
                name = "Babar Jutt",
                alias = "Speedy Babar",
                wantedLevel = 2,
                crimes = listOf("Motorcycle Theft Ring", "Speeding through Alleys"),
                previousArrests = 1,
                knownLocationZone = "Bus Stand",
                avatarColorHex = 0xFF303F9F,
                cellNumber = 6
            ),
            CriminalProfile(
                id = "CR-107",
                name = "Munir Auto",
                alias = "Ustad Munir",
                wantedLevel = 2,
                crimes = listOf("Auto-Rickshaw Hijacking", "Chassis Number Tampering"),
                previousArrests = 2,
                knownLocationZone = "Railway Area",
                avatarColorHex = 0xFF0097A7,
                cellNumber = 7
            ),
            CriminalProfile(
                id = "CR-108",
                name = "Waseem Sheikh",
                alias = "V8 Waseem",
                wantedLevel = 3,
                crimes = listOf("High-End Car Theft", "Fleeing Roadblocks"),
                previousArrests = 2,
                knownLocationZone = "Highway",
                avatarColorHex = 0xFF00796B,
                cellNumber = 8
            ),
            CriminalProfile(
                id = "CR-109",
                name = "Nadeem Awan",
                alias = "Shadow Nadeem",
                wantedLevel = 3,
                crimes = listOf("Getaway Driving", "Stolen Vehicle Transport"),
                previousArrests = 4,
                knownLocationZone = "Industrial Area",
                avatarColorHex = 0xFF388E3C,
                cellNumber = 9
            ),
            CriminalProfile(
                id = "CR-110",
                name = "Aslam Niazi",
                alias = "Daku Aslam",
                wantedLevel = 3,
                crimes = listOf("Highway Freight Hijacking", "Armed Extortion"),
                previousArrests = 3,
                knownLocationZone = "Highway",
                avatarColorHex = 0xFF689F38,
                cellNumber = 10
            ),
            CriminalProfile(
                id = "CR-111",
                name = "Suleman Rind",
                alias = "Sharpshooter Suleman",
                wantedLevel = 4,
                crimes = listOf("Illegal Weapons Smuggling", "Attacking Patrol Officers"),
                previousArrests = 5,
                knownLocationZone = "Village Outskirts",
                avatarColorHex = 0xFFE65100,
                cellNumber = 11
            ),
            CriminalProfile(
                id = "CR-112",
                name = "Haider Kazmi",
                alias = "Don Haider",
                wantedLevel = 4,
                crimes = listOf("Bazaar Bhatta Extortion", "Protection Racket Operations"),
                previousArrests = 4,
                knownLocationZone = "Old Bazaar",
                avatarColorHex = 0xFFC2185B,
                cellNumber = 12
            ),
            CriminalProfile(
                id = "CR-113",
                name = "Iqbal Baloch",
                alias = "Target Iqbal",
                wantedLevel = 4,
                crimes = listOf("Underground Syndicate Enforcer", "Illegal Arms Distribution"),
                previousArrests = 6,
                knownLocationZone = "Criminal Hideout Area",
                avatarColorHex = 0xFFB71C1C,
                cellNumber = 13
            ),
            CriminalProfile(
                id = "CR-114",
                name = "Shera Gujjar",
                alias = "Badmash Shera",
                wantedLevel = 5,
                crimes = listOf("Armed Convoy Robbery", "Leading Outlaw Gang"),
                previousArrests = 7,
                knownLocationZone = "Industrial Area",
                avatarColorHex = 0xFF880E4F,
                cellNumber = 14
            ),
            CriminalProfile(
                id = "CR-115",
                name = "Sikandar Khan",
                alias = "Tiger Sikandar",
                wantedLevel = 5,
                crimes = listOf("Mastermind of Citywide Smuggling Network", "Major Cartel Operations"),
                previousArrests = 8,
                knownLocationZone = "Criminal Hideout Area",
                avatarColorHex = 0xFF212121,
                cellNumber = 15
            )
        )
    }
}
