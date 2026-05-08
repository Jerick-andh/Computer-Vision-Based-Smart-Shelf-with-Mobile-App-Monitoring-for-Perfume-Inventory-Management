package com.ottoscents.smartshelf

import com.ottoscents.smartshelf.data.*

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue

import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val AppBg = Color(0xFFF0EDE8)
private val CardBg = Color(0xFFF5F3EE)
private val SoftGray = Color(0xFFF7F7F7)
private val BorderGray = Color(0xFFE5E7EB)
private val TextBlack = Color(0xFF111827)
private val Muted = Color(0xFF6B7280)
private val LightMuted = Color(0xFF9CA3AF)
private val Red = Color(0xFFDC2626)
private val RedBg = Color(0xFFFEF2F2)
private val Orange = Color(0xFFEA580C)
private val OrangeBg = Color(0xFFFFF7ED)
private val Green = Color(0xFF16A34A)
private val GreenBg = Color(0xFFF0FDF4)
private val Blue = Color(0xFF2563EB)
private val BlueBg = Color(0xFFEFF6FF)
private val Yellow = Color(0xFFCA8A04)
private val YellowBg = Color(0xFFFEFCE8)
private val PurpleBg = Color(0xFFFAF5FF)
private val IndigoBg = Color(0xFFEEF2FF)
private val TealBg = Color(0xFFF0FDFA)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OttoScentsApp()
        }
    }
}

private enum class Screen {
    Login,
    Home,
    Inventory,
    ProductDetail,
    ProductForm,
    Shelf,
    ShelfAreaDetail,
    Temperature,
    Schedule,
    Alerts,
    Settings,
    Reports,
    ShelfHistory,
    CloudBackup,
    StockLogs,
    Restock,
    CreateRestock,
    ReceivingVerification,
    FanLogs,
    SystemLogs,
    Help
}

@Composable
fun OttoScentsApp() {
    val viewModel: MainViewModel = viewModel()
    val backStack = remember { mutableStateListOf<Screen>() }
    var screen by remember { mutableStateOf(Screen.Login) }

    fun navigate(target: Screen) {
        backStack.add(screen)
        screen = target
    }

    fun replace(target: Screen) {
        backStack.clear()
        screen = target
    }

    fun back() {
        if (backStack.isNotEmpty()) {
            screen = backStack.removeAt(backStack.lastIndex)
        } else {
            screen = Screen.Home
        }
    }

    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = AppBg) {
            if (screen == Screen.Login) {
                LoginScreen(onLogin = { replace(Screen.Home) })
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(AppBg)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        when (screen) {
                            Screen.Home -> HomeScreen(viewModel, ::navigate)
                            Screen.Inventory -> InventoryScreen(viewModel, ::navigate)
                            Screen.ProductDetail -> ProductDetailScreen(::navigate, ::back)
                            Screen.ProductForm -> ProductFormScreen(isEdit = backStack.lastOrNull() == Screen.ProductDetail, navigate = ::navigate, back = ::back)
                            Screen.Shelf -> ShelfScreen(::navigate)
                            Screen.ShelfAreaDetail -> ShelfAreaDetailScreen(::back)
                            Screen.Temperature -> TemperatureMonitorScreen(::navigate, ::back)
                            Screen.Schedule -> ScheduleScreen(::back)
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
                            Screen.Login -> Unit
                        }
                    }
                    BottomNav(current = screen, onNavigate = ::replace)
                }
            }
        }
    }
}

@Composable
private fun TopBar(title: String, showBack: Boolean = false, onBack: (() -> Unit)? = null, right: (@Composable () -> Unit)? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(WindowInsets.statusBars.asPaddingValues())
            .padding(horizontal = 24.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            if (showBack && onBack != null) {
                Text(
                    text = "‹",
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .clickable { onBack() },
                    textAlign = TextAlign.Center,
                    fontSize = 32.sp,
                    color = TextBlack
                )
                Spacer(Modifier.width(6.dp))
            }
            Text(title, fontSize = 24.sp, fontWeight = FontWeight.SemiBold, color = TextBlack, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        if (right != null) right()
    }
}

@Composable
private fun BottomNav(current: Screen, onNavigate: (Screen) -> Unit) {
    val tabs = listOf(
        Triple("Home", Screen.Home, "⌂"),
        Triple("Inventory", Screen.Inventory, "□"),
        Triple("Shelf", Screen.Shelf, "◉"),
        Triple("Alerts", Screen.Alerts, "!"),
        Triple("Settings", Screen.Settings, "⚙")
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .border(1.dp, BorderGray.copy(alpha = 0.6f))
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .padding(WindowInsets.navigationBars.asPaddingValues()),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        tabs.forEach { (label, target, icon) ->
            val active = isTabActive(current, target)
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onNavigate(target) }
                    .padding(horizontal = 8.dp, vertical = 2.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(icon, fontSize = 24.sp, color = if (active) Color.Black else LightMuted)
                Text(label, fontSize = 10.sp, fontWeight = FontWeight.Medium, color = if (active) Color.Black else Color.Transparent)
            }
        }
    }
}

private fun isTabActive(current: Screen, tab: Screen): Boolean = when (tab) {
    Screen.Home -> current == Screen.Home
    Screen.Inventory -> current in listOf(Screen.Inventory, Screen.ProductDetail, Screen.ProductForm, Screen.ShelfHistory, Screen.StockLogs)
    Screen.Shelf -> current in listOf(Screen.Shelf, Screen.ShelfAreaDetail, Screen.Temperature, Screen.Schedule, Screen.CloudBackup, Screen.FanLogs)
    Screen.Alerts -> current == Screen.Alerts
    Screen.Settings -> current in listOf(Screen.Settings, Screen.Reports, Screen.SystemLogs, Screen.Help, Screen.Restock, Screen.CreateRestock, Screen.ReceivingVerification)
    else -> false
}

@Composable
private fun ScrollScreen(background: Color = Color.White, topBar: @Composable () -> Unit, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.fillMaxSize().background(background)) {
        topBar()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 10.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            content = content
        )
    }
}

@Composable
private fun AppCard(
    modifier: Modifier = Modifier,
    background: Color = SoftGray,
    border: Color = BorderGray.copy(alpha = 0.5f),
    radius: Dp = 28.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val base = modifier
        .clip(RoundedCornerShape(radius))
        .background(background)
        .border(1.dp, border, RoundedCornerShape(radius))
    Column(
        modifier = (if (onClick != null) base.clickable { onClick() } else base)
            .padding(18.dp),
        content = content
    )
}

private enum class ChipVariant { Normal, Missing, Review, Low, Returned, Outline, Black, Warning, Critical, Green, Blue }
private enum class ButtonVariant { Primary, Secondary, Outline, Ghost, Danger }

