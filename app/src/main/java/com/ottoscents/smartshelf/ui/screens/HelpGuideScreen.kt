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
