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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.window.Dialog
import com.example.data.CriminalProfile
import com.example.data.CriminalStatus

@Composable
fun LockupScreen(
    criminals: List<CriminalProfile>,
    onBack: () -> Unit
) {
    var inspectedCriminal by remember { mutableStateOf<CriminalProfile?>(null) }

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
                        modifier = Modifier.testTag("lockup_back_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "CENTRAL POLICE HOLDING LOCKUP",
                            color = Color(0xFFFFD700),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "Maximum Security Cell Block • 15 Individual Cells",
                            color = Color(0xFF90CAF9),
                            fontSize = 11.sp
                        )
                    }
                }

                val detainedCount = criminals.count { it.status == CriminalStatus.ARRESTED }
                Surface(
                    color = Color(0xFF1E293B),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color(0xFF8E24AA))
                ) {
                    Text(
                        text = "CELL OCCUPANCY: $detainedCount / 15",
                        color = Color(0xFFCE93D8),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Grid of 15 Holding Cells
            LazyVerticalGrid(
                columns = GridCells.Fixed(5),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(criminals) { crim ->
                    val isDetained = crim.status == CriminalStatus.ARRESTED

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .clickable {
                                if (isDetained) inspectedCriminal = crim
                            }
                            .testTag("cell_${crim.id}"),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isDetained) Color(0xFF1B243B) else Color(0xFF0F1522)
                        ),
                        border = BorderStroke(
                            1.5.dp,
                            if (isDetained) Color(0xFF8E24AA) else Color(0xFF334155)
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "CELL #${crim.id.replace("crim_", "")}",
                                color = Color(0xFF94A3B8),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )

                            if (isDetained) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .background((crim.mugshotColorHex and 0xFFFFFFFFL).toInt().let { Color(it) }, CircleShape)
                                        .border(1.5.dp, Color(0xFFFFD700), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                                }
                                Text(
                                    text = crim.name,
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                            } else {
                                Icon(Icons.Default.Lock, contentDescription = "Vacant", tint = Color(0xFF475569), modifier = Modifier.size(26.dp))
                                Text(
                                    text = "VACANT",
                                    color = Color(0xFF64748B),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Cell Inspection Dialog
        inspectedCriminal?.let { detained ->
            Dialog(onDismissRequest = { inspectedCriminal = null }) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFF0F172A),
                    border = BorderStroke(2.dp, Color(0xFF8E24AA)),
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "DETAINED SUSPECT IN CUSTODY",
                            color = Color(0xFFFFD700),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background((detained.mugshotColorHex and 0xFFFFFFFFL).toInt().let { Color(it) }, CircleShape)
                                .border(2.dp, Color(0xFFFFD700), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(36.dp))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = detained.name, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text(text = "ALIAS: \"${detained.alias}\"", color = Color(0xFF90CAF9), fontSize = 12.sp)

                        Spacer(modifier = Modifier.height(10.dp))
                        DetailLine("Crime Charged", detained.crimeType)
                        DetailLine("Operating Area", detained.zoneLocation)
                        DetailLine("Apprehension Note", detained.arrestHistoryNote)

                        Spacer(modifier = Modifier.height(16.dp))
                        androidx.compose.material3.Button(
                            onClick = { inspectedCriminal = null },
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5)),
                            modifier = Modifier.fillMaxWidth().testTag("close_cell_dialog_button")
                        ) {
                            Text("CLOSE CELL DOSSIER")
                        }
                    }
                }
            }
        }
    }
}
