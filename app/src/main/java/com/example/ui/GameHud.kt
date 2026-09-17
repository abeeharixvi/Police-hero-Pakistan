package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.GameConstants
import com.example.game.GameSession

@Composable
fun GameHud(
    session: GameSession,
    onJoystickMoved: (Float, Float) -> Unit,
    onShoot: () -> Unit,
    onReload: () -> Unit,
    onArrest: () -> Unit,
    onInvestigate: () -> Unit,
    onVehicleToggle: () -> Unit,
    onSwitchWeapon: () -> Unit,
    onSirenToggle: () -> Unit,
    onPause: () -> Unit
) {
    val player = session.player
    val currentObj = session.currentObjective

    Box(modifier = Modifier.fillMaxSize()) {

        // TOP-LEFT: Mission Objective Card & Live Notification
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .width(320.dp)
        ) {
            Surface(
                color = Color(0xDD0A192F),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF1E88E5))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "MISSION ${session.mission.id}: ${session.mission.title.uppercase()}",
                            color = Color(0xFFFFD700),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        Text(
                            text = session.mission.locationZone,
                            color = Color(0xFF90CAF9),
                            fontSize = 11.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Current Active Objective
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .background(Color(0xFF0288D1), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${session.currentStepIndex + 1}",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = currentObj?.description ?: "Return to Station",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Floating Dispatch Radio Notification
            AnimatedVisibility(
                visible = session.notificationTimer > 0f,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Surface(
                    modifier = Modifier.padding(top = 8.dp),
                    color = Color(0xE6263238),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFB300))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "DISPATCH: ",
                            color = Color(0xFFFFB300),
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                        Text(
                            text = session.notificationMessage,
                            color = Color.White,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // TOP-RIGHT: Health Bar, Cash & Controls (Minimap is rendered directly on canvas to the right)
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 16.dp, end = 170.dp), // Leaves room for canvas minimap (140px + margin)
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Cash Badge
            Surface(
                color = Color(0xDD0A192F),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF4CAF50))
            ) {
                Text(
                    text = "Rs ${session.profile.cash}",
                    color = Color(0xFF81C784),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }

            // Health Bar
            Surface(
                color = Color(0xDD0A192F),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE53935))
            ) {
                Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "HP ",
                            color = Color(0xFFEF5350),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        LinearProgressIndicator(
                            progress = { (player.health / GameConstants.PLAYER_MAX_HEALTH).coerceIn(0f, 1f) },
                            modifier = Modifier
                                .width(90.dp)
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = if (player.health > 40f) Color(0xFF4CAF50) else Color(0xFFE53935),
                            trackColor = Color(0xFF37474F)
                        )
                    }
                }
            }

            // Siren Toggle (if inside vehicle)
            if (player.inVehicle != null) {
                HudIconButton(
                    icon = Icons.Default.Emergency,
                    label = if (player.inVehicle!!.isSirenOn) "SIREN ON" else "SIREN OFF",
                    color = if (player.inVehicle!!.isSirenOn) Color(0xFFE53935) else Color(0xFF78909C),
                    onClick = onSirenToggle
                )
            }

            // Pause Button
            HudIconButton(
                icon = Icons.Default.Pause,
                label = "PAUSE",
                color = Color(0xFF90A4AE),
                onClick = onPause
            )
        }

        // BOTTOM-LEFT: Virtual Joystick
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 32.dp, bottom = 24.dp)
                .testTag("virtual_joystick")
        ) {
            VirtualJoystick(
                radiusDp = 65f,
                onValueChange = onJoystickMoved
            )
        }

        // BOTTOM-RIGHT: Contextual Tactical Action Buttons
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = 20.dp),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Secondary row: Contextual interaction buttons
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Investigate Clue (Contextual)
                if (session.nearestClue != null && !session.nearestClue!!.isInvestigated) {
                    HudActionButton(
                        icon = Icons.Default.Search,
                        title = "INVESTIGATE",
                        bgColor = Color(0xFFFFB300),
                        testTag = "investigate_button",
                        onClick = onInvestigate
                    )
                }

                // Arrest Suspect (Contextual)
                if (session.canArrestSuspect) {
                    HudActionButton(
                        icon = Icons.Default.Lock,
                        title = "ARREST",
                        bgColor = Color(0xFF00E676),
                        testTag = "arrest_button",
                        onClick = onArrest
                    )
                }

                // Enter / Exit Vehicle (Contextual)
                if (player.inVehicle != null || session.nearestVehicle != null) {
                    HudActionButton(
                        icon = Icons.Default.DirectionsCar,
                        title = if (player.inVehicle != null) "EXIT" else "DRIVE",
                        bgColor = Color(0xFF0288D1),
                        testTag = "vehicle_toggle_button",
                        onClick = onVehicleToggle
                    )
                }

                // Switch Weapon Button
                HudActionButton(
                    icon = Icons.Default.SwapHoriz,
                    title = "SWITCH",
                    bgColor = Color(0xFF455A64),
                    testTag = "switch_weapon_button",
                    onClick = onSwitchWeapon
                )
            }

            // Primary row: Combat & Reload
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                // Reload Button
                val wep = player.activeWeapon
                val ammoText = if (wep != null) "${wep.currentAmmoInMag}/${wep.reserveAmmo}" else "--"
                HudActionButton(
                    icon = Icons.Default.Refresh,
                    title = if (player.isReloading) "RELOADING..." else "RELOAD\n$ammoText",
                    bgColor = Color(0xFF37474F),
                    testTag = "reload_button",
                    onClick = onReload
                )

                // Shoot Button (Only active when on foot or armed)
                HudActionButton(
                    icon = Icons.Default.Close, // Crosshair icon
                    title = "FIRE",
                    bgColor = Color(0xFFD32F2F),
                    sizeDp = 70,
                    testTag = "shoot_button",
                    onClick = onShoot
                )
            }
        }
    }
}

@Composable
private fun HudActionButton(
    icon: ImageVector,
    title: String,
    bgColor: Color,
    sizeDp: Int = 56,
    testTag: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .size(width = (sizeDp * 1.4).dp, height = sizeDp.dp)
            .clickable { onClick() }
            .testTag(testTag),
        color = bgColor.copy(alpha = 0.9f),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color.White.copy(alpha = 0.6f))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = title,
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                lineHeight = 11.sp
            )
        }
    }
}

@Composable
private fun HudIconButton(
    icon: ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clickable { onClick() },
        color = Color(0xDD0A192F),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