@Composable
private fun StatusChip(text: String, variant: ChipVariant = ChipVariant.Normal, small: Boolean = false, @SuppressLint(
    "ModifierParameter"
) modifier: Modifier = Modifier) {
    val colors = when (variant) {
        ChipVariant.Normal -> Color(0xFFF3F4F6) to Muted
        ChipVariant.Missing, ChipVariant.Critical -> RedBg to Red
        ChipVariant.Review, ChipVariant.Warning -> OrangeBg to Orange
        ChipVariant.Low -> Color(0xFFE5E7EB) to TextBlack
        ChipVariant.Returned, ChipVariant.Outline -> Color.White to Muted
        ChipVariant.Black -> Color.Black to Color.White
        ChipVariant.Green -> GreenBg to Green
        ChipVariant.Blue -> BlueBg to Blue
    }
    Text(
        text = text,
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(colors.first)
            .border(if (variant in listOf(ChipVariant.Returned, ChipVariant.Outline, ChipVariant.Missing, ChipVariant.Review, ChipVariant.Critical, ChipVariant.Warning)) 1.dp else 0.dp, BorderGray, RoundedCornerShape(999.dp))
            .padding(horizontal = if (small) 8.dp else 12.dp, vertical = if (small) 4.dp else 6.dp),
        fontSize = if (small) 10.sp else 12.sp,
        fontWeight = FontWeight.Medium,
        color = colors.second,
        maxLines = 1
    )
}

@Composable
private fun AppButton(
    text: String,
    modifier: Modifier = Modifier,
    variant: ButtonVariant = ButtonVariant.Primary,
    enabled: Boolean = true,
    onClick: () -> Unit = {}
) {
    val container = when (variant) {
        ButtonVariant.Primary -> Color.Black
        ButtonVariant.Secondary -> Color(0xFFF3F4F6)
        ButtonVariant.Outline -> Color.White
        ButtonVariant.Ghost -> Color.Transparent
        ButtonVariant.Danger -> RedBg
    }
    val content = when (variant) {
        ButtonVariant.Primary -> Color.White
        ButtonVariant.Secondary -> Color.Black
        ButtonVariant.Outline -> TextBlack
        ButtonVariant.Ghost -> Muted
        ButtonVariant.Danger -> Red
    }
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(containerColor = container, contentColor = content, disabledContainerColor = Color(0xFFE5E7EB), disabledContentColor = LightMuted),
        modifier = modifier.fillMaxWidth().height(52.dp),
        contentPadding = ButtonDefaults.ContentPadding
    ) {
        Text(text, fontWeight = FontWeight.Medium, fontSize = 14.sp)
    }
}

@Composable
private fun ThinDivider() = HorizontalDivider(color = BorderGray.copy(alpha = 0.6f), thickness = 1.dp)

@Composable
private fun DetailRow(label: String, value: String, valueColor: Color = TextBlack) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(label, color = Muted, fontSize = 14.sp)
        Text(value, color = valueColor, fontSize = 14.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.End)
    }
}

@Composable
private fun Section(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title.uppercase(), fontSize = 11.sp, fontWeight = FontWeight.Medium, letterSpacing = 1.8.sp, color = Muted, modifier = Modifier.padding(start = 4.dp))
        AppCard(background = CardBg, border = BorderGray.copy(alpha = 0.45f), content = content)
    }
}

@Composable
private fun FormField(label: String, value: String, placeholder: String = "", numeric: Boolean = false, onChange: (String) -> Unit = {}) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Muted, fontSize = 14.sp, modifier = Modifier.weight(0.9f))
        OutlinedTextField(
            value = value,
            onValueChange = onChange,
            placeholder = { Text(placeholder, color = LightMuted) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = if (numeric) KeyboardType.Number else KeyboardType.Text),
            modifier = Modifier.weight(1.3f),
            textStyle = androidx.compose.ui.text.TextStyle(textAlign = TextAlign.End, fontSize = 14.sp, fontWeight = FontWeight.Medium),
            shape = RoundedCornerShape(14.dp)
        )
    }
}

@Composable
private fun LoginScreen(onLogin: () -> Unit) {
    var email by remember { mutableStateOf("admin@ottoscents.com") }
    var password by remember { mutableStateOf("password123") }

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
            Text("Forgot password?", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End, fontSize = 14.sp, color = Muted, fontWeight = FontWeight.Medium)
            AppButton("Sign In", onClick = onLogin)
        }
        Text("© 2026 Otto Scents. All rights reserved.", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, fontSize = 12.sp, color = LightMuted)
    }
}

@Composable
private fun HomeScreen(viewModel: MainViewModel, navigate: (Screen) -> Unit) {
    val branch by viewModel.userBranch.collectAsState()
    ScrollScreen(
        topBar = {
            TopBar(
                title = "Dashboard",
                right = {
                    Text(branch ?: "Unknown", modifier = Modifier.clip(RoundedCornerShape(999.dp)).background(Color(0xFFF3F4F6)).padding(horizontal = 16.dp, vertical = 8.dp), fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }
            )
        }
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp), modifier = Modifier.fillMaxWidth()) {
            AppCard(modifier = Modifier.weight(1f), onClick = { navigate(Screen.Inventory) }) {
                Text("142", fontSize = 40.sp, fontWeight = FontWeight.Light, color = TextBlack)
                Text("Bottles Detected", fontSize = 14.sp, color = Muted, fontWeight = FontWeight.Medium)
            }
            AppCard(modifier = Modifier.weight(1f), background = Color(0xFF111827), border = Color.Transparent, onClick = { navigate(Screen.Shelf) }) {
                Text("3", fontSize = 40.sp, fontWeight = FontWeight.Light, color = Color.White)
                Text("⚠ Needs Review", fontSize = 14.sp, color = Color(0xFFD1D5DB), fontWeight = FontWeight.Medium)
            }
        }

        AppCard(onClick = { navigate(Screen.Temperature) }) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("🌡 Shelf Climate", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Muted)
                StatusChip("Normal")
            }
            Spacer(Modifier.height(14.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text("22.4", fontSize = 32.sp, fontWeight = FontWeight.Light, color = TextBlack)
                    Text("°C", fontSize = 16.sp, color = LightMuted, modifier = Modifier.padding(bottom = 4.dp, start = 3.dp))
                }
                Text("☁ Fan Off", fontSize = 14.sp, color = Muted)
            }
        }

        AppCard(background = SoftGray) {
            DetailRow("◷  Last Check", "10 mins ago")
            Spacer(Modifier.height(12.dp)); ThinDivider(); Spacer(Modifier.height(12.dp))
            DetailRow("☁  Cloud Sync", "● Synced")
        }

        Text("Quick Actions", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextBlack, modifier = Modifier.padding(top = 10.dp, start = 4.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            ActionCard("□", "Inventory", Modifier.weight(1f)) { navigate(Screen.Inventory) }
            ActionCard("◉", "Shelf Monitor", Modifier.weight(1f)) { navigate(Screen.Shelf) }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            ActionCard("+", "Add Product", Modifier.weight(1f)) { navigate(Screen.ProductForm) }
            ActionCard("⇄", "Restock", Modifier.weight(1f)) { navigate(Screen.Restock) }
        }
    }
}

