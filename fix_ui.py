import re

def fix():
    with open('app/src/main/java/com/ottoscents/smartshelf/MainActivity.kt', 'r', encoding='utf-8') as f:
        lines = f.readlines()

    out = []
    skip = False
    
    for i, line in enumerate(lines):
        if skip:
            if line.strip() == ')' or line.strip() == ')' + ',':
                skip = False
                continue
            if ')' in line and 'val filtered =' in lines[i+1] if i+1 < len(lines) else False:
                skip = False
            continue

        if 'val data by viewModel.restockRequests.collectAsState()' in line and 'TemperatureMonitorScreen' in lines[i-1]:
            out.append('    val data = listOf(21f, 22f, 22.5f, 23f, 24.5f, 23.5f, 22.4f)\n')
            continue

        if 'private val inventoryItems = listOf(' in line:
            out.append('// REMOVED INVENTORY MOCK\n')
            skip = True
            continue

        if 'val alerts =' in line and 'listOf(' in line:
            out.append('    val alerts by viewModel.alertsList.collectAsState()\n')
            skip = True
            continue

        if 'val items =' in line and 'listOf(' in line and 'ShelfCheckHistoryScreen' in ''.join(lines[i-20:i]):
            out.append('    val items = emptyList<com.ottoscents.smartshelf.data.HistoryItem>()\n')
            skip = True
            continue

        if 'val logs =' in line and 'listOf(' in line and 'StockMovementLogsScreen' in ''.join(lines[i-20:i]):
            out.append('    val logs by viewModel.movementLogs.collectAsState()\n')
            skip = True
            continue

        if 'val data =' in line and 'listOf(' in line and 'RestockManagementScreen' in ''.join(lines[i-20:i]):
            out.append('    val data by viewModel.restockRequests.collectAsState()\n')
            skip = True
            continue

        if 'val logs =' in line and 'listOf(' in line and 'FanActivityLogsScreen' in ''.join(lines[i-20:i]):
            out.append('    val logs by viewModel.fanLogs.collectAsState()\n')
            skip = True
            continue

        if 'val activities =' in line and 'listOf(' in line and 'SystemActivityLogsScreen' in ''.join(lines[i-20:i]):
            out.append('    val activities by viewModel.systemLogs.collectAsState()\n')
            skip = True
            continue

        out.append(line)

    with open('app/src/main/java/com/ottoscents/smartshelf/MainActivity.kt', 'w', encoding='utf-8') as f:
        f.writelines(out)

if __name__ == '__main__':
    fix()
