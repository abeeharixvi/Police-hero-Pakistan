package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.data.GameSettings

@Composable
fun SettingsScreen(
    settings: GameSettings,
    onSettingsChanged: (GameSettings) -> Unit,
    onResetProgress: () -> Unit,
    onBack: () -> Unit
) {
    var showResetDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

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
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack, modifier = Modifier.testTag("settings_back_button")) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "SETTINGS & SYSTEM CONFIGURATION",
                        color = Color(0xFFFFD700),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "Audio, Haptics, Performance & Developer Credentials",
                        color = Color(0xFF90CAF9),
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Left Column: Audio & Graphics Controls
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
                            .padding(18.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(text = "AUDIO & HAPTICS", color = Color(0xFFFFD700), fontSize = 13.sp, fontWeight = FontWeight.Bold)

                        SettingToggleRow("Background Music", settings.isMusicOn) {
                            settings.isMusicOn = it
                            onSettingsChanged(settings)
                        }

                        SettingToggleRow("Sound Effects (SFX)", settings.isSoundOn) {
                            settings.isSoundOn = it
                            onSettingsChanged(settings)
                        }

                        SettingToggleRow("Tactical Vibration", settings.isVibrationOn) {
                            settings.isVibrationOn = it
                            onSettingsChanged(settings)
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "GRAPHICS QUALITY", color = Color(0xFFFFD700), fontSize = 13.sp, fontWeight = FontWeight.Bold)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("Low", "Medium", "High").forEach { q ->
                                val isSel = settings.graphicsQuality == q
                                Button(
                                    onClick = {
                                        settings.graphicsQuality = q
                                        onSettingsChanged(settings)
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isSel) Color(0xFF1E88E5) else Color(0xFF1E293B)
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(text = q, color = if (isSel) Color.White else Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "CONTROL SENSITIVITY (${String.format("%.1f", settings.controlSensitivity)}x)", color = Color(0xFFFFD700), fontSize = 13.sp, fontWeight = FontWeight.Bold)

                        Slider(
                            value = settings.controlSensitivity,
                            onValueChange = {
                                settings.controlSensitivity = it
                                onSettingsChanged(settings)
                            },
                            valueRange = 0.5f..1.5f,
                            colors = SliderDefaults.colors(
                                thumbColor = Color(0xFFFFD700),
                                activeTrackColor = Color(0xFF1E88E5)
                            )
                        )
                    }
                }

                // Right Column: System Options, Privacy, About Developer
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
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(text = "APP & LEGAL INFORMATION", color = Color(0xFFFFD700), fontSize = 13.sp, fontWeight = FontWeight.Bold)

                            Button(
                                onClick = { showAboutDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("about_button")
                            ) {
                                Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF90CAF9))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "ABOUT DEVELOPER & CREDITS", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { showPrivacyDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("privacy_policy_button")
                            ) {
                                Text(text = "PRIVACY POLICY", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        // Reset Progress Button (at bottom)
                        Button(
                            onClick = { showResetDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("reset_progress_button")
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "RESET ALL PROGRESS & CAREER", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Reset Confirmation Dialog
        if (showResetDialog) {
            AlertDialog(
                onDismissRequest = { showResetDialog = false },
                title = { Text(text = "Reset All Career Progress?", color = Color(0xFFFF5252), fontWeight = FontWeight.Bold) },
                text = {
                    Text(
                        text = "Are you sure you want to reset all game data? Your solved cases, unlocked vehicles, weapons, rank, and cash will be permanently deleted.",
                        color = Color.White
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showResetDialog = false
                            onResetProgress()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))
                    ) {
                        Text("YES, RESET EVERYTHING")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showResetDialog = false }) {
                        Text("CANCEL", color = Color(0xFF90CAF9))
                    }
                },
                containerColor = Color(0xFF0F172A)
            )
        }

        // About Dialog
        if (showAboutDialog) {
            AlertDialog(
                onDismissRequest = { showAboutDialog = false },
                title = { Text(text = "POLICE HERO PAKISTAN", color = Color(0xFFFFD700), fontWeight = FontWeight.Black) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(text = "Developed by Arbab Rizvi", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(text = "Phone: 030383631699", color = Color(0xFF90CAF9), fontSize = 13.sp)
                        Text(text = "Email: arbabrixvi@gmail.com", color = Color(0xFF90CAF9), fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "A complete native Android 2D tactical police action game built in Kotlin and Jetpack Compose featuring authentic Pakistani city atmosphere, vehicular pursuits, tactical investigations, criminal dossiers, and holding lockup.",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = { showAboutDialog = false }) {
                        Text("CLOSE")
                    }
                },
                containerColor = Color(0xFF0F172A)
            )
        }

        // Privacy Policy Dialog
        if (showPrivacyDialog) {
            AlertDialog(
                onDismissRequest = { showPrivacyDialog = false },
                title = { Text(text = "Privacy Policy", color = Color(0xFFFFD700), fontWeight = FontWeight.Bold) },
                text = {
                    Text(
                        text = "Police Hero Pakistan respects player privacy. The game operates offline-first and saves all career progression locally on your device storage without collecting personal data. Optional Google AdMob advertising displays non-personalized ads when network connectivity is available.",
                        color = Color(0xFFE2E8F0),
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                },
                confirmButton = {
                    Button(onClick = { showPrivacyDialog = false }) {
                        Text("OK")
                    }
                },
                containerColor = Color(0xFF0F172A)
            )
        }
    }
}

@Composable
private fun SettingToggleRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color(0xFFFFD700),
                checkedTrackColor = Color(0xFF1E88E5),
                uncheckedThumbColor = Color.Gray,
                uncheckedTrackColor = Color(0xFF334155)
            )
        )
    }
}