@Composable
private fun ActionCard(icon: String, label: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Column(
        modifier = modifier
            .height(112.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White)
            .border(1.dp, BorderGray, RoundedCornerShape(24.dp))
            .clickable(onClick = onClick)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(SoftGray), contentAlignment = Alignment.Center) {
            Text(icon, fontSize = 24.sp, color = Color.Black)
        }
        Spacer(Modifier.height(10.dp))
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Muted)
    }
}


@Composable
private fun InventoryScreen(viewModel: MainViewModel, navigate: (Screen) -> Unit) {
    val inventoryItems by viewModel.inventoryList.collectAsState()
    ScrollScreen(
        topBar = { TopBar("Inventory", right = { AppButton("+", modifier = Modifier.width(44.dp).height(44.dp), onClick = { navigate(Screen.ProductForm) }) }) }
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(value = "", onValueChange = {}, placeholder = { Text("Search perfumes...") }, modifier = Modifier.weight(1f), singleLine = true, shape = RoundedCornerShape(18.dp))
            Box(modifier = Modifier.size(56.dp).clip(RoundedCornerShape(18.dp)).background(SoftGray).border(1.dp, BorderGray, RoundedCornerShape(18.dp)), contentAlignment = Alignment.Center) { Text("☰", color = Muted) }
        }
        Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatusChip("All Items", ChipVariant.Black)
            StatusChip("Low Stock", ChipVariant.Outline)
            StatusChip("Missing", ChipVariant.Outline)
            StatusChip("Needs Review", ChipVariant.Outline)
        }
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            inventoryItems.forEach { item -> InventoryCard(item, onClick = { navigate(Screen.ProductDetail) }) }
        }
    }
}

@Composable
private fun InventoryCard(item: InventoryItem, onClick: () -> Unit) {
    val variant = when (item.status) {
        "normal" -> ChipVariant.Normal
        "missing" -> ChipVariant.Missing
        "low" -> ChipVariant.Low
        else -> ChipVariant.Review
    }
    val label = when (item.status) {
        "normal" -> "Normal"
        "missing" -> "Missing"
        "low" -> "Low Stock"
        else -> "Needs Review"
    }
    AppCard(background = Color.White, radius = 24.dp, onClick = onClick) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
            Column {
                Text(item.name, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = TextBlack)
                Text("${item.category} • ${item.shelf}", fontSize = 12.sp, color = Muted, modifier = Modifier.padding(top = 2.dp))
            }
            StatusChip(label, variant)
        }
        Spacer(Modifier.height(14.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
            Row(horizontalArrangement = Arrangement.spacedBy(22.dp)) {
                CountText("Recorded", item.recorded.toString())
                CountText("Detected", if (item.detected == -1) "?" else item.detected.toString())
            }
            Text(item.lastUpdated, fontSize = 12.sp, color = LightMuted)
        }
    }
}

@Composable
private fun CountText(label: String, value: String) {
    Column {
        Text(label.uppercase(), fontSize = 10.sp, letterSpacing = 1.sp, color = LightMuted, fontWeight = FontWeight.Medium)
        Text(value, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = TextBlack)
    }
}

@Composable
private fun ProductDetailScreen(navigate: (Screen) -> Unit, back: () -> Unit) {
    ScrollScreen(topBar = { TopBar("Product Detail", showBack = true, onBack = back, right = { Text("✎", modifier = Modifier.clickable { navigate(Screen.ProductForm) }.padding(8.dp), color = Muted, fontSize = 20.sp) }) }) {
        Box(modifier = Modifier.fillMaxWidth().aspectRatio(1.35f).clip(RoundedCornerShape(30.dp)).background(SoftGray), contentAlignment = Alignment.Center) {
            Text("No Image", color = LightMuted, fontWeight = FontWeight.Medium)
        }
        Column {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Midnight Oud", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = TextBlack)
                    Text("Otto Scents Classic • Woody", fontSize = 14.sp, color = Muted, modifier = Modifier.padding(top = 4.dp))
                }
                StatusChip("Normal Stock")
            }
        }
        AppCard(background = CardBg) {
            DetailRow("Branch", "San Pablo")
            Spacer(Modifier.height(12.dp)); ThinDivider(); Spacer(Modifier.height(12.dp))
            DetailRow("Shelf Area", "Area A1")
            Spacer(Modifier.height(12.dp)); ThinDivider(); Spacer(Modifier.height(12.dp))
            DetailRow("Bottle Size", "50ml")
            Spacer(Modifier.height(12.dp)); ThinDivider(); Spacer(Modifier.height(12.dp))
            DetailRow("Min. Threshold", "5 bottles")
        }
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp), modifier = Modifier.fillMaxWidth()) {
            CountCard("Recorded", "12", Modifier.weight(1f))
            CountCard("Detected", "12", Modifier.weight(1f), dark = true)
        }
        AppButton("Create Restock", onClick = { navigate(Screen.CreateRestock) })
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(SoftGray)
                .border(1.dp, BorderGray.copy(alpha = 0.6f), RoundedCornerShape(18.dp))
                .clickable { navigate(Screen.ShelfHistory) }
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("⌘  Recent Movement History", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextBlack)
            Text("View", fontSize = 12.sp, color = LightMuted)
        }
    }
}

@Composable
private fun CountCard(label: String, value: String, modifier: Modifier = Modifier, dark: Boolean = false) {
    AppCard(modifier = modifier, background = if (dark) Color(0xFF111827) else Color.White, border = if (dark) Color.Transparent else BorderGray) {
        Text(label.uppercase(), fontSize = 11.sp, letterSpacing = 1.sp, color = if (dark) Color(0xFFD1D5DB) else Muted, fontWeight = FontWeight.Medium)
        Text(value, fontSize = 34.sp, fontWeight = FontWeight.Light, color = if (dark) Color.White else TextBlack, modifier = Modifier.padding(top = 6.dp))
    }
}

@Composable
private fun ProductFormScreen(isEdit: Boolean, navigate: (Screen) -> Unit, back: () -> Unit) {
    var name by remember { mutableStateOf(if (isEdit) "Midnight Oud" else "") }
    var line by remember { mutableStateOf(if (isEdit) "Otto Scents Classic" else "") }
    var family by remember { mutableStateOf(if (isEdit) "Woody" else "") }
    var size by remember { mutableStateOf(if (isEdit) "50" else "") }
    var branch by remember { mutableStateOf(if (isEdit) "San Pablo" else "") }
    var area by remember { mutableStateOf(if (isEdit) "Area A1" else "") }
    var threshold by remember { mutableStateOf(if (isEdit) "5" else "") }
    var recorded by remember { mutableStateOf(if (isEdit) "12" else "") }

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
            FormField("Branch", branch, "Select branch") { branch = it }; ThinDivider()
            FormField("Shelf Area", area, "Area A1") { area = it }
        }
        Section("Stock") {
            FormField("Min. Threshold", threshold, "5", numeric = true) { threshold = it }; ThinDivider()
            FormField("Recorded Count", recorded, "0", numeric = true) { recorded = it }
        }
        AppButton(if (isEdit) "Save Changes" else "Add Product", onClick = back)
        if (isEdit) AppButton("Delete Product", variant = ButtonVariant.Danger, onClick = back)
        TextButton(onClick = back, modifier = Modifier.fillMaxWidth()) { Text("Cancel", color = Muted) }
    }
}

