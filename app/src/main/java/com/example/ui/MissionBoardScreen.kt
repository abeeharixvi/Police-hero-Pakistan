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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MissionData
import com.example.data.PlayerProfile

@Composable
fun MissionBoardScreen(
    profile: PlayerProfile,
    missions: List<MissionData>,
    onLaunchMission: (MissionData) -> Unit,
    onBack: () -> Unit
) {
    var selectedMission by remember { mutableStateOf(missions.firstOrNull { !it.isCompleted } ?: missions.first()) }

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
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("mission_board_back_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "CITY POLICE DISPATCH BOARD",
                            color = Color(0xFFFFD700),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "30 Classified Case Files - Select and deploy",
                            color = Color(0xFF90CAF9),
                            fontSize = 11.sp
                        )
                    }
                }

                Surface(
                    color = Color(0xFF1E293B),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color(0xFF1E88E5))
                ) {
                    Text(
                        text = "SOLVED: ${profile.completedMissions.size} / 30",
                        color = Color(0xFF90CAF9),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Main Split: Left = Missions List, Right = Selected Mission Dossier & Launch Button
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Left Column: Scrollable List of 30 Missions
                LazyColumn(
                    modifier = Modifier
                        .weight(1.1f)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(missions) { m ->
                        val isLocked = profile.level < m.unlockLevel && !profile.completedMissions.contains(m.id - 1) && m.id > 1
                        val isSelected = m.id == selectedMission.id

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (!isLocked) {
                                        selectedMission = m
                                    }
                                }
                                .testTag("mission_item_${m.id}"),
                            colors = CardDefaults.cardColors(
                                containerColor = when {
                                    isSelected -> Color(0xFF1E3A8A)
                                    isLocked -> Color(0xFF161E2E)
                                    m.isCompleted -> Color(0xFF14271B)
                                    else -> Color(0xFF131E3A)
                                }
                            ),
                            border = BorderStroke(
                                1.5.dp,
                                when {
                                    isSelected -> Color(0xFFFFD700)
                                    m.isCompleted -> Color(0xFF4CAF50)
                                    else -> Color(0xFF2E3D60)
                                }
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    // Number badge
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .background(
                                                if (m.isCompleted) Color(0xFF2E7D32) else if (isLocked) Color.DarkGray else Color(0xFF1565C0),
                                                RoundedCornerShape(6.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "${m.id}",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = m.title,
                                            color = if (isLocked) Color(0xFF718096) else Color.White,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "${m.crimeType} • ${m.locationZone}",
                                            color = Color(0xFF94A3B8),
                                            fontSize = 10.sp
                                        )
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (m.isVehicleChase) {
                                        Icon(Icons.Default.DirectionsCar, contentDescription = "Vehicle Chase", tint = Color(0xFF00E5FF), modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                    }

                                    if (m.isCompleted) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = "Completed", tint = Color(0xFF4CAF50), modifier = Modifier.size(18.dp))
                                    } else if (isLocked) {
                                        Icon(Icons.Default.Lock, contentDescription = "Locked", tint = Color(0xFF718096), modifier = Modifier.size(16.dp))
                                    } else {
                                        Text(text = "Rs ${m.rewardCash}", color = Color(0xFFFFD700), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                // Right Column: Active Mission Briefing & Launch Panel
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
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "CASE FILE #${selectedMission.id}",
                                    color = Color(0xFFFFD700),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Row {
                                    for (i in 1..selectedMission.difficulty) {
                                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(16.dp))
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = selectedMission.title,
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Surface(
                                color = Color(0xFF0B132B),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = "INCIDENT BRIEFING:",
                                        color = Color(0xFF90CAF9),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = selectedMission.briefing,
                                        color = Color(0xFFE2E8F0),
                                        fontSize = 12.sp,
                                        lineHeight = 16.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Details
                            DetailLine("Target Zone", selectedMission.locationZone)
                            DetailLine("Suspect Target", selectedMission.suspectId)
                            DetailLine("Pursuit Type", if (selectedMission.isVehicleChase) "Vehicular High-Speed Chase" else "Tactical Foot Investigation")
                            DetailLine("Optional Objective", selectedMission.optionalObjectiveDesc)
                            DetailLine("Cash Reward", "Rs ${selectedMission.rewardCash}")
                            DetailLine("XP Reward", "+${selectedMission.rewardXp} XP")
                        }

                        // Launch button
                        Button(
                            onClick = { onLaunchMission(selectedMission) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("launch_mission_button")
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = "Launch", tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "DEPLOY TO CRIME SCENE",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
