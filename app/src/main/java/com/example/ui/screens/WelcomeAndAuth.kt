package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.viewmodel.GuardBookViewModel

@Composable
fun WelcomeScreen(
    viewModel: GuardBookViewModel,
    onCreateAccount: () -> Unit,
    onLoginClick: () -> Unit
) {
    val isEnglish = viewModel.selectedLanguage.collectAsState().value == "EN"

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxHeight()
        ) {
            // Header Logo Area
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 40.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .background(GlassSurface, CircleShape)
                        .border(1.5.dp, GlowingCyan, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "GuardBook Logo",
                        tint = GlowingCyan,
                        modifier = Modifier.size(48.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "GuardBook",
                    color = LightText,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = if (isEnglish) "Smart Security. Simple Management." else "स्मार्ट सुरक्षा। सरल प्रबंधन।",
                    color = GlowingCyan,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.5.sp,
                    textAlign = TextAlign.Center
                )
            }

            // Welcome Text & Callout
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = if (isEnglish) "Welcome, Guard 👋" else "स्वागत है, सुरक्षा गार्ड 👋",
                    color = LightText,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (isEnglish) "Create your Guard account to get started." else "शुरू करने के लिए अपना गार्ड अकाउंट बनाएं।",
                    color = DimText,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(20.dp))
                GlowButton(
                    text = if (isEnglish) "CREATE GUARD ACCOUNT" else "गार्ड अकाउंट बनाएं",
                    onClick = onCreateAccount,
                    modifier = Modifier.fillMaxWidth(),
                    glowColor = GlowGreen,
                    testTag = "create_account_setup_button"
                )
            }

            // Already have account section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 30.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isEnglish) "Already have a Guard account?" else "क्या आपके पास पहले से गार्ड अकाउंट है?",
                    color = DimText,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                GlowButton(
                    text = if (isEnglish) "LOGIN" else "लॉगिन करें",
                    onClick = onLoginClick,
                    modifier = Modifier.fillMaxWidth(0.6f),
                    glowColor = GlowingCyan,
                    testTag = "login_setup_button"
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Language / भाषा: ",
                        color = DimText,
                        fontSize = 13.sp
                    )
                    Text(
                        text = if (isEnglish) "English" else "हिंदी",
                        color = GlowingCyan,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable {
                                viewModel.selectedLanguage.value = if (isEnglish) "HI" else "EN"
                            }
                            .padding(4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CreateAccountScreen(
    viewModel: GuardBookViewModel,
    onAccountCreated: () -> Unit,
    onBackToLogin: () -> Unit
) {
    val isEnglish = viewModel.selectedLanguage.collectAsState().value == "EN"

    var name by remember { mutableStateOf("") }
    var mobile by remember { mutableStateOf("") }
    var guardId by remember { mutableStateOf("") }
    var pin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }

    var errorMessage by remember { mutableStateOf("") }
    var isSuccess by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        if (!isSuccess) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(GlassSurface, CircleShape)
                        .border(1.dp, GlowingCyan, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.PersonAdd, "Create Guard", tint = GlowingCyan, modifier = Modifier.size(32.dp))
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = if (isEnglish) "CREATE GUARD ACCOUNT" else "सुरक्षा गार्ड अकाउंट बनाएं",
                    color = LightText,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(20.dp))

                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = GlowRed,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }

                GlassCard(
                    borderColor = GlassBorder.copy(alpha = 0.3f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    GlassTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = if (isEnglish) "Full Name" else "पूरा नाम",
                        leadingIcon = { Icon(Icons.Default.Person, null, tint = GlowingCyan) },
                        testTag = "reg_name_input"
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    GlassTextField(
                        value = mobile,
                        onValueChange = { mobile = it },
                        label = if (isEnglish) "Mobile Number" else "मोबाइल नंबर",
                        leadingIcon = { Icon(Icons.Default.Phone, null, tint = GlowingCyan) },
                        testTag = "reg_mobile_input"
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    GlassTextField(
                        value = guardId,
                        onValueChange = { guardId = it },
                        label = if (isEnglish) "Guard ID (e.g. GRD1001)" else "गार्ड ID (जैसे GRD1001)",
                        leadingIcon = { Icon(Icons.Default.Badge, null, tint = GlowingCyan) },
                        testTag = "reg_guard_id_input"
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    GlassTextField(
                        value = pin,
                        onValueChange = { pin = it },
                        label = if (isEnglish) "Create 4-Digit PIN" else "4-अंकों का पिन बनाएं",
                        leadingIcon = { Icon(Icons.Default.Lock, null, tint = GlowingCyan) },
                        visualTransformation = PasswordVisualTransformation(),
                        testTag = "reg_pin_input"
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    GlassTextField(
                        value = confirmPin,
                        onValueChange = { confirmPin = it },
                        label = if (isEnglish) "Confirm PIN" else "पिन की पुष्टि करें",
                        leadingIcon = { Icon(Icons.Default.LockReset, null, tint = GlowingCyan) },
                        visualTransformation = PasswordVisualTransformation(),
                        testTag = "reg_confirm_pin_input"
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                GlowButton(
                    text = if (isEnglish) "CREATE ACCOUNT" else "अकाउंट बनाएं",
                    onClick = {
                        if (name.trim().isEmpty() || mobile.trim().isEmpty() || guardId.trim().isEmpty() || pin.trim().isEmpty()) {
                            errorMessage = if (isEnglish) "Please fill in all fields!" else "कृपया सभी फ़ील्ड भरें!"
                        } else if (pin.trim() != confirmPin.trim()) {
                            errorMessage = if (isEnglish) "PINs do not match!" else "दोनों पिन मेल नहीं खाते!"
                        } else if (pin.trim().length < 4) {
                            errorMessage = if (isEnglish) "PIN must be at least 4 digits!" else "पिन कम से कम 4 अंकों का होना चाहिए!"
                        } else {
                            errorMessage = ""
                            viewModel.createGuardAccount(
                                name = name,
                                mobile = mobile,
                                guardId = guardId,
                                pin = pin,
                                onSuccess = {
                                    isSuccess = true
                                },
                                onError = { err ->
                                    errorMessage = err
                                }
                            )
                        }
                    },
                    glowColor = GlowGreen,
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "submit_registration_button"
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = if (isEnglish) "Already have a Guard account? Login" else "पहले से अकाउंट है? लॉगिन करें",
                    color = GlowingCyan,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable { onBackToLogin() }
                        .padding(8.dp)
                )
            }
        } else {
            // SUCCESS LAYOUT
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(GlowGreen.copy(alpha = 0.15f), CircleShape)
                        .border(1.5.dp, GlowGreen, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.CheckCircle, "Success", tint = GlowGreen, modifier = Modifier.size(48.dp))
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = if (isEnglish) "✓ GUARD ACCOUNT CREATED" else "✓ गार्ड अकाउंट बनाया गया",
                    color = GlowGreen,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))

                GlassCard(
                    borderColor = GlowGreen.copy(alpha = 0.4f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (isEnglish) "Account Summary" else "अकाउंट विवरण",
                        color = GlowingCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    Text(
                        text = "${if (isEnglish) "Guard Name:" else "गार्ड का नाम:"} $name",
                        color = LightText,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${if (isEnglish) "Guard ID:" else "गार्ड ID:"} ${guardId.uppercase()}",
                        color = LightText,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (isEnglish) "Your Guard account has been created successfully." else "आपका सुरक्षा गार्ड अकाउंट सफलतापूर्वक बना दिया गया है।",
                        color = DimText,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(30.dp))

                GlowButton(
                    text = if (isEnglish) "CONTINUE TO LOGIN" else "लॉगिन करें",
                    onClick = onAccountCreated,
                    glowColor = GlowingCyan,
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "continue_to_login_button"
                )
            }
        }
    }
}

@Composable
fun SignInScreen(
    viewModel: GuardBookViewModel,
    onSignInSuccess: () -> Unit,
    onCreateAccountRedirect: () -> Unit
) {
    val isEnglish = viewModel.selectedLanguage.collectAsState().value == "EN"

    var guardId by remember { mutableStateOf("") }
    var pin by remember { mutableStateOf("") }
    var selectedGateState by remember { mutableStateOf("Gate 1 — Main Gate") }
    var selectedShiftState by remember { mutableStateOf("Morning Shift") }
    
    var errorMessage by remember { mutableStateOf("") }

    val gates = listOf("Gate 1 — Main Gate", "Gate 2 — Parking Gate", "Gate 3 — Service Gate")
    val shifts = listOf("Morning Shift (06:00 - 14:00)", "Evening Shift (14:00 - 22:00)", "Night Shift (22:00 - 06:00)")

    var showGateDropdown by remember { mutableStateOf(false) }
    var showShiftDropdown by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(GlassSurface, CircleShape)
                    .border(1.5.dp, GlowingCyan, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = "GuardBook Logo",
                    tint = GlowingCyan,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (isEnglish) "WELCOME BACK" else "स्वागत है",
                color = LightText,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                letterSpacing = 1.sp
            )
            Text(
                text = if (isEnglish) "Enter Guard ID & PIN to operate" else "संचालन के लिए गार्ड ID और पिन दर्ज करें",
                color = DimText,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
            )

            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = GlowRed,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // Input Fields Card
            GlassCard(
                borderColor = GlassBorder.copy(alpha = 0.3f),
                modifier = Modifier.fillMaxWidth()
            ) {
                GlassTextField(
                    value = guardId,
                    onValueChange = { guardId = it },
                    label = if (isEnglish) "Guard ID" else "गार्ड ID",
                    leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null, tint = GlowingCyan) },
                    testTag = "username_input"
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                GlassTextField(
                    value = pin,
                    onValueChange = { pin = it },
                    label = if (isEnglish) "PIN" else "पिन",
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = GlowingCyan) },
                    visualTransformation = PasswordVisualTransformation(),
                    testTag = "password_input"
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Gate Selector dropdown trigger
                Text(
                    text = if (isEnglish) "Assigned Gate" else "चयनित गेट",
                    color = DimText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0x0F000000), RoundedCornerShape(12.dp))
                        .border(1.dp, GlassBorder, RoundedCornerShape(12.dp))
                        .clickable { showGateDropdown = true }
                        .padding(14.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(selectedGateState, color = LightText, fontSize = 14.sp)
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = GlowingCyan)
                    }
                    DropdownMenu(
                        expanded = showGateDropdown,
                        onDismissRequest = { showGateDropdown = false },
                        modifier = Modifier.background(Color(0xFF141F3C))
                    ) {
                        gates.forEach { gate ->
                            DropdownMenuItem(
                                text = { Text(gate, color = LightText) },
                                onClick = {
                                    selectedGateState = gate
                                    showGateDropdown = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Shift Selector dropdown trigger
                Text(
                    text = if (isEnglish) "Current Shift" else "चयनित शिफ्ट",
                    color = DimText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0x0F000000), RoundedCornerShape(12.dp))
                        .border(1.dp, GlassBorder, RoundedCornerShape(12.dp))
                        .clickable { showShiftDropdown = true }
                        .padding(14.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(selectedShiftState, color = LightText, fontSize = 14.sp)
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = GlowingCyan)
                    }
                    DropdownMenu(
                        expanded = showShiftDropdown,
                        onDismissRequest = { showShiftDropdown = false },
                        modifier = Modifier.background(Color(0xFF141F3C))
                    ) {
                        shifts.forEach { shift ->
                            DropdownMenuItem(
                                text = { Text(shift, color = LightText) },
                                onClick = {
                                    selectedShiftState = shift
                                    showShiftDropdown = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action button
            GlowButton(
                text = if (isEnglish) "LOGIN (लॉगिन करें)" else "लॉगिन करें (LOGIN)",
                onClick = {
                    if (guardId.trim().isEmpty() || pin.trim().isEmpty()) {
                        errorMessage = if (isEnglish) "Please enter ID and PIN!" else "कृपया ID और PIN दर्ज करें!"
                    } else {
                        viewModel.selectedGate.value = selectedGateState
                        viewModel.selectedShift.value = selectedShiftState
                        viewModel.loginGuard(
                            guardId = guardId,
                            pin = pin,
                            onSuccess = {
                                viewModel.currentScreen.value = "dashboard"
                                onSignInSuccess()
                            },
                            onError = { err ->
                                errorMessage = err
                            }
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                glowColor = GlowingCyan,
                testTag = "submit_button"
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Help links
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isEnglish) "Forgot PIN?" else "पिन भूल गए?",
                    color = DimText,
                    fontSize = 12.sp,
                    modifier = Modifier.clickable {
                        errorMessage = if (isEnglish) "Please contact Admin to reset PIN." else "कृपया पिन रीसेट करने के लिए एडमिन से संपर्क करें।"
                    }
                )

                Text(
                    text = if (isEnglish) "Create Guard Account" else "नया अकाउंट बनाएं",
                    color = GlowingCyan,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onCreateAccountRedirect() }
                )
            }
        }
    }
}
