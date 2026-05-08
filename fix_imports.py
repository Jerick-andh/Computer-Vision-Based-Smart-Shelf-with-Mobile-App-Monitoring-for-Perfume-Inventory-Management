import re

def fix_imports():
    with open('app/src/main/java/com/ottoscents/smartshelf/MainActivity.kt', 'r', encoding='utf-8') as f:
        content = f.read()

    # Add import
    if 'import com.ottoscents.smartshelf.data.*' not in content:
        content = content.replace('package com.ottoscents.smartshelf\n', 'package com.ottoscents.smartshelf\n\nimport com.ottoscents.smartshelf.data.*\n')

    # Delete all private data class declarations
    # They span multiple lines usually, but in MainActivity they are one-liners!
    # "private data class InventoryItem(val id: Int, val name: String, val category: String, val shelf: String, val recorded: Int, val detected: Int, val status: String, val lastUpdated: String)"
    content = re.sub(r'private data class [A-Za-z0-9_]+\([^)]+\)\n?', '', content)

    # Some might span lines if they have default values. Let's make sure RestockItem is deleted.
    # "private data class RestockItem(val productName: String, val productId: String, val quantity: Int, val fromBranch: String, val toBranch: String, val requestedBy: String, val requestedDate: String, val status: String, val estimatedArrival: String? = null, val completedDate: String? = null)"
    # The regex `\([^)]+\)` won't work if there's no nested parenthesis, wait, `String? = null` doesn't have parenthesis. It works!
    
    with open('app/src/main/java/com/ottoscents/smartshelf/MainActivity.kt', 'w', encoding='utf-8') as f:
        f.write(content)

if __name__ == '__main__':
    fix_imports()
