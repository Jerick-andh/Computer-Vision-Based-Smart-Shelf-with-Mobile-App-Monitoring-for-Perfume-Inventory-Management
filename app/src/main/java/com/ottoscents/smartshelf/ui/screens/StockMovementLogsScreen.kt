package com.ottoscents.smartshelf.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
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
import com.ottoscents.smartshelf.data.MovementLog
import com.ottoscents.smartshelf.ui.components.*

@Composable
fun StockMovementLogsScreen(viewModel: MainViewModel, navigate: (Screen) -> Unit, back: () -> Unit) {
    val logs by viewModel.movementLogs.collectAsState()
    
    var filterType by remember { mutableStateOf<String?>(null) } // "Branch", "Status", or null
    var filterValue by remember { mutableStateOf("All") }
    var showFilterDialog by remember { mutableStateOf(false) }

    val filteredLogs = logs.filter { log ->
        when (filterType) {
            "Branch" -> filterValue == "All" || log.branch.equals(filterValue, ignoreCase = true)
            "Status" -> {
                val statusLabel = when (log.status) {
                    "present" -> "Bottle Present"
                    "missing" -> "Bottle Missing"
                    "returned" -> "Bottle Returned"
                    "restocked" -> "Restocked"
                    "verified" -> "Manually Verified"
                    else -> "Needs Review"
                }
                filterValue == "All" || statusLabel == filterValue
            }
            else -> true
        }
    }

    ScrollScreen(background = AppBg, topBar = { TopBar("Stock Movement Logs", showBack = true, onBack = back) }) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text("Recent Movements".uppercase(), fontSize = 11.sp, color = Muted, fontWeight = FontWeight.Medium, letterSpacing = 1.8.sp)
                if (filterType != null && filterValue != "All") {
                    Text("Filter: $filterType ($filterValue)", fontSize = 10.sp, color = Blue, fontWeight = FontWeight.Bold)
                }
            }
            StatusChip(
                if (filterType != null && filterValue != "All") "Filter: $filterValue" else "Filter",
                if (filterType != null && filterValue != "All") ChipVariant.Blue else ChipVariant.Outline,
                small = true,
                modifier = Modifier.clickable { showFilterDialog = true }
            )
        }
        filteredLogs.forEach { log -> MovementLogCard(log) { navigate(Screen.ProductDetail) } }
    }

    if (showFilterDialog) {
        val isSubStep = filterType != null
        val title = if (!isSubStep) "Select Filter Type" else "Select $filterType"
        val options = if (!isSubStep) {
            listOf("Branch", "Status")
        } else {
            when (filterType) {
                "Branch" -> listOf("All", "Lipa", "San Pablo")
                "Status" -> listOf("All", "Bottle Present", "Bottle Missing", "Bottle Returned", "Restocked", "Manually Verified", "Needs Review")
                else -> emptyList()
            }
        }

        SelectionDialog(
            title = title,
            options = options,
            current = if (!isSubStep) "" else filterValue,
            onDismiss = { 
                showFilterDialog = false
                filterType = null 
            },
            onBack = if (isSubStep) { { filterType = null } } else null,
            onSelect = { selected ->
                if (!isSubStep) {
                    filterType = selected
                } else {
                    filterValue = selected
                    showFilterDialog = false
                    if (selected == "All") filterType = null
                }
            }
        )
    }
}

@Composable
private fun MovementLogCard(log: MovementLog, onClick: () -> Unit) {
    val label = when (log.status) { "present" -> "Bottle Present"; "missing" -> "Bottle Missing"; "returned" -> "Bottle Returned"; "restocked" -> "Restocked"; "verified" -> "Manually Verified"; else -> "Needs Review" }
    val variant = when (log.status) { "missing" -> ChipVariant.Critical; "needs_review" -> ChipVariant.Warning; else -> ChipVariant.Normal }
    Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(Color.White).border(1.dp, BorderGray.copy(alpha = 0.5f), RoundedCornerShape(18.dp)).clickable(onClick = onClick).padding(16.dp)) {
        Box(
            modifier = Modifier.size(40.dp).clip(CircleShape).background(
                if (variant == ChipVariant.Critical) RedBg 
                else if (variant == ChipVariant.Warning) YellowBg 
                else GreenBg
            ), 
            contentAlignment = Alignment.Center
        ) { 
            Icon(
                imageVector = if (log.status == "missing") Icons.Rounded.PriorityHigh else Icons.Rounded.Check,
                contentDescription = null,
                tint = if (variant == ChipVariant.Critical) Red else if (variant == ChipVariant.Warning) Yellow else Green,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Column { Text(label, fontSize = 14.sp, fontWeight = FontWeight.Medium); Text(log.productName, fontSize = 14.sp, fontWeight = FontWeight.SemiBold) }; StatusChip(log.quantity.toString(), variant, small = true) }
            Text("${log.shelfArea} • ${log.branch} • ${log.timestamp}", fontSize = 12.sp, color = Muted, modifier = Modifier.padding(top = 8.dp))
        }
    }
}
