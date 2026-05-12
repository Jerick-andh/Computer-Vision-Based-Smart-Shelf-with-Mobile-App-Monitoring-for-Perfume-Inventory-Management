package com.ottoscents.smartshelf.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ottoscents.smartshelf.MainViewModel
import com.ottoscents.smartshelf.Screen
import com.ottoscents.smartshelf.data.*
import com.ottoscents.smartshelf.ui.components.*

@Composable
fun ShelfAreaDetailScreen(back: () -> Unit) {
    ScrollScreen(topBar = { TopBar("Area Details", showBack = true, onBack = back) }) {
        Box(modifier = Modifier.fillMaxWidth().aspectRatio(1.75f).clip(RoundedCornerShape(28.dp)).background(SoftGray).border(1.dp, BorderGray, RoundedCornerShape(28.dp)), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) { Text("◉", color = LightMuted, fontSize = 28.sp); Text("Area A2 Image Cropped", color = LightMuted, fontSize = 12.sp, fontWeight = FontWeight.Medium) }
        }
        Column {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Area A2", fontSize = 30.sp, fontWeight = FontWeight.Bold, color = TextBlack)
                StatusChip("Needs Review", ChipVariant.Review)
            }
            Text("Assigned: Rose Petal", color = Muted, fontWeight = FontWeight.Medium, modifier = Modifier.padding(top = 4.dp))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp), modifier = Modifier.fillMaxWidth()) {
            CountCard("Live Shelf", "14", Modifier.weight(1f), dark = true)
            CountCard("System Goal", "15", Modifier.weight(1f))
        }
        Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).border(1.dp, BorderGray, RoundedCornerShape(18.dp)).padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("⚠", fontSize = 24.sp, color = LightMuted)
            Spacer(Modifier.width(12.dp))
            Column {
                Text("System confidence: Low", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextBlack)
                Text("Please check manually if a bottle is missing or blocked.", fontSize = 12.sp, color = Muted, modifier = Modifier.padding(top = 2.dp))
            }
        }
        AppButton("✓  Mark as Verified")
        AppButton("⚠  Report Issue", variant = ButtonVariant.Outline)
    }
}

@Composable
private fun CountCard(label: String, value: String, modifier: Modifier = Modifier, dark: Boolean = false) {
    AppCard(modifier = modifier, background = if (dark) Color(0xFF111827) else Color.White, border = if (dark) Color.Transparent else BorderGray) {
        Text(label.uppercase(), fontSize = 11.sp, letterSpacing = 1.sp, color = if (dark) Color(0xFFD1D5DB) else Muted, fontWeight = FontWeight.Medium)
        Text(value, fontSize = 34.sp, fontWeight = FontWeight.Light, color = if (dark) Color.White else TextBlack, modifier = Modifier.padding(top = 6.dp))
    }
}

@Composable
fun ReportsScreen(back: () -> Unit) {
    ScrollScreen(background = AppBg, topBar = { TopBar("Reports", showBack = true, onBack = back) }) {
        AppCard(background = CardBg) {
            Text("Inventory Summary", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextBlack)
            Text("Overview of stock and monitoring records", fontSize = 12.sp, color = Muted, modifier = Modifier.padding(top = 4.dp))
        }
        listOf(
            "Inventory Summary" to "Total bottles, detected count, and stock status",
            "Stock Movement Report" to "Removal, return, restock, and verification records",
            "Temperature Report" to "Shelf temperature readings and alert history",
            "Fan Activity Report" to "Cooling fan activation and stop records",
            "Access Log Report" to "Lipa branch access control events",
            "Exception Report" to "Detection mismatch, blocked view, and sync issues"
        ).forEach { (title, desc) ->
            Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(Color.White).border(1.dp, BorderGray.copy(alpha = 0.55f), RoundedCornerShape(20.dp)).padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) { Text(title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold); Text(desc, fontSize = 12.sp, color = Muted, modifier = Modifier.padding(top = 3.dp)) }
                Text("View", fontSize = 12.sp, color = LightMuted)
            }
        }
    }
}

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

