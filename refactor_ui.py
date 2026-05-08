import re

def refactor_ui():
    with open('app/src/main/java/com/ottoscents/smartshelf/MainActivity.kt', 'r', encoding='utf-8') as f:
        content = f.read()

    # 1. Update OttoScentsApp invocations
    content = content.replace('Screen.Inventory -> InventoryScreen(::navigate)', 'Screen.Inventory -> InventoryScreen(viewModel, ::navigate)')
    content = content.replace('Screen.Alerts -> AlertsScreen(::navigate)', 'Screen.Alerts -> AlertsScreen(viewModel, ::navigate)')
    content = content.replace('Screen.StockLogs -> StockMovementLogsScreen(::navigate, ::back)', 'Screen.StockLogs -> StockMovementLogsScreen(viewModel, ::navigate, ::back)')
    content = content.replace('Screen.Restock -> RestockManagementScreen(::navigate, ::back)', 'Screen.Restock -> RestockManagementScreen(viewModel, ::navigate, ::back)')
    content = content.replace('Screen.FanLogs -> FanActivityLogsScreen(::back)', 'Screen.FanLogs -> FanActivityLogsScreen(viewModel, ::back)')
    content = content.replace('Screen.SystemLogs -> SystemActivityLogsScreen(::back)', 'Screen.SystemLogs -> SystemActivityLogsScreen(viewModel, ::back)')

    # 2. InventoryScreen
    content = re.sub(r'private val inventoryItems = listOf\([^)]+\)\n', '', content, flags=re.DOTALL)
    content = content.replace('private fun InventoryScreen(navigate: (Screen) -> Unit) {', 'private fun InventoryScreen(viewModel: MainViewModel, navigate: (Screen) -> Unit) {\n    val inventoryItems by viewModel.inventoryList.collectAsState()')

    # 3. AlertsScreen
    content = re.sub(r'val alerts = listOf\([^)]+\)\n', 'val alerts by viewModel.alertsList.collectAsState()\n', content, flags=re.DOTALL)
    content = content.replace('private fun AlertsScreen(navigate: (Screen) -> Unit) {', 'private fun AlertsScreen(viewModel: MainViewModel, navigate: (Screen) -> Unit) {')

    # 4. StockMovementLogsScreen
    content = re.sub(r'val logs = listOf\([^)]+\)\n', 'val logs by viewModel.movementLogs.collectAsState()\n', content, flags=re.DOTALL)
    content = content.replace('private fun StockMovementLogsScreen(navigate: (Screen) -> Unit, back: () -> Unit) {', 'private fun StockMovementLogsScreen(viewModel: MainViewModel, navigate: (Screen) -> Unit, back: () -> Unit) {')

    # 5. RestockManagementScreen
    content = re.sub(r'val data = listOf\([^)]+\)\n', 'val data by viewModel.restockRequests.collectAsState()\n', content, flags=re.DOTALL)
    content = content.replace('private fun RestockManagementScreen(navigate: (Screen) -> Unit, back: () -> Unit) {', 'private fun RestockManagementScreen(viewModel: MainViewModel, navigate: (Screen) -> Unit, back: () -> Unit) {')

    # 6. FanActivityLogsScreen
    # Watch out, there are two `val logs = listOf(...)` but we replace all
    content = re.sub(r'val logs = listOf\(FanActivity[^)]+\)\n', 'val logs by viewModel.fanLogs.collectAsState()\n', content, flags=re.DOTALL)
    content = content.replace('private fun FanActivityLogsScreen(back: () -> Unit) {', 'private fun FanActivityLogsScreen(viewModel: MainViewModel, back: () -> Unit) {')

    # 7. SystemActivityLogsScreen
    content = re.sub(r'val activities = listOf\([^)]+\)\n', 'val activities by viewModel.systemLogs.collectAsState()\n', content, flags=re.DOTALL)
    content = content.replace('private fun SystemActivityLogsScreen(back: () -> Unit) {', 'private fun SystemActivityLogsScreen(viewModel: MainViewModel, back: () -> Unit) {')

    # 8. SettingsScreen Role restrictions
    old_settings_profile = """AppCard(background = Color(0xFF111827), border = Color.Transparent) {
            Text("Maria Santos", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("Branch Staff • San Pablo", color = Color(0xFFD1D5DB), fontSize = 13.sp, modifier = Modifier.padding(top = 4.dp))
            Text("maria@ottoscents.com", color = Color(0xFF9CA3AF), fontSize = 12.sp, modifier = Modifier.padding(top = 2.dp))
        }"""
    
    new_settings_profile = """AppCard(background = Color(0xFF111827), border = Color.Transparent) {
            val userRole by viewModel.userRole.collectAsState()
            val userBranch by viewModel.userBranch.collectAsState()
            val email = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.email ?: "Unknown User"
            Text(email, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("${userRole?.uppercase() ?: "STAFF"} • ${userBranch ?: "Unknown"}", color = Color(0xFFD1D5DB), fontSize = 13.sp, modifier = Modifier.padding(top = 4.dp))
        }"""
    content = content.replace(old_settings_profile, new_settings_profile)

    old_system_section = """SettingSection("System") {
            SettingRow("Seed Database", "Click to populate perfumes") { viewModel.seedDatabase() }
            SettingRow("Capture Schedule", "Every hour") { navigate(Screen.Schedule) }
            SettingRow("Temperature Threshold", "25°C") { navigate(Screen.Temperature) }
            SettingRow("Low Stock Threshold", "5 bottles") {}
            SettingRow("Cloud Sync Status", "Connected") {}
        }"""
        
    new_system_section = """val role by viewModel.userRole.collectAsState()
        SettingSection("System") {
            SettingRow("Seed Database", "Click to populate perfumes") { viewModel.seedDatabase() }
            if (role == "admin") {
                SettingRow("Capture Schedule", "Every hour") { navigate(Screen.Schedule) }
                SettingRow("Temperature Threshold", "25°C") { navigate(Screen.Temperature) }
                SettingRow("Low Stock Threshold", "5 bottles") {}
            }
            SettingRow("Cloud Sync Status", "Connected") {}
        }"""
    content = content.replace(old_system_section, new_system_section)

    old_reports_section = 'SettingSection("Reports") { SettingRow("Reports", null) { navigate(Screen.Reports) }; SettingRow("System Activity Logs", null) { navigate(Screen.SystemLogs) } }'
    new_reports_section = 'if (role == "admin") {\n            SettingSection("Reports") { SettingRow("Reports", null) { navigate(Screen.Reports) }; SettingRow("System Activity Logs", null) { navigate(Screen.SystemLogs) } }\n        }'
    content = content.replace(old_reports_section, new_reports_section)

    with open('app/src/main/java/com/ottoscents/smartshelf/MainActivity.kt', 'w', encoding='utf-8') as f:
        f.write(content)

if __name__ == '__main__':
    refactor_ui()
