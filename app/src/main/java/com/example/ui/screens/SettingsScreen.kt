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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.viewmodel.GuardBookViewModel

@Composable
fun SettingsScreen(
    viewModel: GuardBookViewModel,
    onLogout: () -> Unit
) {
    val isEnglish = viewModel.selectedLanguage.collectAsState().value == "EN"

    var subScreen by remember { mutableStateOf("list") } // list, about, help, privacy

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
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = if (isEnglish) "Settings & Profile" else "सेटिंग्स और प्रोफ़ाइल",
                    color = LightText,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                // Language toggle card
                Text(
                    text = if (isEnglish) "PREFERENCES" else "प्राथमिकताएं",
                    color = GlowingCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                GlassCard(
                    borderColor = GlassBorder.copy(alpha = 0.3f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Language, null, tint = GlowingCyan)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = if (isEnglish) "App Language" else "ऐप की भाषा",
                                color = LightText,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "English",
                                color = if (isEnglish) GlowingCyan else DimText,
                                fontSize = 13.sp,
                                fontWeight = if (isEnglish) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier
                                    .clickable { viewModel.selectedLanguage.value = "EN" }
                                    .padding(4.dp)
                            )
                            Box(modifier = Modifier.size(width = 1.dp, height = 12.dp).background(DimText))
                            Text(
                                text = "हिंदी",
                                color = if (!isEnglish) GlowingCyan else DimText,
                                fontSize = 13.sp,
                                fontWeight = if (!isEnglish) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier
                                    .clickable { viewModel.selectedLanguage.value = "HI" }
                                    .padding(4.dp)
                            )
                        }
                    }
                }

                // Security & Privacy settings
                Text(
                    text = if (isEnglish) "APPLICATION" else "एप्लिकेशन",
                    color = GlowingCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                ) {
                    SettingsNavigationItem(
                        title = if (isEnglish) "About GuardBook" else "गार्डबुक के बारे में",
                        icon = Icons.Default.Info,
                        color = GlowingCyan
                    ) { subScreen = "about" }

                    SettingsNavigationItem(
                        title = if (isEnglish) "Help & Customer Support" else "सहायता और कस्टमर सपोर्ट",
                        icon = Icons.Default.SupportAgent,
                        color = GlowingBlue
                    ) { subScreen = "help" }

                    SettingsNavigationItem(
                        title = if (isEnglish) "Privacy Policy & GDPR" else "गोपनीयता नीति",
                        icon = Icons.Default.PrivacyTip,
                        color = GlowGreen
                    ) { subScreen = "privacy" }
                }

                // Log out duty section
                Text(
                    text = if (isEnglish) "DUTY CONTROLS" else "ड्यूटी कंट्रोल्स",
                    color = GlowingCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                GlassCard(
                    borderColor = GlowRed.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = if (isEnglish) "Ready to finish your active session?" else "क्या आप अपनी ड्यूटी समाप्त करना चाहते हैं?",
                            color = LightText,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = if (isEnglish) "Ending duty will log total shift time into reports." else "ड्यूटी बंद करने से आपका कुल ड्यूटी समय रिपोर्ट्स में दर्ज हो जाएगा।",
                            color = DimText,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                        )
                        GlowButton(
                            text = if (isEnglish) "END DUTY (लॉगआउट)" else "ड्यूटी समाप्त करें (LOG OUT)",
                            onClick = {
                                viewModel.endDuty {
                                    onLogout()
                                }
                            },
                            glowColor = GlowRed,
                            modifier = Modifier.fillMaxWidth(),
                            testTag = "end_duty_button"
                        )
                    }
                }
            }
        } else if (subScreen == "about") {
            // About Developer & Suresh Yadav circular profile card layout
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
                        .padding(bottom = 24.dp)
                ) {
                    IconButton(onClick = { subScreen = "list" }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = GlowingCyan)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isEnglish) "About GuardBook" else "गार्डबुक के बारे में",
                        color = LightText,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Styled circular avatar representing Suresh Yadav (Developer Creator)
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .background(
                            Brush.sweepGradient(
                                listOf(GlowingCyan, GlowingBlue, GlowingCyan)
                            ), CircleShape
                        )
                        .padding(3.dp)
                        .clip(CircleShape)
                        .background(DeepNavy),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "SY",
                        color = GlowingCyan,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "GuardBook – Smart Security",
                    color = LightText,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Smart Security. Simple Management.",
                    color = GlowingCyan,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(24.dp))

                GlassCard(
                    borderColor = GlassBorder.copy(alpha = 0.3f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (isEnglish) "DEVELOPER INFORMATION" else "डेवलपर जानकारी",
                        color = GlowingCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "Creator / Developer: Suresh Yadav",
                        color = LightText,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Email: sureshyadav83695080@gmail.com",
                        color = DimText,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "Version: 1.0 (Stable release)",
                        color = DimText,
                        fontSize = 13.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (isEnglish) {
                            "GuardBook replaces old paper security books with a premium glassmorphic, glowing security technology platform. Built with Jetpack Compose, Room persistence database, and local secure operations."
                        } else {
                            "गार्डबुक पुराने कागजी सुरक्षा रजिस्टर की जगह लेता है। यह जेटपैक कंपोज, रूम लोकल डेटाबेस, और आधुनिक ग्लास डिजाइन के साथ बनाया गया है।"
                        },
                        color = LightText.copy(alpha = 0.9f),
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        } else if (subScreen == "help") {
            // Help Screen
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                ) {
                    IconButton(onClick = { subScreen = "list" }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = GlowingCyan)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isEnglish) "Help & Customer Support" else "सहायता और संपर्क",
                        color = LightText,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                GlassCard(borderColor = GlowingBlue.copy(alpha = 0.3f), modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = if (isEnglish) "CONTACT CUSTOMER DESK" else "सपोर्ट डेस्क संपर्क",
                        color = GlowingBlue,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "Security Supervisor Hotline: +91 99999 88888",
                        color = LightText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Developer Support: sureshyadav83695080@gmail.com",
                        color = DimText,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (isEnglish) "We are available 24/7 to resolve technical issues or properties setup." else "हम प्रॉपर्टी सेटअप और तकनीकी समस्याओं को दूर करने के लिए उपलब्ध हैं।",
                        color = DimText,
                        fontSize = 12.sp
                    )
                }
            }
        } else {
            // Privacy Screen
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                ) {
                    IconButton(onClick = { subScreen = "list" }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = GlowingCyan)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isEnglish) "Privacy & GDPR" else "गोपनीयता नीति",
                        color = LightText,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                GlassCard(borderColor = GlowGreen.copy(alpha = 0.3f), modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = if (isEnglish) "DATA PROTECTION MATTERS" else "डेटा सुरक्षा",
                        color = GlowGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = if (isEnglish) {
                            "Your visitor database and vehicle challan logs are strictly saved in a local sandboxed SQLite Room Database. Under GDPR and privacy compliance guidelines:\n\n" +
                            "• No guard can share mobile numbers of residents.\n" +
                            "• Audit logs register every action.\n" +
                            "• Inactive profiles are scrubbed after 90 days."
                        } else {
                            "आपके विज़िटर और चालान प्रविष्टियाँ पूरी तरह से सुरक्षित स्थानीय डेटाबेस में सुरक्षित हैं।\n\n" +
                            "• कोई भी कर्मचारी रहवासियों के नंबर साझा नहीं कर सकता।\n" +
                            "• प्रत्येक गतिविधि ऑडिट ट्रेल में सहेजी जाती है।"
                        },
                        color = LightText,
                        fontSize = 13.sp,
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsNavigationItem(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    GlassCard(
        borderColor = GlassBorder.copy(alpha = 0.15f),
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(color.copy(alpha = 0.1f), CircleShape)
                        .border(1.dp, color.copy(alpha = 0.3f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = title,
                    color = LightText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = DimText)
        }
    }
}
