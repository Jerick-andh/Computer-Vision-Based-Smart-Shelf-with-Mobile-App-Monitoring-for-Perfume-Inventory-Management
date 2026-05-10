package com.ottoscents.smartshelf.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
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
import com.ottoscents.smartshelf.data.InventoryItem
import com.ottoscents.smartshelf.ui.components.*

@Composable
fun InventoryScreen(viewModel: MainViewModel, onSelectItem: (InventoryItem) -> Unit) {
    val inventoryItems by viewModel.inventoryList.collectAsState()
    ScrollScreen(
        topBar = { TopBar("Inventory", right = { AppButton("+", modifier = Modifier.width(44.dp).height(44.dp), onClick = { /* navigate(Screen.ProductForm) logic would go here if needed */ }) }) }
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(value = "", onValueChange = {}, placeholder = { Text("Search perfumes...") }, modifier = Modifier.weight(1f), singleLine = true, shape = RoundedCornerShape(18.dp))
            Box(modifier = Modifier.size(56.dp).clip(RoundedCornerShape(18.dp)).background(SoftGray).border(1.dp, BorderGray, RoundedCornerShape(18.dp)), contentAlignment = Alignment.Center) { Text("☰", color = Muted) }
        }
        Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatusChip("All Items", ChipVariant.Black)
            StatusChip("Low Stock", ChipVariant.Outline)
            StatusChip("Missing", ChipVariant.Outline)
            StatusChip("Needs Review", ChipVariant.Outline)
        }
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            inventoryItems.forEach { item -> InventoryCard(item, onClick = { onSelectItem(item) }) }
        }
    }
}

@Composable
private fun InventoryCard(item: InventoryItem, onClick: () -> Unit) {
    val variant = when (item.status) {
        "normal" -> ChipVariant.Normal
        "missing" -> ChipVariant.Missing
        "low" -> ChipVariant.Low
        else -> ChipVariant.Review
    }
    val label = when (item.status) {
        "normal" -> "Normal"
        "missing" -> "Missing"
        "low" -> "Low Stock"
        else -> "Needs Review"
    }
    AppCard(background = Color.White, radius = 24.dp, onClick = onClick) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
            Column {
                Text(item.name, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = TextBlack)
                Text("${item.category} • ${item.shelf}", fontSize = 12.sp, color = Muted, modifier = Modifier.padding(top = 2.dp))
            }
            StatusChip(label, variant)
        }
        Spacer(Modifier.height(14.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
            Row(horizontalArrangement = Arrangement.spacedBy(22.dp)) {
                CountText("Recorded", item.recorded.toString())
                CountText("Detected", if (item.detected == -1) "?" else item.detected.toString())
            }
            Text(item.lastUpdated, fontSize = 12.sp, color = LightMuted)
        }
    }
}

@Composable
private fun CountText(label: String, value: String) {
    Column {
        Text(label.uppercase(), fontSize = 10.sp, letterSpacing = 1.sp, color = LightMuted, fontWeight = FontWeight.Medium)
        Text(value, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = TextBlack)
    }
}
