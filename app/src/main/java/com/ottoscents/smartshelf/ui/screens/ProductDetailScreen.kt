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
import androidx.compose.ui.text.style.TextAlign
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
    val userRole by viewModel.userRole.collectAsState()
    
    ScrollScreen(
        topBar = { 
            TopBar(
                title = "Product Detail", 
                showBack = true, 
                onBack = back, 
                right = { 
                    if (userRole == "admin") {
                        Text("✎", modifier = Modifier.clickable { navigate(Screen.ProductForm) }.padding(8.dp), color = Muted, fontSize = 20.sp) 
                    }
                }
            ) 
        }
    ) {
        // 1. Header Section (Centered Title & Category)
        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val fullDisplayName = if (item?.perfumeCode?.isNotEmpty() == true) "Product ${item.perfumeCode}: ${item.name}" else (item?.name ?: "Unknown Product")
            Text(
                text = fullDisplayName,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = TextBlack,
                textAlign = TextAlign.Center,
                lineHeight = 34.sp
            )
            Spacer(Modifier.height(8.dp))
            StatusChip(
                text = (item?.category ?: "General").uppercase(),
                variant = ChipVariant.Black
            )
        }

        // 2. Stock Status Overview
        AppCard(background = Color.White, radius = 28.dp) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text(
                    "CURRENT STOCK",
                    fontSize = 11.sp,
                    letterSpacing = 1.2.sp,
                    color = LightMuted,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(12.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    val detectedVal = if (item?.detected == -1) "?" else item?.detected.toString()
                    Text(
                        text = detectedVal,
                        fontSize = 54.sp,
                        fontWeight = FontWeight.Light,
                        color = TextBlack
                    )
                    Text(
                        text = " / ${item?.recorded ?: 0}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Medium,
                        color = Muted,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }
                Spacer(Modifier.height(12.dp))
                
                val statusVariant = when (item?.status) {
                    "normal" -> ChipVariant.Normal
                    "low" -> ChipVariant.Low
                    "missing" -> ChipVariant.Missing
                    else -> ChipVariant.Review
                }
                val statusLabel = when (item?.status) {
                    "normal" -> "Stock Level: Normal"
                    "low" -> "Critical: Low Stock"
                    "missing" -> "Alert: Item Missing"
                    else -> "Status: Needs Review"
                }
                StatusChip(statusLabel, statusVariant)
            }
        }

        // 3. Information Grid
        Section("Placement & Info") {
            DetailRow("Branch", item?.branch ?: "Unknown")
            ThinDivider()
            DetailRow("Shelf Area", item?.shelf ?: "Area A1")
            ThinDivider()
            DetailRow("Last Updated", item?.lastUpdated ?: "Never")
        }

        // 4. Action Buttons
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            if (userRole == "admin") {
                AppButton("Delete Product", variant = ButtonVariant.Danger, onClick = {
                    item?.let { 
                        viewModel.deleteInventoryItem(it)
                    }
                    back()
                })
            }
        }

        // 5. History Link
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(Color.White)
                .border(1.dp, BorderGray.copy(alpha = 0.5f), RoundedCornerShape(22.dp))
                .clickable { navigate(Screen.ShelfHistory) }
                .padding(18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("📋", fontSize = 16.sp)
                Spacer(Modifier.width(12.dp))
                Text("View Movement History", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextBlack)
            }
            Text("→", fontSize = 16.sp, color = LightMuted)
        }
    }
}
