package com.ottoscents.smartshelf.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ottoscents.smartshelf.MainViewModel
import com.ottoscents.smartshelf.Screen
import com.ottoscents.smartshelf.ui.components.*

@Composable
fun HomeScreen(viewModel: MainViewModel, navigate: (Screen) -> Unit) {
    val branch by viewModel.userBranch.collectAsState()
    val currentTemp by viewModel.currentTemperature.collectAsState()
    val fanActive by viewModel.isFanActive.collectAsState()
    val inventoryItems by viewModel.inventoryList.collectAsState()
    
    val totalBottles = inventoryItems.sumOf { it.detected.coerceAtLeast(0) }
    val needsReviewCount = inventoryItems.count { it.status == "needs_review" }

    ScrollScreen(
        topBar = {
            TopBar(
                title = "Dashboard",
                right = {
                    Text(branch ?: "Unknown", modifier = Modifier.clip(RoundedCornerShape(999.dp)).background(Color(0xFFF3F4F6)).padding(horizontal = 16.dp, vertical = 8.dp), fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }
            )
        }
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp), modifier = Modifier.fillMaxWidth()) {
            AppCard(modifier = Modifier.weight(1f), onClick = { navigate(Screen.Inventory) }) {
                Text(totalBottles.toString(), fontSize = 40.sp, fontWeight = FontWeight.Light, color = TextBlack)
                Text("Bottles Detected", fontSize = 14.sp, color = Muted, fontWeight = FontWeight.Medium)
            }
            AppCard(modifier = Modifier.weight(1f), background = Color(0xFF111827), border = Color.Transparent, onClick = { navigate(Screen.Shelf) }) {
                Text(needsReviewCount.toString(), fontSize = 40.sp, fontWeight = FontWeight.Light, color = Color.White)
                Text("⚠ Needs Review", fontSize = 14.sp, color = Color(0xFFD1D5DB), fontWeight = FontWeight.Medium)
            }
        }

        AppCard(onClick = { navigate(Screen.Temperature) }) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("🌡 Shelf Climate", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Muted)
                StatusChip(if (currentTemp > 25) "High" else "Normal", variant = if (currentTemp > 25) ChipVariant.Warning else ChipVariant.Normal)
            }
            Spacer(Modifier.height(14.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(String.format("%.1f", currentTemp), fontSize = 32.sp, fontWeight = FontWeight.Light, color = TextBlack)
                    Text("°C", fontSize = 16.sp, color = LightMuted, modifier = Modifier.padding(bottom = 4.dp, start = 3.dp))
                }
                Text(if (fanActive) "☁ Fan ON" else "☁ Fan Off", color = if (fanActive) Blue else Muted, fontSize = 14.sp, fontWeight = if (fanActive) FontWeight.Bold else FontWeight.Normal)
            }
        }

        AppCard(background = SoftGray) {
            DetailRow("◷  Last Check", "10 mins ago")
            Spacer(Modifier.height(12.dp)); ThinDivider(); Spacer(Modifier.height(12.dp))
            DetailRow("☁  Cloud Sync", "● Synced")
        }

        Text("Quick Actions", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextBlack, modifier = Modifier.padding(top = 10.dp, start = 4.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            ActionCard("□", "Inventory", Modifier.weight(1f)) { navigate(Screen.Inventory) }
            ActionCard("◉", "Shelf Monitor", Modifier.weight(1f)) { navigate(Screen.Shelf) }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            ActionCard("+", "Add Product", Modifier.weight(1f)) { navigate(Screen.ProductForm) }
            ActionCard("⇄", "Restock", Modifier.weight(1f)) { navigate(Screen.Restock) }
        }
    }
}

@Composable
private fun ActionCard(icon: String, label: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Column(
        modifier = modifier
            .height(112.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White)
            .border(1.dp, BorderGray, RoundedCornerShape(24.dp))
            .clickable(onClick = onClick)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(SoftGray), contentAlignment = Alignment.Center) {
            Text(icon, fontSize = 24.sp, color = Color.Black)
        }
        Spacer(Modifier.height(10.dp))
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Muted)
    }
}
