import re

def final_fix():
    with open('app/src/main/java/com/ottoscents/smartshelf/MainActivity.kt', 'r', encoding='utf-8') as f:
        content = f.read()

    # FanActivityLogsScreen mock list
    content = re.sub(r'val logs = listOf\(FanActivity\([^)]*\)[^)]*\)\n', 'val logs by viewModel.fanLogs.collectAsState()\n', content)
    # Wait, the list has multiple items: `val logs = listOf(FanActivity(...), FanActivity(...))`
    # A safe way is to find `val logs = listOf(FanActivity` and replace the whole line.
    lines = content.split('\n')
    for i, line in enumerate(lines):
        if 'val logs = listOf(FanActivity' in line:
            lines[i] = '    val logs by viewModel.fanLogs.collectAsState()'
        if 'val activities = listOf(SystemActivity' in line or ('val activities = listOf(' in line and 'SystemActivityLogsScreen' in ''.join(lines[max(0, i-5):i])):
            lines[i] = '    val activities by viewModel.systemLogs.collectAsState()'
            # check if it spans multiple lines
            j = i + 1
            while j < len(lines) and 'SystemActivity(' in lines[j]:
                lines[j] = ''
                j += 1
            if j < len(lines) and lines[j].strip() == ')':
                lines[j] = ''

    content = '\n'.join(lines)

    # Revert TemperatureMonitorScreen to original mock data just in case
    # If it says `val data by viewModel.restockRequests.collectAsState()`
    lines = content.split('\n')
    for i, line in enumerate(lines):
        if 'val data by viewModel.restockRequests.collectAsState()' in line and 'TemperatureMonitorScreen' in ''.join(lines[max(0, i-5):i]):
            lines[i] = '    val data = listOf(21f, 22f, 22.5f, 23f, 24.5f, 23.5f, 22.4f)'

    content = '\n'.join(lines)

    with open('app/src/main/java/com/ottoscents/smartshelf/MainActivity.kt', 'w', encoding='utf-8') as f:
        f.write(content)

if __name__ == '__main__':
    final_fix()
