import re

def refactor_main_activity():
    with open('app/src/main/java/com/ottoscents/smartshelf/MainActivity.kt', 'r', encoding='utf-8') as f:
        content = f.read()

    # 1. Add imports
    imports = """
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import com.ottoscents.smartshelf.data.*
import kotlinx.coroutines.launch
"""
    content = content.replace("import androidx.compose.ui.unit.sp", "import androidx.compose.ui.unit.sp\n" + imports)

    # 2. Remove private data classes
    data_classes = [
        r"private data class InventoryItem\(.*?\)\n",
        r"private data class AlertItem\(.*?\)\n",
        r"private data class HistoryItem\(.*?\)\n",
        r"private data class MovementLog\(.*?\)\n",
        r"private data class RestockItem\(.*?\)\n",
        r"private data class FanActivity\(.*?\)\n",
        r"private data class HelpTopic\(.*?\)\n",
        r"private data class SystemActivity\(.*?\)\n"
    ]
    for pattern in data_classes:
        content = re.sub(pattern, "", content)

    # 3. Update OttoScentsApp
    old_app = """@Composable
fun OttoScentsApp() {
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
    }"""
    
    new_app = """@Composable
fun OttoScentsApp(viewModel: MainViewModel = viewModel()) {
    val backStack = remember { mutableStateListOf<Screen>() }
    var screen by remember { mutableStateOf(Screen.Login) }
    
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    
    if (isLoggedIn && screen == Screen.Login) {
        screen = Screen.Home
    } else if (!isLoggedIn && screen != Screen.Login) {
        screen = Screen.Login
        backStack.clear()
    }

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
    }"""
    content = content.replace(old_app, new_app)
    
    # Update login screen usage
    content = content.replace("LoginScreen(onLogin = { replace(Screen.Home) })", "LoginScreen(viewModel)")

    # Update LoginScreen signature
    old_login_sig = "private fun LoginScreen(onLogin: () -> Unit) {"
    new_login_sig = """private fun LoginScreen(viewModel: MainViewModel) {
    val loginError by viewModel.loginError.collectAsState()
"""
    content = content.replace(old_login_sig, new_login_sig)
    
    # Update login button
    content = content.replace('AppButton("Sign In", onClick = onLogin)', 'if (loginError != null) Text(loginError!!, color = Red, fontSize = 12.sp, modifier = Modifier.padding(bottom = 8.dp))\n            AppButton("Sign In", onClick = { viewModel.login(email, password) })')

    # Fix InventoryItems mock to use Strings
    content = content.replace('InventoryItem(1,', 'InventoryItem("1",')
    content = content.replace('InventoryItem(2,', 'InventoryItem("2",')
    content = content.replace('InventoryItem(3,', 'InventoryItem("3",')
    content = content.replace('InventoryItem(4,', 'InventoryItem("4",')

    # Update Settings screen to use logout
    content = content.replace('SettingsScreen(::navigate, onLogout = { replace(Screen.Login) })', 'SettingsScreen(::navigate, onLogout = { viewModel.logout() })')

    with open('app/src/main/java/com/ottoscents/smartshelf/MainActivity.kt', 'w', encoding='utf-8') as f:
        f.write(content)
        
if __name__ == '__main__':
    refactor_main_activity()
