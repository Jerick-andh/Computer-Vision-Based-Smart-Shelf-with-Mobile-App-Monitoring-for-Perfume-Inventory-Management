package com.ottoscents.smartshelf.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
            CountCard("Expected", "15", Modifier.weight(1f))
            CountCard("Detected", "14", Modifier.weight(1f), dark = false)
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
    ScrollScreen(topBar = { TopBar("Settings") }) {
        AppCard(background = Color(0xFF111827), border = Color.Transparent) {
            val userBranch by viewModel.userBranch.collectAsState()
            val email = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.email ?: "Unknown User"
            Text(email, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("${userBranch ?: "Unknown"} Branch", color = Color(0xFFD1D5DB), fontSize = 13.sp, modifier = Modifier.padding(top = 4.dp))
        }
        SettingSection("System") {
            SettingRow("Capture Schedule", "Every hour") { navigate(Screen.Schedule) }
            SettingRow("Temperature Threshold", "25°C") { navigate(Screen.Temperature) }
            SettingRow("Low Stock Threshold", "5 bottles") {}
            SettingRow("Cloud Sync Status", "Connected") {}
        }
        SettingSection("Reports") { SettingRow("Reports", null) { navigate(Screen.Reports) }; SettingRow("System Activity Logs", null) { navigate(Screen.SystemLogs) } }
        SettingSection("Help & Guides") { SettingRow("Help & Guides", null) { navigate(Screen.Help) } }
        AppButton("Logout", variant = ButtonVariant.Outline, onClick = onLogout)
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
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp), modifier = Modifier.fillMaxWidth()) { CountCard("Bottles Detected", "11", Modifier.weight(1f)); CountCard("Confidence", "87%", Modifier.weight(1f)) }
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
    ScrollScreen(background = AppBg, topBar = { TopBar("Stock Movement Logs", showBack = true, onBack = back) }) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text("Recent Movements".uppercase(), fontSize = 11.sp, color = Muted, fontWeight = FontWeight.Medium, letterSpacing = 1.8.sp); StatusChip("Filter", ChipVariant.Outline, small = true) }
        logs.forEach { log -> MovementLogCard(log) { navigate(Screen.ProductDetail) } }
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
fun RestockManagementScreen(viewModel: MainViewModel, navigate: (Screen) -> Unit, back: () -> Unit) {
    var tab by remember { mutableStateOf("requests") }
    val data by viewModel.restockRequests.collectAsState()
    val filtered = data.filter { when (tab) { "requests" -> it.status == "pending"; "transfers" -> it.status == "in_transit"; "received" -> it.status == "received"; else -> it.status == "completed" } }
    ScrollScreen(background = AppBg, topBar = { TopBar("Restock Management", showBack = true, onBack = back, right = { StatusChip("New Request", ChipVariant.Black, small = true, modifier = Modifier.clickable { navigate(Screen.CreateRestock) }) }) }) {
        Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(CardBg).border(1.dp, BorderGray.copy(alpha = 0.5f), RoundedCornerShape(18.dp)).padding(6.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            TabPill("Requests", tab == "requests", Modifier.weight(1f)) { tab = "requests" }
            TabPill("Transit", tab == "transfers", Modifier.weight(1f)) { tab = "transfers" }
            TabPill("Received", tab == "received", Modifier.weight(1f)) { tab = "received" }
            TabPill("Done", tab == "completed", Modifier.weight(1f)) { tab = "completed" }
        }
        filtered.forEach { item -> RestockCard(item, onClick = { navigate(Screen.ProductDetail) }, verify = { navigate(Screen.ReceivingVerification) }) }
    }
}

@Composable
private fun TabPill(label: String, active: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(modifier = modifier.height(42.dp).clip(RoundedCornerShape(14.dp)).background(if (active) Color.White else Color.Transparent).clickable(onClick = onClick), contentAlignment = Alignment.Center) {
        Text(label, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = if (active) TextBlack else Muted)
    }
}

