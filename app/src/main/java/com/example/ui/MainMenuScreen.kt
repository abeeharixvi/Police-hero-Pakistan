package com.example.ui

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import com.example.data.PlayerProfile

data class MenuAction(
    val id: String,
    val title: String,
    val icon: ImageVector,
    val color: Color,
    val testTag: String
)

@Composable
fun MainMenuScreen(
    profile: PlayerProfile,
    onNavigate: (String) -> Unit
) {
    val nextRank = profile.rank.nextRank()
    val xpProgress = if (nextRank != null) {
        val curBase = profile.rank.requiredXp
        val nextBase = nextRank.requiredXp
        ((profile.xp - curBase).toFloat() / (nextBase - curBase)).coerceIn(0f, 1f)
    } else 1f

    val quickActions = listOf(
        MenuAction("mission_board", "MISSIONS (30)", Icons.Default.Assignment, Color(0xFF1E88E5), "menu_missions"),
        MenuAction("police_station", "POLICE HQ HUB", Icons.Default.Security, Color(0xFF00ACC1), "menu_station"),
        MenuAction("garage", "MOTOR POOL", Icons.Default.DirectionsCar, Color(0xFF43A047), "menu_garage"),
        MenuAction("weapons", "WEAPONS ARMORY", Icons.Default.Security, Color(0xFFFB8C00), "menu_weapons"),
        MenuAction("criminals", "CRIMINAL RECORDS", Icons.Default.FormatListNumbered, Color(0xFFE53935), "menu_criminals"),
        MenuAction("lockup", "HOLDING LOCKUP", Icons.Default.Lock, Color(0xFF8E24AA), "menu_lockup"),
        MenuAction("daily_duties", "DAILY DUTIES", Icons.Default.AssignmentTurnedIn, Color(0xFF3949AB), "menu_daily_duties"),
        MenuAction("settings", "SETTINGS", Icons.Default.Settings, Color(0xFF607D8B), "menu_settings")
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF070D1E))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Left Column: Officer Profile Card & Main Continue/New Game CTA
            Card(
                modifier = Modifier
                    .weight(0.9f)
                    .fillMaxSize(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1B38)),
                border = BorderStroke(2.dp, Color(0xFF1E88E5)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // Police Badge & Title
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(Color(0xFF1565C0), CircleShape)
                                    .border(1.5.dp, Color(0xFFFFD700), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Badge, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "POLICE HERO PAKISTAN",
                                    color = Color(0xFFFFD700),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = "TACTICAL LAW ENFORCEMENT",
                                    color = Color(0xFF90CAF9),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Officer identity block
                        Surface(
                            color = Color(0xFF16254A),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, Color(0xFFFFD700)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = profile.officerName.uppercase(), color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Black)
                                Text(
                                    text = "${profile.rank.title.uppercase()} • ID: ${profile.officerId}",
                                    color = Color(0xFFFFD700),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = "LEVEL ${profile.level}", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    Text(text = "${profile.xp} XP", color = Color(0xFF90CAF9), fontSize = 10.sp)
                                }
                                Spacer(modifier = Modifier.height(3.dp))
                                LinearProgressIndicator(
                                    progress = { xpProgress },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = Color(0xFFFFD700),
                                    trackColor = Color(0xFF0A1128)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Quick career stats
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            MiniStatBox("FUNDS", "Rs ${profile.cash}", Color(0xFF81C784))
                            MiniStatBox("SOLVED", "${profile.completedMissions.size}/30", Color(0xFF64B5F6))
                            MiniStatBox("ARRESTS", "${profile.totalArrests}", Color(0xFFFF8A80))
                        }
                    }

                    // Main Primary Actions
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { onNavigate("continue_game") },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("menu_continue_game")
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "CONTINUE INVESTIGATION", fontWeight = FontWeight.Black, fontSize = 12.sp, letterSpacing = 1.sp)
                        }

                        Button(
                            onClick = { onNavigate("police_station") },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F2B5C)),
                            border = BorderStroke(1.dp, Color(0xFF64B5F6)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(38.dp)
                                .testTag("menu_enter_station")
                        ) {
                            Text(text = "ENTER POLICE STATION HQ", color = Color(0xFF90CAF9), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }
            }

            // Right Column: Grid of Management Departments & Features
            Column(
                modifier = Modifier
                    .weight(1.1f)
                    .fillMaxSize()
            ) {
                Text(
                    text = "DEPARTMENT MODULES & TACTICAL OPERATIONS",
                    color = Color(0xFF90CAF9),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(10.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(quickActions) { action ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(64.dp)
                                .clickable { onNavigate(action.id) }
                                .testTag(action.testTag),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1B38)),
                            border = BorderStroke(1.2.dp, action.color.copy(alpha = 0.7f)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .background(action.color.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                        .border(1.dp, action.color, RoundedCornerShape(8.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(action.icon, contentDescription = null, tint = action.color, modifier = Modifier.size(20.dp))
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = action.title,
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MiniStatBox(label: String, value: String, color: Color) {
    Surface(
        color = Color(0xFF070D1E),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color(0xFF1E293B))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = label, color = Color(0xFF94A3B8), fontSize = 9.sp, fontWeight = FontWeight.Bold)
            Text(text = value, color = color, fontSize = 11.sp, fontWeight = FontWeight.Black)
        }
    }
}
