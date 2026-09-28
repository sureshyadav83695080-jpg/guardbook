package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.viewmodel.GuardBookViewModel

@Composable
fun DashboardScreen(
    viewModel: GuardBookViewModel,
    onNavigate: (String) -> Unit
) {
    val isEnglish = viewModel.selectedLanguage.collectAsState().value == "EN"
    val loggedInGuard = viewModel.loggedInGuard.collectAsState().value
    val activeDuty = viewModel.activeDutySession.collectAsState().value
    val property = viewModel.currentProperty.collectAsState().value
    val gate = viewModel.selectedGate.collectAsState().value
    val shift = viewModel.selectedShift.collectAsState().value
    
    val online = viewModel.isOnline.collectAsState().value
    val isPro = viewModel.isProUser.collectAsState().value

    // Data Counters from flows
    val insideCount = viewModel.visitorsCurrentlyInside.collectAsState().value.size
    val totalVisitors = viewModel.allVisitors.collectAsState().value.size
    val totalChallans = viewModel.allChallans.collectAsState().value.filter { it.status == "PENDING" }.size
    val announcements = viewModel.allAnnouncements.collectAsState().value

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepNavy)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp)
        ) {
            // Header Card
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isEnglish) "GuardBook 👋" else "गार्डबुक 👋",
                            color = GlowingCyan,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = property,
                            color = LightText,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    // Online/Offline & Sync Indicator
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(Color(0x0F000000), RoundedCornerShape(12.dp))
                            .border(1.dp, GlassBorder.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                            .clickable { viewModel.requestSync() }
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(if (online) GlowGreen else GlowAmber, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (online) {
                                if (isEnglish) "ONLINE" else "ऑनलाइन"
                            } else {
                                if (isEnglish) "OFFLINE" else "ऑफ़लाइन"
                            },
                            color = if (online) GlowGreen else GlowAmber,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Guard Status Card
            item {
                GlassCard(
                    borderColor = GlowGreen.copy(alpha = 0.4f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${loggedInGuard?.name ?: " Ramesh Kumar"}",
                                color = LightText,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "$gate • $shift",
                                color = DimText,
                                fontSize = 12.sp
                            )
                            if (activeDuty != null) {
                                Spacer(modifier = Modifier.height(10.dp))
                                GlowButton(
                                    text = if (isEnglish) "END DUTY (ड्यूटी समाप्त)" else "ड्यूटी समाप्त करें",
                                    onClick = { viewModel.endDuty {} },
                                    glowColor = GlowRed,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.height(36.dp).wrapContentWidth()
                                )
                            }
                        }
                        GlowBadge(
                            text = if (activeDuty != null) {
                                if (isEnglish) "🟢 ON DUTY" else "🟢 ड्यूटी पर"
                            } else {
                                if (isEnglish) "🔴 OFF DUTY" else "🔴 ड्यूटी बंद"
                            },
                            color = if (activeDuty != null) GlowGreen else GlowRed
                        )
                    }
                }
            }

            // START DUTY Prominent Banner if OFF DUTY
            if (activeDuty == null) {
                item {
                    GlassCard(
                        borderColor = GlowGreen,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 20.dp),
                        backgroundColor = GlassSurface.copy(alpha = 0.4f)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.PlayCircle, null, tint = GlowGreen, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (isEnglish) "Start Your Active Duty" else "अपनी ड्यूटी शुरू करें",
                                color = LightText,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isEnglish) "Select gate & shift during login to operate.\nPress START DUTY below." else "काम शुरू करने के लिए ड्यूटी शुरू करें।\nनीचे 'START DUTY' बटन दबाएं।",
                                color = DimText,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            GlowButton(
                                text = if (isEnglish) "START DUTY (ड्यूटी शुरू करें)" else "ड्यूटी शुरू करें (START DUTY)",
                                onClick = { viewModel.startDuty() },
                                glowColor = GlowGreen,
                                modifier = Modifier.fillMaxWidth(),
                                testTag = "start_duty_button"
                            )
                        }
                    }
                }
            }

            // Counters Row (Currently Inside, Challans, etc.)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .alpha(if (activeDuty != null) 1f else 0.4f),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Currently Inside
                    Box(modifier = Modifier.weight(1f)) {
                        GlassCard(
                            borderColor = GlassBorder,
                            modifier = Modifier.fillMaxWidth(),
                            onClick = if (activeDuty != null) { { onNavigate("visitors") } } else null
                        ) {
                            Text(
                                text = if (isEnglish) "Currently Inside" else "अभी अंदर",
                                color = DimText,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "$insideCount",
                                color = GlowingCyan,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }

                    // Pending Challans
                    Box(modifier = Modifier.weight(1f)) {
                        GlassCard(
                            borderColor = if (totalChallans > 0) GlowRed.copy(alpha = 0.6f) else GlassBorder,
                            modifier = Modifier.fillMaxWidth(),
                            onClick = if (activeDuty != null) { { onNavigate("vehicles") } } else null
                        ) {
                            Text(
                                text = if (isEnglish) "Pending Challans" else "लंबित चालान",
                                color = DimText,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "$totalChallans",
                                color = if (totalChallans > 0) GlowRed else LightText,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }

            // Notice Board Notice
            if (announcements.isNotEmpty()) {
                item {
                    val firstNotice = announcements.first()
                    GlassCard(
                        borderColor = GlowAmber.copy(alpha = 0.5f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Announcement, contentDescription = null, tint = GlowAmber)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = firstNotice.title,
                                    color = LightText,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = firstNotice.description,
                                    color = DimText,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }

            // Quick Actions Panel Grid
            item {
                Text(
                    text = if (isEnglish) "QUICK OPERATIONS" else "त्वरित संचालन",
                    color = GlowingCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        DashboardActionCard(
                            title = if (isEnglish) "Search Profile" else "प्रोफ़ाइल खोजें",
                            icon = Icons.Default.Search,
                            color = GlowingCyan,
                            enabled = activeDuty != null,
                            modifier = Modifier.weight(1f)
                        ) { onNavigate("visitors") }

                        DashboardActionCard(
                            title = if (isEnglish) "New Visitor" else "नया विज़िटर",
                            icon = Icons.Default.PersonAdd,
                            color = GlowingCyan,
                            enabled = activeDuty != null,
                            modifier = Modifier.weight(1f)
                        ) { onNavigate("visitors") }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        DashboardActionCard(
                            title = if (isEnglish) "Delivery Entry" else "डिलीवरी एंट्री",
                            icon = Icons.Default.LocalShipping,
                            color = GlowingCyan,
                            enabled = activeDuty != null,
                            modifier = Modifier.weight(1f)
                        ) { onNavigate("visitors") }

                        DashboardActionCard(
                            title = if (isEnglish) "Vehicle & Challan" else "वाहन और चालान",
                            icon = Icons.Default.DirectionsCar,
                            color = GlowingBlue,
                            enabled = activeDuty != null,
                            modifier = Modifier.weight(1f)
                        ) { onNavigate("vehicles") }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        DashboardActionCard(
                            title = if (isEnglish) "Service / Repair" else "सर्विस / मरम्मत",
                            icon = Icons.Default.Build,
                            color = GlowingBlue,
                            enabled = activeDuty != null,
                            modifier = Modifier.weight(1f)
                        ) { onNavigate("services") }

                        DashboardActionCard(
                            title = if (isEnglish) "Residents Pass" else "रहवासी पास",
                            icon = Icons.Default.QrCodeScanner,
                            color = GlowingCyan,
                            enabled = activeDuty != null,
                            modifier = Modifier.weight(1f)
                        ) { onNavigate("residents") }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        DashboardActionCard(
                            title = if (isEnglish) "Incidents Log" else "घटना रिपोर्ट",
                            icon = Icons.Default.ReportProblem,
                            color = GlowAmber,
                            enabled = activeDuty != null,
                            modifier = Modifier.weight(1f)
                        ) { onNavigate("incidents") }

                        DashboardActionCard(
                            title = if (isEnglish) "Security Alert" else "सुरक्षा अलर्ट",
                            icon = Icons.Default.Emergency,
                            color = GlowRed,
                            enabled = activeDuty != null,
                            modifier = Modifier.weight(1f)
                        ) { onNavigate("alert") }
                    }
                }
            }

            // Monetization AD Architecture Banner
            if (!isPro) {
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    GlassCard(
                        borderColor = GlowingBlue.copy(alpha = 0.3f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "GuardBook Premium Ad",
                                    color = DimText,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (isEnglish) "Tired of banner Ads? Upgrade to remove Ads!" else "विज्ञापनों से परेशान हैं? अभी अपग्रेड करें!",
                                    color = LightText,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Button(
                                onClick = { viewModel.isProUser.value = true },
                                colors = ButtonDefaults.buttonColors(containerColor = GlowingCyan),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = if (isEnglish) "PRO" else "प्रो",
                                    color = DeepNavy,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DashboardActionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val alpha = if (enabled) 1f else 0.35f
    val finalClick = if (enabled) onClick else null

    GlassCard(
        borderColor = if (enabled) color.copy(alpha = 0.3f) else Color(0x11FFFFFF),
        modifier = modifier.alpha(alpha),
        onClick = finalClick
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(if (enabled) color.copy(alpha = 0.1f) else Color(0x05FFFFFF), CircleShape)
                    .border(1.dp, if (enabled) color.copy(alpha = 0.3f) else Color(0x11FFFFFF), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (enabled) color else DimText,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                color = if (enabled) LightText else DimText,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}
