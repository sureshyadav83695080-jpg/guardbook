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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.viewmodel.GuardBookViewModel

data class ExtraTask(
    val id: String,
    val title: String,
    val description: String,
    val duration: String,
    val reward: Double,
    val provider: String,
    val category: String
)

@Composable
fun ExtraIncomeScreen(
    viewModel: GuardBookViewModel
) {
    val isEnglish = viewModel.selectedLanguage.collectAsState().value == "EN"

    val balance by viewModel.earningsBalance.collectAsState()
    val pendingBalance by viewModel.pendingEarnings.collectAsState()
    val completedCount by viewModel.completedTasksCount.collectAsState()

    var activeTaskForDetails by remember { mutableStateOf<ExtraTask?>(null) }
    var taskFinishedDialog by remember { mutableStateOf(false) }
    var showReportDialog by remember { mutableStateOf(false) }

    val tasks = listOf(
        ExtraTask(
            "1",
            if (isEnglish) "Quick Feedback Survey" else "त्वरित फीडबैक सर्वे",
            if (isEnglish) "Answer 5 multiple choice questions about smartphone safety habits." else "स्मार्टफोन सुरक्षा आदतों के बारे में 5 बहुविकल्पीय प्रश्नों के उत्तर दें।",
            "5 min",
            20.0,
            "Verified Partner",
            "Survey"
        ),
        ExtraTask(
            "2",
            if (isEnglish) "Hindi Pronunciation Task" else "हिंदी उच्चारण टास्क",
            if (isEnglish) "Record 10 common safety phrases aloud to train conversational models." else "कन्वर्सेशनल मॉडल को प्रशिक्षित करने के लिए 10 सामान्य सुरक्षा वाक्यांश रिकॉर्ड करें।",
            "8 min",
            30.0,
            "Speech Labs Inc",
            "Voice Contribution"
        ),
        ExtraTask(
            "3",
            if (isEnglish) "Local Store Directory Check" else "स्थानीय स्टोर सूची ऑडिट",
            if (isEnglish) "Confirm spelling and address coordinates of 3 shops nearby." else "आस-पास की 3 दुकानों के पते और वर्तनी की पुष्टि करें।",
            "12 min",
            50.0,
            "Local Map Partner",
            "Data Collection"
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepNavy)
    ) {
        if (activeTaskForDetails == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Text(
                    text = if (isEnglish) "💰 Extra Income Hub" else "💰 अतिरिक्त आय केंद्र",
                    color = LightText,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                Text(
                    text = if (isEnglish) "Complete micro-tasks in free time. Guard duty always comes first!" else "फ्री समय में छोटे टास्क पूरे करें। ड्यूटी और सुरक्षा हमेशा पहले आती है!",
                    color = GlowAmber,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Safety Alert Banner (CRITICAL RULES)
                GlassCard(
                    borderColor = GlowRed.copy(alpha = 0.5f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    backgroundColor = Color(0x22FF1744)
                ) {
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(Icons.Default.Security, null, tint = GlowRed, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (isEnglish) "CRITICAL SAFETY WARNINGS" else "महत्वपूर्ण सुरक्षा चेतावनी",
                                color = GlowRed,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isEnglish) {
                                    "• NEVER pay money to get a task.\n" +
                                    "• NEVER share OTP, UPI PIN or Bank Passwords.\n" +
                                    "• Reject scams, gambling or illegal money transfer schemes."
                                } else {
                                    "• कार्य प्राप्त करने के लिए कभी भी पैसे न दें।\n" +
                                    "• कभी भी अपना OTP, UPI पिन या बैंक पासवर्ड साझा न करें।\n" +
                                    "• जुआ, सट्टेबाजी या मनी ट्रांसफर घोटालों को तुरंत ब्लॉक करें।"
                                },
                                color = LightText,
                                fontSize = 11.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }

                // Balance Tracker Card
                GlassCard(
                    borderColor = GlowGreen.copy(alpha = 0.3f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (isEnglish) "VERIFIED WALLET BALANCE" else "सत्यापित वॉलेट बैलेंस",
                                color = DimText,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "₹${balance.toInt()}",
                                color = GlowGreen,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = if (isEnglish) "Completed Tasks: $completedCount" else "पूरे किए गए टास्क: $completedCount",
                                color = LightText,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = if (isEnglish) "Pending: ₹${pendingBalance.toInt()}" else "लंबित: ₹${pendingBalance.toInt()}",
                                color = DimText,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                Text(
                    text = if (isEnglish) "AVAILABLE MICRO TASKS" else "उपलब्ध छोटे कार्य",
                    color = GlowingCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Render tasks
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    tasks.forEach { task ->
                        GlassCard(
                            borderColor = GlassBorder.copy(alpha = 0.3f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(task.title, color = LightText, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        GlowBadge(text = task.category, color = GlowingCyan)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(task.description, color = DimText, fontSize = 12.sp)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text("Est. Time: ${task.duration} • Provider: ${task.provider}", color = DimText, fontSize = 11.sp)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("₹${task.reward.toInt()}", color = GlowGreen, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    GlowButton(
                                        text = if (isEnglish) "VIEW" else "देखें",
                                        onClick = { activeTaskForDetails = task },
                                        glowColor = GlowGreen,
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.width(68.dp),
                                        testTag = "view_task_button"
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Task Execution Simulator Layout
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
                    IconButton(onClick = { activeTaskForDetails = null }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = GlowingCyan)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = activeTaskForDetails!!.title,
                        color = LightText,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                GlassCard(
                    borderColor = GlassBorder,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (isEnglish) "TASK INSTRUCTIONS" else "कार्य निर्देश",
                        color = GlowingCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = activeTaskForDetails!!.description,
                        color = LightText,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (isEnglish) {
                            "To complete this task and get ₹${activeTaskForDetails!!.reward.toInt()}:\n" +
                            "1. Read all questions thoroughly.\n" +
                            "2. Answer truthfully based on your experience.\n" +
                            "3. Do not minimize the app during completion."
                        } else {
                            "इस कार्य को पूरा करने और ₹${activeTaskForDetails!!.reward.toInt()} प्राप्त करने के लिए:\n" +
                            "१. सभी प्रश्नों को ध्यान से पढ़ें।\n" +
                            "२. अपने अनुभव के आधार पर सही उत्तर दें।\n" +
                            "३. कार्य के दौरान ऐप बंद न करें।"
                        },
                        color = DimText,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    GlowButton(
                        text = if (isEnglish) "REPORT SCAM" else "रिपोर्ट स्कैम",
                        onClick = { showReportDialog = true },
                        glowColor = GlowRed,
                        modifier = Modifier.weight(1f),
                        testTag = "report_task_button"
                    )

                    GlowButton(
                        text = if (isEnglish) "COMPLETE TASK" else "कार्य पूर्ण करें",
                        onClick = {
                            viewModel.completeTask(activeTaskForDetails!!.reward)
                            taskFinishedDialog = true
                        },
                        glowColor = GlowGreen,
                        modifier = Modifier.weight(1.5f),
                        testTag = "complete_task_button"
                    )
                }
            }
        }

        // Task Completed Dialog
        if (taskFinishedDialog && activeTaskForDetails != null) {
            AlertDialog(
                onDismissRequest = { taskFinishedDialog = false },
                containerColor = Color(0xFF141F3C),
                title = {
                    Text(
                        text = if (isEnglish) "🎉 Task Completed!" else "🎉 कार्य पूर्ण हुआ!",
                        color = GlowGreen,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(
                        text = if (isEnglish) {
                            "Congratulations! You have successfully completed this micro-work task.\n\n" +
                            "₹${activeTaskForDetails!!.reward.toInt()} has been credited to your verified wallet."
                        } else {
                            "बधाई हो! आपने इस छोटे कार्य को सफलतापूर्वक पूर्ण कर लिया है।\n\n" +
                            "₹${activeTaskForDetails!!.reward.toInt()} आपके सत्यापित वॉलेट में जोड़ दिए गए हैं।"
                        },
                        color = LightText
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            taskFinishedDialog = false
                            activeTaskForDetails = null
                        }
                    ) {
                        Text(if (isEnglish) "DONE" else "पूर्ण हुआ", color = GlowGreen, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }

        // Report Dialog
        if (showReportDialog) {
            AlertDialog(
                onDismissRequest = { showReportDialog = false },
                containerColor = Color(0xFF141F3C),
                title = {
                    Text(
                        text = if (isEnglish) "Report Task" else "टास्क रिपोर्ट करें",
                        color = GlowRed,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(
                        text = if (isEnglish) {
                            "Are you sure this task is suspicious or requests sensitive info?\n\nOur security partners will audit it immediately."
                        } else {
                            "क्या आपको यकीन है कि यह टास्क संदिग्ध है या व्यक्तिगत जानकारी मांगता है?\n\nहमारी टीम तुरंत इसकी जांच करेगी।"
                        },
                        color = LightText
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showReportDialog = false
                            activeTaskForDetails = null
                        }
                    ) {
                        Text(if (isEnglish) "REPORT" else "रिपोर्ट करें", color = GlowRed, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showReportDialog = false }) {
                        Text(if (isEnglish) "CANCEL" else "रद्द करें", color = LightText)
                    }
                }
            )
        }
    }
}