@Composable
fun ShelfCheckHistoryScreen(back: () -> Unit) {
    val items = listOf(
        HistoryItem("May 1, 2026", "2:30 PM", "San Pablo", "completed"), HistoryItem("May 1, 2026", "8:15 AM", "San Pablo", "needs_review"), HistoryItem("Apr 30, 2026", "6:45 PM", "San Pablo", "cloud_checked"), HistoryItem("Apr 30, 2026", "2:00 PM", "San Pablo", "failed"), HistoryItem("Apr 30, 2026", "8:00 AM", "San Pablo", "completed"), HistoryItem("Apr 29, 2026", "6:30 PM", "San Pablo", "needs_review")
    )
    ScrollScreen(background = AppBg, topBar = { TopBar("Shelf Check History", showBack = true, onBack = back) }) {
        AppCard(background = CardBg) { Text("Midnight Oud", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, fontSize = 18.sp, fontWeight = FontWeight.Bold); Text("Area A1 • San Pablo", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, fontSize = 12.sp, color = Muted) }
        Text("Recent Checks".uppercase(), fontSize = 11.sp, color = Muted, fontWeight = FontWeight.Medium, letterSpacing = 1.8.sp)
        items.forEach { h ->
            val label = when (h.status) { "completed" -> "Completed"; "needs_review" -> "Needs Review"; "cloud_checked" -> "Cloud Checked"; else -> "Failed" }
            val variant = when (h.status) { "completed" -> ChipVariant.Green; "needs_review" -> ChipVariant.Warning; "cloud_checked" -> ChipVariant.Blue; else -> ChipVariant.Critical }
            Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(Color.White).border(1.dp, BorderGray.copy(alpha = 0.5f), RoundedCornerShape(18.dp)).padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column { Text("Shelf Check", fontSize = 14.sp, fontWeight = FontWeight.Medium); Text("${h.date} • ${h.time}", fontSize = 12.sp, color = Muted); Text(h.branch, fontSize = 12.sp, color = Muted) }
                StatusChip(label, variant, small = true)
            }
        }
    }
}

@Composable
fun CloudBackupCheckScreen(navigate: (Screen) -> Unit, back: () -> Unit) {
    var processing by remember { mutableStateOf(false) }
    ScrollScreen(background = AppBg, topBar = { TopBar("Cloud Backup Check", showBack = true, onBack = back) }) {
        AppCard(background = YellowBg, border = Color(0xFFFDE68A)) { Text("⚠  Unclear Results", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = TextBlack); Text("The local check produced ambiguous detection results.", fontSize = 14.sp, color = Muted, modifier = Modifier.padding(top = 4.dp)) }
        AppCard(background = CardBg) {
            Text("✓  Cloud Processing Complete", fontSize = 16.sp, color = Green, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(14.dp))
            DetailRow("Product", "Midnight Oud"); ThinDivider(); DetailRow("Branch", "San Pablo"); ThinDivider(); DetailRow("Shelf Area", "Area A1"); ThinDivider(); DetailRow("Timestamp", "May 1, 2026 • 2:30 PM")
        }
        Text("Processed Image".uppercase(), fontSize = 11.sp, color = Muted, fontWeight = FontWeight.Medium, letterSpacing = 1.8.sp)
        Box(modifier = Modifier.fillMaxWidth().aspectRatio(1.75f).clip(RoundedCornerShape(28.dp)).background(SoftGray).border(1.dp, BorderGray, RoundedCornerShape(28.dp)), contentAlignment = Alignment.Center) { Column(horizontalAlignment = Alignment.CenterHorizontally) { Text("☁", fontSize = 30.sp, color = LightMuted); Text("Image processed by cloud", fontSize = 14.sp, color = LightMuted) } }
        Text("Detection Results".uppercase(), fontSize = 11.sp, color = Muted, fontWeight = FontWeight.Medium, letterSpacing = 1.8.sp)
        AppCard(background = Color.White) {
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp), modifier = Modifier.fillMaxWidth()) { CountCard("Bottles on Shelf", "11", Modifier.weight(1f)); CountCard("Confidence", "87%", Modifier.weight(1f)) }
            Spacer(Modifier.height(16.dp)); ThinDivider(); Spacer(Modifier.height(12.dp))
            Text("⚠  1 item needs manual review", fontSize = 14.sp, color = Muted)
        }
        AppButton(if (processing) "Accepting..." else "Accept Result", enabled = !processing, onClick = { processing = true; back() })
        AppButton("Manual Review", variant = ButtonVariant.Outline, onClick = { navigate(Screen.ProductDetail) })
    }
}

