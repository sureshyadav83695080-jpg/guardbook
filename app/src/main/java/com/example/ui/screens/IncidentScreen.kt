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
import com.example.data.Incident
import com.example.data.LostFound

@Composable
fun IncidentScreen(
    viewModel: GuardBookViewModel
) {
    val isEnglish = viewModel.selectedLanguage.collectAsState().value == "EN"

    var currentTab by remember { mutableStateOf("incidents") } // incidents, lost_found
    var subScreen by remember { mutableStateOf("list") } // list, add_incident, add_lost_found

    // Incident Form State
    var iType by remember { mutableStateOf("Property Damage") }
    var iSeverity by remember { mutableStateOf("Medium") }
    var iPeople by remember { mutableStateOf("") }
    var iVehicle by remember { mutableStateOf("") }
    var iDesc by remember { mutableStateOf("") }

    // Lost Found Form State
    var lfName by remember { mutableStateOf("") }
    var lfLoc by remember { mutableStateOf("") }
    var lfBy by remember { mutableStateOf("") }
    var lfDesc by remember { mutableStateOf("") }

    // Claim Dialog State
    var claimDialog by remember { mutableStateOf(false) }
    var selectedItemForClaim by remember { mutableStateOf<LostFound?>(null) }
    var claimantName by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepNavy)
    ) {
        if (subScreen == "list") {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header Tabs Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = if (isEnglish) "Incidents" else "सुरक्षा घटनाएँ",
                            color = if (currentTab == "incidents") GlowingCyan else DimText,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clickable { currentTab = "incidents" }
                                .padding(vertical = 4.dp, horizontal = 8.dp)
                        )
                        Text(
                            text = if (isEnglish) "Lost & Found" else "खोया-पाया सामान",
                            color = if (currentTab == "lost_found") GlowingCyan else DimText,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clickable { currentTab = "lost_found" }
                                .padding(vertical = 4.dp, horizontal = 8.dp)
                        )
                    }

                    IconButton(
                        onClick = {
                            if (currentTab == "incidents") {
                                iType = "Property Damage"
                                iSeverity = "Medium"
                                iPeople = ""
                                iVehicle = ""
                                iDesc = ""
                                subScreen = "add_incident"
                            } else {
                                lfName = ""
                                lfLoc = ""
                                lfBy = ""
                                lfDesc = ""
                                subScreen = "add_lost_found"
                            }
                        },
                        modifier = Modifier.background(GlowingCyan.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Item", tint = GlowingCyan)
                    }
                }

                // Main Tab layouts
                if (currentTab == "incidents") {
                    val incidents by viewModel.allIncidents.collectAsState()

                    if (incidents.isEmpty()) {
                        Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text(if (isEnglish) "No incidents recorded." else "कोई सुरक्षा घटना दर्ज नहीं है।", color = DimText, fontSize = 14.sp)
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            items(incidents) { incident ->
                                IncidentItemCard(incident = incident, isEnglish = isEnglish, viewModel = viewModel)
                            }
                        }
                    }
                } else {
                    val items by viewModel.allLostFound.collectAsState()

                    if (items.isEmpty()) {
                        Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text(if (isEnglish) "No items found recorded." else "कोई खोया-पाया सामान दर्ज नहीं है।", color = DimText, fontSize = 14.sp)
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            items(items) { lfItem ->
                                LostFoundItemCard(
                                    item = lfItem,
                                    isEnglish = isEnglish,
                                    onClaimClick = {
                                        selectedItemForClaim = lfItem
                                        claimantName = ""
                                        claimDialog = true
                                    }
                                )
                            }
                        }
                    }
                }
            }
        } else if (subScreen == "add_incident") {
            // Add Incident form
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
                        text = if (isEnglish) "Record Security Incident" else "सुरक्षा घटना रिपोर्ट करें",
                        color = LightText,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                GlassCard(
                    borderColor = GlowAmber.copy(alpha = 0.3f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isEnglish) "Incident Type" else "घटना प्रकार", color = DimText, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 4.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("Property Damage", "Gate Block", "Theft Report", "Other").forEach { type ->
                            val selected = iType == type
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(if (selected) GlowAmber.copy(alpha = 0.15f) else Color(0x0F000000), RoundedCornerShape(8.dp))
                                    .border(1.dp, if (selected) GlowAmber else Color(0x22FFFFFF), RoundedCornerShape(8.dp))
                                    .clickable { iType = type }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(type, color = if (selected) GlowAmber else LightText, fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(if (isEnglish) "Severity Level" else "तीव्रता स्तर", color = DimText, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 4.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Low", "Medium", "High").forEach { sev ->
                            val selected = iSeverity == sev
                            val sColor = when (sev) {
                                "Low" -> GlowGreen
                                "Medium" -> GlowAmber
                                else -> GlowRed
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(if (selected) sColor.copy(alpha = 0.15f) else Color(0x0F000000), RoundedCornerShape(8.dp))
                                    .border(1.dp, if (selected) sColor else Color(0x22FFFFFF), RoundedCornerShape(8.dp))
                                    .clickable { iSeverity = sev }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(sev, color = if (selected) sColor else LightText, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    GlassTextField(
                        value = iPeople,
                        onValueChange = { iPeople = it },
                        label = if (isEnglish) "People Involved" else "शामिल व्यक्ति",
                        leadingIcon = { Icon(Icons.Default.People, null, tint = GlowingCyan) }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    GlassTextField(
                        value = iVehicle,
                        onValueChange = { iVehicle = it },
                        label = if (isEnglish) "Vehicle Involved (Optional)" else "शामिल वाहन (वैकल्पिक)",
                        leadingIcon = { Icon(Icons.Default.DirectionsCar, null, tint = GlowingCyan) }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    GlassTextField(
                        value = iDesc,
                        onValueChange = { iDesc = it },
                        label = if (isEnglish) "Incident Description" else "घटना का विस्तृत विवरण",
                        leadingIcon = { Icon(Icons.Default.Edit, null, tint = GlowingCyan) },
                        testTag = "incident_desc_input"
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                GlowButton(
                    text = if (isEnglish) "SUBMIT REPORT" else "रिपोर्ट जमा करें",
                    onClick = {
                        if (iDesc.trim().isEmpty()) {
                            // handle
                        } else {
                            viewModel.reportIncident(iType, iDesc, iSeverity, iPeople, iVehicle)
                            subScreen = "list"
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    glowColor = GlowRed,
                    testTag = "submit_incident_button"
                )
            }
        } else if (subScreen == "add_lost_found") {
            // Add Lost Found
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
                        text = if (isEnglish) "Register Lost / Found Item" else "खोया-पाया सामान दर्ज करें",
                        color = LightText,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                GlassCard(
                    borderColor = GlowingCyan.copy(alpha = 0.3f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    GlassTextField(
                        value = lfName,
                        onValueChange = { lfName = it },
                        label = if (isEnglish) "Item Name (e.g. Wallet, Keys)" else "सामान का नाम (जैसे बटुआ, चाबियां)",
                        leadingIcon = { Icon(Icons.Default.Category, null, tint = GlowingCyan) },
                        testTag = "item_name_input"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    GlassTextField(
                        value = lfLoc,
                        onValueChange = { lfLoc = it },
                        label = if (isEnglish) "Found Location (e.g. Lift A, Parking)" else "कहाँ मिला (जैसे लिफ्ट ए, पार्किंग)",
                        leadingIcon = { Icon(Icons.Default.LocationOn, null, tint = GlowingCyan) }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    GlassTextField(
                        value = lfBy,
                        onValueChange = { lfBy = it },
                        label = if (isEnglish) "Found By (Guard / Resident Name)" else "किसने ढूंढा (नाम)",
                        leadingIcon = { Icon(Icons.Default.Person, null, tint = GlowingCyan) }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    GlassTextField(
                        value = lfDesc,
                        onValueChange = { lfDesc = it },
                        label = if (isEnglish) "Item Details / Description" else "सामान का हुलिया / विवरण",
                        leadingIcon = { Icon(Icons.Default.Edit, null, tint = GlowingCyan) }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                GlowButton(
                    text = if (isEnglish) "REGISTER ITEM" else "सामान पंजीकृत करें",
                    onClick = {
                        if (lfName.trim().isEmpty() || lfLoc.trim().isEmpty() || lfBy.trim().isEmpty()) {
                            // validation
                        } else {
                            viewModel.reportLostFound(lfName, lfLoc, lfBy, lfDesc)
                            subScreen = "list"
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    glowColor = GlowingCyan,
                    testTag = "submit_item_button"
                )
            }
        }

        // Claim Dialog
        if (claimDialog && selectedItemForClaim != null) {
            AlertDialog(
                onDismissRequest = { claimDialog = false },
                containerColor = Color(0xFF141F3C),
                title = {
                    Text(
                        text = if (isEnglish) "Mark Item as Claimed" else "सामान को हैंडओवर घोषित करें",
                        color = GlowGreen,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text("Item: ${selectedItemForClaim?.itemName}", color = LightText, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Found Location: ${selectedItemForClaim?.foundLocation}", color = DimText, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(16.dp))

                        GlassTextField(
                            value = claimantName,
                            onValueChange = { claimantName = it },
                            label = if (isEnglish) "Claimant Full Name & Phone" else "दावेदार का पूरा नाम और फोन नंबर",
                            leadingIcon = { Icon(Icons.Default.Person, null, tint = GlowingCyan) },
                            testTag = "claimant_name_input"
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (claimantName.trim().isNotEmpty()) {
                                claimDialog = false
                                viewModel.claimLostFound(selectedItemForClaim!!, claimantName)
                            }
                        }
                    ) {
                        Text(if (isEnglish) "CONFIRM HANDOVER" else "हैंडओवर सफल करें", color = GlowGreen, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { claimDialog = false }) {
                        Text(if (isEnglish) "CANCEL" else "रद्द करें", color = LightText)
                    }
                }
            )
        }
    }
}

@Composable
fun IncidentItemCard(
    incident: Incident,
    isEnglish: Boolean,
    viewModel: GuardBookViewModel
) {
    val severityColor = when (incident.severity) {
        "High" -> GlowRed
        "Medium" -> GlowAmber
        else -> GlowGreen
    }

    GlassCard(
        borderColor = severityColor.copy(alpha = 0.4f),
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
                        text = incident.type,
                        color = LightText,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    GlowBadge(text = incident.severity, color = severityColor)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = incident.description,
                    color = LightText.copy(alpha = 0.9f),
                    fontSize = 13.sp
                )
                Text(
                    text = "Reporter: ${incident.reporter} • Date: ${incident.date} ${incident.time}",
                    color = DimText,
                    fontSize = 11.sp
                )
                if (incident.peopleInvolved.isNotEmpty() || incident.vehicleInvolved.isNotEmpty()) {
                    Text(
                        text = "People: ${incident.peopleInvolved} • Vehicle: ${incident.vehicleInvolved}",
                        color = DimText,
                        fontSize = 11.sp
                    )
                }
            }

            // Status Badge / Action
            GlowBadge(
                text = incident.status,
                color = when (incident.status) {
                    "RESOLVED" -> GlowGreen
                    "UNDER REVIEW" -> GlowAmber
                    else -> GlowRed
                }
            )
        }
    }
}

@Composable
fun LostFoundItemCard(
    item: LostFound,
    isEnglish: Boolean,
    onClaimClick: () -> Unit
) {
    val isFound = item.status == "FOUND"

    GlassCard(
        borderColor = if (isFound) GlowingCyan.copy(alpha = 0.4f) else GlowGreen.copy(alpha = 0.2f),
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
                        text = item.itemName,
                        color = LightText,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    GlowBadge(text = item.status, color = if (isFound) GlowingCyan else GlowGreen)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.description,
                    color = LightText.copy(alpha = 0.8f),
                    fontSize = 13.sp
                )
                Text(
                    text = "Found At: ${item.foundLocation} • By: ${item.foundBy}",
                    color = DimText,
                    fontSize = 11.sp
                )
                Text(
                    text = "Date: ${item.date} ${item.time}",
                    color = DimText,
                    fontSize = 11.sp
                )

                if (item.claimantInfo.isNotEmpty()) {
                    Text(
                        text = "Claimant Info: ${item.claimantInfo}",
                        color = GlowGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (isFound) {
                GlowButton(
                    text = if (isEnglish) "CLAIM" else "दावा",
                    onClick = onClaimClick,
                    glowColor = GlowGreen,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.width(76.dp),
                    testTag = "claim_button"
                )
            }
        }
    }
}
