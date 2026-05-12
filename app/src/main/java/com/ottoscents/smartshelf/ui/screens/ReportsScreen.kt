package com.ottoscents.smartshelf.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.ottoscents.smartshelf.ui.components.*

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
