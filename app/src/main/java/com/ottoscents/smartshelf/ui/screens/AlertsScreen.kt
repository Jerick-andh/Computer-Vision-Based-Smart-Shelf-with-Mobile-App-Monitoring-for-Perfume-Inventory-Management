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
            val isLowStock = alert.title.startsWith("Low Stock")
            val isOutOfStock = alert.title.startsWith("Out of Stock")
            val isMisplaced = alert.title.startsWith("Misplaced")
            val isTemp = alert.title.contains("Temperature")
            
            val icon = when {
                isLowStock -> "📉"
                isOutOfStock -> "🚫"
                isMisplaced -> "📍"
                isTemp -> "🔥"
                alert.type == "cloud" -> "☁"
                else -> "!"
            }
            
            val iconBg = when {
                isOutOfStock || isTemp -> RedBg
                isLowStock -> YellowBg
                isMisplaced -> PurpleBg
                else -> SoftGray
            }
            
            val iconColor = when {
                isOutOfStock || isTemp -> Red
                isLowStock -> Yellow
                isMisplaced -> Purple
                else -> Muted
            }

            val titleColor = when {
                isOutOfStock || isTemp -> Red
                isLowStock -> Yellow
                isMisplaced -> Purple
                else -> TextBlack
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color.White)
                    .border(1.dp, BorderGray.copy(alpha = 0.5f), RoundedCornerShape(28.dp))
                    .clickable { 
                        if (alert.type == "cloud") navigate(Screen.CloudBackup) 
                        else navigate(Screen.ProductDetail) 
                    }
                    .padding(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(iconBg),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = icon, color = iconColor, fontSize = 20.sp)
                }
                Spacer(Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(alert.title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = titleColor)
                        Text(alert.time, fontSize = 10.sp, color = LightMuted)
                    }
                    Text(
                        text = alert.desc, 
                        fontSize = 12.sp, 
                        color = Muted, 
                        lineHeight = 18.sp, 
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 12.dp)) {
                        val variant = when {
                            isOutOfStock || isTemp -> ChipVariant.Critical
                            isLowStock -> ChipVariant.Low
                            isMisplaced -> ChipVariant.Misplaced
                            else -> ChipVariant.Outline
                        }
                        StatusChip(alert.branch, variant = variant)
                        StatusChip("View Details", ChipVariant.Outline, small = true)
                    }
                }
            }
        }
    }
}
