package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
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
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.viewmodel.GuardBookViewModel
import com.example.data.Resident

@Composable
fun ResidentScreen(
    viewModel: GuardBookViewModel
) {
    val isEnglish = viewModel.selectedLanguage.collectAsState().value == "EN"

    var currentTab by remember { mutableStateOf("directory") } // directory, qr_passes
    var subScreen by remember { mutableStateOf("list") } // list, add_resident, create_pass, verify_pass

    // Add Resident Form State
    var rFlat by remember { mutableStateOf("") }
    var rName by remember { mutableStateOf("") }
    var rMobile by remember { mutableStateOf("") }
    var rFamily by remember { mutableStateOf("") }

    // Create QR Pass Form State
    var passVisitorName by remember { mutableStateOf("") }
    var passFlatNumber by remember { mutableStateOf("") }
    var passExpiryDays by remember { mutableStateOf("1") } // 1 day, or 0 (Expired) to test

    // Verified Result State
    var passVerificationResult by remember { mutableStateOf<String?>(null) } // null, VERIFIED, EXPIRED

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
                            text = if (isEnglish) "Resident List" else "रहवासी सूची",
                            color = if (currentTab == "directory") GlowingCyan else DimText,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clickable { currentTab = "directory" }
                                .padding(vertical = 4.dp, horizontal = 8.dp)
                        )
                        Text(
                            text = if (isEnglish) "QR Passes" else "क्यूआर पास",
                            color = if (currentTab == "qr_passes") GlowingCyan else DimText,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clickable { currentTab = "qr_passes" }
                                .padding(vertical = 4.dp, horizontal = 8.dp)
                        )
                    }

                    IconButton(
                        onClick = {
                            if (currentTab == "directory") {
                                rFlat = ""
                                rName = ""
                                rMobile = ""
                                rFamily = ""
                                subScreen = "add_resident"
                            } else {
                                passVisitorName = ""
                                passFlatNumber = ""
                                passExpiryDays = "1"
                                passVerificationResult = null
                                subScreen = "create_pass"
                            }
                        },
                        modifier = Modifier.background(GlowingCyan.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Item", tint = GlowingCyan)
                    }
                }

                if (currentTab == "directory") {
                    val residents by viewModel.allResidents.collectAsState()

                    if (residents.isEmpty()) {
                        Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text(if (isEnglish) "No residents recorded." else "कोई रहवासी दर्ज नहीं है।", color = DimText, fontSize = 14.sp)
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            items(residents) { resident ->
                                ResidentItemCard(resident = resident, isEnglish = isEnglish, viewModel = viewModel)
                            }
                        }
                    }
                } else {
                    // QR Pass Area with scan simulation option
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.QrCodeScanner, null, tint = GlowingCyan, modifier = Modifier.size(72.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (isEnglish) "Simulate Visitor Pass Scan" else "क्यूआर पास स्कैन सिम्युलेटर",
                            color = LightText,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isEnglish) "Generate a new pass and check its validity below" else "नीचे नया पास बनाकर उसकी वैधता जांचें",
                            color = DimText,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 24.dp).padding(top = 4.dp)
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        GlowButton(
                            text = if (isEnglish) "CREATE NEW QR PASS" else "नया क्यूआर पास बनाएं",
                            onClick = {
                                passVisitorName = ""
                                passFlatNumber = ""
                                passExpiryDays = "1"
                                passVerificationResult = null
                                subScreen = "create_pass"
                            },
                            glowColor = GlowingCyan,
                            testTag = "generate_pass_trigger"
                        )
                    }
                }
            }
        } else if (subScreen == "add_resident") {
            // Add Resident
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
                        text = if (isEnglish) "Add Resident Directory" else "नया रहवासी जोड़ें",
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
                        value = rFlat,
                        onValueChange = { rFlat = it },
                        label = if (isEnglish) "Flat / Block / Suite" else "फ्लैट / ब्लॉक संख्या",
                        leadingIcon = { Icon(Icons.Default.Home, null, tint = GlowingCyan) },
                        testTag = "flat_input"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    GlassTextField(
                        value = rName,
                        onValueChange = { rName = it },
                        label = if (isEnglish) "Resident Full Name" else "रहवासी का नाम",
                        leadingIcon = { Icon(Icons.Default.Person, null, tint = GlowingCyan) },
                        testTag = "resident_name_input"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    GlassTextField(
                        value = rMobile,
                        onValueChange = { rMobile = it },
                        label = if (isEnglish) "Mobile Number" else "मोबाइल नंबर",
                        leadingIcon = { Icon(Icons.Default.Phone, null, tint = GlowingCyan) },
                        testTag = "resident_mobile_input"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    GlassTextField(
                        value = rFamily,
                        onValueChange = { rFamily = it },
                        label = if (isEnglish) "Family Details (e.g. 3 members)" else "परिवार का विवरण (जैसे 3 सदस्य)",
                        leadingIcon = { Icon(Icons.Default.People, null, tint = GlowingCyan) }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                GlowButton(
                    text = if (isEnglish) "ADD TO DIRECTORY" else "सूची में सहेजें",
                    onClick = {
                        if (rFlat.trim().isEmpty() || rName.trim().isEmpty() || rMobile.trim().isEmpty()) {
                            // error
                        } else {
                            viewModel.addResident(rFlat, rName, rMobile, rFamily)
                            subScreen = "list"
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    glowColor = GlowGreen,
                    testTag = "save_resident_button"
                )
            }
        } else if (subScreen == "create_pass") {
            // Create temporary QR pass Form
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp)
                ) {
                    IconButton(onClick = { subScreen = "list" }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = GlowingCyan)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isEnglish) "Create Temporary Pass" else "अस्थायी क्यूआर पास बनाएं",
                        color = LightText,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (passVerificationResult == null) {
                    GlassCard(
                        borderColor = GlassBorder.copy(alpha = 0.3f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        GlassTextField(
                            value = passVisitorName,
                            onValueChange = { passVisitorName = it },
                            label = if (isEnglish) "Visitor Full Name" else "अतिथि / विज़िटर का नाम",
                            leadingIcon = { Icon(Icons.Default.Person, null, tint = GlowingCyan) },
                            testTag = "qr_visitor_name_input"
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        GlassTextField(
                            value = passFlatNumber,
                            onValueChange = { passFlatNumber = it },
                            label = if (isEnglish) "Flat Number (Self)" else "फ्लैट नंबर (खुद का)",
                            leadingIcon = { Icon(Icons.Default.Home, null, tint = GlowingCyan) },
                            testTag = "qr_flat_input"
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = if (isEnglish) "Pass Validity Setup" else "पास वैधता अवधि",
                            color = DimText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("1", "0").forEach { validity ->
                                val selected = passExpiryDays == validity
                                val isExpiredState = validity == "0"
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .background(
                                            if (selected) GlowingCyan.copy(alpha = 0.15f) else Color(0x0F000000),
                                            RoundedCornerShape(8.dp)
                                        )
                                        .border(
                                            1.dp,
                                            if (selected) (if (isExpiredState) GlowRed else GlowGreen) else Color(0x22FFFFFF),
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable { passExpiryDays = validity }
                                        .padding(vertical = 12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (isExpiredState) {
                                            if (isEnglish) "EXPIRED (for testing)" else "समाप्त अवधि (परीक्षण हेतु)"
                                        } else {
                                            if (isEnglish) "VALID (1 Day)" else "वैध (१ दिन)"
                                        },
                                        color = if (selected) (if (isExpiredState) GlowRed else GlowGreen) else LightText,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    GlowButton(
                        text = if (isEnglish) "GENERATE QR PASS" else "क्यूआर पास बनाएं",
                        onClick = {
                            if (passVisitorName.trim().isNotEmpty() && passFlatNumber.trim().isNotEmpty()) {
                                passVerificationResult = if (passExpiryDays == "1") "VERIFIED" else "EXPIRED"
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        glowColor = GlowingCyan,
                        testTag = "submit_pass_button"
                    )
                } else {
                    // Render Gorgeous Simulated QR Pass Card
                    GlassCard(
                        borderColor = if (passVerificationResult == "VERIFIED") GlowGreen.copy(alpha = 0.5f) else GlowRed.copy(alpha = 0.5f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (isEnglish) "PASS GENERATED SUCCESSFULLY" else "पास सफलतापूर्वक जनरेट किया गया",
                                color = GlowGreen,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            // Render a custom glowing mock QR graphic using Canvas
                            Box(
                                modifier = Modifier
                                    .size(160.dp)
                                    .background(Color(0x1F000000))
                                    .border(2.dp, if (passVerificationResult == "VERIFIED") GlowGreen else GlowRed)
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    // Custom abstract QR code design
                                    val sizeX = size.width
                                    val sizeY = size.height
                                    val block = sizeX / 4
                                    drawRect(Color.White, topLeft = androidx.compose.ui.geometry.Offset(0f, 0f), size = androidx.compose.ui.geometry.Size(block, block))
                                    drawRect(Color.White, topLeft = androidx.compose.ui.geometry.Offset(sizeX - block, 0f), size = androidx.compose.ui.geometry.Size(block, block))
                                    drawRect(Color.White, topLeft = androidx.compose.ui.geometry.Offset(0f, sizeY - block), size = androidx.compose.ui.geometry.Size(block, block))
                                    drawRect(Color.White, topLeft = androidx.compose.ui.geometry.Offset(block * 1.5f, block * 1.5f), size = androidx.compose.ui.geometry.Size(block, block))
                                    drawRect(Color.White, topLeft = androidx.compose.ui.geometry.Offset(block * 2.5f, block * 2.5f), size = androidx.compose.ui.geometry.Size(block * 0.5f, block * 0.5f))
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(passVisitorName, color = LightText, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Text("Flat No: $passFlatNumber", color = DimText, fontSize = 13.sp)
                            
                            val textResult = if (passVerificationResult == "VERIFIED") {
                                if (isEnglish) "✓ VISITOR PASS VERIFIED" else "✓ विज़िटर पास वैध"
                            } else {
                                if (isEnglish) "⚠ PASS EXPIRED" else "⚠ पास अवधि समाप्त"
                            }
                            
                            val colorResult = if (passVerificationResult == "VERIFIED") GlowGreen else GlowRed

                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = textResult,
                                color = colorResult,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    GlowButton(
                        text = if (isEnglish) "CLOSE" else "बंद करें",
                        onClick = {
                            passVerificationResult = null
                            subScreen = "list"
                        },
                        modifier = Modifier.fillMaxWidth(),
                        glowColor = GlowingCyan,
                        testTag = "close_pass_button"
                    )
                }
            }
        }
    }
}

@Composable
fun ResidentItemCard(
    resident: Resident,
    isEnglish: Boolean,
    viewModel: GuardBookViewModel
) {
    val isActive = resident.status == "Active"

    GlassCard(
        borderColor = if (isActive) GlowingCyan.copy(alpha = 0.3f) else Color(0x22FFFFFF),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = resident.name,
                        color = if (isActive) LightText else DimText,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    GlowBadge(
                        text = "Flat ${resident.flat}",
                        color = GlowingCyan
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Mob: ${resident.mobile}",
                    color = LightText.copy(alpha = 0.8f),
                    fontSize = 13.sp
                )
                if (resident.familyInfo.isNotEmpty()) {
                    Text(
                        text = "Family: ${resident.familyInfo}",
                        color = DimText,
                        fontSize = 11.sp
                    )
                }
            }

            // Quick Status Switch
            TextButton(
                onClick = { viewModel.toggleResidentStatus(resident) }
            ) {
                Text(
                    text = if (isActive) {
                        if (isEnglish) "ACTIVE" else "सक्रिय"
                    } else {
                        if (isEnglish) "INACTIVE" else "निष्क्रिय"
                    },
                    color = if (isActive) GlowGreen else GlowRed,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
    }
}
