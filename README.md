# Police Hero Pakistan 🚔🇵🇰

A complete, fully playable 2D top-down police action & investigation game for Android, set in a vibrant fictional Pakistani city.

Developed by **Arbab Rizvi**  
Phone: `030383631699`  
Email: `arbabrixvi@gmail.com`

---

## 🎮 Game Overview

Step into the shoes of a dedicated police officer in Pakistan. Patrol the bustling streets of a fictional Pakistani metropolis—from the colorful stalls of the Old Bazaar to the winding alleys of the Residential Mohalla, busy Bus Stands, and sprawling Industrial Outskirts.

Investigate fictional crimes, gather evidence, interrogate witnesses, pursue fleeing suspects on foot or by vehicle, make arrests, maintain the criminal records database, upgrade your arsenal and patrol fleet, and rise from **Constable** to **DSP**!

### The Central Loop
`CRIME REPORT ➔ TRAVEL ➔ INVESTIGATE ➔ FIND SUSPECT ➔ CHASE ➔ ARREST ➔ RETURN TO POLICE STATION ➔ UPDATE CRIMINAL RECORD ➔ LOCKUP ➔ RECEIVE XP/CASH ➔ UPGRADE ➔ UNLOCK NEXT MISSION`

---

## 🌟 Key Features

1. **Top-Down 2D Action & Driving Game Engine**:
   - Custom high-performance 2D Canvas game loop (30–60 FPS).
   - Dynamic camera tracking, collision detection with buildings, walls, and market stalls.
   - On-foot gameplay + Enterable vehicles with realistic acceleration, steering, sirens, and collision physics.

2. **30 Handcrafted Playable Missions**:
   - From *Mobile Snatching*, *Rickshaw Theft*, and *Bazaar Chases* to *Wanted Gang Leaders*, *Highway Pursuits*, and the climactic *Final Operation*.
   - Multi-stage objective tracker: Reach Location ➔ Investigate Clues ➔ Spot Suspect ➔ Foot/Vehicle Pursuit ➔ Arrest ➔ Return to Station.

3. **15 Persistent Fictional Criminals**:
   - Unique dossiers, wanted levels, crimes, mugshot colors, and dynamic WANTED ➔ ARRESTED statuses.
   - Interactive Lockup cells (Cell 01 – Cell 15) in the Police Station.

4. **Patrol Fleet & Weapon Armory**:
   - **Vehicles**: Police Motorcycle, Police Car, Police Jeep, Police Heavy Van with Engine, Handling, and Durability upgrades.
   - **Weapons**: Service Pistol, Tactical Shotgun, Patrol Assault Rifle with distinct range, accuracy, ammo, and reload mechanics.

5. **Police Station Hub**:
   - Navigate freely between Mission Board, Officer Desk, Criminal Records, Weapon Locker, Motor Pool Garage, Lockup Cells, and City Dispatch.

6. **Friendly Police Support AI**:
   - Call backup units or have AI patrol units assist in roadblocks and vehicle interceptions as you gain rank.

7. **Synthesized Dynamic Audio**:
   - Real-time procedural audio engine generating police sirens, radio squeaks, engine revs, footsteps, gunshots, and handcuff clicks without requiring bulky external media downloads.

8. **Offline-First & Local Persistence**:
   - Complete local JSON save system storing player profile, XP, cash, unlocked missions, weapon inventory, vehicle upgrades, and criminal lockup data.
   - 100% playable offline. Optional AdMob integration with test unit IDs and safe fallbacks.

---

## 📱 Tech Stack

- **Language**: 100% Kotlin
- **UI Framework**: Jetpack Compose + Material Design 3
- **Game Engine**: Android 2D Canvas View with real-time game loop
- **Target Platform**: Android (minSdk 24, targetSdk 36) - Landscape Orientation
- **Build System**: Gradle Kotlin DSL (.gradle.kts)
