package com.ottoscents.smartshelf.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ottoscents.smartshelf.MainViewModel
import com.ottoscents.smartshelf.Screen
import com.ottoscents.smartshelf.data.InventoryItem
import com.ottoscents.smartshelf.ui.components.*

@Composable
fun InventoryScreen(viewModel: MainViewModel, navigate: (Screen) -> Unit, onSelectItem: (InventoryItem) -> Unit) {
    val inventoryItems by viewModel.inventoryList.collectAsState()
    val userRole by viewModel.userRole.collectAsState()
    val selectedBranch by viewModel.selectedInventoryBranch.collectAsState()
    
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All Items") }

    val filteredItems = inventoryItems.filter { item ->
        val matchesSearch = item.name.contains(searchQuery, ignoreCase = true) || 
                          item.category.contains(searchQuery, ignoreCase = true)
        val matchesStatus = when (selectedFilter) {
            "Low Stock" -> item.status == "low"
            "Missing" -> item.status == "missing"
            "Misplaced" -> item.status == "misplaced"
            "Needs Review" -> item.status == "needs_review"
            else -> true
        }
        val matchesBranch = when (selectedBranch) {
            "All Branches" -> true
            else -> item.branch.equals(selectedBranch, ignoreCase = true)
        }
        matchesSearch && matchesStatus && matchesBranch
    }

    ScrollScreen(
        topBar = { 
            TopBar(
                title = "Inventory", 
                right = { 
                    if (userRole == "admin") {
                        AppButton("+", modifier = Modifier.width(44.dp).height(44.dp), onClick = { navigate(Screen.ProductForm) }) 
                    }
                }
            ) 
        }
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = searchQuery, 
                onValueChange = { searchQuery = it }, 
                placeholder = { Text("Search perfumes...") }, 
                modifier = Modifier.weight(1f), 
                singleLine = true, 
                shape = RoundedCornerShape(18.dp)
            )
            Box(modifier = Modifier.size(56.dp).clip(RoundedCornerShape(18.dp)).background(SoftGray).border(1.dp, BorderGray, RoundedCornerShape(18.dp)), contentAlignment = Alignment.Center) { Text("☰", color = Muted) }
        }
        Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val filters = listOf("All Items", "Low Stock", "Missing", "Misplaced", "Needs Review")
            filters.forEach { filter ->
                val isActive = selectedFilter == filter
                StatusChip(
                    text = filter, 
                    variant = if (isActive) ChipVariant.Black else ChipVariant.Outline,
                    modifier = Modifier.clickable { selectedFilter = filter }
                )
            }
        }
        
        Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val branches = listOf("All Branches", "Lipa", "San Pablo")
            branches.forEach { b ->
                val isActive = selectedBranch == b
                StatusChip(
                    text = b,
                    variant = if (isActive) ChipVariant.Black else ChipVariant.Outline,
                    modifier = Modifier.clickable { viewModel.setInventoryBranch(b) }
                )
            }
        }
        
        if (filteredItems.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().padding(top = 40.dp), contentAlignment = Alignment.Center) {
                Text("No perfumes found matching your search.", color = LightMuted, fontSize = 14.sp)
            }
        } else {
            val categories = listOf("Men", "Women")
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                categories.forEach { category ->
                    val itemsInCategory = filteredItems
                        .filter { it.category.equals(category, ignoreCase = true) }
                        .sortedBy { it.perfumeCode }
                    
                    if (itemsInCategory.isNotEmpty()) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = category.uppercase(),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = LightMuted,
                                letterSpacing = 1.sp,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
                            )
                            itemsInCategory.forEach { item ->
                                InventoryCard(item, onClick = { onSelectItem(item) })
                            }
                        }
                    }
                }
                
                // Show items that don't match standard categories if any
                val otherItems = filteredItems
                    .filter { it.category.lowercase() !in listOf("men", "women") }
                    .sortedBy { it.name }
                if (otherItems.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "OTHERS",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = LightMuted,
                            letterSpacing = 1.sp,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
                        )
                        otherItems.forEach { item ->
                            InventoryCard(item, onClick = { onSelectItem(item) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InventoryCard(item: InventoryItem, onClick: () -> Unit) {
    val variant = when (item.status) {
        "normal" -> ChipVariant.Normal
        "missing" -> ChipVariant.Missing
        "low" -> ChipVariant.Low
        "misplaced" -> ChipVariant.Misplaced
        else -> ChipVariant.Review
    }
    val label = when (item.status) {
        "normal" -> "Normal"
        "missing" -> "Missing"
        "low" -> "Low Stock"
        "misplaced" -> "Misplaced"
        else -> "Needs Review"
    }
    AppCard(background = Color.White, radius = 24.dp, onClick = onClick) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
            Column(modifier = Modifier.weight(1f)) {
                val displayName = if (item.perfumeCode.isNotEmpty()) "${item.perfumeCode} - ${item.name}" else item.name
                Text(displayName, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = TextBlack)
                Text("${item.category} • ${item.shelf}", fontSize = 12.sp, color = Muted, modifier = Modifier.padding(top = 2.dp))
                if (item.branch.isNotEmpty()) {
                    Text(
                        text = "📍 ${item.branch}",
                        fontSize = 11.sp,
                        color = Blue,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
            StatusChip(label, variant)
        }
        Spacer(Modifier.height(14.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
            Column {
                Text("STOCK:", fontSize = 10.sp, letterSpacing = 1.sp, color = LightMuted, fontWeight = FontWeight.Medium)
                val detectedVal = if (item.detected == -1) "?" else item.detected.toString()
                Text("$detectedVal / ${item.recorded}", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = TextBlack)
            }
            Text(item.lastUpdated, fontSize = 12.sp, color = LightMuted)
        }
    }
}
