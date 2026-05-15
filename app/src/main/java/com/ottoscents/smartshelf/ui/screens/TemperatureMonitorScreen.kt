package com.ottoscents.smartshelf.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ottoscents.smartshelf.MainViewModel
import com.ottoscents.smartshelf.Screen
import com.ottoscents.smartshelf.ui.components.*

@Composable
fun TemperatureMonitorScreen(viewModel: MainViewModel, navigate: (Screen) -> Unit, back: () -> Unit) {
    val currentTemp by viewModel.currentTemperature.collectAsState()
    val fanActive by viewModel.isFanActive.collectAsState()
    val userRole by viewModel.userRole.collectAsState()
    val data = listOf(21f, 22f, 22.5f, 23f, 24.5f, 23.5f, currentTemp)
    val labels = listOf("08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "Now")
    
    ScrollScreen(topBar = { TopBar("Climate Control", showBack = true, onBack = back) }) {
        Column(modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier.size(196.dp).clip(CircleShape).border(6.dp, Color(0xFFF3F4F6), CircleShape), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.Top) { 
                        Text(String.format("%.1f", currentTemp), fontSize = 56.sp, fontWeight = FontWeight.Light, color = TextBlack)
                        Text("°C", fontSize = 24.sp, color = LightMuted, modifier = Modifier.padding(top = 5.dp)) 
                    }
                    Text("Current Temp", fontSize = 14.sp, color = Muted, fontWeight = FontWeight.Medium)
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 18.dp)) { 
                StatusChip("Safe Range: 18°C - 24°C")
                StatusChip(if (currentTemp > 25) "High" else "Normal", variant = if (currentTemp > 25) ChipVariant.Warning else ChipVariant.Normal) 
            }
        }
        
        AppCard(background = SoftGray) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) { 
                    Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(Color.White).border(1.dp, BorderGray, CircleShape), contentAlignment = Alignment.Center) { 
                        Icon(Icons.Rounded.Air, contentDescription = null, tint = if (fanActive) Blue else Muted, modifier = Modifier.size(20.dp))
                    }
                    Spacer(Modifier.width(12.dp))
                    Column { Text("Cooling Fan", fontWeight = FontWeight.SemiBold); Text(if (fanActive) "Running..." else "Last active: 12:00 PM", fontSize = 12.sp, color = Muted) } 
                }
                StatusChip(if (fanActive) "ON" else "OFF", variant = if (fanActive) ChipVariant.Blue else ChipVariant.Outline)
            }
        }

        if (userRole == "admin" || userRole == "staff") {
            Text("Prototype Controls", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextBlack, modifier = Modifier.padding(top = 10.dp, start = 4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = { viewModel.simulateTemperatureSpike() },
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black, contentColor = Color.White),
                    modifier = Modifier.weight(1f).height(52.dp)
                ) {
                    Icon(Icons.Rounded.TrendingUp, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Trigger Spike", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                }
                
                Button(
                    onClick = { viewModel.resetCoolingSystem() },
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SoftGray, contentColor = TextBlack),
                    modifier = Modifier.weight(1f).height(52.dp)
                ) {
                    Icon(Icons.Rounded.RestartAlt, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Reset", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(SoftGray).border(1.dp, BorderGray.copy(alpha = 0.6f), RoundedCornerShape(18.dp)).clickable { navigate(Screen.FanLogs) }.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("◷  Fan Activity Logs", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextBlack)
            Text("View", fontSize = 12.sp, color = LightMuted)
        }
        AppCard(background = Color.White, border = BorderGray) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text("Temperature History", fontSize = 14.sp, fontWeight = FontWeight.SemiBold); Text("Today", fontSize = 12.sp, color = Muted) }
            Spacer(Modifier.height(20.dp))
            LineChart(values = data, labels = labels, modifier = Modifier.fillMaxWidth().height(150.dp))
        }
    }
}

@Composable
private fun LineChart(values: List<Float>, labels: List<String>, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxWidth().weight(1f)) {
            val max = values.maxOrNull() ?: 1f
            val min = values.minOrNull() ?: 0f
            val stepX = if (values.size > 1) size.width / (values.size - 1) else size.width
            val path = Path()
            values.forEachIndexed { index, value ->
                val x = stepX * index
                val y = size.height - ((value - min) / (max - min + 0.01f)) * size.height
                if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            drawPath(path, Color.Black, style = Stroke(width = 4f))
            val lastX = stepX * (values.size - 1)
            val lastY = size.height - ((values.last() - min) / (max - min + 0.01f)) * size.height
            drawCircle(Color.Black, 7f, Offset(lastX, lastY))
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            labels.forEach { Text(it, fontSize = 10.sp, color = LightMuted) }
        }
    }
}
