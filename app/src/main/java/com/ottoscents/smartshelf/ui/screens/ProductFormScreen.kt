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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ottoscents.smartshelf.MainViewModel
import com.ottoscents.smartshelf.Screen
import com.ottoscents.smartshelf.data.InventoryItem
import com.ottoscents.smartshelf.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductFormScreen(viewModel: MainViewModel, item: InventoryItem?, isEdit: Boolean, navigate: (Screen) -> Unit, back: () -> Unit) {
    val userBranch by viewModel.userBranch.collectAsState()
    
    var name by remember { mutableStateOf(if (isEdit) item?.name ?: "" else "") }
    var line by remember { mutableStateOf(if (isEdit) "" else "") } 
    var family by remember { mutableStateOf(if (isEdit) item?.category ?: "" else "") }
    var size by remember { mutableStateOf(if (isEdit) "50" else "") }
    var branch by remember { mutableStateOf(if (isEdit) userBranch ?: "San Pablo" else userBranch ?: "San Pablo") }
    var area by remember { mutableStateOf(if (isEdit) item?.shelf ?: "Area A1" else "Area A1") }
    var threshold by remember { mutableStateOf(if (isEdit) "5" else "5") }
    var recorded by remember { mutableStateOf(if (isEdit) item?.recorded?.toString() ?: "0" else "0") }

    ScrollScreen(background = AppBg, topBar = { TopBar(if (isEdit) "Edit Product" else "Add Product", showBack = true, onBack = back) }) {
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier.size(128.dp).clip(RoundedCornerShape(32.dp)).background(Color(0xFFE8E5DF)).border(1.dp, BorderGray, RoundedCornerShape(32.dp)), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) { Text("+"); Text("Add Photo", fontSize = 12.sp, color = Muted, fontWeight = FontWeight.Medium) }
            }
            Text("JPG or PNG, up to 5MB", fontSize = 12.sp, color = LightMuted, modifier = Modifier.padding(top = 8.dp))
        }
        Section("Product") {
            FormField("Name", name, "e.g. Midnight Oud") { name = it }; ThinDivider()
            FormField("Line", line, "e.g. Otto Scents Classic") { line = it }; ThinDivider()
            FormField("Scent Family", family, "Woody, Floral...") { family = it }; ThinDivider()
            FormField("Bottle Size (ml)", size, "50", numeric = true) { size = it }
        }
        Section("Placement") {
            FormDropdownField("Branch", branch, listOf("San Pablo", "Lipa")) { branch = it }
            ThinDivider()
            FormDropdownField("Shelf Area", area, listOf("Area A1", "Area A2", "Area B1", "Area B2", "Area C1", "Area C2")) { area = it }
        }
        Section("Stock") {
            FormField("Min. Threshold", threshold, "5", numeric = true) { threshold = it }; ThinDivider()
            FormField("Recorded Count", recorded, "0", numeric = true) { recorded = it }
        }
        AppButton(if (isEdit) "Save Changes" else "Add Product", onClick = {
            viewModel.saveInventoryItem(
                InventoryItem(
                    id = if (isEdit) item?.id ?: "" else "",
                    name = name,
                    category = family,
                    shelf = area,
                    recorded = recorded.toIntOrNull() ?: 0,
                    status = if (isEdit) item?.status ?: "normal" else "normal",
                    lastUpdated = java.text.SimpleDateFormat("MMM d, yyyy • h:mm a", java.util.Locale.getDefault()).format(java.util.Date())
                )
            )
            back()
        })
        if (isEdit) AppButton("Delete Product", variant = ButtonVariant.Danger, onClick = {
            item?.id?.let { viewModel.deleteInventoryItem(it) }
            back()
        })
        TextButton(onClick = back, modifier = Modifier.fillMaxWidth()) { Text("Cancel", color = Muted) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FormDropdownField(
    label: String,
    value: String,
    options: List<String>,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Muted, fontSize = 14.sp, modifier = Modifier.weight(0.9f))
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.weight(1.3f)
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                modifier = Modifier.menuAnchor(),
                textStyle = androidx.compose.ui.text.TextStyle(textAlign = TextAlign.End, fontSize = 14.sp, fontWeight = FontWeight.Medium),
                shape = RoundedCornerShape(14.dp)
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { selectionOption ->
                    DropdownMenuItem(
                        text = { Text(selectionOption) },
                        onClick = {
                            onSelect(selectionOption)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