@Composable
fun StockMovementLogsScreen(viewModel: MainViewModel, navigate: (Screen) -> Unit, back: () -> Unit) {
    val logs by viewModel.movementLogs.collectAsState()
    
    var filterType by remember { mutableStateOf<String?>(null) } // "Branch", "Status", or null
    var filterValue by remember { mutableStateOf("All") }
    var showFilterDialog by remember { mutableStateOf(false) }

    val filteredLogs = logs.filter { log ->
        when (filterType) {
            "Branch" -> filterValue == "All" || log.branch.equals(filterValue, ignoreCase = true)
            "Status" -> {
                val statusLabel = when (log.status) {
                    "present" -> "Bottle Present"
                    "missing" -> "Bottle Missing"
                    "returned" -> "Bottle Returned"
                    "restocked" -> "Restocked"
                    "verified" -> "Manually Verified"
                    else -> "Needs Review"
                }
                filterValue == "All" || statusLabel == filterValue
            }
            else -> true
        }
    }

    ScrollScreen(background = AppBg, topBar = { TopBar("Stock Movement Logs", showBack = true, onBack = back) }) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text("Recent Movements".uppercase(), fontSize = 11.sp, color = Muted, fontWeight = FontWeight.Medium, letterSpacing = 1.8.sp)
                if (filterType != null && filterValue != "All") {
                    Text("Filter: $filterType ($filterValue)", fontSize = 10.sp, color = Blue, fontWeight = FontWeight.Bold)
                }
            }
            StatusChip(
                if (filterType != null && filterValue != "All") "Filter: $filterValue" else "Filter",
                if (filterType != null && filterValue != "All") ChipVariant.Blue else ChipVariant.Outline,
                small = true,
                modifier = Modifier.clickable { showFilterDialog = true }
            )
        }
        filteredLogs.forEach { log -> MovementLogCard(log) { navigate(Screen.ProductDetail) } }
    }

    if (showFilterDialog) {
        val isSubStep = filterType != null
        val title = if (!isSubStep) "Select Filter Type" else "Select $filterType"
        val options = if (!isSubStep) {
            listOf("Branch", "Status")
        } else {
            when (filterType) {
                "Branch" -> listOf("All", "Lipa", "San Pablo")
                "Status" -> listOf("All", "Bottle Present", "Bottle Missing", "Bottle Returned", "Restocked", "Manually Verified", "Needs Review")
                else -> emptyList()
            }
        }

        SelectionDialog(
            title = title,
            options = options,
            current = if (!isSubStep) "" else filterValue,
            onDismiss = { 
                showFilterDialog = false
                filterType = null 
            },
            onBack = if (isSubStep) { { filterType = null } } else null,
            onSelect = { selected ->
                if (!isSubStep) {
                    filterType = selected
                } else {
                    filterValue = selected
                    showFilterDialog = false
                    if (selected == "All") filterType = null
                }
            }
        )
    }
}

@Composable
private fun MovementLogCard(log: MovementLog, onClick: () -> Unit) {
    val label = when (log.status) { "present" -> "Bottle Present"; "missing" -> "Bottle Missing"; "returned" -> "Bottle Returned"; "restocked" -> "Restocked"; "verified" -> "Manually Verified"; else -> "Needs Review" }
    val variant = when (log.status) { "missing" -> ChipVariant.Critical; "needs_review" -> ChipVariant.Warning; else -> ChipVariant.Normal }
    Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(Color.White).border(1.dp, BorderGray.copy(alpha = 0.5f), RoundedCornerShape(18.dp)).clickable(onClick = onClick).padding(16.dp)) {
        Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(if (variant == ChipVariant.Critical) RedBg else if (variant == ChipVariant.Warning) YellowBg else GreenBg), contentAlignment = Alignment.Center) { Text(if (log.status == "missing") "!" else "✓") }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Column { Text(label, fontSize = 14.sp, fontWeight = FontWeight.Medium); Text(log.productName, fontSize = 14.sp, fontWeight = FontWeight.SemiBold) }; StatusChip(log.quantity.toString(), variant, small = true) }
            Text("${log.shelfArea} • ${log.branch} • ${log.timestamp}", fontSize = 12.sp, color = Muted, modifier = Modifier.padding(top = 8.dp))
        }
    }
}

