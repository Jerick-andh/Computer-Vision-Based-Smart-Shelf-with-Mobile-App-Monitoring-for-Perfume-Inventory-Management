package com.ottoscents.smartshelf.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ottoscents.smartshelf.MainViewModel
import com.ottoscents.smartshelf.Screen
import com.ottoscents.smartshelf.data.InventoryItem
import com.ottoscents.smartshelf.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductFormScreen(viewModel: MainViewModel, item: InventoryItem?, isEdit: Boolean, navigate: (Screen) -> Unit, back: () -> Unit) {
    var name by remember { mutableStateOf(if (isEdit) item?.name ?: "" else "") }
    // Store the Letter Code (A, B, C, D)
    var codeLetter by remember { 
        mutableStateOf(
            if (isEdit) item?.perfumeCode ?: "A" 
            else "A"
        ) 
    }
    var category by remember { mutableStateOf(if (isEdit) item?.category ?: "Men" else "Men") }
    // Branch is now always "Both Branches" since branches share perfumes and shelf areas
    val branch = "Both Branches"
    
    // Match shelf area to perfume code (e.g., Code A -> Area A)
    val area = remember(codeLetter) { "Area $codeLetter" }

    ScrollScreen(background = AppBg, topBar = { TopBar(if (isEdit) "Edit Product" else "Add Product", showBack = true, onBack = back) }) {
        Section("Basic Information") {
            FormDropdownField(
                label = "Perfume Letter Code",
                value = codeLetter,
                options = listOf("A", "B", "C", "D")
            ) { codeLetter = it }
            ThinDivider()
            FormField("Product Name", name, "e.g. Giorgio Armani") { name = it }
            ThinDivider()
            FormDropdownField("Category", category, listOf("Men", "Women")) { category = it }
        }
        
        Section("Placement") {
            // Shelf Area is now automatically derived from the Perfume Code
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Assigned Shelf Area", fontSize = 11.sp, color = Muted, fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp)
                Text(area, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = TextBlack, modifier = Modifier.padding(top = 4.dp))
                Text("Shelf area is automatically matched to your Perfume Code.", fontSize = 12.sp, color = LightMuted, modifier = Modifier.padding(top = 2.dp))
            }
        }

        Spacer(Modifier.height(12.dp))

        AppButton(
            text = if (isEdit) "Save Changes" else "Add Product", 
            onClick = {
                val recordedStock = if (isEdit) item?.recorded ?: 0 else 0
                val detectedStock = if (isEdit) item?.detected ?: 0 else 0
                
                viewModel.saveInventoryItem(
                    InventoryItem(
                        id = if (isEdit) item?.id ?: "" else "",
                        perfumeCode = codeLetter,
                        name = name,
                        category = category,
                        branch = branch,
                        shelf = area,
                        recorded = recordedStock,
                        detected = detectedStock,
                        status = if (isEdit) item?.status ?: "normal" else "normal",
                        lastUpdated = java.text.SimpleDateFormat("MMM d, yyyy • h:mm a", java.util.Locale.getDefault()).format(java.util.Date()),
                    )
                )
                back()
            }
        )

        if (isEdit) {
            AppButton(
                text = "Delete Product", 
                variant = ButtonVariant.Danger, 
                onClick = {
                    item?.let { viewModel.deleteInventoryItem(it) }
                    back()
                }
            )
        }
        
        TextButton(onClick = back, modifier = Modifier.fillMaxWidth()) { 
            Text("Cancel", color = Muted) 
        }
    }
}