@Composable
private fun ShelfScreen(navigate: (Screen) -> Unit) {
    ScrollScreen(topBar = { TopBar("Shelf Monitor") }) {
        Box(modifier = Modifier.fillMaxWidth().aspectRatio(0.75f).clip(RoundedCornerShape(32.dp)).background(Color(0xFFE5E7EB)).border(1.dp, BorderGray, RoundedCornerShape(32.dp))) {
            Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) { Text("◉", color = LightMuted, fontSize = 32.sp); Text("Camera Feed", color = LightMuted, fontSize = 12.sp, fontWeight = FontWeight.Medium) }
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
            StatusChip("142 Detected", ChipVariant.Normal)
            StatusChip("3 Needs Review", ChipVariant.Review)
            StatusChip("1 Missing", ChipVariant.Missing)
        }
        Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(SoftGray).border(1.dp, BorderGray.copy(alpha = 0.5f), RoundedCornerShape(18.dp)).padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("☁  Cloud Sync Active", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Muted)
            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Green))
        }
        AppButton("◉  Run Shelf Check Now")
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

@Composable
private fun ShelfAreaDetailScreen(back: () -> Unit) {
    ScrollScreen(topBar = { TopBar("Area Details", showBack = true, onBack = back) }) {
        Box(modifier = Modifier.fillMaxWidth().aspectRatio(1.75f).clip(RoundedCornerShape(28.dp)).background(SoftGray).border(1.dp, BorderGray, RoundedCornerShape(28.dp)), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) { Text("◉", color = LightMuted, fontSize = 28.sp); Text("Area A2 Image Cropped", color = LightMuted, fontSize = 12.sp, fontWeight = FontWeight.Medium) }
        }
        Column {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Area A2", fontSize = 30.sp, fontWeight = FontWeight.Bold, color = TextBlack)
                StatusChip("Needs Review", ChipVariant.Review)
            }
            Text("Assigned: Rose Petal", color = Muted, fontWeight = FontWeight.Medium, modifier = Modifier.padding(top = 4.dp))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp), modifier = Modifier.fillMaxWidth()) {
            CountCard("Expected", "15", Modifier.weight(1f))
            CountCard("Detected", "14", Modifier.weight(1f), dark = false)
        }
        Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).border(1.dp, BorderGray, RoundedCornerShape(18.dp)).padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("⚠", fontSize = 24.sp, color = LightMuted)
            Spacer(Modifier.width(12.dp))
            Column {
                Text("System confidence: Low", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextBlack)
                Text("Please check manually if a bottle is missing or blocked.", fontSize = 12.sp, color = Muted, modifier = Modifier.padding(top = 2.dp))
            }
        }
        AppButton("✓  Mark as Verified")
        AppButton("⚠  Report Issue", variant = ButtonVariant.Outline)
    }
}

@Composable
private fun TemperatureMonitorScreen(navigate: (Screen) -> Unit, back: () -> Unit) {
    val data = listOf(21f, 22f, 22.5f, 23f, 24.5f, 23.5f, 22.4f)
    val labels = listOf("08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00")
    ScrollScreen(topBar = { TopBar("Climate Control", showBack = true, onBack = back) }) {
        Column(modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier.size(196.dp).clip(CircleShape).border(6.dp, Color(0xFFF3F4F6), CircleShape), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.Top) { Text("22.4", fontSize = 56.sp, fontWeight = FontWeight.Light, color = TextBlack); Text("°C", fontSize = 24.sp, color = LightMuted, modifier = Modifier.padding(top = 5.dp)) }
                    Text("Current Temp", fontSize = 14.sp, color = Muted, fontWeight = FontWeight.Medium)
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 18.dp)) { StatusChip("Safe Range: 18°C - 24°C"); StatusChip("Normal") }
        }
        AppCard(background = SoftGray) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) { Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(Color.White).border(1.dp, BorderGray, CircleShape), contentAlignment = Alignment.Center) { Text("☁") }; Spacer(Modifier.width(12.dp)); Column { Text("Cooling Fan", fontWeight = FontWeight.SemiBold); Text("Last active: 12:00 PM", fontSize = 12.sp, color = Muted) } }
                StatusChip("Off", ChipVariant.Outline)
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

@Composable
private fun ScheduleScreen(back: () -> Unit) {
    var mode by remember { mutableStateOf("interval") }
    ScrollScreen(topBar = { TopBar("Capture Schedule", showBack = true, onBack = back) }) {
        Text("The shelf will take a photo based on this schedule to update inventory.", fontSize = 14.sp, color = Muted, lineHeight = 21.sp)
        Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(SoftGray).border(1.dp, BorderGray.copy(alpha = 0.5f), RoundedCornerShape(18.dp)).padding(4.dp)) {
            TabPill("Interval", mode == "interval", Modifier.weight(1f)) { mode = "interval" }
            TabPill("Specific Times", mode == "specific", Modifier.weight(1f)) { mode = "specific" }
        }
        if (mode == "interval") {
            listOf("Every 30 minutes", "Every hour", "Every 2 hours", "Every 3 hours").forEachIndexed { i, opt ->
                Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).border(1.dp, BorderGray, RoundedCornerShape(18.dp)).padding(14.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(opt, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    RadioButton(selected = i == 1, onClick = {})
                }
            }
            Text("+ Custom interval", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Muted, modifier = Modifier.padding(14.dp))
        } else {
            listOf("09:00 AM", "12:00 PM", "03:00 PM", "06:00 PM").forEach { time ->
                Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).border(1.dp, BorderGray, RoundedCornerShape(18.dp)).background(Color.White).padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("◷  $time", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    Text("⌫", color = LightMuted)
                }
            }
            Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).border(1.dp, BorderGray, RoundedCornerShape(18.dp)).padding(16.dp), contentAlignment = Alignment.Center) { Text("+  Add Time", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Muted) }
        }
        Spacer(Modifier.height(10.dp))
        AppButton("Save Schedule")
    }
}

@Composable
private fun TabPill(label: String, active: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(modifier = modifier.height(42.dp).clip(RoundedCornerShape(14.dp)).background(if (active) Color.White else Color.Transparent).clickable(onClick = onClick), contentAlignment = Alignment.Center) {
        Text(label, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = if (active) TextBlack else Muted)
    }
}