@Composable
fun FanActivityLogsScreen(viewModel: MainViewModel, back: () -> Unit) {
    val logs by viewModel.fanLogs.collectAsState()
    
    var filterType by remember { mutableStateOf<String?>(null) } // "Branch", "Status", or null
    var filterValue by remember { mutableStateOf("All") }
    var showFilterDialog by remember { mutableStateOf(false) }

    val filteredLogs = logs.filter { log ->
        when (filterType) {
            "Branch" -> filterValue == "All" || log.branch.equals(filterValue, ignoreCase = true)
            "Status" -> {
                val statusLabel = when (log.status) {
                    "active" -> "Currently Running"
                    "manual_stop" -> "Manually Stopped"
                    else -> "Auto Stopped"
                }
                filterValue == "All" || statusLabel == filterValue
            }
            else -> true
        }
    }

    ScrollScreen(background = AppBg, topBar = { TopBar("Fan Activity Logs", showBack = true, onBack = back) }) {
        AppCard(background = CardBg) { Text("Cooling system activity records", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, fontSize = 14.sp, color = Muted); Text("Fan activates when temperature exceeds 25°C", modifier = Modifier.fillMaxWidth().padding(top = 4.dp), textAlign = TextAlign.Center, fontSize = 12.sp, color = LightMuted) }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text("Recent Activity".uppercase(), fontSize = 11.sp, color = Muted, fontWeight = FontWeight.Medium, letterSpacing = 1.8.sp)
                if (filterType != null && filterValue != "All") {
                    Text("Filter: $filterType ($filterValue)", fontSize = 10.sp, color = Blue, fontWeight = FontWeight.Bold)
                }
            }
            StatusChip(
                if (filterType != null && filterValue != "All") "Filter: $filterValue" else "Filter",
                if (filterType != null && filterValue != "All") ChipVariant.Blue else ChipVariant.Outline,
                small = true,
                modifier = Modifier.clickable { showFilterDialog = true }
            )
        }
        filteredLogs.forEach { FanActivityCard(it) }
    }

    if (showFilterDialog) {
        val isSubStep = filterType != null
        val title = if (!isSubStep) "Select Filter Type" else "Select $filterType"
        val options = if (!isSubStep) {
            listOf("Branch", "Status")
        } else {
            when (filterType) {
                "Branch" -> listOf("All", "Lipa", "San Pablo")
                "Status" -> listOf("All", "Currently Running", "Manually Stopped", "Auto Stopped")
                else -> emptyList()
            }
        }

        SelectionDialog(
            title = title,
            options = options,
            current = if (!isSubStep) "" else filterValue,
            onDismiss = { 
                showFilterDialog = false
                filterType = null 
            },
            onBack = if (isSubStep) { { filterType = null } } else null,
            onSelect = { selected ->
                if (!isSubStep) {
                    filterType = selected
                } else {
                    filterValue = selected
                    showFilterDialog = false
                    if (selected == "All") filterType = null
                }
            }
        )
    }
}

@Composable
fun FanActivityCard(log: FanActivity) {
    val label = when (log.status) { "active" -> "Currently Running"; "manual_stop" -> "Manually Stopped"; else -> "Auto Stopped" }
    val variant = if (log.status == "manual_stop") ChipVariant.Warning else ChipVariant.Normal
    AppCard(background = Color.White, radius = 18.dp) {
        Row(verticalAlignment = Alignment.Top) { Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(if (log.status == "active") BlueBg else SoftGray), contentAlignment = Alignment.Center) { Text("☁") }; Spacer(Modifier.width(12.dp)); Column(modifier = Modifier.weight(1f)) { Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text(label, fontSize = 14.sp, fontWeight = FontWeight.Medium); StatusChip(log.status.replace("_", " "), variant, small = true) }; Text("${log.branch} • ${log.shelfArea}", fontSize = 12.sp, color = Muted, modifier = Modifier.padding(top = 4.dp)) } }
        Spacer(Modifier.height(12.dp)); ThinDivider(); Spacer(Modifier.height(10.dp))
        DetailRow("Trigger Temp:", "${log.triggerTemperature}°C", Red); DetailRow("Started:", log.startTime); if (log.stopTime != null) DetailRow("Stopped:", log.stopTime); ThinDivider(); DetailRow("Total Duration", log.duration)
        if (log.status == "active") Text("● Fan is currently running", fontSize = 12.sp, color = Blue, fontWeight = FontWeight.Medium, modifier = Modifier.padding(top = 8.dp))
    }
}