@Composable
private fun RestockCard(item: RestockItem, onClick: () -> Unit, verify: () -> Unit) {
    val label = when (item.status) { "pending" -> "Pending Request"; "in_transit" -> "In Transit"; "received" -> "Received"; else -> "Completed" }
    val variant = if (item.status == "pending") ChipVariant.Warning else ChipVariant.Normal
    AppCard(background = Color.White, radius = 18.dp, onClick = onClick) {
        Row(verticalAlignment = Alignment.Top) {
            Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(if (item.status == "pending") YellowBg else if (item.status == "in_transit") BlueBg else if (item.status == "received") GreenBg else SoftGray), contentAlignment = Alignment.Center) { Text(if (item.status == "in_transit") "⇄" else "□") }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) { Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text(item.productName, fontSize = 14.sp, fontWeight = FontWeight.SemiBold); StatusChip(item.quantity.toString(), variant, small = true) }; Text(label, fontSize = 12.sp, color = Muted, modifier = Modifier.padding(top = 3.dp)) }
        }
        Spacer(Modifier.height(12.dp)); ThinDivider(); Spacer(Modifier.height(10.dp))
        Text("From: ${item.fromBranch}  ⇄  To: ${item.toBranch}", fontSize = 12.sp, color = TextBlack, fontWeight = FontWeight.Medium)
        Text("Requested by: ${item.requestedBy}        ${item.requestedDate}", fontSize = 12.sp, color = Muted, modifier = Modifier.padding(top = 5.dp))
        if (item.estimatedArrival != null) Text("Est. arrival: ${item.estimatedArrival}", fontSize = 12.sp, color = Blue, fontWeight = FontWeight.Medium, modifier = Modifier.padding(top = 5.dp))
        if (item.completedDate != null) Text("Completed: ${item.completedDate}", fontSize = 12.sp, color = Muted, modifier = Modifier.padding(top = 5.dp))
        if (item.status == "received") { Spacer(Modifier.height(10.dp)); AppButton("Verify & Complete", onClick = verify) }
    }
}

@Composable
fun CreateRestockRequestScreen(back: () -> Unit) {
    var source by remember { mutableStateOf("") }; var destination by remember { mutableStateOf("") }; var product by remember { mutableStateOf("") }; var quantity by remember { mutableStateOf("") }; var shelf by remember { mutableStateOf("") }; var notes by remember { mutableStateOf("") }
    val invalid = source.isNotBlank() && destination.isNotBlank() && source == destination
    ScrollScreen(background = AppBg, topBar = { TopBar("Create Restock Request", showBack = true, onBack = back) }) {
        AppCard(background = CardBg) { Text("⇄  Branch Transfer Request", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, fontSize = 14.sp, color = Muted, fontWeight = FontWeight.Medium) }
        Section("Source & Destination") { FormField("From Branch", source, "Select source") { source = it }; ThinDivider(); FormField("To Branch", destination, "Select destination") { destination = it } }
        Section("Product Details") { FormField("Product", product, "Select product") { product = it }; ThinDivider(); FormField("Quantity", quantity, "0", numeric = true) { quantity = it }; ThinDivider(); FormField("Target Shelf Area", shelf, "Select area") { shelf = it } }
        Section("Additional Information") { OutlinedTextField(value = notes, onValueChange = { notes = it }, placeholder = { Text("Optional notes or special instructions...") }, modifier = Modifier.fillMaxWidth().height(110.dp), shape = RoundedCornerShape(18.dp)) }
        if (invalid) AppCard(background = RedBg, border = Color(0xFFFECACA), radius = 16.dp) { Text("Source and destination branches must be different", fontSize = 14.sp, color = Red) }
        AppButton("Submit Request", enabled = !invalid && source.isNotBlank() && destination.isNotBlank() && product.isNotBlank() && quantity.isNotBlank() && shelf.isNotBlank(), onClick = back)
        TextButton(onClick = back, modifier = Modifier.fillMaxWidth()) { Text("Cancel", color = Muted) }
    }
}

