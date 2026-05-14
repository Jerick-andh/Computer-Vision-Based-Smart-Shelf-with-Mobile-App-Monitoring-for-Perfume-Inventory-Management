# Smart Shelf Simulator Setup (Ubuntu VM)

This guide explains how to set up your Ubuntu VM to act as the "Smart Shelf" hardware, running YOLO detections and syncing them to your Android app in real-time.

## 1. Prerequisites (on Ubuntu VM)

Open your terminal in Ubuntu and run:

```bash
# Update and install Python tools
sudo apt update
sudo apt install python3-pip python3-venv python3-full

# Create project folder
mkdir ~/smart_shelf
cd ~/smart_shelf

# Set up Virtual Environment
python3 -m venv venv
source venv/bin/activate

# Install required libraries
# We use 'headless' version for better compatibility with Pi Zero 2W later
pip install ultralytics firebase-admin opencv-python-headless
```

## 2. Get Firebase Credentials

To allow your Linux VM to talk to your database, you need a "Service Account Key":

1.  Go to the [Firebase Console](https://console.firebase.google.com/).
2.  Select your **Otto Scents** project.
3.  Click the **Gear icon (Settings)** -> **Project Settings**.
4.  Go to the **Service accounts** tab.
5.  Click **Generate new private key**.
6.  A `.json` file will download. **Rename it to `serviceAccountKey.json`** and move it to your Ubuntu VM's `~/smart_shelf` folder.

## 3. The Simulator Script

Create a file named `shelf_simulator.py` in your `~/smart_shelf` folder. This script listens for a "Manual Inventory" command from your Android app and cycles through your capture scenarios.

```python
import firebase_admin
from firebase_admin import credentials, firestore
from ultralytics import YOLO
import time
import os
from datetime import datetime
import threading

# --- CONFIGURATION ---
MODEL_PATH = "best.pt"
CREDENTIALS_PATH = "serviceAccountKey.json"

# Scenario Images from your 'shelf_captures' folder
SCENARIOS = [
    ("scenario_full.jpg", "Initial Full Restock"),
    ("scenario_sale.jpg", "Sales Simulation"),
    ("scenario_partial_restock.jpg", "Partial Restock Simulation"),
    ("scenario_misplaced.jpg", "Misplacement Simulation"),
    ("scenario_final_restock.jpg", "Final Full Restock")
]

# State
scenario_index = 0
trigger_event = threading.Event()
target_branch = "Lipa"

# --- SETUP ---
try:
    cred = credentials.Certificate(CREDENTIALS_PATH)
    firebase_admin.initialize_app(cred)
    db = firestore.client()
    print(">>> Connected to Firestore Successfully")
except Exception as e:
    print(f"Error connecting to Firebase: {e}")
    exit()

# Load YOLO model
print(">>> Loading YOLO Model (Large)... ")
model = YOLO(MODEL_PATH)

def process_inventory(image_path, description, branch):
    global scenario_index
    
    if not os.path.exists(image_path):
        print(f"!!! Error: Image {image_path} not found.")
        return

    now = datetime.now().strftime("%b %d, %Y • %I:%M %p")
    print(f"\n--- Processing Manual Inventory: {description} for {branch} ---")
    
    # Run YOLO Prediction
    results = model.predict(source=image_path, conf=0.25, verbose=False)
    
    # Process Results
    bottle_count = len(results[0].boxes)
    print(f"[{now}] Detected: {bottle_count} bottles.")

    # --- SYNC TO DATABASE ---
    try:
        # 1. Update ALL Inventory Items in the branch
        # Mapping branch names to Firestore collection names (e.g., "San Pablo" -> "inventory_san_pablo")
        collection_suffix = branch.lower().replace(" ", "_")
        inventory_ref = db.collection(f"inventory_{collection_suffix}")
        docs = inventory_ref.stream()
        
        batch = db.batch()
        count_updated = 0
        for doc in docs:
            item_ref = inventory_ref.document(doc.id)
            update_data = {
                "detected": bottle_count,
                "status": "normal" if bottle_count > 5 else "low",
                "lastUpdated": now
            }
            # Set recorded to 10 on initial restock (scenario 1)
            if description == "Initial Full Restock":
                update_data["recorded"] = 10
            
            batch.update(item_ref, update_data)
            count_updated += 1
        
        batch.commit()
        print(f">>> Updated {count_updated} perfume records.")

        # 2. Log Activity
        log_ref = db.collection("system_logs").document()
        log_ref.set({
            "type": "shelf_check",
            "description": f"Manual Check Triggered: {bottle_count} bottles found ({description}).",
            "user": "Smart_Shelf_Hardware",
            "branch": branch,
            "timestamp": now,
            "createdAt": int(time.time() * 1000)
        })
        
        # 3. Reset Trigger in Firestore
        db.collection("settings").document("global_config").update({
            "manualTriggerPending": False
        })
        
        print(">>> Trigger reset. Ready for next run.")
        
        # Increment scenario for next time (loops back to 0 after 5)
        scenario_index = (scenario_index + 1) % len(SCENARIOS)
        
    except Exception as e:
        print(f"Error updating database: {e}")

# Firestore Listener for Manual Trigger
def on_snapshot(doc_snapshot, changes, read_time):
    global target_branch
    for doc in doc_snapshot:
        data = doc.to_dict()
        if data and data.get("manualTriggerPending") == True:
            target_branch = data.get("triggerBranch", "Lipa")
            print(f"\n[!] Manual Trigger Received from App for {target_branch}")
            trigger_event.set()

if __name__ == "__main__":
    print(f"Smart Shelf Listener Active. Waiting for trigger from App...")
    
    # Start Listener
    settings_ref = db.collection("settings").document("global_config")
    settings_ref.on_snapshot(on_snapshot)
    
    try:
        while True:
            # Wait for trigger_event to be set by the listener
            if trigger_event.wait(timeout=1.0):
                img, desc = SCENARIOS[scenario_index]
                process_inventory(img, desc, target_branch)
                trigger_event.clear()
    except KeyboardInterrupt:
        print("Stopping simulator...")
```

## 4. Running the Demo

1.  **Copy Images**: Move the images from your `docs/shelf_captures` folder into your Ubuntu VM's `~/smart_shelf` folder.
2.  **Activate Environment**: `source venv/bin/activate`
3.  **Run the script**: `python3 shelf_simulator.py`.
4.  **Observe**: 
    - Go to the **Shelf Monitor** screen in your Android App.
    - Tap **◉ Run Manual Inventory Check**.
    - Watch your Ubuntu terminal process the image!
    - See the inventory update across **all perfumes** in the app.
