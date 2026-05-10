package com.ottoscents.smartshelf.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ottoscents.smartshelf.MainViewModel
import com.ottoscents.smartshelf.Screen
import com.ottoscents.smartshelf.data.InventoryItem
import com.ottoscents.smartshelf.ui.components.*

@Composable
fun ProductDetailScreen(item: InventoryItem?, navigate: (Screen) -> Unit, back: () -> Unit) {
    val viewModel: MainViewModel = viewModel()
    val userBranch by viewModel.userBranch.collectAsState()
    
    ScrollScreen(topBar = { TopBar("Product Detail", showBack = true, onBack = back, right = { Text("✎", modifier = Modifier.clickable { navigate(Screen.ProductForm) }.padding(8.dp), color = Muted, fontSize = 20.sp) }) }) {
        Box(modifier = Modifier.fillMaxWidth().aspectRatio(1.35f).clip(RoundedCornerShape(30.dp)).background(SoftGray), contentAlignment = Alignment.Center) {
            Text("No Image", color = LightMuted, fontWeight = FontWeight.Medium)
        }
        Column {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(item?.name ?: "Midnight Oud", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = TextBlack)
                    Text("${item?.category ?: "Otto Scents Classic"} • woody", fontSize = 14.sp, color = Muted, modifier = Modifier.padding(top = 4.dp))
                }
                StatusChip(if (item?.status == "normal") "Normal Stock" else "Needs Review", variant = if (item?.status == "normal") ChipVariant.Normal else ChipVariant.Review)
            }
        }
        AppCard(background = CardBg) {
            DetailRow("Branch", userBranch ?: "San Pablo")
            Spacer(Modifier.height(12.dp)); ThinDivider(); Spacer(Modifier.height(12.dp))
            DetailRow("Shelf Area", item?.shelf ?: "Area A1")
            Spacer(Modifier.height(12.dp)); ThinDivider(); Spacer(Modifier.height(12.dp))
            DetailRow("Bottle Size", "50ml")
            Spacer(Modifier.height(12.dp)); ThinDivider(); Spacer(Modifier.height(12.dp))
            DetailRow("Min. Threshold", "5 bottles")
        }
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp), modifier = Modifier.fillMaxWidth()) {
            CountCard("Recorded", item?.recorded?.toString() ?: "12", Modifier.weight(1f))
            CountCard("Detected", item?.detected?.toString() ?: "12", Modifier.weight(1f), dark = true)
        }
        AppButton("Create Restock", onClick = { navigate(Screen.CreateRestock) })
        
        AppButton("Delete Product", variant = ButtonVariant.Danger, onClick = {
            item?.id?.let { id ->
                viewModel.deleteInventoryItem(id)
            }
            back()
        })

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(SoftGray)
                .border(1.dp, BorderGray.copy(alpha = 0.6f), RoundedCornerShape(18.dp))
                .clickable { navigate(Screen.ShelfHistory) }
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("⌘  Recent Movement History", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextBlack)
            Text("View", fontSize = 12.sp, color = LightMuted)
        }
    }
}

@Composable
private fun CountCard(label: String, value: String, modifier: Modifier = Modifier, dark: Boolean = false) {
    AppCard(modifier = modifier, background = if (dark) Color(0xFF111827) else Color.White, border = if (dark) Color.Transparent else BorderGray) {
        Text(label.uppercase(), fontSize = 11.sp, letterSpacing = 1.sp, color = if (dark) Color(0xFFD1D5DB) else Muted, fontWeight = FontWeight.Medium)
        Text(value, fontSize = 34.sp, fontWeight = FontWeight.Light, color = if (dark) Color.White else TextBlack, modifier = Modifier.padding(top = 6.dp))
    }
}
