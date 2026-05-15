package com.ottoscents.smartshelf.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Air
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
import com.ottoscents.smartshelf.data.FanActivity
import com.ottoscents.smartshelf.ui.components.*

@Composable
fun FanActivityLogsScreen(viewModel: MainViewModel, back: () -> Unit) {
    val logs by viewModel.fanLogs.collectAsState()
    
    var filterType by remember { mutableStateOf<String?>(null) } // "Branch", "Status", or null
    var filterValue by remember { mutableStateOf("All") }
    var showFilterDialog by remember { mutableStateOf(false) }

    val filteredLogs = logs.filter { log ->
        when (filterType) {
            "Branch" -> filterValue == "All" || log.branch.equals(filterValue, ignoreCase = true)
            "Status" -> {
                val statusLabel = when (log.status) {
                    "active" -> "Currently Running"
                    "manual_stop" -> "Manually Stopped"
                    else -> "Auto Stopped"
                }
                filterValue == "All" || statusLabel == filterValue
            }
            else -> true
        }
    }

    ScrollScreen(background = AppBg, topBar = { TopBar("Fan Activity Logs", showBack = true, onBack = back) }) {
        AppCard(background = CardBg) { Text("Cooling system activity records", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, fontSize = 14.sp, color = Muted); Text("Fan activates when temperature exceeds 25°C", modifier = Modifier.fillMaxWidth().padding(top = 4.dp), textAlign = TextAlign.Center, fontSize = 12.sp, color = LightMuted) }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text("Recent Activity".uppercase(), fontSize = 11.sp, color = Muted, fontWeight = FontWeight.Medium, letterSpacing = 1.8.sp)
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
        filteredLogs.forEach { FanActivityCard(it) }
    }

    if (showFilterDialog) {
        val isSubStep = filterType != null
        val title = if (!isSubStep) "Select Filter Type" else "Select $filterType"
        val options = if (!isSubStep) {
            listOf("Branch", "Status")
        } else {
            when (filterType) {
                "Branch" -> listOf("All", "Lipa", "San Pablo")
                "Status" -> listOf("All", "Currently Running", "Manually Stopped", "Auto Stopped")
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
fun FanActivityCard(log: FanActivity) {
    val label = when (log.status) { "active" -> "Currently Running"; "manual_stop" -> "Manually Stopped"; else -> "Auto Stopped" }
    val variant = if (log.status == "manual_stop") ChipVariant.Warning else ChipVariant.Normal
    AppCard(background = Color.White, radius = 18.dp) {
        Row(verticalAlignment = Alignment.Top) { 
            Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(if (log.status == "active") BlueBg else SoftGray), contentAlignment = Alignment.Center) { 
                Icon(Icons.Rounded.Air, contentDescription = null, tint = if (log.status == "active") Blue else Muted, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) { 
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { 
                    Text(label, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    StatusChip(log.status.replace("_", " "), variant, small = true) 
                }; Text("${log.branch} • ${log.shelfArea}", fontSize = 12.sp, color = Muted, modifier = Modifier.padding(top = 4.dp)) 
            } 
        }
        Spacer(Modifier.height(12.dp)); ThinDivider(); Spacer(Modifier.height(10.dp))
        DetailRow("Trigger Temp:", "${log.triggerTemperature}°C", Red); DetailRow("Started:", log.startTime); if (log.stopTime != null) DetailRow("Stopped:", log.stopTime); ThinDivider(); DetailRow("Total Duration", log.duration)
        if (log.status == "active") Text("● Fan is currently running", fontSize = 12.sp, color = Blue, fontWeight = FontWeight.Medium, modifier = Modifier.padding(top = 8.dp))
    }
}
