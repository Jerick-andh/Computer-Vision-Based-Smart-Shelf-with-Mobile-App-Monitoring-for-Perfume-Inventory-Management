package com.ottoscents.smartshelf.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ottoscents.smartshelf.MainViewModel
import com.ottoscents.smartshelf.Screen
import com.ottoscents.smartshelf.ui.components.*

@Composable
fun ShelfScreen(viewModel: MainViewModel, navigate: (Screen) -> Unit) {
    val isEdgeActive by viewModel.isEdgeConnected.collectAsState()
    val calibrationImage by viewModel.calibrationImage.collectAsState()
    var showBranchDialog by remember { mutableStateOf(false) }

    if (showBranchDialog) {
        AlertDialog(
            onDismissRequest = { showBranchDialog = false },
            title = { Text("Inventory Check Mode", fontWeight = FontWeight.Bold) },
            text = { Text("Choose a branch for sequential simulation or use 'Live Scan' to trigger the real-time webcam and AI detection.") },
            confirmButton = {},
            dismissButton = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    AppButton("Live Webcam Scan", variant = ButtonVariant.Primary) { 
                        viewModel.triggerManualInventory("Live")
                        showBranchDialog = false
                    }
                    AppButton("Lipa (Simulated)") { 
                        viewModel.triggerManualInventory("Lipa")
                        showBranchDialog = false
                    }
                    AppButton("San Pablo (Simulated)") { 
                        viewModel.triggerManualInventory("San Pablo")
                        showBranchDialog = false
                    }
                    AppButton("Both (Simulated)", variant = ButtonVariant.Secondary) { 
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

    ScrollScreen(topBar = { TopBar("Shelf & ROI Tester") }) {
        AppCard(background = Color.Black) {
            Column {
                Text("Calibration Tool", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = LightMuted)
                Text("Align Shelf Tray ROI", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(Modifier.height(8.dp))
                Text(
                    "Use this tool to physically align your webcam with the shelf zones (A, B, C, D). A live feed will open on the edge device for 30 seconds.",
                    fontSize = 13.sp,
                    color = LightMuted,
                    lineHeight = 18.sp
                )
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = { viewModel.triggerManualInventory("Calibration") },
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SoftGray, contentColor = TextBlack),
                    modifier = Modifier.fillMaxWidth().height(52.dp)
                ) {
                    Icon(Icons.Rounded.Bolt, contentDescription = null, tint = Orange, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Start Live Calibration", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                }
            }
        }

        Box(modifier = Modifier.fillMaxWidth().aspectRatio(1.2f).clip(RoundedCornerShape(32.dp)).background(Color(0xFFE5E7EB)).border(1.dp, BorderGray, RoundedCornerShape(32.dp))) {
            if (calibrationImage != null) {
                Image(
                    bitmap = calibrationImage!!.asImageBitmap(),
                    contentDescription = "Live Calibration Feed",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Column(modifier = Modifier.padding(16.dp).fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ShelfAreaLabel("ZONE A", Modifier.weight(1f).fillMaxHeight())
                        ShelfAreaLabel("ZONE B", Modifier.weight(1f).fillMaxHeight())
                    }
                    Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ShelfAreaLabel("ZONE C", Modifier.weight(1f).fillMaxHeight())
                        ShelfAreaLabel("ZONE D", Modifier.weight(1f).fillMaxHeight())
                    }
                }
            }
            
            val overlayLabel = if (calibrationImage != null) "LIVE CAMERA FEED" else "ROI OVERLAY ACTIVE"
            Text(overlayLabel, modifier = Modifier.align(Alignment.Center).clip(RoundedCornerShape(999.dp)).background(Color.Black.copy(alpha = 0.5f)).padding(horizontal = 12.dp, vertical = 6.dp), color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }

        Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(SoftGray).border(1.dp, BorderGray.copy(alpha = 0.5f), RoundedCornerShape(18.dp)).padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text("Physical Alignment Status", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Muted, letterSpacing = 1.sp)
                Text(
                    if (isEdgeActive) "Edge Device Connected" else "Edge Device Offline", 
                    fontSize = 14.sp, 
                    fontWeight = FontWeight.Medium, 
                    color = TextBlack
                )
            }
            Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(if (isEdgeActive) Green else Red))
        }
        
        Button(
            onClick = { showBranchDialog = true },
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black, contentColor = Color.White),
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) {
            Icon(Icons.Rounded.CenterFocusStrong, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text("Run Inventory Check / Live Scan", fontWeight = FontWeight.Medium, fontSize = 14.sp)
        }
    }
}

@Composable
private fun ShelfAreaLabel(text: String, modifier: Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.Black.copy(alpha = 0.08f))
            .border(2.dp, Color.White.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
            .padding(8.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Text(text, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Muted, textAlign = TextAlign.Center)
    }
}
