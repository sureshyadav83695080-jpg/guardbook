package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.example.data.AuditLog

@Composable
fun ReportsScreen(
    viewModel: GuardBookViewModel
) {
    val isEnglish = viewModel.selectedLanguage.collectAsState().value == "EN"

    var selectedFilter by remember { mutableStateOf("Daily") } // Daily, Weekly, Monthly
    var exportToastMessage by remember { mutableStateOf("") }

    val auditLogs by viewModel.allAuditLogs.collectAsState()
    val dutySessions by viewModel.allDutySessions.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepNavy)
    ) {
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
                    text = if (isEnglish) "Audit Trails & Reports" else "ऑडिट ट्रेल और रिपोर्ट्स",
                    color = LightText,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(
                        onClick = {
                            exportToastMessage = if (isEnglish) "✓ Exported successfully as CSV" else "✓ सफलतापूर्वक CSV के रूप में निर्यात किया गया"
                        },
                        modifier = Modifier.background(GlowingCyan.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                    ) {
                        Icon(Icons.Default.Download, contentDescription = "CSV", tint = GlowingCyan)
                    }
                    IconButton(
                        onClick = {
                            exportToastMessage = if (isEnglish) "✓ Exported successfully as PDF" else "✓ सफलतापूर्वक PDF के रूप में निर्यात किया गया"
                        },
                        modifier = Modifier.background(GlowingBlue.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                    ) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = "PDF", tint = GlowingBlue)
                    }
                }
            }

            if (exportToastMessage.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .background(GlowGreen.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                        .border(1.dp, GlowGreen.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(exportToastMessage, color = GlowGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(
                            "Dismiss",
                            color = LightText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { exportToastMessage = "" }
                        )
                    }
                }
            }

            // Report Filters daily, weekly, monthly
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Daily", "Weekly", "Monthly").forEach { filter ->
                    val selected = selectedFilter == filter
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                if (selected) GlowingCyan.copy(alpha = 0.15f) else Color(0x0F000000),
                                RoundedCornerShape(10.dp)
                            )
                            .border(
                                1.dp,
                                if (selected) GlowingCyan else Color(0x22FFFFFF),
                                RoundedCornerShape(10.dp)
                            )
                            .clickable { selectedFilter = filter }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (filter == "Daily") {
                                if (isEnglish) "Today" else "आज"
                            } else if (filter == "Weekly") {
                                if (isEnglish) "Weekly" else "साप्ताहिक"
                            } else {
                                if (isEnglish) "Monthly" else "मासिक"
                            },
                            color = if (selected) GlowingCyan else LightText,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Live Factual Activity Counters
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                val shiftCount = dutySessions.size
                val actCount = dutySessions.sumOf { it.activityCount }

                Box(modifier = Modifier.weight(1f)) {
                    GlassCard(borderColor = GlassBorder.copy(alpha = 0.3f), modifier = Modifier.fillMaxWidth()) {
                        Text(if (isEnglish) "Duty Shifts" else "कुल ड्यूटी", color = DimText, fontSize = 11.sp)
                        Text("$shiftCount", color = LightText, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Box(modifier = Modifier.weight(1f)) {
                    GlassCard(borderColor = GlassBorder.copy(alpha = 0.3f), modifier = Modifier.fillMaxWidth()) {
                        Text(if (isEnglish) "Operations Handled" else "कुल प्रविष्टियां", color = DimText, fontSize = 11.sp)
                        Text("$actCount", color = GlowingCyan, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Text(
                text = if (isEnglish) "FACTUAL SECURITY AUDIT LOGS" else "सुरक्षा ऑडिट लॉग रिकॉर्ड",
                color = GlowingCyan,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            if (auditLogs.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isEnglish) "No audit log trails generated." else "कोई लॉग प्रविष्टि नहीं है।",
                        color = DimText,
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(auditLogs) { log ->
                        AuditLogItemCard(log = log, isEnglish = isEnglish)
                    }
                }
            }
        }
    }
}

@Composable
fun AuditLogItemCard(
    log: AuditLog,
    isEnglish: Boolean
) {
    GlassCard(
        borderColor = GlassBorder.copy(alpha = 0.15f),
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = GlassSurface.copy(alpha = 0.15f)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = log.username,
                    color = GlowingCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${log.date} ${log.time}",
                    color = DimText,
                    fontSize = 10.sp
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = log.action,
                color = LightText,
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Role: ${log.role} • Gate: ${log.gate}",
                color = DimText,
                fontSize = 10.sp
            )
        }
    }
}
