package com.ottoscents.smartshelf.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ottoscents.smartshelf.data.HelpTopic
import com.ottoscents.smartshelf.ui.components.*

@Composable
fun HelpGuideScreen(back: () -> Unit) {
    var expanded by remember { mutableStateOf("calibrate_roi") }
    val topics = listOf(
        HelpTopic(
            id = "calibrate_roi", 
            title = "Aligning the Shelf (ROI)", 
            description = "How to calibrate your camera using Region of Interest (ROI)", 
            paragraphs = listOf(
                "Go to the 'Shelf & ROI Tester' screen and tap 'Start Live Calibration'.",
                "A live feed window will open on your edge device with 4 green zone boxes (A, B, C, D).",
                "Physically adjust your camera until each shelf tray area is perfectly centered within its green box.",
                "This ensures the AI accurately identifies which perfume belongs in which area.",
                "Press 'Q' on the edge device to close the calibration window early."
            )
        ),
        HelpTopic(
            id = "inventory_modes", 
            title = "Live vs. Simulated Scans", 
            description = "Choose between demo scenarios and real hardware scans", 
            paragraphs = listOf(
                "When you tap 'Run Inventory Check', you have two powerful options:",
                "1. Simulated Scans (Lipa/San Pablo): These cycle through 6 professional demo scenarios (Full, Sale, Low Stock, etc.).",
                "2. Live Webcam Scan: This snaps a REAL photo from your camera and runs the YOLO AI on it instantly.",
                "Use 'Live Scan' to prove the hardware integration, and 'Simulated' to show how the app handles complex stock changes."
            )
        ),
        HelpTopic(
            id = "edge_connectivity", 
            title = "Monitoring Edge & Cloud Health", 
            description = "Understanding the real-time connectivity lights", 
            paragraphs = listOf(
                "Check the status cards on your Dashboard for two real-time indicators:",
                "● Cloud Link: Green means your app is online. Red means you have no internet/Wi-Fi.",
                "● Edge Status: Green means your simulator is running and 'pinging' the cloud. Red means the hardware is offline.",
                "The 'Last Check' time is dynamic and updates automatically after every successful scan activity."
            )
        ),
        HelpTopic(
            id = "misplaced_logic", 
            title = "Detecting Misplaced Items", 
            description = "How the AI identifies bottles in the wrong area", 
            paragraphs = listOf(
                "The system uses the YOLO model to detect the type of perfume (A, B, C, or D) and its coordinates.",
                "If Perfume A is detected physically sitting in Area D, the app will flag it as 'Misplaced'.",
                "You will receive a purple 📍 alert telling you exactly where the bottle is and where it belongs.",
                "This prevents stock confusion and ensures your shelf layout is always correct."
            )
        ),
        HelpTopic(
            id = "automated_cooling", 
            title = "Automated Climate Control", 
            description = "How the cooling fan simulation keeps perfumes safe", 
            paragraphs = listOf(
                "Perfumes must be stored at safe temperatures (typically below 25°C) to maintain quality.",
                "If the temperature goes above your set threshold, the edge hardware automatically 'activates' the cooling fan.",
                "You can watch the temperature drop in real-time on the Climate Control screen.",
                "The fan turns off automatically once the safe temperature (22°C) is reached, and the event is logged."
            )
        )
    )
    ScrollScreen(background = AppBg, topBar = { TopBar("Help & Guide", showBack = true, onBack = back) }) {
        AppCard(background = CardBg) { Text("Quick Help Guide", fontSize = 16.sp, fontWeight = FontWeight.SemiBold); Text("Instructions for your refined Smart Shelf monitoring system.", fontSize = 14.sp, color = Muted, modifier = Modifier.padding(top = 4.dp)) }
        Text("System Manual".uppercase(), fontSize = 11.sp, color = Muted, fontWeight = FontWeight.Medium, letterSpacing = 1.8.sp)
        topics.forEach { topic -> HelpTopicCard(topic, expanded == topic.id, onClick = { expanded = if (expanded == topic.id) "" else topic.id }) }
        AppCard(background = Color.White) { Text("Demonstration Ready", fontSize = 14.sp, fontWeight = FontWeight.SemiBold); Text("Your Smart Shelf prototype is now fully integrated with AI vision, real-time hardware monitoring, and automated cooling.", fontSize = 14.sp, color = Muted, modifier = Modifier.padding(top = 6.dp, bottom = 14.dp)); AppButton("View System Logs") }
    }
}

@Composable
private fun HelpTopicCard(topic: HelpTopic, expanded: Boolean, onClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(Color.White).border(1.dp, BorderGray.copy(alpha = 0.5f), RoundedCornerShape(18.dp))) {
        Row(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) { Text(topic.title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = if (expanded) Blue else TextBlack); Text(topic.description, fontSize = 12.sp, color = Muted, modifier = Modifier.padding(top = 2.dp)) }
            Text(if (expanded) "−" else "+", fontSize = 24.sp, color = if (expanded) Blue else Muted)
        }
        if (expanded) {
            ThinDivider()
            Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(16.dp)) { topic.paragraphs.forEachIndexed { index, paragraph -> Row { Text("${index + 1}.", fontSize = 14.sp, color = Blue, modifier = Modifier.width(24.dp)); Text(paragraph, fontSize = 14.sp, color = TextBlack, lineHeight = 20.sp) } } }
        }
    }
}
