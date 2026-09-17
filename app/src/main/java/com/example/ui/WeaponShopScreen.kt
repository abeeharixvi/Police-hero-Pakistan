package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import com.example.data.Weapon

@Composable
fun WeaponShopScreen(
    profile: PlayerProfile,
    weapons: Map<String, Weapon>,
    onSelectWeapon: (String) -> Unit,
    onBuyWeapon: (String) -> Unit,
    onBuyAmmo: (String) -> Unit,
    onBack: () -> Unit
) {
    var selectedWeaponId by remember { mutableStateOf(profile.currentWeaponId) }
    val currentWep = weapons[selectedWeaponId] ?: weapons.values.first()

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
                    IconButton(onClick = onBack, modifier = Modifier.testTag("weapon_shop_back_button")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "CENTRAL POLICE ARMORY & WEAPON LOCKER",
                            color = Color(0xFFFFD700),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "Service Firearms, Tactical Munitions & Supply Requisitions",
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
                        text = "FUNDS: Rs ${profile.cash}",
                        color = Color(0xFF81C784),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Split: Weapons list & Selected weapon inspector
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Weapons List
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    weapons.values.forEach { wep ->
                        val isSelected = wep.id == selectedWeaponId
                        val isEquipped = wep.id == profile.currentWeaponId

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedWeaponId = wep.id }
                                .testTag("weapon_card_${wep.id}"),
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
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Security, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(26.dp))
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(text = wep.name, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        Text(text = "Ammo: ${wep.currentAmmoInMag}/${wep.reserveAmmo}", color = Color(0xFF94A3B8), fontSize = 10.sp)
                                    }
                                }

                                if (isEquipped) {
                                    Surface(color = Color(0xFF2E7D32), shape = RoundedCornerShape(4.dp)) {
                                        Text(text = "EQUIPPED", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                } else if (!wep.isUnlocked) {
                                    Text(text = "Rs ${wep.price}", color = Color(0xFFFFD700), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // Weapon Specs & Purchase/Equip Panel
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
                                    text = currentWep.name.uppercase(),
                                    color = Color.White,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = if (currentWep.isUnlocked) "ACQUIRED" else "LOCKER LOCKED",
                                    color = if (currentWep.isUnlocked) Color(0xFF4CAF50) else Color(0xFFEF5350),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            WeaponStatBar("Stopping Power (Damage)", currentWep.damage / 100f, "${currentWep.damage} DMG")
                            WeaponStatBar("Effective Range", currentWep.range / 700f, "${currentWep.range.toInt()} m")
                            WeaponStatBar("Fire Rate", (1000f / currentWep.fireRateMs) / 10f, "${(1000f / currentWep.fireRateMs).toInt()} rnd/s")
                            WeaponStatBar("Accuracy Spread", currentWep.accuracy, "${(currentWep.accuracy * 100).toInt()}%")
                            WeaponStatBar("Magazine Capacity", currentWep.magazineCapacity / 30f, "${currentWep.magazineCapacity} rounds")

                            Spacer(modifier = Modifier.height(14.dp))

                            // Munitions Requisition Button
                            Button(
                                onClick = { onBuyAmmo(currentWep.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF455A64)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("buy_ammo_button")
                            ) {
                                Icon(Icons.Default.AddShoppingCart, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "REQUISITION AMMO CRATE (+50 ROUNDS - Rs ${currentWep.ammoPrice})",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Equip or Unlock Button
                        if (currentWep.isUnlocked) {
                            Button(
                                onClick = { onSelectWeapon(currentWep.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .testTag("equip_weapon_button")
                            ) {
                                Text(
                                    text = if (currentWep.id == profile.currentWeaponId) "ASSIGNED TO SERVICE HOLSTER" else "EQUIP TO SERVICE HOLSTER",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        } else {
                            Button(
                                onClick = { onBuyWeapon(currentWep.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .testTag("unlock_weapon_button")
                            ) {
                                Text(
                                    text = "PURCHASE FROM ARMORY (Rs ${currentWep.price})",
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
}

@Composable
private fun WeaponStatBar(label: String, progress: Float, valueStr: String) {
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
            color = Color(0xFFFFB300),
            trackColor = Color(0xFF1E293B)
        )
    }
}
