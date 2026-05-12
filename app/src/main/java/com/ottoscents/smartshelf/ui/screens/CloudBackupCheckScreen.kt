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
import com.ottoscents.smartshelf.Screen
import com.ottoscents.smartshelf.ui.components.*

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
private fun CountCard(label: String, value: String, modifier: Modifier = Modifier, dark: Boolean = false) {
    AppCard(modifier = modifier, background = if (dark) Color(0xFF111827) else Color.White, border = if (dark) Color.Transparent else BorderGray) {
        Text(label.uppercase(), fontSize = 11.sp, letterSpacing = 1.sp, color = if (dark) Color(0xFFD1D5DB) else Muted, fontWeight = FontWeight.Medium)
        Text(value, fontSize = 34.sp, fontWeight = FontWeight.Light, color = if (dark) Color.White else TextBlack, modifier = Modifier.padding(top = 6.dp))
    }
}
