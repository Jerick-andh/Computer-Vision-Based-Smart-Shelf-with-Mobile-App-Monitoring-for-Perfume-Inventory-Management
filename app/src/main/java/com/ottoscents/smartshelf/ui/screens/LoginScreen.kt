package com.ottoscents.smartshelf.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ottoscents.smartshelf.MainViewModel
import com.ottoscents.smartshelf.ui.components.*

@Composable
fun LoginScreen(viewModel: MainViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val loginError by viewModel.loginError.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp)
            .padding(top = 126.dp, bottom = 30.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
            Column {
                Text("Otto Scents", fontSize = 36.sp, fontWeight = FontWeight.Bold, letterSpacing = (-0.6).sp, color = TextBlack)
                Text("Smart Shelf Monitoring", fontSize = 18.sp, color = LightMuted, modifier = Modifier.padding(top = 6.dp, bottom = 28.dp))
            }
            OutlinedTextField(value = email, onValueChange = { email = it }, placeholder = { Text("Email address") }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(18.dp))
            OutlinedTextField(value = password, onValueChange = { password = it }, placeholder = { Text("Password") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(18.dp))
            
            if (loginError != null) {
                Text(loginError!!, color = Red, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            }

            Text("Forgot password?", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End, fontSize = 14.sp, color = Muted, fontWeight = FontWeight.Medium)
            AppButton("Sign In", onClick = { viewModel.login(email, password) })
        }
        Text("© 2026 Otto Scents. All rights reserved.", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, fontSize = 12.sp, color = LightMuted)
    }
}
