package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.viewmodel.GuardBookViewModel
import com.example.data.Visitor

@Composable
fun VisitorScreen(
    viewModel: GuardBookViewModel
) {
    val isEnglish = viewModel.selectedLanguage.collectAsState().value == "EN"
    val scope = rememberCoroutineScope()

    // Sub-screens: list, create
    var subScreen by remember { mutableStateOf("list") }

    // Forms
    var vName by remember { mutableStateOf("") }
    var vMobile by remember { mutableStateOf("") }
    var vVehicleNo by remember { mutableStateOf("") }
    var vVehicleType by remember { mutableStateOf("None") }
    var vFlat by remember { mutableStateOf("") }
    var vPurpose by remember { mutableStateOf("Personal") }
    var vNotes by remember { mutableStateOf("") }

    // Duplicate Check State
    var duplicateFoundDialog by remember { mutableStateOf(false) }
    var existingVisitorObject by remember { mutableStateOf<Visitor?>(null) }
    var formError by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepNavy)
    ) {
        if (subScreen == "list") {
            VisitorListLayout(
                viewModel = viewModel,
                onAddClick = {
                    vName = ""
                    vMobile = ""
                    vVehicleNo = ""
                    vVehicleType = "None"
                    vFlat = ""
                    vPurpose = "Personal"
                    vNotes = ""
                    formError = ""
                    subScreen = "create"
                }
            )
        } else {
            // Create New Visitor form
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
                        text = if (isEnglish) "Visitor Entry Registration" else "विज़िटर एंट्री पंजीकरण",
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
                        value = vName,
                        onValueChange = { vName = it },
                        label = if (isEnglish) "Full Name" else "पूरा नाम",
                        leadingIcon = { Icon(Icons.Default.Person, null, tint = GlowingCyan) },
                        testTag = "visitor_name_input"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    GlassTextField(
                        value = vMobile,
                        onValueChange = { vMobile = it },
                        label = if (isEnglish) "Mobile Number" else "मोबाइल नंबर",
                        leadingIcon = { Icon(Icons.Default.Phone, null, tint = GlowingCyan) },
                        testTag = "visitor_mobile_input"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    GlassTextField(
                        value = vFlat,
                        onValueChange = { vFlat = it },
                        label = if (isEnglish) "Flat / Office No" else "फ्लैट / ऑफिस नंबर",
                        leadingIcon = { Icon(Icons.Default.Home, null, tint = GlowingCyan) },
                        testTag = "visitor_flat_input"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    GlassTextField(
                        value = vVehicleNo,
                        onValueChange = { vVehicleNo = it },
                        label = if (isEnglish) "Vehicle Number (Optional)" else "वाहन नंबर (वैकल्पिक)",
                        leadingIcon = { Icon(Icons.Default.DirectionsCar, null, tint = GlowingCyan) },
                        testTag = "visitor_vehicle_input"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Vehicle Type selector
                    Text(
                        text = if (isEnglish) "Vehicle Type" else "वाहन का प्रकार",
                        color = DimText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("None", "2-Wheeler", "4-Wheeler").forEach { type ->
                            val selected = vVehicleType == type
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
                                    .clickable { vVehicleType = type }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = type,
                                    color = if (selected) GlowingCyan else LightText,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Purpose selector
                    Text(
                        text = if (isEnglish) "Purpose of Visit" else "आने का उद्देश्य",
                        color = DimText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Personal", "Delivery", "Service", "Other").forEach { purpose ->
                            val selected = vPurpose == purpose
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
                                    .clickable { vPurpose = purpose }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = purpose,
                                    color = if (selected) GlowingCyan else LightText,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    GlassTextField(
                        value = vNotes,
                        onValueChange = { vNotes = it },
                        label = if (isEnglish) "Additional Notes" else "अतिरिक्त टिप्पणी",
                        leadingIcon = { Icon(Icons.Default.Edit, null, tint = GlowingCyan) }
                    )
                }

                if (formError.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = formError,
                        color = GlowRed,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                GlowButton(
                    text = if (isEnglish) "Check & Verify Entry" else "सत्यापित करें और सहेजें",
                    onClick = {
                        if (vName.trim().isEmpty() || vMobile.trim().isEmpty() || vFlat.trim().isEmpty()) {
                            formError = if (isEnglish) "Please fill in Name, Mobile, and Flat!" else "कृपया नाम, मोबाइल और फ्लैट दर्ज करें!"
                        } else {
                            formError = ""
                            // Run smart duplicate check
                            scope.launch {
                                val duplicateByMobile = viewModel.repository.getVisitorByMobile(vMobile)
                                val duplicateByVehicle = if (vVehicleNo.isNotEmpty()) {
                                    viewModel.repository.getVisitorByVehicleNumber(vVehicleNo)
                                } else null

                                val duplicate = duplicateByMobile ?: duplicateByVehicle

                                if (duplicate != null) {
                                    existingVisitorObject = duplicate
                                    duplicateFoundDialog = true
                                } else {
                                    // Save immediately
                                    viewModel.recordVisitorEntry(
                                        name = vName,
                                        mobile = vMobile,
                                        flat = vFlat,
                                        vehicleNumber = vVehicleNo,
                                        vehicleType = vVehicleType,
                                        purpose = vPurpose,
                                        notes = vNotes
                                    ) {
                                        subScreen = "list"
                                    }
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    glowColor = GlowGreen,
                    testTag = "save_visitor_button"
                )
            }
        }

        // Duplicate Warning Dialog
        if (duplicateFoundDialog && existingVisitorObject != null) {
            AlertDialog(
                onDismissRequest = { duplicateFoundDialog = false },
                containerColor = Color(0xFF141F3C),
                title = {
                    Text(
                        text = if (isEnglish) "⚠️ Existing Visitor Found" else "⚠️ विज़िटर प्रोफ़ाइल पहले से मौजूद है",
                        color = GlowAmber,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column {
                        Text(
                            text = if (isEnglish) "A profile with this details already exists in the database:" else "इस मोबाइल/वाहन नंबर का एक प्रोफ़ाइल पहले से डेटाबेस में है:",
                            color = LightText,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Name: ${existingVisitorObject?.name}", color = GlowingCyan, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Mobile: ${existingVisitorObject?.mobile}", color = LightText, fontSize = 13.sp)
                        Text("Flat: ${existingVisitorObject?.flatNumber}", color = LightText, fontSize = 13.sp)
                        Text("Vehicle: ${existingVisitorObject?.vehicleNumber}", color = LightText, fontSize = 13.sp)
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            duplicateFoundDialog = false
                            // Fill details and record new visit entry
                            viewModel.recordVisitorEntry(
                                name = existingVisitorObject!!.name,
                                mobile = existingVisitorObject!!.mobile,
                                flat = vFlat.ifEmpty { existingVisitorObject!!.flatNumber },
                                vehicleNumber = vVehicleNo.ifEmpty { existingVisitorObject!!.vehicleNumber },
                                vehicleType = vVehicleType.ifEmpty { existingVisitorObject!!.vehicleType },
                                purpose = vPurpose,
                                notes = vNotes
                            ) {
                                subScreen = "list"
                            }
                        }
                    ) {
                        Text(if (isEnglish) "USE EXISTING PROFILE" else "पुराने प्रोफ़ाइल का उपयोग करें", color = GlowGreen, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            duplicateFoundDialog = false
                            // Force save as completely new profile
                            viewModel.recordVisitorEntry(
                                name = vName,
                                mobile = vMobile,
                                flat = vFlat,
                                vehicleNumber = vVehicleNo,
                                vehicleType = vVehicleType,
                                purpose = vPurpose,
                                notes = vNotes
                            ) {
                                subScreen = "list"
                            }
                        }
                    ) {
                        Text(if (isEnglish) "CREATE NEW PROFILE" else "नया प्रोफ़ाइल बनाएं", color = LightText)
                    }
                }
            )
        }
    }
}

@Composable
fun VisitorListLayout(
    viewModel: GuardBookViewModel,
    onAddClick: () -> Unit
) {
    val isEnglish = viewModel.selectedLanguage.collectAsState().value == "EN"
    val searchQuery by viewModel.universalSearchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val currentlyInside by viewModel.visitorsCurrentlyInside.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Universal Search
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.weight(1f)) {
                GlassTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.universalSearchQuery.value = it },
                    label = if (isEnglish) "Search Name, Mobile, Flat..." else "नाम, मोबाइल, फ्लैट खोजें...",
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = GlowingCyan) },
                    testTag = "visitor_search_input"
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            IconButton(
                onClick = onAddClick,
                modifier = Modifier
                    .background(GlowingCyan, RoundedCornerShape(12.dp))
                    .size(52.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Visitor", tint = DeepNavy)
            }
        }

        // List Header or Query results
        if (searchQuery.isNotEmpty()) {
            Text(
                text = if (isEnglish) "SEARCH RESULTS (${searchResults.size})" else "खोज परिणाम (${searchResults.size})",
                color = GlowingCyan,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            if (searchResults.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isEnglish) "No visitor profile matches." else "कोई विज़िटर प्रोफ़ाइल नहीं मिली।",
                        color = DimText,
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(searchResults) { visitor ->
                        VisitorProfileCard(visitor = visitor, isEnglish = isEnglish, viewModel = viewModel)
                    }
                }
            }
        } else {
            // Display Currently Inside visitors
            Text(
                text = if (isEnglish) "CURRENTLY INSIDE PROPERTY (${currentlyInside.size})" else "अभी प्रॉपर्टी के अंदर (${currentlyInside.size})",
                color = GlowingCyan,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            if (currentlyInside.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.CheckCircle, null, tint = GlowGreen, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isEnglish) "No visitors inside. Gate secured!" else "अंदर कोई विज़िटर नहीं है। गेट सुरक्षित!",
                            color = DimText,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(currentlyInside) { visitor ->
                        VisitorProfileCard(visitor = visitor, isEnglish = isEnglish, viewModel = viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun VisitorProfileCard(
    visitor: Visitor,
    isEnglish: Boolean,
    viewModel: GuardBookViewModel
) {
    val isInside = visitor.currentStatus == "INSIDE"

    GlassCard(
        borderColor = if (isInside) GlowingCyan.copy(alpha = 0.4f) else GlassBorder.copy(alpha = 0.2f),
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
                        text = visitor.name,
                        color = LightText,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    GlowBadge(
                        text = visitor.purpose,
                        color = when (visitor.purpose) {
                            "Delivery" -> GlowingBlue
                            "Service" -> GlowAmber
                            else -> GlowingCyan
                        }
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Flat: ${visitor.flatNumber} • Mob: ${visitor.mobile}",
                    color = LightText.copy(alpha = 0.8f),
                    fontSize = 13.sp
                )
                if (visitor.vehicleNumber.isNotEmpty()) {
                    Text(
                        text = "Vehicle: ${visitor.vehicleNumber} (${visitor.vehicleType})",
                        color = DimText,
                        fontSize = 12.sp
                    )
                }
                Text(
                    text = "Total Visits: ${visitor.totalVisits} • Entry: ${visitor.entryTime}",
                    color = DimText,
                    fontSize = 11.sp
                )
            }

            if (isInside) {
                GlowButton(
                    text = if (isEnglish) "EXIT" else "बाहर",
                    onClick = {
                        viewModel.recordVisitorExit(visitor) {}
                    },
                    glowColor = GlowRed,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.width(80.dp),
                    testTag = "exit_button"
                )
            } else {
                Text(
                    text = if (isEnglish) "EXITED" else "बाहर गए",
                    color = DimText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
        }
    }
}
