package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.viewmodel.GuardBookViewModel
import com.example.data.MaintenanceEntry

@Composable
fun ServiceScreen(
    viewModel: GuardBookViewModel
) {
    val isEnglish = viewModel.selectedLanguage.collectAsState().value == "EN"

    var subScreen by remember { mutableStateOf("list") } // list, add_service

    // Form state
    var sName by remember { mutableStateOf("") }
    var sCompany by remember { mutableStateOf("") }
    var sMobile by remember { mutableStateOf("") }
    var sPurpose by remember { mutableStateOf("Electrician") }
    var sFlat by remember { mutableStateOf("") }
    var sNotes by remember { mutableStateOf("") }

    val serviceTypes = listOf("Electrician", "Plumber", "AC Service", "Internet Tech", "Carpenter", "Other")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepNavy)
    ) {
        if (subScreen == "list") {
            val entries by viewModel.allMaintenanceEntries.collectAsState()
            val insideList = entries.filter { it.status == "INSIDE" }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isEnglish) "Service & Maintenance" else "सर्विस और मरम्मत प्रविष्टियाँ",
                        color = LightText,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(
                        onClick = {
                            sName = ""
                            sCompany = ""
                            sMobile = ""
                            sPurpose = "Electrician"
                            sFlat = ""
                            sNotes = ""
                            subScreen = "add_service"
                        },
                        modifier = Modifier.background(GlowingCyan.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Worker", tint = GlowingCyan)
                    }
                }

                Text(
                    text = if (isEnglish) "WORKERS CURRENTLY INSIDE (${insideList.size})" else "मरम्मत कर्मी जो अभी अंदर हैं (${insideList.size})",
                    color = GlowingCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                if (insideList.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isEnglish) "No active service workers inside." else "अंदर कोई एक्टिव सर्विस कर्मी नहीं है।",
                            color = DimText,
                            fontSize = 14.sp
                        )
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(insideList) { worker ->
                            WorkerItemCard(worker = worker, isEnglish = isEnglish, viewModel = viewModel)
                        }
                    }
                }
            }
        } else {
            // Add Worker form
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 20.dp)
                ) {
                    IconButton(onClick = { subScreen = "list" }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = GlowingCyan)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isEnglish) "New Worker Entry" else "नई सर्विस कर्मी एंट्री",
                        color = LightText,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                GlassCard(
                    borderColor = GlassBorder.copy(alpha = 0.3f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    GlassTextField(
                        value = sName,
                        onValueChange = { sName = it },
                        label = if (isEnglish) "Worker Full Name" else "कर्मी का पूरा नाम",
                        leadingIcon = { Icon(Icons.Default.Person, null, tint = GlowingCyan) },
                        testTag = "worker_name_input"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    GlassTextField(
                        value = sCompany,
                        onValueChange = { sCompany = it },
                        label = if (isEnglish) "Company / Brand (e.g. Urban Company)" else "कंपनी / ब्रांड (जैसे अर्बन कंपनी)",
                        leadingIcon = { Icon(Icons.Default.Business, null, tint = GlowingCyan) }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    GlassTextField(
                        value = sMobile,
                        onValueChange = { sMobile = it },
                        label = if (isEnglish) "Mobile Number" else "मोबाइल नंबर",
                        leadingIcon = { Icon(Icons.Default.Phone, null, tint = GlowingCyan) },
                        testTag = "worker_mobile_input"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    GlassTextField(
                        value = sFlat,
                        onValueChange = { sFlat = it },
                        label = if (isEnglish) "Flat / Area Assigned" else "संबंधित फ्लैट / क्षेत्र",
                        leadingIcon = { Icon(Icons.Default.Home, null, tint = GlowingCyan) }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = if (isEnglish) "Worker Profession" else "पेशा / श्रेणी",
                        color = DimText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("Electrician", "Plumber", "AC Repair", "Other").forEach { type ->
                            val selected = sPurpose == type
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        if (selected) GlowingCyan.copy(alpha = 0.15f) else Color(0x0F000000),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .border(
                                        1.dp,
                                        if (selected) GlowingCyan else Color(0x22FFFFFF),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { sPurpose = type }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = type,
                                    color = if (selected) GlowingCyan else LightText,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    GlassTextField(
                        value = sNotes,
                        onValueChange = { sNotes = it },
                        label = if (isEnglish) "Additional Notes" else "अतिरिक्त विवरण / नोट",
                        leadingIcon = { Icon(Icons.Default.Edit, null, tint = GlowingCyan) }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                GlowButton(
                    text = if (isEnglish) "RECORD INWARD" else "इनवर्ड एंट्री सहेजें",
                    onClick = {
                        if (sName.trim().isEmpty() || sMobile.trim().isEmpty() || sFlat.trim().isEmpty()) {
                            // handle
                        } else {
                            viewModel.recordServiceEntry(sName, sCompany, sMobile, sPurpose, sFlat, sNotes)
                            subScreen = "list"
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    glowColor = GlowGreen,
                    testTag = "save_worker_button"
                )
            }
        }
    }
}

@Composable
fun WorkerItemCard(
    worker: MaintenanceEntry,
    isEnglish: Boolean,
    viewModel: GuardBookViewModel
) {
    val durationMin = (System.currentTimeMillis() - worker.entryTime) / (1000 * 60)

    GlassCard(
        borderColor = GlowingCyan.copy(alpha = 0.4f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = worker.personName,
                        color = LightText,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    GlowBadge(text = worker.purpose, color = GlowAmber)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Company: ${worker.company} • Mob: ${worker.mobile}",
                    color = LightText.copy(alpha = 0.8f),
                    fontSize = 13.sp
                )
                Text(
                    text = "Destination Flat: ${worker.flatNumber}",
                    color = LightText.copy(alpha = 0.8f),
                    fontSize = 13.sp
                )
                Text(
                    text = "Inside for: ${durationMin} minutes",
                    color = DimText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            GlowButton(
                text = if (isEnglish) "OUT" else "बाहर",
                onClick = {
                    viewModel.recordServiceExit(worker)
                },
                glowColor = GlowRed,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.width(76.dp),
                testTag = "worker_exit_button"
            )
        }
    }
}