@Composable
private fun AlertsScreen(viewModel: MainViewModel, navigate: (Screen) -> Unit) {
    val alerts by viewModel.alertsList.collectAsState()
    ScrollScreen(topBar = { TopBar("Alerts") }) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text("Recent Notifications", fontSize = 14.sp, fontWeight = FontWeight.SemiBold); Text("Mark all read", fontSize = 12.sp, color = Muted, fontWeight = FontWeight.Medium) }
        alerts.forEach { alert ->
            val variant = when (alert.type) { "critical" -> ChipVariant.Critical; "warning" -> ChipVariant.Warning; else -> ChipVariant.Normal }
            Row(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(28.dp)).background(Color.White).border(1.dp, BorderGray.copy(alpha = 0.5f), RoundedCornerShape(28.dp)).clickable { if (alert.type == "cloud") navigate(Screen.CloudBackup) else navigate(Screen.ProductDetail) }.padding(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(if (variant == ChipVariant.Critical) RedBg else if (variant == ChipVariant.Warning) OrangeBg else SoftGray), contentAlignment = Alignment.Center) { Text(if (alert.type == "cloud") "☁" else "!", color = if (variant == ChipVariant.Critical) Red else if (variant == ChipVariant.Warning) Orange else Muted) }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text(alert.title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold); Text(alert.time, fontSize = 10.sp, color = LightMuted) }
                    Text(alert.desc, fontSize = 12.sp, color = Muted, lineHeight = 18.sp, modifier = Modifier.padding(top = 4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 12.dp)) { StatusChip(alert.branch); StatusChip("View", ChipVariant.Black, small = true) }
                }
            }
        }
    }
}

@Composable
private fun ReportsScreen(back: () -> Unit) {
    ScrollScreen(background = AppBg, topBar = { TopBar("Reports", showBack = true, onBack = back) }) {
        AppCard(background = CardBg) {
            Text("Inventory Summary", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextBlack)
            Text("Overview of stock and monitoring records", fontSize = 12.sp, color = Muted, modifier = Modifier.padding(top = 4.dp))
        }
        listOf(
            "Inventory Summary" to "Total bottles, detected count, and stock status",
            "Stock Movement Report" to "Removal, return, restock, and verification records",
            "Temperature Report" to "Shelf temperature readings and alert history",
            "Fan Activity Report" to "Cooling fan activation and stop records",
            "Access Log Report" to "Lipa branch access control events",
            "Exception Report" to "Detection mismatch, blocked view, and sync issues"
        ).forEach { (title, desc) ->
            Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(Color.White).border(1.dp, BorderGray.copy(alpha = 0.55f), RoundedCornerShape(20.dp)).padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) { Text(title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold); Text(desc, fontSize = 12.sp, color = Muted, modifier = Modifier.padding(top = 3.dp)) }
                Text("View", fontSize = 12.sp, color = LightMuted)
            }
        }
    }
}

@Composable
private fun SettingsScreen(viewModel: MainViewModel, navigate: (Screen) -> Unit, onLogout: () -> Unit) {
    ScrollScreen(topBar = { TopBar("Settings") }) {
        AppCard(background = Color(0xFF111827), border = Color.Transparent) {
            val userRole by viewModel.userRole.collectAsState()
            val userBranch by viewModel.userBranch.collectAsState()
            val email = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.email ?: "Unknown User"
            Text(email, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("${userRole?.uppercase() ?: "STAFF"} • ${userBranch ?: "Unknown"}", color = Color(0xFFD1D5DB), fontSize = 13.sp, modifier = Modifier.padding(top = 4.dp))
        }
        val role by viewModel.userRole.collectAsState()
        SettingSection("System") {
            SettingRow("Seed Database", "Click to populate perfumes") { viewModel.seedDatabase() }
            if (role == "admin") {
                SettingRow("Capture Schedule", "Every hour") { navigate(Screen.Schedule) }
                SettingRow("Temperature Threshold", "25°C") { navigate(Screen.Temperature) }
                SettingRow("Low Stock Threshold", "5 bottles") {}
            }
            SettingRow("Cloud Sync Status", "Connected") {}
        }
        if (role == "admin") {
            SettingSection("Reports") { SettingRow("Reports", null) { navigate(Screen.Reports) }; SettingRow("System Activity Logs", null) { navigate(Screen.SystemLogs) } }
        }
        SettingSection("Help & Guides") { SettingRow("Help & Guides", null) { navigate(Screen.Help) } }
        AppButton("Logout", variant = ButtonVariant.Outline, onClick = onLogout)
    }
}

@Composable
private fun SettingSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title.uppercase(), fontSize = 11.sp, color = LightMuted, fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp, modifier = Modifier.padding(start = 4.dp, top = 8.dp))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp), content = content)
    }
}

@Composable
private fun SettingRow(title: String, value: String?, onClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(SoftGray).border(1.dp, BorderGray.copy(alpha = 0.5f), RoundedCornerShape(18.dp)).clickable(onClick = onClick).padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(title, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextBlack)
        Row(verticalAlignment = Alignment.CenterVertically) { if (value != null) Text(value, fontSize = 12.sp, color = Muted, fontWeight = FontWeight.Medium); Text("  ›", color = LightMuted, fontSize = 18.sp) }
    }
}

@Composable
private fun ShelfCheckHistoryScreen(back: () -> Unit) {
    val items = listOf(
        HistoryItem("May 1, 2026", "2:30 PM", "San Pablo", "completed"), HistoryItem("May 1, 2026", "8:15 AM", "San Pablo", "needs_review"), HistoryItem("Apr 30, 2026", "6:45 PM", "San Pablo", "cloud_checked"), HistoryItem("Apr 30, 2026", "2:00 PM", "San Pablo", "failed"), HistoryItem("Apr 30, 2026", "8:00 AM", "San Pablo", "completed"), HistoryItem("Apr 29, 2026", "6:30 PM", "San Pablo", "needs_review")
    )
    ScrollScreen(background = AppBg, topBar = { TopBar("Shelf Check History", showBack = true, onBack = back) }) {
        AppCard(background = CardBg) { Text("Midnight Oud", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, fontSize = 18.sp, fontWeight = FontWeight.Bold); Text("Area A1 • San Pablo", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, fontSize = 12.sp, color = Muted) }
        Text("Recent Checks".uppercase(), fontSize = 11.sp, color = Muted, fontWeight = FontWeight.Medium, letterSpacing = 1.8.sp)
        items.forEach { h ->
            val label = when (h.status) { "completed" -> "Completed"; "needs_review" -> "Needs Review"; "cloud_checked" -> "Cloud Checked"; else -> "Failed" }
            val variant = when (h.status) { "completed" -> ChipVariant.Green; "needs_review" -> ChipVariant.Warning; "cloud_checked" -> ChipVariant.Blue; else -> ChipVariant.Critical }
            Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(Color.White).border(1.dp, BorderGray.copy(alpha = 0.5f), RoundedCornerShape(18.dp)).padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column { Text("Shelf Check", fontSize = 14.sp, fontWeight = FontWeight.Medium); Text("${h.date} • ${h.time}", fontSize = 12.sp, color = Muted); Text(h.branch, fontSize = 12.sp, color = Muted) }
                StatusChip(label, variant, small = true)
            }
        }
    }
}

