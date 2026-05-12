package com.ottoscents.smartshelf.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ottoscents.smartshelf.MainViewModel
import com.ottoscents.smartshelf.ui.components.*

@Composable
fun ScheduleScreen(viewModel: MainViewModel, back: () -> Unit) {
    val userRole by viewModel.userRole.collectAsState()
    val isAdmin = userRole == "admin"
    var mode by remember { mutableStateOf("interval") }

    ScrollScreen(topBar = { TopBar("Capture Schedule", showBack = true, onBack = back) }) {
        if (isAdmin) {
            // --- ADMIN ONLY: CONFIGURATION UI ---
            Text("The shelf will take a photo based on this schedule to update inventory.", fontSize = 14.sp, color = Muted, lineHeight = 21.sp)
            Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(SoftGray).border(1.dp, BorderGray.copy(alpha = 0.5f), RoundedCornerShape(18.dp)).padding(4.dp)) {
                TabPill("Interval", mode == "interval", Modifier.weight(1f)) { mode = "interval" }
                TabPill("Specific Times", mode == "specific", Modifier.weight(1f)) { mode = "specific" }
            }
            if (mode == "interval") {
                listOf("Every 30 minutes", "Every hour", "Every 2 hours", "Every 3 hours").forEachIndexed { i, opt ->
                    Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).border(1.dp, BorderGray, RoundedCornerShape(18.dp)).padding(14.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(opt, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        RadioButton(selected = i == 1, onClick = {})
                    }
                }
                Text("+ Custom interval", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Muted, modifier = Modifier.padding(14.dp))
            } else {
                listOf("09:00 AM", "12:00 PM", "03:00 PM", "06:00 PM").forEach { time ->
                    Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).border(1.dp, BorderGray, RoundedCornerShape(18.dp)).background(Color.White).padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("◷  $time", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        Text("⌫", color = LightMuted)
                    }
                }
                Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).border(1.dp, BorderGray, RoundedCornerShape(18.dp)).padding(16.dp), contentAlignment = Alignment.Center) { Text("+  Add Time", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Muted) }
            }
            AppButton("Save Schedule Settings")
            Spacer(Modifier.height(10.dp))
            ThinDivider()
        } else {
            // --- STAFF ONLY: STATUS VIEW ---
            AppCard(background = BlueBg, border = Blue.copy(alpha = 0.1f)) {
                Text("Current Auto-Schedule", fontSize = 12.sp, color = Blue, fontWeight = FontWeight.Bold)
                Text("The system is set to automatically check every 1 hour.", fontSize = 16.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(top = 4.dp))
            }
        }
        
        // --- VISIBLE TO BOTH: MANUAL TRIGGER ---
        Spacer(Modifier.height(10.dp))
        Text("Manual Trigger", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextBlack, modifier = Modifier.padding(start = 4.dp))
        Text("Force an immediate shelf scan and inventory update.", fontSize = 13.sp, color = Muted, modifier = Modifier.padding(start = 4.dp))
        
        AppButton("Run Inventory Now", variant = ButtonVariant.Secondary) {
            viewModel.runInventoryCheck()
        }
    }
}

@Composable
private fun TabPill(label: String, active: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(modifier = modifier.height(42.dp).clip(RoundedCornerShape(14.dp)).background(if (active) Color.White else Color.Transparent).clickable(onClick = onClick), contentAlignment = Alignment.Center) {
        Text(label, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = if (active) TextBlack else Muted)
    }
}
