package com.ottoscents.smartshelf

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ottoscents.smartshelf.data.InventoryItem
import com.ottoscents.smartshelf.ui.components.*
import com.ottoscents.smartshelf.ui.screens.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        com.google.firebase.FirebaseApp.initializeApp(this)
        com.google.firebase.firestore.FirebaseFirestore.setLoggingEnabled(true)
        setContent { OttoScentsApp() }
    }
}

enum class Screen {
    Login, Home, Inventory, ProductDetail, ProductForm, Shelf, ShelfAreaDetail, 
    Temperature, Schedule, Alerts, Settings, Reports, ShelfHistory, CloudBackup, 
    StockLogs, Restock, CreateRestock, ReceivingVerification, FanLogs, SystemLogs, Help
}

@Composable
fun OttoScentsApp() {
    val viewModel: MainViewModel = viewModel()
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val backStack = remember { mutableStateListOf<Screen>() }
    var screen by remember { mutableStateOf(if (isLoggedIn) Screen.Home else Screen.Login) }
    var selectedItem by remember { mutableStateOf<InventoryItem?>(null) }

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            if (screen == Screen.Login) { backStack.clear(); screen = Screen.Home }
        } else {
            backStack.clear(); screen = Screen.Login
        }
    }

    fun navigate(target: Screen) { backStack.add(screen); screen = target }
    fun replace(target: Screen) { backStack.clear(); screen = target }
    fun back() { screen = if (backStack.isNotEmpty()) backStack.removeAt(backStack.lastIndex) else Screen.Home }

    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = AppBg) {
            if (screen == Screen.Login) {
                LoginScreen(viewModel = viewModel)
            } else {
                Column(modifier = Modifier.fillMaxSize().background(AppBg)) {
                    Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                        when (screen) {
                            Screen.Home -> HomeScreen(viewModel, ::navigate)
                            Screen.Inventory -> InventoryScreen(viewModel) { item -> selectedItem = item; navigate(Screen.ProductDetail) }
                            Screen.ProductDetail -> ProductDetailScreen(selectedItem, ::navigate, ::back)
                            Screen.ProductForm -> ProductFormScreen(viewModel, selectedItem, backStack.lastOrNull() == Screen.ProductDetail, ::navigate, ::back)
                            Screen.Shelf -> ShelfScreen(viewModel, ::navigate)
                            Screen.ShelfAreaDetail -> ShelfAreaDetailScreen(::back)
                            Screen.Temperature -> TemperatureMonitorScreen(viewModel, ::navigate, ::back)
                            Screen.Schedule -> ScheduleScreen(viewModel, ::back)
                            Screen.Alerts -> AlertsScreen(viewModel, ::navigate)
                            Screen.Settings -> SettingsScreen(viewModel, ::navigate, onLogout = { viewModel.logout(); replace(Screen.Login) })
                            Screen.Reports -> ReportsScreen(::back)
                            Screen.ShelfHistory -> ShelfCheckHistoryScreen(::back)
                            Screen.CloudBackup -> CloudBackupCheckScreen(::navigate, ::back)
                            Screen.StockLogs -> StockMovementLogsScreen(viewModel, ::navigate, ::back)
                            Screen.Restock -> RestockManagementScreen(viewModel, ::navigate, ::back)
                            Screen.CreateRestock -> CreateRestockRequestScreen(::back)
                            Screen.ReceivingVerification -> ReceivingVerificationScreen(::navigate, ::back)
                            Screen.FanLogs -> FanActivityLogsScreen(viewModel, ::back)
                            Screen.SystemLogs -> SystemActivityLogsScreen(viewModel, ::back)
                            Screen.Help -> HelpGuideScreen(::back)
                            else -> Unit
                        }
                    }
                    BottomNav(current = screen, onNavigate = ::replace)
                }
            }
        }
    }
}
