package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.window.Dialog
import com.example.game.GameSession

@Composable
fun MissionDebriefDialog(
    session: GameSession,
    isSuccess: Boolean,
    onReturnToStation: (cashEarned: Int, xpEarned: Int, stars: Int) -> Unit,
    onRetryMission: () -> Unit,
    onWatchRewardedAd: (onSuccess: () -> Unit) -> Unit
) {
    var isDoubled by remember { mutableStateOf(false) }

    // Calculate rating
    val accuracy = if (session.shotsFiredSession > 0) {
        ((session.shotsHitSession.toFloat() / session.shotsFiredSession) * 100).toInt().coerceIn(0, 100)
    } else 100

    val timeSec = session.missionTimeSeconds.toInt()
    val stars = if (!isSuccess) 0 else {
        var s = 1
        if (accuracy >= 65) s++
        if (timeSec < 150) s++
        s.coerceIn(1, 3)
    }

    val baseCash = if (isSuccess) session.mission.rewardCash else 100
    val baseXp = if (isSuccess) session.mission.rewardXp else 50

    val finalCash = if (isDoubled) baseCash * 2 else baseCash
    val finalXp = if (isDoubled) baseXp * 2 else baseXp

    Dialog(onDismissRequest = { /* Modal */ }) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF0F172A),
            border = androidx.compose.foundation.BorderStroke(2.dp, if (isSuccess) Color(0xFF4CAF50) else Color(0xFFE53935)),
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Banner
                Text(
                    text = if (isSuccess) "★ MISSION ACCOMPLISHED ★" else "MISSION FAILED",
                    color = if (isSuccess) Color(0xFFFFD700) else Color(0xFFFF5252),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black
                )

                Text(
                    text = session.mission.title,
                    color = Color(0xFF90CAF9),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Stars display
                if (isSuccess) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        for (i in 1..3) {
                            Icon(
                                imageVector = if (i <= stars) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = "Star $i",
                                tint = if (i <= stars) Color(0xFFFFD700) else Color.Gray,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // Stats breakdown
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        StatRow("Time Elapsed", "${timeSec / 60}m ${timeSec % 60}s")
                        StatRow("Weapon Accuracy", "$accuracy%")
                        StatRow("Civilian Casualties", "0 (Protected)")
                        StatRow("Optional Goal", session.mission.optionalObjectiveDesc)
                        if (!isSuccess && session.failureReason.isNotEmpty()) {
                            StatRow("Cause of Failure", session.failureReason)
                        }
                        StatRow("Cash Bounty", "Rs $finalCash" + if (isDoubled) " (2X BOOST)" else "")
                        StatRow("XP Award", "$finalXp XP" + if (isDoubled) " (2X BOOST)" else "")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Rewarded Ad Button for 2x Reward or 2nd Chance
                if (!isDoubled) {
                    Button(
                        onClick = {
                            onWatchRewardedAd {
                                isDoubled = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE65100)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("rewarded_ad_button")
                    ) {
                        Icon(Icons.Default.Videocam, contentDescription = "Video Ad", tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isSuccess) "WATCH AD: DOUBLE REWARD (2X CASH & XP)" else "WATCH AD: SECOND CHANCE REVIVE",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (!isSuccess) {
                        OutlinedButton(
                            onClick = onRetryMission,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF64B5F6)),
                            modifier = Modifier.weight(1f).testTag("retry_mission_button")
                        ) {
                            Text("RETRY", fontWeight = FontWeight.Bold)
                        }
                    }

                    Button(
                        onClick = {
                            onReturnToStation(finalCash, finalXp, stars)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).testTag("return_to_station_button")
                    ) {
                        Text(
                            text = "RETURN TO POLICE STATION",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = Color(0xFF94A3B8), fontSize = 12.sp)
        Text(text = value, color = Color(0xFFF1F5F9), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}
