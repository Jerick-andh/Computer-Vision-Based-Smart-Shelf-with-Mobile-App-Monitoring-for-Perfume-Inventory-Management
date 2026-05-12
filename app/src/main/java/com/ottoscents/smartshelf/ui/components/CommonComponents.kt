package com.ottoscents.smartshelf.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ottoscents.smartshelf.Screen

// Colors
val AppBg = Color(0xFFF0EDE8)
val CardBg = Color(0xFFF5F3EE)
val SoftGray = Color(0xFFF7F7F7)
val BorderGray = Color(0xFFE5E7EB)
val TextBlack = Color(0xFF111827)
val Muted = Color(0xFF6B7280)
val LightMuted = Color(0xFF9CA3AF)
val Red = Color(0xFFDC2626)
val RedBg = Color(0xFFFEF2F2)
val Orange = Color(0xFFEA580C)
val OrangeBg = Color(0xFFFFF7ED)
val Green = Color(0xFF16A34A)
val GreenBg = Color(0xFFF0FDF4)
val Blue = Color(0xFF2563EB)
val BlueBg = Color(0xFFEFF6FF)
val Yellow = Color(0xFFCA8A04)
val YellowBg = Color(0xFFFEFCE8)
val PurpleBg = Color(0xFFFAF5FF)
val IndigoBg = Color(0xFFEEF2FF)
val TealBg = Color(0xFFF0FDFA)

enum class ChipVariant { Normal, Missing, Review, Low, Returned, Outline, Black, Warning, Critical, Green, Blue }
enum class ButtonVariant { Primary, Secondary, Outline, Ghost, Danger }

@Composable
fun TopBar(title: String, showBack: Boolean = false, onBack: (() -> Unit)? = null, right: (@Composable () -> Unit)? = null) {
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
fun ScrollScreen(background: Color = Color.White, topBar: @Composable () -> Unit, content: @Composable ColumnScope.() -> Unit) {
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
fun AppCard(
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

@Composable
fun StatusChip(text: String, variant: ChipVariant = ChipVariant.Normal, small: Boolean = false, @SuppressLint(
    "ModifierParameter"
) modifier: Modifier = Modifier) {
    val colors = when (variant) {
        ChipVariant.Normal -> Color(0xFFF3F4F6) to Muted
        ChipVariant.Missing, ChipVariant.Critical -> RedBg to Red
        ChipVariant.Review, ChipVariant.Warning -> OrangeBg to Orange
        ChipVariant.Low -> YellowBg to Yellow
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
fun AppButton(
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
fun ThinDivider() = HorizontalDivider(color = BorderGray.copy(alpha = 0.6f), thickness = 1.dp)

@Composable
fun DetailRow(label: String, value: String, valueColor: Color = TextBlack) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(label, color = Muted, fontSize = 14.sp)
        Text(value, color = valueColor, fontSize = 14.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.End)
    }
}

@Composable
fun Section(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title.uppercase(), fontSize = 11.sp, fontWeight = FontWeight.Medium, letterSpacing = 1.8.sp, color = Muted, modifier = Modifier.padding(start = 4.dp))
        AppCard(background = CardBg, border = BorderGray.copy(alpha = 0.45f), content = content)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormField(label: String, value: String, placeholder: String = "", numeric: Boolean = false, onChange: (String) -> Unit = {}) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Muted, fontSize = 14.sp, modifier = Modifier.weight(0.9f))
        OutlinedTextField(
            value = value,
            onValueChange = onChange,
            placeholder = { Text(placeholder, color = LightMuted) },
            singleLine = true,
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = if (numeric) androidx.compose.ui.text.input.KeyboardType.Number else androidx.compose.ui.text.input.KeyboardType.Text),
            modifier = Modifier.weight(1.3f),
            textStyle = androidx.compose.ui.text.TextStyle(textAlign = TextAlign.End, fontSize = 14.sp, fontWeight = FontWeight.Medium),
            shape = RoundedCornerShape(14.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormDropdownField(
    label: String,
    value: String,
    options: List<String>,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Muted, fontSize = 14.sp, modifier = Modifier.weight(0.9f))
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.weight(1.3f)
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                modifier = Modifier.menuAnchor(),
                textStyle = androidx.compose.ui.text.TextStyle(textAlign = TextAlign.End, fontSize = 14.sp, fontWeight = FontWeight.Medium),
                shape = RoundedCornerShape(14.dp)
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { selectionOption ->
                    DropdownMenuItem(
                        text = { Text(selectionOption) },
                        onClick = {
                            onSelect(selectionOption)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun BottomNav(current: Screen, onNavigate: (Screen) -> Unit) {
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
