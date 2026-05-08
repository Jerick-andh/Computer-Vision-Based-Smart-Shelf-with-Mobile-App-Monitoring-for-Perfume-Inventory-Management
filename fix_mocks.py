import re

def fix_mocks():
    with open('app/src/main/java/com/ottoscents/smartshelf/MainActivity.kt', 'r', encoding='utf-8') as f:
        content = f.read()

    # AlertItem(title, desc, branch, time, type) -> AlertItem("", title, ...)
    content = re.sub(r'AlertItem\("', 'AlertItem("", "', content)
    
    # HistoryItem(date, time, branch, status) -> HistoryItem("", date, ...)
    content = re.sub(r'HistoryItem\("', 'HistoryItem("", "', content)
    
    # MovementLog(productName, shelfArea, branch, timestamp, status, quantity)
    content = re.sub(r'MovementLog\("', 'MovementLog("", "', content)
    
    # RestockItem(productName, productId, quantity, fromBranch, toBranch, requestedBy, requestedDate, status, estimatedArrival, completedDate)
    content = re.sub(r'RestockItem\("', 'RestockItem("", "', content)
    
    # FanActivity(triggerTemperature, startTime, stopTime, duration, status, branch, shelfArea)
    content = re.sub(r'FanActivity\((\d+\.\d+),', r'FanActivity("", \1,', content)
    
    # SystemActivity(type, description, user, branch, timestamp)
    content = re.sub(r'SystemActivity\("', 'SystemActivity("", "', content)

    with open('app/src/main/java/com/ottoscents/smartshelf/MainActivity.kt', 'w', encoding='utf-8') as f:
        f.write(content)

if __name__ == '__main__':
    fix_mocks()
