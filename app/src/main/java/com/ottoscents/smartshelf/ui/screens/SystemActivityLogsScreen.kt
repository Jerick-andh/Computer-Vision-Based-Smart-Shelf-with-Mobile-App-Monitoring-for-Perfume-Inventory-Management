package com.ottoscents.smartshelf.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.History
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
import com.ottoscents.smartshelf.data.SystemActivity
import com.ottoscents.smartshelf.ui.components.*

@Composable
fun SystemActivityLogsScreen(viewModel: MainViewModel, back: () -> Unit) {
    val activities by viewModel.systemLogs.collectAsState()
    
    var filterType by remember { mutableStateOf<String?>(null) } // "Branch", "Event Type", or null
    var filterValue by remember { mutableStateOf("All") }
    var showFilterDialog by remember { mutableStateOf(false) }

    val filteredActivities = activities.filter { activity ->
        when (filterType) {
            "Branch" -> filterValue == "All" || activity.branch.equals(filterValue, ignoreCase = true)
            "Event Type" -> {
                val typeLabel = when (activity.type) {
                    "user_login" -> "User Login"
                    "product_update" -> "Product Update"
                    "schedule_change" -> "Schedule Change"
                    "shelf_check" -> "Shelf Check"
                    "inventory_update" -> "Inventory Update"
                    "temperature_alert" -> "Temperature Alert"
                    "fan_activation" -> "Fan Activation"
                    "cloud_backup" -> "Cloud Backup"
                    else -> "Synchronization"
                }
                filterValue == "All" || typeLabel == filterValue
            }
            else -> true
        }
    }

    ScrollScreen(background = AppBg, topBar = { TopBar("System Activity Logs", showBack = true, onBack = back) }) {
        AppCard(background = CardBg) { Text("System events and user actions", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, fontSize = 14.sp, color = Muted); Text("Track all important activities across branches", modifier = Modifier.fillMaxWidth().padding(top = 4.dp), textAlign = TextAlign.Center, fontSize = 12.sp, color = LightMuted) }
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
        filteredActivities.forEach { ActivityCard(it) }
    }

    if (showFilterDialog) {
        val isSubStep = filterType != null
        val title = if (!isSubStep) "Select Filter Type" else "Select $filterType"
        val options = if (!isSubStep) {
            listOf("Branch", "Event Type")
        } else {
            when (filterType) {
                "Branch" -> listOf("All", "Lipa", "San Pablo")
                "Event Type" -> listOf("All", "User Login", "Product Update", "Schedule Change", "Shelf Check", "Inventory Update", "Temperature Alert", "Fan Activation", "Cloud Backup", "Synchronization")
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
private fun ActivityCard(activity: SystemActivity) {
    val label = when (activity.type) { "user_login" -> "User Login"; "product_update" -> "Product Update"; "schedule_change" -> "Schedule Change"; "shelf_check" -> "Shelf Check"; "inventory_update" -> "Inventory Update"; "temperature_alert" -> "Temperature Alert"; "fan_activation" -> "Fan Activation"; "cloud_backup" -> "Cloud Backup"; else -> "Synchronization" }
    val bg = when (activity.type) { "temperature_alert" -> RedBg; "inventory_update" -> GreenBg; "cloud_backup" -> IndigoBg; "sync_event" -> TealBg; "product_update" -> PurpleBg; "schedule_change" -> OrangeBg; "fan_activation" -> BlueBg; else -> SoftGray }
    val iconColor = when (activity.type) { "temperature_alert" -> Red; "inventory_update" -> Green; "fan_activation" -> Blue; else -> Muted }
    Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(Color.White).border(1.dp, BorderGray.copy(alpha = 0.5f), RoundedCornerShape(18.dp)).padding(16.dp), verticalAlignment = Alignment.Top) {
        Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(bg), contentAlignment = Alignment.Center) { 
            Icon(Icons.Rounded.History, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text(label.uppercase(), fontSize = 11.sp, color = Muted, fontWeight = FontWeight.Medium, letterSpacing = 1.sp); Text(activity.timestamp.substringAfter("•").trim(), fontSize = 12.sp, color = LightMuted) }
            Text(activity.description, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextBlack, modifier = Modifier.padding(top = 4.dp))
            Text(listOfNotNull(activity.user, activity.branch, activity.timestamp.substringBefore("•").trim()).joinToString(" • "), fontSize = 12.sp, color = Muted, modifier = Modifier.padding(top = 6.dp))
        }
    }
}
