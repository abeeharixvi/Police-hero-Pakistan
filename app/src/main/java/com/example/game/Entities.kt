package com.example.game

import android.graphics.PointF
import com.example.data.CriminalProfile
import com.example.data.VehicleConfig
import com.example.data.Weapon
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

enum class CriminalAIState {
    IDLE,
    PATROL,
    DETECT,
    FLEE,
    HIDE,
    ESCAPE_VEHICLE,
    ARRESTED
}

enum class PoliceAIState {
    PATROL,
    SEARCH,
    CHASE,
    BLOCK,
    ARREST,
    RETURN
}

data class BulletEntity(
    var x: Float,
    var y: Float,
    val dirX: Float,
    val dirY: Float,
    val speed: Float = 850f,
    val damage: Int,
    val isFromPlayer: Boolean,
    var distanceTraveled: Float = 0f,
    val maxRange: Float
) {
    var isAlive: Boolean = true

    fun update(dt: Float) {
        val step = speed * dt
        x += dirX * step
        y += dirY * step
        distanceTraveled += step
        if (distanceTraveled >= maxRange) {
            isAlive = false
        }
    }
}

class PlayerEntity(
    var x: Float = GameConstants.POLICE_STATION_X,
    var y: Float = GameConstants.POLICE_STATION_Y + 120f
) {
    var angle: Float = 0f // In radians
    var health: Float = GameConstants.PLAYER_MAX_HEALTH
    var maxHealth: Float = GameConstants.PLAYER_MAX_HEALTH
    var isSprinting: Boolean = false
    var inVehicle: VehicleEntity? = null
    var activeWeapon: Weapon? = null
    var isReloading: Boolean = false
    var reloadTimer: Long = 0L
    var lastShotTime: Long = 0L

    fun move(dx: Float, dy: Float, dt: Float, map: CityMap) {
        val veh = inVehicle
        if (veh != null) {
            veh.drive(dx, dy, dt, map)
            x = veh.x
            y = veh.y
            angle = veh.angle
            return
        }

        val len = sqrt(dx * dx + dy * dy)
        if (len > 0.05f) {
            val normX = dx / len
            val normY = dy / len
            angle = atan2(normY, normX)
            val speed = if (isSprinting) GameConstants.PLAYER_RUN_SPEED else GameConstants.PLAYER_WALK_SPEED
            val newX = x + normX * speed * dt
            val newY = y + normY * speed * dt

            // Attempt movement with wall sliding
            if (!CityMap.isCollidingWithObstacle(newX, y, GameConstants.PLAYER_RADIUS)) {
                x = newX
            }
            if (!CityMap.isCollidingWithObstacle(x, newY, GameConstants.PLAYER_RADIUS)) {
                y = newY
            }
        }
    }

    fun takeDamage(amount: Float) {
        val veh = inVehicle
        if (veh != null) {
            veh.durability -= amount
            if (veh.durability <= 0f) {
                veh.durability = 0f
                health -= amount * 0.5f
            }
        } else {
            health = (health - amount).coerceAtLeast(0f)
        }
    }
}

class VehicleEntity(
    var x: Float,
    var y: Float,
    val config: VehicleConfig,
    val isPolice: Boolean = true,
    var isSuspectVehicle: Boolean = false
) {
    var angle: Float = 0f
    var speed: Float = 0f
    var durability: Float = config.maxDurability
    var isSirenOn: Boolean = true
    val length: Float = 44f
    val width: Float = 24f

    fun drive(inputForward: Float, inputSteer: Float, dt: Float, map: CityMap) {
        // Steering
        if (speed != 0f) {
            val steerMultiplier = if (speed > 0) 1f else -1f
            angle += inputSteer * config.handling * 2.8f * dt * steerMultiplier
        }

        // Acceleration and braking
        if (inputForward > 0.1f) {
            speed = (speed + config.acceleration * inputForward * dt).coerceAtMost(config.maxSpeed)
        } else if (inputForward < -0.1f) {
            speed = (speed - config.acceleration * 1.3f * (-inputForward) * dt).coerceAtLeast(-config.maxSpeed * 0.4f)
        } else {
            // Drag deceleration
            if (speed > 0) {
                speed = (speed - 120f * dt).coerceAtLeast(0f)
            } else if (speed < 0) {
                speed = (speed + 120f * dt).coerceAtMost(0f)
            }
        }

        val moveDist = speed * dt
        val nextX = x + cos(angle) * moveDist
        val nextY = y + sin(angle) * moveDist

        // Check vehicle collision with buildings
        if (!CityMap.isCollidingWithObstacle(nextX, nextY, width * 0.8f)) {
            x = nextX
            y = nextY
        } else {
            // Crash collision
            speed = -speed * 0.35f
            durability = (durability - 15f).coerceAtLeast(0f)
        }
    }
}

