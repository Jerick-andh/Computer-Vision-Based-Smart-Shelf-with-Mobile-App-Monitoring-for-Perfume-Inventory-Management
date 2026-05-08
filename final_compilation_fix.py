def fix():
    with open('app/src/main/java/com/ottoscents/smartshelf/MainActivity.kt', 'r', encoding='utf-8') as f:
        content = f.read()

    # 1. Add imports
    imports_to_add = """
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
"""
    if 'androidx.compose.runtime.collectAsState' not in content:
        content = content.replace('import androidx.compose.runtime.getValue\n', 'import androidx.compose.runtime.getValue\n' + imports_to_add)

    # 2. Add viewModel to OttoScentsApp
    if 'val viewModel: MainViewModel = viewModel()' not in content:
        content = content.replace('fun OttoScentsApp() {\n    var screen by remember { mutableStateOf(Screen.Login) }', 'fun OttoScentsApp() {\n    val viewModel: MainViewModel = viewModel()\n    var screen by remember { mutableStateOf(Screen.Login) }')

    # 3. Fix SettingsScreen in OttoScentsApp
    content = content.replace('Screen.Settings -> SettingsScreen(::navigate, onLogout = { replace(Screen.Login) })', 'Screen.Settings -> SettingsScreen(viewModel, ::navigate, onLogout = { viewModel.logout(); replace(Screen.Login) })')
    # Or if it was something else:
    content = content.replace('Screen.Settings -> SettingsScreen(::navigate, onLogout = { viewModel.logout() })', 'Screen.Settings -> SettingsScreen(viewModel, ::navigate, onLogout = { viewModel.logout() })')

    # 4. Make sure SettingsScreen has the correct signature
    content = content.replace('private fun SettingsScreen(navigate: (Screen) -> Unit, onLogout: () -> Unit)', 'private fun SettingsScreen(viewModel: MainViewModel, navigate: (Screen) -> Unit, onLogout: () -> Unit)')

    with open('app/src/main/java/com/ottoscents/smartshelf/MainActivity.kt', 'w', encoding='utf-8') as f:
        f.write(content)

if __name__ == '__main__':
    fix()