@Composable
private fun CloudBackupCheckScreen(navigate: (Screen) -> Unit, back: () -> Unit) {
    var processing by remember { mutableStateOf(false) }
    ScrollScreen(background = AppBg, topBar = { TopBar("Cloud Backup Check", showBack = true, onBack = back) }) {
        AppCard(background = YellowBg, border = Color(0xFFFDE68A)) { Text("⚠  Unclear Results", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = TextBlack); Text("The local check produced ambiguous detection results.", fontSize = 14.sp, color = Muted, modifier = Modifier.padding(top = 4.dp)) }
        AppCard(background = CardBg) {
            Text("✓  Cloud Processing Complete", fontSize = 16.sp, color = Green, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(14.dp))
            DetailRow("Product", "Midnight Oud"); ThinDivider(); DetailRow("Branch", "San Pablo"); ThinDivider(); DetailRow("Shelf Area", "Area A1"); ThinDivider(); DetailRow("Timestamp", "May 1, 2026 • 2:30 PM")
        }
        Text("Processed Image".uppercase(), fontSize = 11.sp, color = Muted, fontWeight = FontWeight.Medium, letterSpacing = 1.8.sp)
        Box(modifier = Modifier.fillMaxWidth().aspectRatio(1.75f).clip(RoundedCornerShape(28.dp)).background(SoftGray).border(1.dp, BorderGray, RoundedCornerShape(28.dp)), contentAlignment = Alignment.Center) { Column(horizontalAlignment = Alignment.CenterHorizontally) { Text("☁", fontSize = 30.sp, color = LightMuted); Text("Image processed by cloud", fontSize = 14.sp, color = LightMuted) } }
        Text("Detection Results".uppercase(), fontSize = 11.sp, color = Muted, fontWeight = FontWeight.Medium, letterSpacing = 1.8.sp)
        AppCard(background = Color.White) {
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp), modifier = Modifier.fillMaxWidth()) { CountCard("Bottles Detected", "11", Modifier.weight(1f)); CountCard("Confidence", "87%", Modifier.weight(1f)) }
            Spacer(Modifier.height(16.dp)); ThinDivider(); Spacer(Modifier.height(12.dp))
            Text("⚠  1 item needs manual review", fontSize = 14.sp, color = Muted)
        }
        AppButton(if (processing) "Accepting..." else "Accept Result", enabled = !processing, onClick = { processing = true; back() })
        AppButton("Manual Review", variant = ButtonVariant.Outline, onClick = { navigate(Screen.ProductDetail) })
    }
}

@Composable
private fun StockMovementLogsScreen(viewModel: MainViewModel, navigate: (Screen) -> Unit, back: () -> Unit) {
    val logs by viewModel.movementLogs.collectAsState()
    ScrollScreen(background = AppBg, topBar = { TopBar("Stock Movement Logs", showBack = true, onBack = back) }) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text("Recent Movements".uppercase(), fontSize = 11.sp, color = Muted, fontWeight = FontWeight.Medium, letterSpacing = 1.8.sp); StatusChip("Filter", ChipVariant.Outline, small = true) }
        logs.forEach { log -> MovementLogCard(log) { navigate(Screen.ProductDetail) } }
    }
}

@Composable
private fun MovementLogCard(log: MovementLog, onClick: () -> Unit) {
    val label = when (log.status) { "present" -> "Bottle Present"; "missing" -> "Bottle Missing"; "returned" -> "Bottle Returned"; "restocked" -> "Restocked"; "verified" -> "Manually Verified"; else -> "Needs Review" }
    val variant = when (log.status) { "missing" -> ChipVariant.Critical; "needs_review" -> ChipVariant.Warning; else -> ChipVariant.Normal }
    Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(Color.White).border(1.dp, BorderGray.copy(alpha = 0.5f), RoundedCornerShape(18.dp)).clickable(onClick = onClick).padding(16.dp)) {
        Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(if (variant == ChipVariant.Critical) RedBg else if (variant == ChipVariant.Warning) YellowBg else GreenBg), contentAlignment = Alignment.Center) { Text(if (log.status == "missing") "!" else "✓") }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Column { Text(label, fontSize = 14.sp, fontWeight = FontWeight.Medium); Text(log.productName, fontSize = 14.sp, fontWeight = FontWeight.SemiBold) }; StatusChip(log.quantity.toString(), variant, small = true) }
            Text("${log.shelfArea} • ${log.branch} • ${log.timestamp}", fontSize = 12.sp, color = Muted, modifier = Modifier.padding(top = 8.dp))
        }
    }
}

@Composable
private fun RestockManagementScreen(viewModel: MainViewModel, navigate: (Screen) -> Unit, back: () -> Unit) {
    var tab by remember { mutableStateOf("requests") }
    val data by viewModel.restockRequests.collectAsState()
    val filtered = data.filter { when (tab) { "requests" -> it.status == "pending"; "transfers" -> it.status == "in_transit"; "received" -> it.status == "received"; else -> it.status == "completed" } }
    ScrollScreen(background = AppBg, topBar = { TopBar("Restock Management", showBack = true, onBack = back, right = { StatusChip("New Request", ChipVariant.Black, small = true, modifier = Modifier.clickable { navigate(Screen.CreateRestock) }) }) }) {
        Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(CardBg).border(1.dp, BorderGray.copy(alpha = 0.5f), RoundedCornerShape(18.dp)).padding(6.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            TabPill("Requests", tab == "requests", Modifier.weight(1f)) { tab = "requests" }
            TabPill("Transit", tab == "transfers", Modifier.weight(1f)) { tab = "transfers" }
            TabPill("Received", tab == "received", Modifier.weight(1f)) { tab = "received" }
            TabPill("Done", tab == "completed", Modifier.weight(1f)) { tab = "completed" }
        }
        filtered.forEach { item -> RestockCard(item, onClick = { navigate(Screen.ProductDetail) }, verify = { navigate(Screen.ReceivingVerification) }) }
    }
}

@Composable
private fun RestockCard(item: RestockItem, onClick: () -> Unit, verify: () -> Unit) {
    val label = when (item.status) { "pending" -> "Pending Request"; "in_transit" -> "In Transit"; "received" -> "Received"; else -> "Completed" }
    val variant = if (item.status == "pending") ChipVariant.Warning else ChipVariant.Normal
    AppCard(background = Color.White, radius = 18.dp, onClick = onClick) {
        Row(verticalAlignment = Alignment.Top) {
            Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(if (item.status == "pending") YellowBg else if (item.status == "in_transit") BlueBg else if (item.status == "received") GreenBg else SoftGray), contentAlignment = Alignment.Center) { Text(if (item.status == "in_transit") "⇄" else "□") }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) { Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text(item.productName, fontSize = 14.sp, fontWeight = FontWeight.SemiBold); StatusChip(item.quantity.toString(), variant, small = true) }; Text(label, fontSize = 12.sp, color = Muted, modifier = Modifier.padding(top = 3.dp)) }
        }
        Spacer(Modifier.height(12.dp)); ThinDivider(); Spacer(Modifier.height(10.dp))
        Text("From: ${item.fromBranch}  ⇄  To: ${item.toBranch}", fontSize = 12.sp, color = TextBlack, fontWeight = FontWeight.Medium)
        Text("Requested by: ${item.requestedBy}        ${item.requestedDate}", fontSize = 12.sp, color = Muted, modifier = Modifier.padding(top = 5.dp))
        if (item.estimatedArrival != null) Text("Est. arrival: ${item.estimatedArrival}", fontSize = 12.sp, color = Blue, fontWeight = FontWeight.Medium, modifier = Modifier.padding(top = 5.dp))
        if (item.completedDate != null) Text("Completed: ${item.completedDate}", fontSize = 12.sp, color = Muted, modifier = Modifier.padding(top = 5.dp))
        if (item.status == "received") { Spacer(Modifier.height(10.dp)); AppButton("Verify & Complete", onClick = verify) }
    }
}