@Composable
fun HelpGuideScreen(back: () -> Unit) {
    var expanded by remember { mutableStateOf("needs_review") }
    val topics = listOf(
        HelpTopic("needs_review", "Understanding \"Needs Review\"", "Learn what this status means and how to handle it", listOf("When you see 'Needs Review', it means the smart shelf detected something unclear or unexpected.", "This could be due to poor lighting, bottles placed incorrectly, or new products not yet in the system.", "To resolve: Go to the shelf, check the bottles manually, and confirm the actual count in the app.", "You can also take a new photo to help the system learn better.")),
        HelpTopic("verify_shelf", "Verifying Shelf Results", "How to confirm what the camera detected", listOf("After each shelf check, the app shows you how many bottles are on the shelf.", "Compare this number (Shelf) with what you see on the shelf.", "If the numbers match the system record (System), tap 'Confirm' to accept the result.", "If they don't match, tap 'Needs Review' and count manually.", "Always verify important changes like restocks or missing bottles.")),
        HelpTopic("schedule", "Changing Shelf Check Schedule", "Adjust how often the system checks inventory", listOf("Go to Settings → Shelf Check Schedule.", "You can choose how often the camera checks the shelf (every 1, 2, 4, or 8 hours).", "For busy stores, check more often (every 1-2 hours).", "For slower stores, every 4-8 hours is enough.", "Changes apply immediately after you save.")),
        HelpTopic("temperature", "Responding to Temperature Alerts", "What to do when the shelf gets too hot", listOf("Perfumes should be stored below 25°C to maintain quality.", "When temperature goes above 25°C, the cooling fan turns on automatically.", "If you get an alert, check that the fan is working (you'll hear it running).", "Make sure nothing is blocking the fan vents.", "If temperature stays high, move bottles to a cooler area temporarily.")),
        HelpTopic("cloud_backup", "Handling Cloud Backup Checks", "When the local camera needs cloud help", listOf("Sometimes the camera on the shelf can't get a clear result (poor light, unclear image).", "When this happens, the photo is sent to the cloud for better processing.", "You'll get a notification when cloud processing is done.", "Review the cloud result and confirm if it looks correct.", "If still unclear, you can manually count and update the inventory."))
    )
    ScrollScreen(background = AppBg, topBar = { TopBar("Help & Guide", showBack = true, onBack = back) }) {
        AppCard(background = CardBg) { Text("Quick Help Guide", fontSize = 16.sp, fontWeight = FontWeight.SemiBold); Text("Simple instructions for using the smart shelf monitoring app.", fontSize = 14.sp, color = Muted, modifier = Modifier.padding(top = 4.dp)) }
        Text("Common Topics".uppercase(), fontSize = 11.sp, color = Muted, fontWeight = FontWeight.Medium, letterSpacing = 1.8.sp)
        topics.forEach { topic -> HelpTopicCard(topic, expanded == topic.id, onClick = { expanded = if (expanded == topic.id) "" else topic.id }) }
        AppCard(background = Color.White) { Text("Need More Help?", fontSize = 14.sp, fontWeight = FontWeight.SemiBold); Text("Contact your system administrator for technical support or hardware issues.", fontSize = 14.sp, color = Muted, modifier = Modifier.padding(top = 6.dp, bottom = 14.dp)); AppButton("Contact Support") }
    }
}

@Composable
private fun HelpTopicCard(topic: HelpTopic, expanded: Boolean, onClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(Color.White).border(1.dp, BorderGray.copy(alpha = 0.5f), RoundedCornerShape(18.dp))) {
        Row(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) { Text(topic.title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold); Text(topic.description, fontSize = 12.sp, color = Muted, modifier = Modifier.padding(top = 2.dp)) }
            Text(if (expanded) "−" else "+", fontSize = 24.sp, color = Muted)
        }
        if (expanded) {
            ThinDivider()
            Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(16.dp)) { topic.paragraphs.forEachIndexed { index, paragraph -> Row { Text("${index + 1}.", fontSize = 14.sp, color = Muted, modifier = Modifier.width(24.dp)); Text(paragraph, fontSize = 14.sp, color = TextBlack, lineHeight = 20.sp) } } }
        }
    }
}

