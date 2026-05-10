package com.ottoscents.smartshelf.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ottoscents.smartshelf.MainViewModel
import com.ottoscents.smartshelf.Screen
import com.ottoscents.smartshelf.ui.components.*

@Composable
fun AlertsScreen(viewModel: MainViewModel, navigate: (Screen) -> Unit) {
    val alerts by viewModel.alertsList.collectAsState()
    ScrollScreen(topBar = { TopBar("Alerts") }) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text("Recent Notifications", fontSize = 14.sp, fontWeight = FontWeight.SemiBold); Text("Mark all read", fontSize = 12.sp, color = Muted, fontWeight = FontWeight.Medium) }
        alerts.forEach { alert ->
            val variant = when (alert.type) { "critical" -> ChipVariant.Critical; "warning" -> ChipVariant.Warning; else -> ChipVariant.Normal }
            Row(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(28.dp)).background(Color.White).border(1.dp, BorderGray.copy(alpha = 0.5f), RoundedCornerShape(28.dp)).clickable { if (alert.type == "cloud") navigate(Screen.CloudBackup) else navigate(Screen.ProductDetail) }.padding(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(if (variant == ChipVariant.Critical) RedBg else if (variant == ChipVariant.Warning) OrangeBg else SoftGray), contentAlignment = Alignment.Center) { Text(if (alert.type == "cloud") "☁" else "!", color = if (variant == ChipVariant.Critical) Red else if (variant == ChipVariant.Warning) Orange else Muted) }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text(alert.title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold); Text(alert.time, fontSize = 10.sp, color = LightMuted) }
                    Text(alert.desc, fontSize = 12.sp, color = Muted, lineHeight = 18.sp, modifier = Modifier.padding(top = 4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 12.dp)) { StatusChip(alert.branch); StatusChip("View", ChipVariant.Black, small = true) }
                }
            }
        }
    }
}
