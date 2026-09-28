package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.viewmodel.GuardBookViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val viewModel: GuardBookViewModel = viewModel()
                MainContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainContent(viewModel: GuardBookViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val isEnglish by viewModel.selectedLanguage.collectAsState()
    val activeDuty by viewModel.activeDutySession.collectAsState()
    val guards by viewModel.allGuards.collectAsState()

    var showEmergencyOverlay by remember { mutableStateOf(false) }

    // Automatic Welcoming Setup redirect if guard accounts already exist
    LaunchedEffect(guards) {
        if (guards.isNotEmpty() && currentScreen == "welcome") {
            viewModel.currentScreen.value = "sign_in"
        }
    }

    // Global back handler for custom state navigation
    if (currentScreen != "welcome" && currentScreen != "sign_in" && currentScreen != "create_account" && currentScreen != "dashboard") {
        BackHandler {
            viewModel.currentScreen.value = "dashboard"
        }
    } else if (currentScreen == "sign_in") {
        BackHandler {
            viewModel.currentScreen.value = if (guards.isNotEmpty()) "sign_in" else "welcome"
        }
    } else if (currentScreen == "create_account") {
        BackHandler {
            viewModel.currentScreen.value = if (guards.isNotEmpty()) "sign_in" else "welcome"
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            // Show bottom navigation bar only when logged in & ON DUTY
            if (activeDuty != null && currentScreen != "welcome" && currentScreen != "sign_in" && currentScreen != "create_account") {
                GlassBottomNavigationBar(
                    currentScreen = currentScreen,
                    isEnglish = isEnglish == "EN",
                    onTabSelected = { target ->
                        if (target == "alert") {
                            showEmergencyOverlay = true
                        } else {
                            viewModel.currentScreen.value = target
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            SecurityBackground {
                when (currentScreen) {
                    "welcome" -> WelcomeScreen(
                        viewModel = viewModel,
                        onCreateAccount = { viewModel.currentScreen.value = "create_account" },
                        onLoginClick = { viewModel.currentScreen.value = "sign_in" }
                    )
                    "create_account" -> CreateAccountScreen(
                        viewModel = viewModel,
                        onAccountCreated = { viewModel.currentScreen.value = "sign_in" },
                        onBackToLogin = { viewModel.currentScreen.value = "sign_in" }
                    )
                    "sign_in" -> SignInScreen(
                        viewModel = viewModel,
                        onSignInSuccess = { viewModel.currentScreen.value = "dashboard" },
                        onCreateAccountRedirect = { viewModel.currentScreen.value = "create_account" }
                    )
                    "dashboard" -> DashboardScreen(
                        viewModel = viewModel,
                        onNavigate = { screen ->
                            if (screen == "alert") {
                                showEmergencyOverlay = true
                            } else {
                                viewModel.currentScreen.value = screen
                            }
                        }
                    )
                    "visitors" -> VisitorScreen(viewModel = viewModel)
                    "vehicles" -> VehicleScreen(viewModel = viewModel)
                    "incidents" -> IncidentScreen(viewModel = viewModel)
                    "residents" -> ResidentScreen(viewModel = viewModel)
                    "services" -> ServiceScreen(viewModel = viewModel)
                    "reports" -> ReportsScreen(viewModel = viewModel)
                    "extra_income" -> ExtraIncomeScreen(viewModel = viewModel)
                    "settings" -> SettingsScreen(
                        viewModel = viewModel,
                        onLogout = { viewModel.currentScreen.value = "welcome" }
                    )
                }
            }

            // Emergency Overlay Dialog (🚨 Security Alerts Contact Board)
            if (showEmergencyOverlay) {
                EmergencyAlertOverlay(
                    isEnglish = isEnglish == "EN",
                    onDismiss = { showEmergencyOverlay = false }
                )
            }
        }
    }
}

@Composable
fun GlassBottomNavigationBar(
    currentScreen: String,
    isEnglish: Boolean,
    onTabSelected: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(GlassSurface.copy(alpha = 0.85f), RoundedCornerShape(24.dp))
                .border(1.dp, GlassBorder.copy(alpha = 0.25f), RoundedCornerShape(24.dp))
                .padding(vertical = 8.dp, horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(
                title = if (isEnglish) "Home" else "मुख्य",
                icon = Icons.Default.Shield,
                selected = currentScreen == "dashboard",
                onClick = { onTabSelected("dashboard") }
            )
            BottomNavItem(
                title = if (isEnglish) "Visitors" else "विज़िटर",
                icon = Icons.Default.People,
                selected = currentScreen == "visitors",
                onClick = { onTabSelected("visitors") }
            )
            BottomNavItem(
                title = if (isEnglish) "Challans" else "चालान",
                icon = Icons.Default.Receipt,
                selected = currentScreen == "vehicles",
                onClick = { onTabSelected("vehicles") }
            )
            BottomNavItem(
                title = if (isEnglish) "Earning" else "कमाई",
                icon = Icons.Default.Paid,
                selected = currentScreen == "extra_income",
                onClick = { onTabSelected("extra_income") }
            )
            BottomNavItem(
                title = if (isEnglish) "Alert" else "अलर्ट",
                icon = Icons.Default.Emergency,
                selected = false,
                isAlert = true,
                onClick = { onTabSelected("alert") }
            )
            BottomNavItem(
                title = if (isEnglish) "Profile" else "प्रोफ़ाइल",
                icon = Icons.Default.Settings,
                selected = currentScreen == "settings",
                onClick = { onTabSelected("settings") }
            )
        }
    }
}

@Composable
fun BottomNavItem(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    isAlert: Boolean = false,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = if (isAlert) GlowRed else (if (selected) GlowingCyan else DimText),
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = title,
            color = if (isAlert) GlowRed else (if (selected) GlowingCyan else DimText),
            fontSize = 9.sp,
            fontWeight = if (selected || isAlert) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun EmergencyAlertOverlay(
    isEnglish: Boolean,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF141F3C),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Warning, null, tint = GlowRed, modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isEnglish) "🚨 SECURITY EMERGENCY ALERT" else "🚨 सुरक्षा आपातकालीन अलर्ट",
                    color = GlowRed,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = if (isEnglish) {
                        "Immediately contact supervisor or property desk if you observe suspicious activities or accident."
                    } else {
                        "यदि आप किसी संदिग्ध गतिविधि या दुर्घटना को देखते हैं, तो तुरंत सुपरवाइजर या प्रॉपर्टी डेस्क से संपर्क करें।"
                    },
                    color = LightText,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(if (isEnglish) "QUICK ACTION DIRECTORIES" else "त्वरित संपर्क सूत्र", color = DimText, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 6.dp))

                EmergencyContactRow(name = if (isEnglish) "Security Supervisor Desk" else "सुरक्षा सुपरवाइजर डेस्क", phone = "+91 99999 00011")
                EmergencyContactRow(name = if (isEnglish) "ABC Resident Admin Desk" else "एबीसी रहवासी एडमिन", phone = "+91 99999 00022")
                EmergencyContactRow(name = if (isEnglish) "Local Fire & Emergency Desk" else "स्थानीय फायर स्टेशन", phone = "101")
                EmergencyContactRow(name = if (isEnglish) "Local Police Help desk" else "स्थानीय पुलिस स्टेशन", phone = "112")
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(if (isEnglish) "DISMISS ALERT" else "अलर्ट बंद करें", color = GlowRed, fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
fun EmergencyContactRow(name: String, phone: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .background(Color(0x0A000000), RoundedCornerShape(8.dp))
            .border(1.dp, GlassBorder.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(name, color = LightText, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text(phone, color = DimText, fontSize = 11.sp)
        }
        Icon(
            imageVector = Icons.Default.Phone,
            contentDescription = "Call",
            tint = GlowGreen,
            modifier = Modifier
                .size(20.dp)
                .clickable { /* Call simulated */ }
        )
    }
}
