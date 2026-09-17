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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PlayerProfile
import com.example.data.VehicleConfig

@Composable
fun GarageScreen(
    profile: PlayerProfile,
    vehicles: Map<String, VehicleConfig>,
    onSelectVehicle: (String) -> Unit,
    onUpgradeVehicle: (String, upgradeType: String) -> Unit,
    onBack: () -> Unit
) {
    var selectedVehicleId by remember { mutableStateOf(profile.currentVehicleId) }
    val currentVeh = vehicles[selectedVehicleId] ?: vehicles.values.first()

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
                    IconButton(onClick = onBack, modifier = Modifier.testTag("garage_back_button")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "POLICE MOTOR POOL GARAGE",
                            color = Color(0xFFFFD700),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "Fleet Management & Tactical Tuning",
                            color = Color(0xFF90CAF9),
                            fontSize = 11.sp
                        )
                    }
                }

                Surface(
                    color = Color(0xFF1E293B),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color(0xFF4CAF50))
                ) {
                    Text(
                        text = "BUDGET: Rs ${profile.cash}",
                        color = Color(0xFF81C784),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Split: Left = Fleet list, Right = Selected Vehicle stats & tuning
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Fleet List
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    vehicles.values.forEach { veh ->
                        val isSelected = veh.id == selectedVehicleId
                        val isEquipped = veh.id == profile.currentVehicleId

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedVehicleId = veh.id }
                                .testTag("vehicle_card_${veh.id}"),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) Color(0xFF1E3A8A) else Color(0xFF131E3A)
                            ),
                            border = BorderStroke(
                                1.5.dp,
                                when {
                                    isEquipped -> Color(0xFF4CAF50)
                                    isSelected -> Color(0xFFFFD700)
                                    else -> Color(0xFF2E3D60)
                                }
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.DirectionsCar,
                                        contentDescription = null,
                                        tint = if (veh.isUnlocked) Color(0xFF64B5F6) else Color.Gray,
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(text = veh.name, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        Text(text = veh.description, color = Color(0xFF94A3B8), fontSize = 10.sp)
                                    }
                                }

                                if (isEquipped) {
                                    Surface(color = Color(0xFF2E7D32), shape = RoundedCornerShape(4.dp)) {
                                        Text(text = "DEPLOYED", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                } else if (!veh.isUnlocked) {
                                    Text(text = "Rs ${veh.price}", color = Color(0xFFFFD700), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // Selected Vehicle Stats & Upgrade Panel
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
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = currentVeh.name.uppercase(),
                                    color = Color.White,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = "STATUS: ${if (currentVeh.isUnlocked) "UNLOCKED" else "LOCKED"}",
                                    color = if (currentVeh.isUnlocked) Color(0xFF4CAF50) else Color(0xFFEF5350),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Stats Bars
                            VehicleStatBar("Top Speed", currentVeh.maxSpeed / 300f, "${currentVeh.maxSpeed.toInt()} km/h")
                            VehicleStatBar("Acceleration", currentVeh.acceleration / 180f, "${currentVeh.acceleration.toInt()} m/s²")
                            VehicleStatBar("Handling", currentVeh.handling / 1.5f, "${(currentVeh.handling * 10).toInt()}/10")
                            VehicleStatBar("Armor Durability", currentVeh.maxDurability / 400f, "${currentVeh.maxDurability.toInt()} HP")

                            Spacer(modifier = Modifier.height(14.dp))

                            // Tuning Upgrades
                            Text(text = "PERFORMANCE TUNING:", color = Color(0xFF90CAF9), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                UpgradeButton(
                                    title = "ENGINE",
                                    level = currentVeh.upgrades.engineLevel,
                                    modifier = Modifier.weight(1f),
                                    onClick = { onUpgradeVehicle(currentVeh.id, "engine") }
                                )
                                UpgradeButton(
                                    title = "HANDLING",
                                    level = currentVeh.upgrades.handlingLevel,
                                    modifier = Modifier.weight(1f),
                                    onClick = { onUpgradeVehicle(currentVeh.id, "handling") }
                                )
                                UpgradeButton(
                                    title = "ARMOR",
                                    level = currentVeh.upgrades.durabilityLevel,
                                    modifier = Modifier.weight(1f),
                                    onClick = { onUpgradeVehicle(currentVeh.id, "durability") }
                                )
                            }
                        }

                        // Equip Button
                        Button(
                            onClick = { onSelectVehicle(currentVeh.id) },
                            enabled = currentVeh.isUnlocked,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("deploy_vehicle_button")
                        ) {
                            Text(
                                text = if (currentVeh.id == profile.currentVehicleId) "VEHICLE ASSIGNED AS ACTIVE" else "ASSIGN VEHICLE TO PATROL",
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
}

@Composable
private fun VehicleStatBar(label: String, progress: Float, valueStr: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, color = Color(0xFF94A3B8), fontSize = 11.sp)
            Text(text = valueStr, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(3.dp))
        LinearProgressIndicator(
            progress = { progress.coerceIn(0.1f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = Color(0xFF00E5FF),
            trackColor = Color(0xFF1E293B)
        )
    }
}

@Composable
private fun UpgradeButton(title: String, level: Int, modifier: Modifier, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.testTag("upgrade_${title.lowercase()}_button"),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color(0xFF64B5F6))
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = title, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Text(text = "LVL $level/5 (Rs 600)", color = Color(0xFFFFD700), fontSize = 9.sp)
        }
    }
}
