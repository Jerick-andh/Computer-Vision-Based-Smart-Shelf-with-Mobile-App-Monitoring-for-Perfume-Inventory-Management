package com.ottoscents.smartshelf.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.border
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ottoscents.smartshelf.MainViewModel
import com.ottoscents.smartshelf.Screen
import com.ottoscents.smartshelf.ui.components.*

@Composable
fun SettingsScreen(viewModel: MainViewModel, navigate: (Screen) -> Unit, onLogout: () -> Unit) {
    val userRole by viewModel.userRole.collectAsState()
    val schedule by viewModel.captureSchedule.collectAsState()
    val threshold by viewModel.lowStockThreshold.collectAsState()
    
    var scheduleDialogStep by remember { mutableStateOf<String?>(null) }
    var showThresholdDialog by remember { mutableStateOf(false) }

    ScrollScreen(topBar = { TopBar("Settings") }) {
        AppCard(background = Color(0xFF111827), border = Color.Transparent) {
            val userBranch by viewModel.userBranch.collectAsState()
            val email = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.email ?: "Unknown User"
            Text(email, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("${userBranch ?: "Unknown"} Branch", color = Color(0xFFD1D5DB), fontSize = 13.sp, modifier = Modifier.padding(top = 4.dp))
        }
        if (userRole == "admin") {
            SettingSection("System") {
                SettingRow("Capture Schedule", schedule) { scheduleDialogStep = "main" }
                SettingRow("Temperature Threshold", "25°C") { navigate(Screen.Temperature) }
                SettingRow("Low Stock Threshold", "$threshold bottles") { showThresholdDialog = true }
                SettingRow("Cloud Sync Status", "Connected") {}
            }
            SettingSection("Reports") { 
                SettingRow("Reports", null) { navigate(Screen.Reports) }
                SettingRow("System Activity Logs", null) { navigate(Screen.SystemLogs) } 
            }
        } else {
            SettingSection("System Status") {
                SettingRow("Capture Schedule", schedule) { navigate(Screen.Schedule) }
                SettingRow("Temperature Monitor", "View Readings") { navigate(Screen.Temperature) }
            }
        }
        SettingSection("Help & Guides") { SettingRow("Help & Guides", null) { navigate(Screen.Help) } }
        AppButton("Logout", variant = ButtonVariant.Outline, onClick = onLogout)
    }

    if (scheduleDialogStep != null) {
        val title = when (scheduleDialogStep) {
            "main" -> "Capture Schedule"
            "interval" -> "Select Interval"
            "time" -> "Select Time"
            else -> ""
        }
        val options = when (scheduleDialogStep) {
            "main" -> listOf("Manual Only", "Interval", "Custom Time")
            "interval" -> listOf("Every 30 mins", "Every hour", "Every 2 hours", "Every 4 hours")
            "time" -> listOf("08:00 AM", "10:00 AM", "12:00 PM", "02:00 PM", "04:00 PM", "06:00 PM", "08:00 PM")
            else -> emptyList()
        }
        
        val currentMain = if (schedule == "Manual Only") "Manual Only"
        else if (schedule.startsWith("Every")) "Interval"
        else "Custom Time"

        SelectionDialog(
            title = title,
            options = options,
            current = if (scheduleDialogStep == "main") currentMain else schedule,
            onDismiss = { scheduleDialogStep = null },
            onBack = if (scheduleDialogStep != "main") { { scheduleDialogStep = "main" } } else null,
            onSelect = { selected ->
                when (scheduleDialogStep) {
                    "main" -> {
                        when (selected) {
                            "Manual Only" -> {
                                viewModel.setCaptureSchedule("Manual Only")
                                scheduleDialogStep = null
                            }
                            "Interval" -> scheduleDialogStep = "interval"
                            "Custom Time" -> scheduleDialogStep = "time"
                        }
                    }
                    else -> {
                        viewModel.setCaptureSchedule(selected)
                        scheduleDialogStep = null
                    }
                }
            }
        )
    }

    if (showThresholdDialog) {
        val options = listOf("3 bottles", "5 bottles", "8 bottles", "10 bottles")
        SelectionDialog(
            title = "Low Stock Threshold",
            options = options,
            current = "$threshold bottles",
            onDismiss = { showThresholdDialog = false },
            onSelect = { 
                val valInt = it.split(" ")[0].toInt()
                viewModel.setLowStockThreshold(valInt)
                showThresholdDialog = false 
            }
        )
    }
}

@Composable
fun SelectionDialog(
    title: String, 
    options: List<String>, 
    current: String, 
    onDismiss: () -> Unit, 
    onSelect: (String) -> Unit,
    onBack: (() -> Unit)? = null
) {
    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        AppCard(background = Color.White, radius = 28.dp) {
            Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                options.forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (option == current) Blue.copy(alpha = 0.08f) else Color.Transparent)
                            .clickable { onSelect(option) }
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = option, 
                            fontSize = 15.sp, 
                            fontWeight = if (option == current) FontWeight.SemiBold else FontWeight.Medium,
                            color = if (option == current) Blue else TextBlack
                        )
                        if (option == current) {
                            Box(modifier = Modifier.size(24.dp).clip(CircleShape).background(Blue), contentAlignment = Alignment.Center) {
                                Text("✓", color = Color.White, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
            Row(modifier = Modifier.fillMaxWidth().padding(top = 16.dp), horizontalArrangement = Arrangement.End) {
                if (onBack != null) {
                    TextButton(onClick = onBack) {
                        Text("Back", color = Blue)
                    }
                    Spacer(Modifier.width(8.dp))
                }
                TextButton(onClick = onDismiss) {
                    Text("Cancel", color = Muted)
                }
            }
        }
    }
}

@Composable
private fun SettingSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title.uppercase(), fontSize = 11.sp, color = LightMuted, fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp, modifier = Modifier.padding(start = 4.dp, top = 8.dp))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp), content = content)
    }
}

@Composable
private fun SettingRow(title: String, value: String?, onClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(SoftGray).border(1.dp, BorderGray.copy(alpha = 0.5f), RoundedCornerShape(18.dp)).clickable(onClick = onClick).padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(title, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextBlack)
        Row(verticalAlignment = Alignment.CenterVertically) { if (value != null) Text(value, fontSize = 12.sp, color = Muted, fontWeight = FontWeight.Medium); Text("  ›", color = LightMuted, fontSize = 18.sp) }
    }
}