@Composable
fun SystemActivityLogsScreen(viewModel: MainViewModel, back: () -> Unit) {
    val activities by viewModel.systemLogs.collectAsState()
    
    var filterType by remember { mutableStateOf<String?>(null) } // "Branch", "Event Type", or null
    var filterValue by remember { mutableStateOf("All") }
    var showFilterDialog by remember { mutableStateOf(false) }

    val filteredActivities = activities.filter { activity ->
        when (filterType) {
            "Branch" -> filterValue == "All" || activity.branch.equals(filterValue, ignoreCase = true)
            "Event Type" -> {
                val typeLabel = when (activity.type) {
                    "user_login" -> "User Login"
                    "product_update" -> "Product Update"
                    "schedule_change" -> "Schedule Change"
                    "shelf_check" -> "Shelf Check"
                    "inventory_update" -> "Inventory Update"
                    "temperature_alert" -> "Temperature Alert"
                    "fan_activation" -> "Fan Activation"
                    "cloud_backup" -> "Cloud Backup"
                    else -> "Synchronization"
                }
                filterValue == "All" || typeLabel == filterValue
            }
            else -> true
        }
    }

    ScrollScreen(background = AppBg, topBar = { TopBar("System Activity Logs", showBack = true, onBack = back) }) {
        AppCard(background = CardBg) { Text("System events and user actions", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, fontSize = 14.sp, color = Muted); Text("Track all important activities across branches", modifier = Modifier.fillMaxWidth().padding(top = 4.dp), textAlign = TextAlign.Center, fontSize = 12.sp, color = LightMuted) }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text("Recent Activity".uppercase(), fontSize = 11.sp, color = Muted, fontWeight = FontWeight.Medium, letterSpacing = 1.8.sp)
                if (filterType != null && filterValue != "All") {
                    Text("Filter: $filterType ($filterValue)", fontSize = 10.sp, color = Blue, fontWeight = FontWeight.Bold)
                }
            }
            StatusChip(
                if (filterType != null && filterValue != "All") "Filter: $filterValue" else "Filter",
                if (filterType != null && filterValue != "All") ChipVariant.Blue else ChipVariant.Outline,
                small = true,
                modifier = Modifier.clickable { showFilterDialog = true }
            )
        }
        filteredActivities.forEach { ActivityCard(it) }
    }

    if (showFilterDialog) {
        val isSubStep = filterType != null
        val title = if (!isSubStep) "Select Filter Type" else "Select $filterType"
        val options = if (!isSubStep) {
            listOf("Branch", "Event Type")
        } else {
            when (filterType) {
                "Branch" -> listOf("All", "Lipa", "San Pablo")
                "Event Type" -> listOf("All", "User Login", "Product Update", "Schedule Change", "Shelf Check", "Inventory Update", "Temperature Alert", "Fan Activation", "Cloud Backup", "Synchronization")
                else -> emptyList()
            }
        }

        SelectionDialog(
            title = title,
            options = options,
            current = if (!isSubStep) "" else filterValue,
            onDismiss = { 
                showFilterDialog = false
                filterType = null 
            },
            onBack = if (isSubStep) { { filterType = null } } else null,
            onSelect = { selected ->
                if (!isSubStep) {
                    filterType = selected
                } else {
                    filterValue = selected
                    showFilterDialog = false
                    if (selected == "All") filterType = null
                }
            }
        )
    }
}

@Composable
private fun ActivityCard(activity: SystemActivity) {
    val label = when (activity.type) { "user_login" -> "User Login"; "product_update" -> "Product Update"; "schedule_change" -> "Schedule Change"; "shelf_check" -> "Shelf Check"; "inventory_update" -> "Inventory Update"; "temperature_alert" -> "Temperature Alert"; "fan_activation" -> "Fan Activation"; "cloud_backup" -> "Cloud Backup"; else -> "Synchronization" }
    val bg = when (activity.type) { "temperature_alert" -> RedBg; "inventory_update" -> GreenBg; "cloud_backup" -> IndigoBg; "sync_event" -> TealBg; "product_update" -> PurpleBg; "schedule_change" -> OrangeBg; "fan_activation" -> BlueBg; else -> SoftGray }
    Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(Color.White).border(1.dp, BorderGray.copy(alpha = 0.5f), RoundedCornerShape(18.dp)).padding(16.dp), verticalAlignment = Alignment.Top) {
        Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(bg), contentAlignment = Alignment.Center) { Text("•") }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text(label.uppercase(), fontSize = 11.sp, color = Muted, fontWeight = FontWeight.Medium, letterSpacing = 1.sp); Text(activity.timestamp.substringAfter("•").trim(), fontSize = 12.sp, color = LightMuted) }
            Text(activity.description, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextBlack, modifier = Modifier.padding(top = 4.dp))
            Text(listOfNotNull(activity.user, activity.branch, activity.timestamp.substringBefore("•").trim()).joinToString(" • "), fontSize = 12.sp, color = Muted, modifier = Modifier.padding(top = 6.dp))
        }
    }
}
