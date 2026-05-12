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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ottoscents.smartshelf.data.HistoryItem
import com.ottoscents.smartshelf.ui.components.*

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
