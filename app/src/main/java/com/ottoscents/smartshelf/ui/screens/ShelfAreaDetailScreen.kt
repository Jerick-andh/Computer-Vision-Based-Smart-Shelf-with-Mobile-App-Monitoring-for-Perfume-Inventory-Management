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