@Composable
private fun CreateRestockRequestScreen(back: () -> Unit) {
    var source by remember { mutableStateOf("") }; var destination by remember { mutableStateOf("") }; var product by remember { mutableStateOf("") }; var quantity by remember { mutableStateOf("") }; var shelf by remember { mutableStateOf("") }; var notes by remember { mutableStateOf("") }
    val invalid = source.isNotBlank() && destination.isNotBlank() && source == destination
    ScrollScreen(background = AppBg, topBar = { TopBar("Create Restock Request", showBack = true, onBack = back) }) {
        AppCard(background = CardBg) { Text("⇄  Branch Transfer Request", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, fontSize = 14.sp, color = Muted, fontWeight = FontWeight.Medium) }
        Section("Source & Destination") { FormField("From Branch", source, "Select source") { source = it }; ThinDivider(); FormField("To Branch", destination, "Select destination") { destination = it } }
        Section("Product Details") { FormField("Product", product, "Select product") { product = it }; ThinDivider(); FormField("Quantity", quantity, "0", numeric = true) { quantity = it }; ThinDivider(); FormField("Target Shelf Area", shelf, "Select area") { shelf = it } }
        Section("Additional Information") { OutlinedTextField(value = notes, onValueChange = { notes = it }, placeholder = { Text("Optional notes or special instructions...") }, modifier = Modifier.fillMaxWidth().height(110.dp), shape = RoundedCornerShape(18.dp)) }
        if (invalid) AppCard(background = RedBg, border = Color(0xFFFECACA), radius = 16.dp) { Text("Source and destination branches must be different", fontSize = 14.sp, color = Red) }
        AppButton("Submit Request", enabled = !invalid && source.isNotBlank() && destination.isNotBlank() && product.isNotBlank() && quantity.isNotBlank() && shelf.isNotBlank(), onClick = back)
        TextButton(onClick = back, modifier = Modifier.fillMaxWidth()) { Text("Cancel", color = Muted) }
    }
}

@Composable
private fun ReceivingVerificationScreen(navigate: (Screen) -> Unit, back: () -> Unit) {
    var confirming by remember { mutableStateOf(false) }
    val expected = 15
    val detected = 15
    val difference = detected - expected
    val title = if (difference == 0) "Quantities Match" else if (kotlin.math.abs(difference) <= 2) "Review Required" else "Quantity Mismatch"
    val desc = if (difference == 0) "The detected quantity matches the expected restock amount." else "Manual verification required."
    val color = if (difference == 0) Green else if (kotlin.math.abs(difference) <= 2) Yellow else Red
    val bg = if (difference == 0) GreenBg else if (kotlin.math.abs(difference) <= 2) YellowBg else RedBg
    ScrollScreen(background = AppBg, topBar = { TopBar("Receiving Verification", showBack = true, onBack = back) }) {
        AppCard(background = bg, border = color.copy(alpha = 0.25f)) { Text("✓", fontSize = 42.sp, color = color, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center); Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = color, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()); Text(desc, fontSize = 14.sp, color = Muted, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) }
        AppCard(background = CardBg) { Text("Ocean Breeze", fontSize = 16.sp, fontWeight = FontWeight.SemiBold); Text("Lipa → San Pablo", fontSize = 12.sp, color = Muted, modifier = Modifier.padding(top = 3.dp)); Spacer(Modifier.height(12.dp)); DetailRow("Shelf Area", "Area B3"); ThinDivider(); DetailRow("Requested By", "Maria Santos"); ThinDivider(); DetailRow("Transfer Date", "May 1, 2026") }
        Text("Quantity Comparison".uppercase(), fontSize = 11.sp, color = Muted, fontWeight = FontWeight.Medium, letterSpacing = 1.8.sp)
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp), modifier = Modifier.fillMaxWidth()) { CountCard("Expected", expected.toString(), Modifier.weight(1f)); CountCard("Detected", detected.toString(), Modifier.weight(1f), dark = true) }
        AppCard(background = Color.White) { Text("Shelf Check Result", fontSize = 14.sp, fontWeight = FontWeight.Medium); Spacer(Modifier.height(10.dp)); DetailRow("Check Method", "AI Detection"); ThinDivider(); DetailRow("Confidence", "95%", Green); ThinDivider(); DetailRow("Check Time", "2 mins ago") }
        AppButton(if (confirming) "Confirming..." else "Confirm & Complete", enabled = !confirming, onClick = { confirming = true; back() })
        AppButton("Manual Recount", variant = ButtonVariant.Outline, onClick = { navigate(Screen.ShelfAreaDetail) })
        TextButton(onClick = back, modifier = Modifier.fillMaxWidth()) { Text("Cancel", color = Muted) }
    }
}

@Composable
private fun FanActivityLogsScreen(viewModel: MainViewModel, back: () -> Unit) {
    val logs by viewModel.fanLogs.collectAsState()
    ScrollScreen(background = AppBg, topBar = { TopBar("Fan Activity Logs", showBack = true, onBack = back) }) {
        AppCard(background = CardBg) { Text("Cooling system activity records", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, fontSize = 14.sp, color = Muted); Text("Fan activates when temperature exceeds 25°C", modifier = Modifier.fillMaxWidth().padding(top = 4.dp), textAlign = TextAlign.Center, fontSize = 12.sp, color = LightMuted) }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text("Recent Activity".uppercase(), fontSize = 11.sp, color = Muted, fontWeight = FontWeight.Medium, letterSpacing = 1.8.sp); StatusChip("Filter", ChipVariant.Outline, small = true) }
        logs.forEach { FanActivityCard(it) }
    }
}

@Composable
private fun FanActivityCard(log: FanActivity) {
    val label = when (log.status) { "active" -> "Currently Running"; "manual_stop" -> "Manually Stopped"; else -> "Auto Stopped" }
    val variant = if (log.status == "manual_stop") ChipVariant.Warning else ChipVariant.Normal
    AppCard(background = Color.White, radius = 18.dp) {
        Row(verticalAlignment = Alignment.Top) { Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(if (log.status == "active") BlueBg else SoftGray), contentAlignment = Alignment.Center) { Text("☁") }; Spacer(Modifier.width(12.dp)); Column(modifier = Modifier.weight(1f)) { Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text(label, fontSize = 14.sp, fontWeight = FontWeight.Medium); StatusChip(log.status.replace("_", " "), variant, small = true) }; Text("${log.branch} • ${log.shelfArea}", fontSize = 12.sp, color = Muted, modifier = Modifier.padding(top = 4.dp)) } }
        Spacer(Modifier.height(12.dp)); ThinDivider(); Spacer(Modifier.height(10.dp))
        DetailRow("Trigger Temp:", "${log.triggerTemperature}°C", Red); DetailRow("Started:", log.startTime); if (log.stopTime != null) DetailRow("Stopped:", log.stopTime); ThinDivider(); DetailRow("Total Duration", log.duration)
        if (log.status == "active") Text("● Fan is currently running", fontSize = 12.sp, color = Blue, fontWeight = FontWeight.Medium, modifier = Modifier.padding(top = 8.dp))
    }
}

