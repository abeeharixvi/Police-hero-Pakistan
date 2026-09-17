package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.OfficerRank
import com.example.data.PlayerProfile

@Composable
fun ProfileScreen(
    profile: PlayerProfile,
    onBack: () -> Unit
) {
    val accuracy = if (profile.shotsFired > 0) {
        ((profile.shotsHit.toFloat() / profile.shotsFired) * 100).toInt().coerceIn(0, 100)
    } else 100

    val nextRank = profile.rank.nextRank()
    val xpProgress = if (nextRank != null) {
        val currentBase = profile.rank.requiredXp
        val nextBase = nextRank.requiredXp
        ((profile.xp - currentBase).toFloat() / (nextBase - currentBase)).coerceIn(0f, 1f)
    } else 1f

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A1128))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 14.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("profile_back_button")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "OFFICER SERVICE DOSSIER & SERVICE RECORD",
                            color = Color(0xFFFFD700),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "Official Personnel Record • Government of Pakistan Police Service",
                            color = Color(0xFF90CAF9),
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Left Column: Officer Identity & Rank Progress
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF131E3A)),
                    border = BorderStroke(1.5.dp, Color(0xFF1E88E5)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .background(Color(0xFF0D47A1), CircleShape)
                                .border(2.5.dp, Color(0xFFFFD700), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Badge, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(44.dp))
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = profile.officerName.uppercase(),
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "BADGE NO: ${profile.officerId}",
                            color = Color(0xFF90CAF9),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Surface(
                            color = Color(0xFF1E293B),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color(0xFFFFD700)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "COMMISSIONED RANK",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = profile.rank.title.uppercase(),
                                    color = Color(0xFFFFD700),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = "XP: ${profile.xp}", color = Color.White, fontSize = 11.sp)
                                    Text(
                                        text = if (nextRank != null) "NEXT: ${nextRank.title} (${nextRank.requiredXp} XP)" else "MAX RANK REACHED",
                                        color = Color(0xFF81C784),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                LinearProgressIndicator(
                                    progress = { xpProgress },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp)),
                                    color = Color(0xFFFFD700),
                                    trackColor = Color(0xFF0F172A)
                                )
                            }
                        }
                    }
                }

                // Right Column: Career Statistics & Gear Deployment
                Card(
                    modifier = Modifier
                        .weight(1.1f)
                        .fillMaxSize(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF131E3A)),
                    border = BorderStroke(1.5.dp, Color(0xFF1E88E5)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "CAREER PERFORMANCE METRICS",
                            color = Color(0xFFFFD700),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )

                        DetailLine("Missions Solved", "${profile.completedMissions.size} / 30 Cases")
                        DetailLine("Apprehended Criminals", "${profile.totalArrests} Suspects in Lockup")
                        DetailLine("City Patrol Distance", "${(profile.patrolDistanceMeters / 1000f).toInt()} km / ${(profile.patrolDistanceMeters).toInt()} m")
                        DetailLine("Shots Fired / Connected", "${profile.shotsFired} / ${profile.shotsHit}")
                        DetailLine("Service Weapon Accuracy", "$accuracy%")
                        DetailLine("Department Funds", "Rs ${profile.cash}")
                        DetailLine("Current Primary Weapon", profile.currentWeaponId.replace("_", " ").uppercase())
                        DetailLine("Current Patrol Vehicle", profile.currentVehicleId.replace("_", " ").uppercase())

                        Spacer(modifier = Modifier.height(8.dp))

                        // Honor Badges
                        Text(text = "SERVICE COMMENDATIONS & MEDALS:", color = Color(0xFF90CAF9), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            BadgeChip("Quaid-e-Azam Medal", profile.totalArrests >= 5)
                            BadgeChip("Marksman Ribbon", accuracy >= 70 && profile.shotsFired >= 20)
                            BadgeChip("Highway Patrol Star", profile.patrolDistanceMeters >= 3000f)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BadgeChip(label: String, isEarned: Boolean) {
    Surface(
        color = if (isEarned) Color(0xFF1E3A8A) else Color(0xFF1E293B),
        border = BorderStroke(1.dp, if (isEarned) Color(0xFFFFD700) else Color(0xFF475569)),
        shape = RoundedCornerShape(6.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.MilitaryTech,
                contentDescription = null,
                tint = if (isEarned) Color(0xFFFFD700) else Color.Gray,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                color = if (isEarned) Color.White else Color.Gray,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
