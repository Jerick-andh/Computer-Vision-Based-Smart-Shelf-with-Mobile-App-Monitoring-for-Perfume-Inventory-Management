package com.ottoscents.smartshelf.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ottoscents.smartshelf.MainViewModel
import com.ottoscents.smartshelf.Screen
import com.ottoscents.smartshelf.ui.components.*

@Composable
fun ShelfScreen(viewModel: MainViewModel, navigate: (Screen) -> Unit) {
    val handshakeStatus by viewModel.handshakeStatus.collectAsState()
    var showBranchDialog by remember { mutableStateOf(false) }

    if (showBranchDialog) {
        AlertDialog(
            onDismissRequest = { showBranchDialog = false },
            title = { Text("Manual Inventory Check", fontWeight = FontWeight.Bold) },
            text = { Text("Which branch shelf would you like to scan? Each branch has its own set of simulation scenarios.") },
            confirmButton = {},
            dismissButton = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    AppButton("Lipa Branch") { 
                        viewModel.triggerManualInventory("Lipa")
                        showBranchDialog = false
                    }
                    AppButton("San Pablo Branch") { 
                        viewModel.triggerManualInventory("San Pablo")
                        showBranchDialog = false
                    }
                    AppButton("Both Branches", variant = ButtonVariant.Secondary) { 
                        viewModel.triggerManualInventory("Both")
                        showBranchDialog = false
                    }
                    TextButton(onClick = { showBranchDialog = false }, modifier = Modifier.fillMaxWidth()) {
                        Text("Cancel", color = Muted)
                    }
                }
            },
            shape = RoundedCornerShape(28.dp),
            containerColor = Color.White
        )
    }

    ScrollScreen(topBar = { TopBar("Shelf Monitor") }) {
        AppCard(
            background = if (handshakeStatus.contains("VERIFIED")) GreenBg else if (handshakeStatus.contains("ERROR")) RedBg else BlueBg,
            border = if (handshakeStatus.contains("VERIFIED")) Green else Color.Transparent
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text("Hardware Handshake Status", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Muted)
                    Text(handshakeStatus, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = if (handshakeStatus.contains("VERIFIED")) Green else TextBlack)
                }
                if (handshakeStatus != "CONNECTION_VERIFIED_200_OK") {
                    AppButton("Test Link", modifier = Modifier.width(100.dp).height(36.dp), onClick = { viewModel.triggerShelfCameraHandshake() })
                }
            }
        }

        Box(modifier = Modifier.fillMaxWidth().aspectRatio(0.75f).clip(RoundedCornerShape(32.dp)).background(Color(0xFFE5E7EB)).border(1.dp, BorderGray, RoundedCornerShape(32.dp))) {
            Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) { Text("◉", color = LightMuted, fontSize = 32.sp); Text("Live View Unavailable", color = LightMuted, fontSize = 12.sp, fontWeight = FontWeight.Medium); Text("Monitor via Dashboard", color = LightMuted, fontSize = 10.sp) }
            
            Column(modifier = Modifier.padding(16.dp).fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ShelfArea("Area A1", Modifier.weight(1f).fillMaxHeight()) { navigate(Screen.ShelfAreaDetail) }
                    ShelfArea("Area A2", Modifier.weight(1f).fillMaxHeight(), color = Orange, highlighted = true) { navigate(Screen.ShelfAreaDetail) }
                }
                Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ShelfArea("Area B1", Modifier.weight(1f).fillMaxHeight()) { navigate(Screen.ShelfAreaDetail) }
                    ShelfArea("Area B2", Modifier.weight(1f).fillMaxHeight(), color = Red, highlighted = true) { navigate(Screen.ShelfAreaDetail) }
                }
                Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ShelfArea("Area C1", Modifier.weight(1f).fillMaxHeight()) { navigate(Screen.ShelfAreaDetail) }
                    ShelfArea("Area C2", Modifier.weight(1f).fillMaxHeight()) { navigate(Screen.ShelfAreaDetail) }
                }
            }
            Text("● 10 mins ago", modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp).clip(RoundedCornerShape(999.dp)).background(Color.Black.copy(alpha = 0.65f)).padding(horizontal = 12.dp, vertical = 8.dp), color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Medium)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            StatusChip("142 on Shelf", ChipVariant.Normal)
            StatusChip("3 Needs Review", ChipVariant.Review)
            StatusChip("1 Missing", ChipVariant.Missing)
        }
        Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(SoftGray).border(1.dp, BorderGray.copy(alpha = 0.5f), RoundedCornerShape(18.dp)).padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("☁  Cloud Sync Active", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Muted)
            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Green))
        }
        
        AppButton(
            text = "◉  Run Manual Inventory Check", 
            onClick = { showBranchDialog = true }
        )

        AppButton("Capture Schedule", variant = ButtonVariant.Secondary, onClick = { navigate(Screen.Schedule) })
    }
}

@Composable
private fun ShelfArea(text: String, modifier: Modifier, color: Color = Color.White, highlighted: Boolean = false, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (highlighted) color.copy(alpha = 0.12f) else Color.Black.copy(alpha = 0.08f))
            .border(2.dp, if (highlighted) color.copy(alpha = 0.8f) else Color.White.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(8.dp),
        contentAlignment = Alignment.BottomStart
    ) {
        Text(text, modifier = Modifier.clip(RoundedCornerShape(999.dp)).background(if (highlighted) color else Color.White.copy(alpha = 0.9f)).padding(horizontal = 8.dp, vertical = 3.dp), fontSize = 10.sp, fontWeight = FontWeight.Medium, color = if (highlighted) Color.White else Color.Black)
    }
}