@Composable
private fun HelpGuideScreen(back: () -> Unit) {
    var expanded by remember { mutableStateOf("needs_review") }
    val topics = listOf(
        HelpTopic("needs_review", "Understanding \"Needs Review\"", "Learn what this status means and how to handle it", listOf("When you see 'Needs Review', it means the smart shelf detected something unclear or unexpected.", "This could be due to poor lighting, bottles placed incorrectly, or new products not yet in the system.", "To resolve: Go to the shelf, check the bottles manually, and confirm the actual count in the app.", "You can also take a new photo to help the system learn better.")),
        HelpTopic("verify_shelf", "Verifying Shelf Results", "How to confirm what the camera detected", listOf("After each shelf check, the app shows you how many bottles were detected.", "Compare this number with what you see on the shelf.", "If the numbers match, tap 'Confirm' to accept the result.", "If they don't match, tap 'Needs Review' and count manually.", "Always verify important changes like restocks or missing bottles.")),
        HelpTopic("schedule", "Changing Shelf Check Schedule", "Adjust how often the system checks inventory", listOf("Go to Settings → Shelf Check Schedule.", "You can choose how often the camera checks the shelf (every 1, 2, 4, or 8 hours).", "For busy stores, check more often (every 1-2 hours).", "For slower stores, every 4-8 hours is enough.", "Changes apply immediately after you save.")),
        HelpTopic("temperature", "Responding to Temperature Alerts", "What to do when the shelf gets too hot", listOf("Perfumes should be stored below 25°C to maintain quality.", "When temperature goes above 25°C, the cooling fan turns on automatically.", "If you get an alert, check that the fan is working (you'll hear it running).", "Make sure nothing is blocking the fan vents.", "If temperature stays high, move bottles to a cooler area temporarily.")),
        HelpTopic("cloud_backup", "Handling Cloud Backup Checks", "When the local camera needs cloud help", listOf("Sometimes the camera on the shelf can't get a clear result (poor light, unclear image).", "When this happens, the photo is sent to the cloud for better processing.", "You'll get a notification when cloud processing is done.", "Review the cloud result and confirm if it looks correct.", "If still unclear, you can manually count and update the inventory."))
    )
    ScrollScreen(background = AppBg, topBar = { TopBar("Help & Guide", showBack = true, onBack = back) }) {
        AppCard(background = CardBg) { Text("Quick Help Guide", fontSize = 16.sp, fontWeight = FontWeight.SemiBold); Text("Simple instructions for using the smart shelf monitoring app.", fontSize = 14.sp, color = Muted, modifier = Modifier.padding(top = 4.dp)) }
        Text("Common Topics".uppercase(), fontSize = 11.sp, color = Muted, fontWeight = FontWeight.Medium, letterSpacing = 1.8.sp)
        topics.forEach { topic -> HelpTopicCard(topic, expanded == topic.id, onClick = { expanded = if (expanded == topic.id) "" else topic.id }) }
        AppCard(background = Color.White) { Text("Need More Help?", fontSize = 14.sp, fontWeight = FontWeight.SemiBold); Text("Contact your system administrator for technical support or hardware issues.", fontSize = 14.sp, color = Muted, modifier = Modifier.padding(top = 6.dp, bottom = 14.dp)); AppButton("Contact Support") }
    }
}

@Composable
private fun HelpTopicCard(topic: HelpTopic, expanded: Boolean, onClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(Color.White).border(1.dp, BorderGray.copy(alpha = 0.5f), RoundedCornerShape(18.dp))) {
        Row(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) { Text(topic.title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold); Text(topic.description, fontSize = 12.sp, color = Muted, modifier = Modifier.padding(top = 2.dp)) }
            Text(if (expanded) "−" else "+", fontSize = 24.sp, color = Muted)
        }
        if (expanded) {
            ThinDivider()
            Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(16.dp)) { topic.paragraphs.forEachIndexed { index, paragraph -> Row { Text("${index + 1}.", fontSize = 14.sp, color = Muted, modifier = Modifier.width(24.dp)); Text(paragraph, fontSize = 14.sp, color = TextBlack, lineHeight = 20.sp) } } }
        }
    }
}

@Composable
private fun SystemActivityLogsScreen(viewModel: MainViewModel, back: () -> Unit) {
    val activities by viewModel.systemLogs.collectAsState()
    ScrollScreen(background = AppBg, topBar = { TopBar("System Activity Logs", showBack = true, onBack = back) }) {
        AppCard(background = CardBg) { Text("System events and user actions", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, fontSize = 14.sp, color = Muted); Text("Track all important activities across branches", modifier = Modifier.fillMaxWidth().padding(top = 4.dp), textAlign = TextAlign.Center, fontSize = 12.sp, color = LightMuted) }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text("Recent Activity".uppercase(), fontSize = 11.sp, color = Muted, fontWeight = FontWeight.Medium, letterSpacing = 1.8.sp); StatusChip("Filter", ChipVariant.Outline, small = true) }
        activities.forEach { ActivityCard(it) }
    }
}

@Composable
private fun ActivityCard(activity: SystemActivity) {
    val label = when (activity.type) { "user_login" -> "User Login"; "product_update" -> "Product Update"; "schedule_change" -> "Schedule Change"; "shelf_check" -> "Shelf Check"; "inventory_update" -> "Inventory Update"; "temperature_alert" -> "Temperature Alert"; "fan_activation" -> "Fan Activation"; "cloud_backup" -> "Cloud Backup"; else -> "Synchronization" }
    val bg = when (activity.type) { "temperature_alert" -> RedBg; "inventory_update" -> GreenBg; "cloud_backup" -> IndigoBg; "sync_event" -> TealBg; "product_update" -> PurpleBg; "schedule_change" -> OrangeBg; "fan_activation" -> BlueBg; else -> SoftGray }
    Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(Color.White).border(1.dp, BorderGray.copy(alpha = 0.5f), RoundedCornerShape(18.dp)).padding(16.dp), verticalAlignment = Alignment.Top) {
        Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(bg), contentAlignment = Alignment.Center) { Text("•") }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text(label.uppercase(), fontSize = 11.sp, color = Muted, fontWeight = FontWeight.Medium, letterSpacing = 1.sp); Text(activity.timestamp.substringAfter("•").trim(), fontSize = 12.sp, color = LightMuted) }
            Text(activity.description, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextBlack, modifier = Modifier.padding(top = 4.dp))
            Text(listOfNotNull(activity.user, activity.branch, activity.timestamp.substringBefore("•").trim()).joinToString(" • "), fontSize = 12.sp, color = Muted, modifier = Modifier.padding(top = 6.dp))
        }
    }
}
