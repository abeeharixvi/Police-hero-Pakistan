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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
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
import com.example.data.CriminalProfile
import com.example.data.CriminalStatus

@Composable
fun CriminalRecordsScreen(
    criminals: List<CriminalProfile>,
    onBack: () -> Unit
) {
    var selectedCriminal by remember { mutableStateOf(criminals.first()) }

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
                        modifier = Modifier.testTag("criminal_records_back_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "CENTRAL CRIMINAL INVESTIGATION DATABASE",
                            color = Color(0xFFFFD700),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "15 High-Profile Suspects & Syndicate Syndicate Heads",
                            color = Color(0xFF90CAF9),
                            fontSize = 11.sp
                        )
                    }
                }

                val arrestedCount = criminals.count { it.status == CriminalStatus.ARRESTED }
                Surface(
                    color = Color(0xFF1E293B),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color(0xFFE53935))
                ) {
                    Text(
                        text = "APPREHENDED: $arrestedCount / 15",
                        color = Color(0xFFFF8A80),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Split: Left = Criminal List, Right = Detailed Dossier
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // List of 15 Criminals
                LazyColumn(
                    modifier = Modifier
                        .weight(1.1f)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(criminals) { crim ->
                        val isSelected = crim.id == selectedCriminal.id
                        val isArrested = crim.status == CriminalStatus.ARRESTED

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedCriminal = crim }
                                .testTag("criminal_card_${crim.id}"),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) Color(0xFF1E3A8A) else Color(0xFF131E3A)
                            ),
                            border = BorderStroke(
                                1.5.dp,
                                when {
                                    isSelected -> Color(0xFFFFD700)
                                    isArrested -> Color(0xFF4CAF50)
                                    else -> Color(0xFFE53935)
                                }
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    // Mugshot circle
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .background(
                                                (crim.mugshotColorHex and 0xFFFFFFFFL).toInt().let { Color(it) },
                                                CircleShape
                                            )
                                            .border(1.dp, Color.White, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = crim.name,
                                            color = Color.White,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "\"${crim.alias}\" • ${crim.crimeType}",
                                            color = Color(0xFF94A3B8),
                                            fontSize = 10.sp
                                        )
                                    }
                                }

                                Surface(
                                    color = if (isArrested) Color(0xFF1B5E20) else Color(0xFFB71C1C),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = crim.status.name,
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Detailed Dossier
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
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .background((selectedCriminal.mugshotColorHex and 0xFFFFFFFFL).toInt().let { Color(it) }, CircleShape)
                                    .border(2.dp, Color(0xFFFFD700), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(40.dp))
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = selectedCriminal.name.uppercase(),
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = "ALIAS: \"${selectedCriminal.alias}\"",
                                    color = Color(0xFFFFD700),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Row(modifier = Modifier.padding(top = 4.dp)) {
                                    for (i in 1..selectedCriminal.wantedLevel) {
                                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFF5252), modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        DetailLine("Status", selectedCriminal.status.name)
                        DetailLine("Primary Crime", selectedCriminal.crimeType)
                        DetailLine("Operating Zone", selectedCriminal.zoneLocation)
                        DetailLine("Prior Arrests", "${selectedCriminal.previousArrests} record(s)")

                        Spacer(modifier = Modifier.height(10.dp))

                        Surface(
                            color = Color(0xFF0B132B),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(text = "EVIDENCE RECOVERED:", color = Color(0xFFFFB300), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Text(text = selectedCriminal.evidenceFound, color = Color(0xFFE2E8F0), fontSize = 11.sp)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(text = "CASE FILE NOTE:", color = Color(0xFF90CAF9), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Text(text = selectedCriminal.arrestHistoryNote, color = Color(0xFFE2E8F0), fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
