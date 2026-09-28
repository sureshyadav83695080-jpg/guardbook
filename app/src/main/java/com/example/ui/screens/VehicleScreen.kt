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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.viewmodel.GuardBookViewModel
import com.example.data.Challan

@Composable
fun VehicleScreen(
    viewModel: GuardBookViewModel
) {
    val isEnglish = viewModel.selectedLanguage.collectAsState().value == "EN"

    var subScreen by remember { mutableStateOf("dashboard") } // dashboard, add_challan

    var searchVehicleQuery by remember { mutableStateOf("") }
    val allChallans by viewModel.allChallans.collectAsState()

    // Create Challan Form State
    var cVehicleNo by remember { mutableStateOf("") }
    var cViolation by remember { mutableStateOf("Illegal Parking") }
    var cAmount by remember { mutableStateOf("500") }
    var cNotes by remember { mutableStateOf("") }
    var cLocation by remember { mutableStateOf("Main Gate Parking") }
    var attachmentPdfName by remember { mutableStateOf("") }

    // Pay Challan Dialog State
    var payChallanDialog by remember { mutableStateOf(false) }
    var selectedChallanForPayment by remember { mutableStateOf<Challan?>(null) }
    var payMethod by remember { mutableStateOf("UPI") }
    var payRefNo by remember { mutableStateOf("") }

    val violationsList = listOf("Illegal Parking", "Speeding in Society", "Blocking Main Gate", "Unauthorized Entry", "Trash Dumping")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepNavy)
    ) {
        if (subScreen == "dashboard") {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Title
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isEnglish) "Vehicle & Challans" else "वाहन और चालान प्रबंधन",
                        color = LightText,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(
                        onClick = {
                            cVehicleNo = searchVehicleQuery
                            cViolation = "Illegal Parking"
                            cAmount = "500"
                            cNotes = ""
                            cLocation = "Main Gate Parking"
                            attachmentPdfName = ""
                            subScreen = "add_challan"
                        },
                        modifier = Modifier.background(GlowRed.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                    ) {
                        Icon(Icons.Default.AddAlert, contentDescription = "Add Challan", tint = GlowRed)
                    }
                }

                // Vehicle Search
                GlassTextField(
                    value = searchVehicleQuery,
                    onValueChange = { searchVehicleQuery = it },
                    label = if (isEnglish) "Search Vehicle (e.g. MH12AB1234)" else "वाहन नंबर खोजें (जैसे MH12AB1234)",
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = GlowingCyan) },
                    testTag = "vehicle_search_input"
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Factual History Counters
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    val pendingCount = allChallans.filter { it.status == "PENDING" }.size
                    val paidCount = allChallans.filter { it.status == "PAID" }.size
                    val pendingAmount = allChallans.filter { it.status == "PENDING" }.sumOf { it.amount }

                    // Pending
                    Box(modifier = Modifier.weight(1f)) {
                        GlassCard(borderColor = GlowRed.copy(alpha = 0.4f), modifier = Modifier.fillMaxWidth()) {
                            Text(if (isEnglish) "Pending" else "लंबित", color = DimText, fontSize = 11.sp)
                            Text("$pendingCount (₹${pendingAmount.toInt()})", color = GlowRed, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Paid
                    Box(modifier = Modifier.weight(1f)) {
                        GlassCard(borderColor = GlowGreen.copy(alpha = 0.4f), modifier = Modifier.fillMaxWidth()) {
                            Text(if (isEnglish) "Paid" else "भुगतान", color = DimText, fontSize = 11.sp)
                            val totalPaid = allChallans.filter { it.status == "PAID" }.sumOf { it.amount }
                            Text("$paidCount (₹${totalPaid.toInt()})", color = GlowGreen, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Text(
                    text = if (isEnglish) "RECENT CHALLANS" else "हालिया चालान सूची",
                    color = GlowingCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Filter list by vehicle search query
                val filteredChallans = if (searchVehicleQuery.trim().isEmpty()) {
                    allChallans
                } else {
                    allChallans.filter { it.vehicleNumber.contains(searchVehicleQuery.trim(), ignoreCase = true) }
                }

                if (filteredChallans.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isEnglish) "No challan records found." else "कोई चालान रिकॉर्ड नहीं मिला।",
                            color = DimText,
                            fontSize = 14.sp
                        )
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(filteredChallans) { challan ->
                            ChallanItemCard(
                                challan = challan,
                                isEnglish = isEnglish,
                                onPayClick = {
                                    selectedChallanForPayment = challan
                                    payRefNo = ""
                                    payMethod = "UPI"
                                    payChallanDialog = true
                                }
                            )
                        }
                    }
                }
            }
        } else {
            // Add Challan form screen
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
                    IconButton(onClick = { subScreen = "dashboard" }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = GlowingCyan)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isEnglish) "Issue Vehicle Challan" else "वाहन चालान जारी करें",
                        color = LightText,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                GlassCard(
                    borderColor = GlowRed.copy(alpha = 0.3f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    GlassTextField(
                        value = cVehicleNo,
                        onValueChange = { cVehicleNo = it.uppercase() },
                        label = if (isEnglish) "Vehicle Number" else "वाहन नंबर",
                        leadingIcon = { Icon(Icons.Default.DirectionsCar, null, tint = GlowingCyan) },
                        testTag = "challan_vehicle_input"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = if (isEnglish) "Violation Type" else "उल्लंघन का प्रकार",
                        color = DimText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        violationsList.forEach { violation ->
                            val selected = cViolation == violation
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        if (selected) GlowRed.copy(alpha = 0.1f) else Color.Transparent,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .border(
                                        1.dp,
                                        if (selected) GlowRed.copy(alpha = 0.5f) else Color(0x22FFFFFF),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable {
                                        cViolation = violation
                                        cAmount = when (violation) {
                                            "Illegal Parking" -> "500"
                                            "Speeding in Society" -> "1000"
                                            "Blocking Main Gate" -> "800"
                                            "Unauthorized Entry" -> "1500"
                                            else -> "300"
                                        }
                                    }
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .background(if (selected) GlowRed else Color.Transparent, RoundedCornerShape(2.dp))
                                        .border(1.dp, if (selected) GlowRed else DimText, RoundedCornerShape(2.dp))
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(violation, color = if (selected) LightText else DimText, fontSize = 13.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    GlassTextField(
                        value = cAmount,
                        onValueChange = { cAmount = it },
                        label = if (isEnglish) "Fine Amount (₹)" else "जुर्माना राशि (₹)",
                        leadingIcon = { Icon(Icons.Default.AttachMoney, null, tint = GlowingCyan) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        testTag = "challan_amount_input"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    GlassTextField(
                        value = cLocation,
                        onValueChange = { cLocation = it },
                        label = if (isEnglish) "Violation Location" else "उल्लंघन स्थान",
                        leadingIcon = { Icon(Icons.Default.LocationOn, null, tint = GlowingCyan) }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    GlassTextField(
                        value = cNotes,
                        onValueChange = { cNotes = it },
                        label = if (isEnglish) "Description & Remarks" else "विवरण और टिप्पणी",
                        leadingIcon = { Icon(Icons.Default.Edit, null, tint = GlowingCyan) }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Simulate PDF doc attachment
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0x0F000000), RoundedCornerShape(8.dp))
                            .border(1.dp, GlassBorder, RoundedCornerShape(8.dp))
                            .clickable { attachmentPdfName = "Challan_Evidence_${cVehicleNo.ifEmpty { "IMG" }}.pdf" }
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CloudUpload, null, tint = GlowingCyan)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (attachmentPdfName.isNotEmpty()) attachmentPdfName else (if (isEnglish) "Attach Photo / Evidence (PDF/JPG)" else "फोटो / साक्ष्य जोड़ें (PDF/JPG)"),
                                color = if (attachmentPdfName.isNotEmpty()) GlowGreen else LightText,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                GlowButton(
                    text = if (isEnglish) "ISSUE CHALLAN" else "चालान जारी करें",
                    onClick = {
                        if (cVehicleNo.trim().isEmpty() || cAmount.trim().isEmpty()) {
                            // error
                        } else {
                            viewModel.createChallan(
                                vehicleNumber = cVehicleNo,
                                violation = cViolation,
                                amount = cAmount.toDoubleOrNull() ?: 500.0,
                                location = cLocation,
                                notes = cNotes
                            )
                            subScreen = "dashboard"
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    glowColor = GlowRed,
                    testTag = "submit_challan_button"
                )
            }
        }

        // Record Payment Dialog
        if (payChallanDialog && selectedChallanForPayment != null) {
            AlertDialog(
                onDismissRequest = { payChallanDialog = false },
                containerColor = Color(0xFF141F3C),
                title = {
                    Text(
                        text = if (isEnglish) "Record Challan Payment" else "चालान भुगतान सहेजें",
                        color = GlowGreen,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Challan No: ${selectedChallanForPayment?.challanNumber}",
                            color = LightText,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Vehicle: ${selectedChallanForPayment?.vehicleNumber} • Amount: ₹${selectedChallanForPayment?.amount?.toInt()}",
                            color = DimText,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Text(if (isEnglish) "Payment Method" else "भुगतान का प्रकार", color = DimText, fontSize = 12.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            listOf("UPI", "Cash", "Card").forEach { method ->
                                val selected = payMethod == method
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .background(if (selected) GlowGreen.copy(alpha = 0.15f) else Color.Transparent, RoundedCornerShape(8.dp))
                                        .border(1.dp, if (selected) GlowGreen else Color(0x22FFFFFF), RoundedCornerShape(8.dp))
                                        .clickable { payMethod = method }
                                        .padding(10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(method, color = if (selected) GlowGreen else LightText, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        GlassTextField(
                            value = payRefNo,
                            onValueChange = { payRefNo = it },
                            label = if (isEnglish) "Ref No / Transaction ID" else "संदर्भ संख्या / ट्रांजेक्शन ID",
                            leadingIcon = { Icon(Icons.Default.Receipt, null, tint = GlowingCyan) },
                            testTag = "pay_reference_input"
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (payRefNo.trim().isNotEmpty()) {
                                payChallanDialog = false
                                viewModel.payChallan(
                                    challan = selectedChallanForPayment!!,
                                    paymentMethod = payMethod,
                                    referenceNo = payRefNo
                                )
                            }
                        }
                    ) {
                        Text(if (isEnglish) "MARK PAID" else "भुगतान सफल", color = GlowGreen, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { payChallanDialog = false }) {
                        Text(if (isEnglish) "CANCEL" else "रद्द करें", color = LightText)
                    }
                }
            )
        }
    }
}

@Composable
fun ChallanItemCard(
    challan: Challan,
    isEnglish: Boolean,
    onPayClick: () -> Unit
) {
    val isPending = challan.status == "PENDING"

    GlassCard(
        borderColor = if (isPending) GlowRed.copy(alpha = 0.4f) else GlowGreen.copy(alpha = 0.3f),
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
                        text = challan.vehicleNumber,
                        color = LightText,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    GlowBadge(
                        text = challan.status,
                        color = if (isPending) GlowRed else GlowGreen
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${challan.violation} • ₹${challan.amount.toInt()}",
                    color = LightText.copy(alpha = 0.8f),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Challan No: ${challan.challanNumber} • Loc: ${challan.location}",
                    color = DimText,
                    fontSize = 11.sp
                )
                Text(
                    text = "Date: ${challan.date} ${challan.time} • Due: ${challan.dueDate}",
                    color = DimText,
                    fontSize = 11.sp
                )

                if (challan.paymentMethod.isNotEmpty()) {
                    Text(
                        text = "Paid via: ${challan.paymentMethod} • Ref: ${challan.paymentReference}",
                        color = GlowGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (isPending) {
                GlowButton(
                    text = if (isEnglish) "PAY" else "भुगतान",
                    onClick = onPayClick,
                    glowColor = GlowGreen,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.width(76.dp),
                    testTag = "pay_now_button"
                )
            }
        }
    }
}
