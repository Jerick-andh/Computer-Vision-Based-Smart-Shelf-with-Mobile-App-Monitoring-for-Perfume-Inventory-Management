import re

def safe_refactor():
    with open('app/src/main/java/com/ottoscents/smartshelf/MainActivity.kt', 'r', encoding='utf-8') as f:
        content = f.read()

    # 1. Update OttoScentsApp invocations
    content = content.replace('Screen.Home -> HomeScreen(::navigate)', 'Screen.Home -> HomeScreen(viewModel, ::navigate)')
    content = content.replace('Screen.Inventory -> InventoryScreen(::navigate)', 'Screen.Inventory -> InventoryScreen(viewModel, ::navigate)')
    content = content.replace('Screen.Alerts -> AlertsScreen(::navigate)', 'Screen.Alerts -> AlertsScreen(viewModel, ::navigate)')
    content = content.replace('Screen.Settings -> SettingsScreen(::navigate, onLogout = { viewModel.logout() })', 'Screen.Settings -> SettingsScreen(viewModel, ::navigate, onLogout = { viewModel.logout() })')
    content = content.replace('Screen.StockLogs -> StockMovementLogsScreen(::navigate, ::back)', 'Screen.StockLogs -> StockMovementLogsScreen(viewModel, ::navigate, ::back)')
    content = content.replace('Screen.Restock -> RestockManagementScreen(::navigate, ::back)', 'Screen.Restock -> RestockManagementScreen(viewModel, ::navigate, ::back)')
    content = content.replace('Screen.FanLogs -> FanActivityLogsScreen(::back)', 'Screen.FanLogs -> FanActivityLogsScreen(viewModel, ::back)')
    content = content.replace('Screen.SystemLogs -> SystemActivityLogsScreen(::back)', 'Screen.SystemLogs -> SystemActivityLogsScreen(viewModel, ::back)')

    # 2. LoginScreen modifications (Remove branch)
    login_old = """    var email by remember { mutableStateOf("admin@ottoscents.com") }
    var password by remember { mutableStateOf("password123") }
    var branch by remember { mutableStateOf("San Pablo Branch") }"""
    login_new = """    var email by remember { mutableStateOf("admin@ottoscents.com") }
    var password by remember { mutableStateOf("password123") }"""
    content = content.replace(login_old, login_new)

    login_field_old = """            OutlinedTextField(value = email, onValueChange = { email = it }, placeholder = { Text("Email address") }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(18.dp))
            OutlinedTextField(value = password, onValueChange = { password = it }, placeholder = { Text("Password") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(18.dp))
            OutlinedTextField(value = branch, onValueChange = { branch = it }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(18.dp))"""
    login_field_new = """            OutlinedTextField(value = email, onValueChange = { email = it }, placeholder = { Text("Email address") }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(18.dp))
            OutlinedTextField(value = password, onValueChange = { password = it }, placeholder = { Text("Password") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(18.dp))"""
    content = content.replace(login_field_old, login_field_new)

    # 3. HomeScreen modifications
    home_sig_old = "private fun HomeScreen(navigate: (Screen) -> Unit) {"
    home_sig_new = "private fun HomeScreen(viewModel: MainViewModel, navigate: (Screen) -> Unit) {\n    val branch by viewModel.userBranch.collectAsState()"
    content = content.replace(home_sig_old, home_sig_new)
    
    home_branch_old = 'Text("San Pablo", modifier = Modifier.clip(RoundedCornerShape(999.dp))'
    home_branch_new = 'Text(branch ?: "Unknown", modifier = Modifier.clip(RoundedCornerShape(999.dp))'
    content = content.replace(home_branch_old, home_branch_new)

    # 4. SettingsScreen Role restrictions
    content = content.replace('private fun SettingsScreen(viewModel: MainViewModel = viewModel(), navigate: (Screen) -> Unit, onLogout: () -> Unit)', 'private fun SettingsScreen(viewModel: MainViewModel, navigate: (Screen) -> Unit, onLogout: () -> Unit)')
    content = content.replace('private fun SettingsScreen(navigate: (Screen) -> Unit, onLogout: () -> Unit)', 'private fun SettingsScreen(viewModel: MainViewModel, navigate: (Screen) -> Unit, onLogout: () -> Unit)')
    
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
    # Note: Seed Database button might not be in the original if I reverted it all. Let's just handle both cases.
    content = content.replace(old_system_section, new_system_section)

    old_sys_no_seed = """SettingSection("System") {
            SettingRow("Capture Schedule", "Every hour") { navigate(Screen.Schedule) }
            SettingRow("Temperature Threshold", "25°C") { navigate(Screen.Temperature) }
            SettingRow("Low Stock Threshold", "5 bottles") {}
            SettingRow("Cloud Sync Status", "Connected") {}
        }"""
    content = content.replace(old_sys_no_seed, new_system_section)

    old_reports_section = 'SettingSection("Reports") { SettingRow("Reports", null) { navigate(Screen.Reports) }; SettingRow("System Activity Logs", null) { navigate(Screen.SystemLogs) } }'
    new_reports_section = 'if (role == "admin") {\n            SettingSection("Reports") { SettingRow("Reports", null) { navigate(Screen.Reports) }; SettingRow("System Activity Logs", null) { navigate(Screen.SystemLogs) } }\n        }'
    content = content.replace(old_reports_section, new_reports_section)

    # 5. Remove Mock Data Lists Carefully using regex but bounded within specific functions

    def replace_in_func(func_name, old_sig, new_sig, list_regex, replace_text, code_str):
        idx = code_str.find(old_sig)
        if idx == -1: return code_str
        end_idx = code_str.find('private fun', idx + 10)
        if end_idx == -1: end_idx = len(code_str)
        func_body = code_str[idx:end_idx]
        func_body = func_body.replace(old_sig, new_sig)
        func_body = re.sub(list_regex, replace_text, func_body, flags=re.DOTALL)
        return code_str[:idx] + func_body + code_str[end_idx:]

    content = replace_in_func('InventoryScreen', 
        'private fun InventoryScreen(navigate: (Screen) -> Unit) {', 
        'private fun InventoryScreen(viewModel: MainViewModel, navigate: (Screen) -> Unit) {\n    val inventoryItems by viewModel.inventoryList.collectAsState()',
        r'', '', content) # Already handled in MainActivity by viewModel? No, wait. 
        
    # The inventoryItems list is module-level in MainActivity.kt!
    content = re.sub(r'private val inventoryItems = listOf\([\s\S]*?\n\)\n', '', content)

    content = replace_in_func('AlertsScreen',
        'private fun AlertsScreen(navigate: (Screen) -> Unit) {',
        'private fun AlertsScreen(viewModel: MainViewModel, navigate: (Screen) -> Unit) {',
        r'val alerts = listOf\([^\]]*?\n    \)', 'val alerts by viewModel.alertsList.collectAsState()', content)

    content = replace_in_func('StockMovementLogsScreen',
        'private fun StockMovementLogsScreen(navigate: (Screen) -> Unit, back: () -> Unit) {',
        'private fun StockMovementLogsScreen(viewModel: MainViewModel, navigate: (Screen) -> Unit, back: () -> Unit) {',
        r'val logs = listOf\([^\]]*?\n    \)', 'val logs by viewModel.movementLogs.collectAsState()', content)

    content = replace_in_func('RestockManagementScreen',
        'private fun RestockManagementScreen(navigate: (Screen) -> Unit, back: () -> Unit) {',
        'private fun RestockManagementScreen(viewModel: MainViewModel, navigate: (Screen) -> Unit, back: () -> Unit) {',
        r'val data = listOf\([^\]]*?\n    \)', 'val data by viewModel.restockRequests.collectAsState()', content)

    content = replace_in_func('FanActivityLogsScreen',
        'private fun FanActivityLogsScreen(back: () -> Unit) {',
        'private fun FanActivityLogsScreen(viewModel: MainViewModel, back: () -> Unit) {',
        r'val logs = listOf\([^\]]*?\n    \)', 'val logs by viewModel.fanLogs.collectAsState()', content)

    content = replace_in_func('SystemActivityLogsScreen',
        'private fun SystemActivityLogsScreen(back: () -> Unit) {',
        'private fun SystemActivityLogsScreen(viewModel: MainViewModel, back: () -> Unit) {',
        r'val activities = listOf\([^\]]*?\n    \)', 'val activities by viewModel.systemLogs.collectAsState()', content)

    with open('app/src/main/java/com/ottoscents/smartshelf/MainActivity.kt', 'w', encoding='utf-8') as f:
        f.write(content)

if __name__ == '__main__':
    safe_refactor()