@Composable
fun ReceivingVerificationScreen(navigate: (Screen) -> Unit, back: () -> Unit) {
    var confirming by remember { mutableStateOf(false) }
    val expected = 15
    val detected = 15
    val difference = detected - expected
    val title = if (difference == 0) "Quantities Match" else if (kotlin.math.abs(difference) <= 2) "Review Required" else "Quantity Mismatch"
    val desc = if (difference == 0) "The detected quantity matches the expected restock amount." else "Manual verification required."
    val color = if (difference == 0) Green else if (kotlin.math.abs(difference) <= 2) Yellow else Red
    val bg = if (difference == 0) GreenBg else if (kotlin.math.abs(difference) <= 2) YellowBg else RedBg
    ScrollScreen(background = AppBg, topBar = { TopBar("Receiving Verification", showBack = true, onBack = back) }) {
        AppCard(background = bg, border = color.copy(alpha = 0.25f)) { Text("✓", fontSize = 42.sp, color = color, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center); Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = color, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()); Text(desc, fontSize = 14.sp, color = Muted, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) }
        AppCard(background = CardBg) { Text("Ocean Breeze", fontSize = 16.sp, fontWeight = FontWeight.SemiBold); Text("Lipa → San Pablo", fontSize = 12.sp, color = Muted, modifier = Modifier.padding(top = 3.dp)); Spacer(Modifier.height(12.dp)); DetailRow("Shelf Area", "Area B3"); ThinDivider(); DetailRow("Requested By", "Maria Santos"); ThinDivider(); DetailRow("Transfer Date", "May 1, 2026") }
        Text("Quantity Comparison".uppercase(), fontSize = 11.sp, color = Muted, fontWeight = FontWeight.Medium, letterSpacing = 1.8.sp)
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp), modifier = Modifier.fillMaxWidth()) { CountCard("Expected", expected.toString(), Modifier.weight(1f)); CountCard("Detected", detected.toString(), Modifier.weight(1f), dark = true) }
        AppCard(background = Color.White) { Text("Shelf Check Result", fontSize = 14.sp, fontWeight = FontWeight.Medium); Spacer(Modifier.height(10.dp)); DetailRow("Check Method", "AI Detection"); ThinDivider(); DetailRow("Confidence", "95%", Green); ThinDivider(); DetailRow("Check Time", "2 mins ago") }
        AppButton(if (confirming) "Confirming..." else "Confirm & Complete", enabled = !confirming, onClick = { confirming = true; back() })
        AppButton("Manual Recount", variant = ButtonVariant.Outline, onClick = { navigate(Screen.ShelfAreaDetail) })
        TextButton(onClick = back, modifier = Modifier.fillMaxWidth()) { Text("Cancel", color = Muted) }
    }
}

@Composable
fun FanActivityLogsScreen(viewModel: MainViewModel, back: () -> Unit) {
    val logs by viewModel.fanLogs.collectAsState()
    ScrollScreen(background = AppBg, topBar = { TopBar("Fan Activity Logs", showBack = true, onBack = back) }) {
        AppCard(background = CardBg) { Text("Cooling system activity records", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, fontSize = 14.sp, color = Muted); Text("Fan activates when temperature exceeds 25°C", modifier = Modifier.fillMaxWidth().padding(top = 4.dp), textAlign = TextAlign.Center, fontSize = 12.sp, color = LightMuted) }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text("Recent Activity".uppercase(), fontSize = 11.sp, color = Muted, fontWeight = FontWeight.Medium, letterSpacing = 1.8.sp); StatusChip("Filter", ChipVariant.Outline, small = true) }
        logs.forEach { FanActivityCard(it) }
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
        HelpTopic("verify_shelf", "Verifying Shelf Results", "How to confirm what the camera detected", listOf("After each shelf check, the app shows you how many bottles were detected.", "Compare this number with what you see on the shelf.", "If the numbers match, tap 'Confirm' to accept the result.", "If they don't match, tap 'Needs Review' and count manually.", "Always verify important changes like restocks or missing bottles.")),
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
    ScrollScreen(background = AppBg, topBar = { TopBar("System Activity Logs", showBack = true, onBack = back) }) {
        AppCard(background = CardBg) { Text("System events and user actions", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, fontSize = 14.sp, color = Muted); Text("Track all important activities across branches", modifier = Modifier.fillMaxWidth().padding(top = 4.dp), textAlign = TextAlign.Center, fontSize = 12.sp, color = LightMuted) }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text("Recent Activity".uppercase(), fontSize = 11.sp, color = Muted, fontWeight = FontWeight.Medium, letterSpacing = 1.8.sp); StatusChip("Filter", ChipVariant.Outline, small = true) }
        activities.forEach { ActivityCard(it) }
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