class CriminalEntity(
    var x: Float,
    var y: Float,
    val profile: CriminalProfile,
    var hasGetawayVehicle: Boolean = false
) {
    var angle: Float = 0f
    var health: Float = 100f
    var aiState: CriminalAIState = CriminalAIState.IDLE
    var fleeTargetX: Float = 4500f
    var fleeTargetY: Float = 4500f
    var vehicle: VehicleEntity? = null
    var stateTimer: Float = 0f
    var shootCooldown: Float = 1.5f

    init {
        if (hasGetawayVehicle) {
            vehicle = VehicleEntity(
                x, y,
                config = VehicleConfig("stolen_car", "Getaway Sedan", "Fleeing criminal vehicle", 220f, 120f, 1.0f, 250f, 0),
                isPolice = false,
                isSuspectVehicle = true
            )
        }
    }

    fun updateAI(dt: Float, player: PlayerEntity, onShoot: (BulletEntity) -> Unit) {
        if (aiState == CriminalAIState.ARRESTED) {
            return
        }

        stateTimer += dt
        val distToPlayer = sqrt((player.x - x) * (player.x - x) + (player.y - y) * (player.y - y))

        // State Machine transitions
        when (aiState) {
            CriminalAIState.IDLE, CriminalAIState.PATROL -> {
                if (distToPlayer < 320f) {
                    aiState = if (hasGetawayVehicle && vehicle != null) CriminalAIState.ESCAPE_VEHICLE else CriminalAIState.FLEE
                } else if (stateTimer > 3f) {
                    stateTimer = 0f
                    // Wander slightly in zone
                    angle += Random.nextFloat() * 1.5f - 0.75f
                }
            }
            CriminalAIState.DETECT -> {
                if (distToPlayer < 400f) {
                    aiState = CriminalAIState.FLEE
                }
            }
            CriminalAIState.FLEE -> {
                // Vector away from player
                val awayX = x - player.x
                val awayY = y - player.y
                val awayLen = sqrt(awayX * awayX + awayY * awayY)
                if (awayLen > 0.01f) {
                    val desiredAngle = atan2(awayY, awayX)
                    angle = desiredAngle
                    val speed = GameConstants.CRIMINAL_RUN_SPEED
                    val nextX = x + cos(angle) * speed * dt
                    val nextY = y + sin(angle) * speed * dt

                    // Obstacle avoidance
                    if (!CityMap.isCollidingWithObstacle(nextX, y, GameConstants.CRIMINAL_RADIUS)) {
                        x = nextX
                    } else {
                        angle += 1.2f // Turn around corner
                    }
                    if (!CityMap.isCollidingWithObstacle(x, nextY, GameConstants.CRIMINAL_RADIUS)) {
                        y = nextY
                    } else {
                        angle += 1.2f
                    }
                }

                // Armed high-level criminals shoot back occasionally
                if (profile.wantedLevel >= 3 && distToPlayer < 280f) {
                    shootCooldown -= dt
                    if (shootCooldown <= 0f) {
                        shootCooldown = 1.8f
                        val aimAngle = atan2(player.y - y, player.x - x)
                        onShoot(
                            BulletEntity(
                                x = x,
                                y = y,
                                dirX = cos(aimAngle),
                                dirY = sin(aimAngle),
                                damage = 15,
                                isFromPlayer = false,
                                maxRange = 300f
                            )
                        )
                    }
                }
            }
            CriminalAIState.ESCAPE_VEHICLE -> {
                val veh = vehicle
                if (veh != null) {
                    // Drive towards hideout or along roads
                    val targetAngle = atan2(fleeTargetY - y, fleeTargetX - x)
                    val diff = targetAngle - veh.angle
                    val steer = (diff).coerceIn(-1f, 1f)
                    veh.drive(1.0f, steer, dt, CityMap)
                    x = veh.x
                    y = veh.y
                    angle = veh.angle

                    // If vehicle destroyed, exit and flee on foot
                    if (veh.durability <= 20f) {
                        aiState = CriminalAIState.FLEE
                        vehicle = null
                    }
                } else {
                    aiState = CriminalAIState.FLEE
                }
            }
            CriminalAIState.HIDE -> {
                // Stay still behind obstacle until player gets too close
                if (distToPlayer < 120f) {
                    aiState = CriminalAIState.FLEE
                }
            }
            CriminalAIState.ARRESTED -> {
                // Handcuffed
            }
        }
    }
}

class PoliceBackupEntity(
    var x: Float,
    var y: Float
) {
    var angle: Float = 0f
    var state: PoliceAIState = PoliceAIState.PATROL
    val radius: Float = 20f

    fun update(dt: Float, targetCriminal: CriminalEntity?) {
        if (targetCriminal == null || targetCriminal.aiState == CriminalAIState.ARRESTED) {
            state = PoliceAIState.PATROL
            return
        }

        // Pursue or block criminal
        val dx = targetCriminal.x - x
        val dy = targetCriminal.y - y
        val dist = sqrt(dx * dx + dy * dy)

        if (dist > 80f) {
            angle = atan2(dy, dx)
            val step = GameConstants.POLICE_AI_SPEED * dt
            val nextX = x + cos(angle) * step
            val nextY = y + sin(angle) * step

            if (!CityMap.isCollidingWithObstacle(nextX, nextY, radius)) {
                x = nextX
                y = nextY
            }
        }
    }
}

class CivilianEntity(
    var x: Float,
    var y: Float,
    val type: String // "vendor", "citizen", "rickshaw_driver"
) {
    var angle: Float = Random.nextFloat() * 6.28f
    var walkTimer: Float = 0f

    fun update(dt: Float) {
        walkTimer += dt
        if (walkTimer > 2.5f) {
            walkTimer = 0f
            angle += Random.nextFloat() * 1.8f - 0.9f
        }
        val speed = 35f
        val nx = x + cos(angle) * speed * dt
        val ny = y + sin(angle) * speed * dt
        if (!CityMap.isCollidingWithObstacle(nx, ny, 12f)) {
            x = nx
            y = ny
        } else {
            angle += 3.14f
        }
    }
}
