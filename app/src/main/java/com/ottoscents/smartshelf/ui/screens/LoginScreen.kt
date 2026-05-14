package com.ottoscents.smartshelf.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoGraph
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
    var isRegisterMode by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val loginError by viewModel.loginError.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp)
            .padding(top = 80.dp, bottom = 30.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
            Column {
                Icon(
                    imageVector = Icons.Rounded.AutoGraph, 
                    contentDescription = null,
                    tint = Blue,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(Modifier.height(16.dp))
                Text("Otto Scents", fontSize = 36.sp, fontWeight = FontWeight.Bold, letterSpacing = (-0.6).sp, color = TextBlack)
                Text(
                    if (isRegisterMode) "Create Staff Account" else "Smart Shelf Monitoring", 
                    fontSize = 18.sp, 
                    color = LightMuted, 
                    modifier = Modifier.padding(top = 6.dp, bottom = 28.dp)
                )
            }
            OutlinedTextField(value = email, onValueChange = { email = it }, placeholder = { Text("Email address") }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(18.dp))
            OutlinedTextField(value = password, onValueChange = { password = it }, placeholder = { Text("Password") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(18.dp))
            
            if (loginError != null) {
                Text(loginError!!, color = Red, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            }

            if (!isRegisterMode) {
                Text("Forgot password?", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End, fontSize = 14.sp, color = Muted, fontWeight = FontWeight.Medium)
            }

            AppButton(
                text = if (isRegisterMode) "Register Account" else "Sign In", 
                onClick = { 
                    if (isRegisterMode) viewModel.register(email, password)
                    else viewModel.login(email, password)
                }
            )

            TextButton(
                onClick = { isRegisterMode = !isRegisterMode },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    if (isRegisterMode) "Already have an account? Sign In" else "Need an account? Register",
                    color = Muted,
                    fontSize = 14.sp
                )
            }
        }
        Text("© 2026 Otto Scents. All rights reserved.", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, fontSize = 12.sp, color = LightMuted)
    }
}
