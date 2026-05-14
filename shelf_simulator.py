import firebase_admin
from firebase_admin import credentials, firestore
from ultralytics import YOLO
import time
import os
from datetime import datetime
import threading
import sys
import queue

# --- CONFIGURATION ---
MODEL_PATH = "docs/shelf_captures/best.pt"
CREDENTIALS_PATH = "serviceAccountKey.json"
REQUIRED_PERFUMES = ["A", "B", "C", "D"]
IMAGE_DIR = "docs/shelf_captures"
TEMP_THRESHOLD = 25.0
COOLING_RATE = 0.4  # Degrees per 2 seconds

# Shelf Zones for detection
SHELF_ZONES = {
    "A": {"x_min": 0.00, "x_max": 0.25},
    "B": {"x_min": 0.25, "x_max": 0.50},
    "C": {"x_min": 0.50, "x_max": 0.75},
    "D": {"x_min": 0.75, "x_max": 1.00}
}

# Sequential Scenario Images
SCENARIOS = [
    ("scenario_full.jpg", "Initial Full Restock"),
    ("scenario_sale.jpg", "Sales Simulation"),
    ("scenario_low_stock.jpg", "Low Stock Simulation"),
    ("scenario_partial_restock.jpg", "Partial Restock Simulation"),
    ("scenario_misplaced.jpg", "Misplacement Simulation"),
    ("scenario_final_restock.jpg", "Final Full Restock")
]

# State
branch_indices = {"Lipa": 0, "San Pablo": 0}
trigger_queue = queue.Queue()
current_temp = 22.4
is_fan_active = False
is_running = True

# --- SETUP ---
try:
    cred = credentials.Certificate(CREDENTIALS_PATH)
    firebase_admin.initialize_app(cred)
    db = firestore.client()
    print(">>> Connected to Firestore Successfully")
except Exception as e:
    print(f"Error connecting to Firebase: {e}")
    print("Ensure 'serviceAccountKey.json' is present in the current directory.")
    exit()

def wait_for_products():
    """Wait until both branches have the required perfumes."""
    branches = ["Lipa", "San Pablo"]
    for branch in branches:
        collection_name = f"inventory_{branch.lower().replace(' ', '_')}"
        print(f"\n>>> Checking database for {branch} inventory...")
        while True:
            try:
                docs = db.collection(collection_name).stream()
                found = {doc.to_dict().get('perfumeCode') for doc in docs}
                missing = [p for p in REQUIRED_PERFUMES if p not in found]
                
                if not missing:
                    print(f">>> All required perfumes found in {branch}.")
                    break
                
                print(f"!!! Missing perfumes in {branch}: {', '.join(missing)}")
                print(">>> Please add these products on the app first. Re-checking in 5s...")
                time.sleep(5)
            except Exception as e:
                print(f"Error checking database: {e}")
                time.sleep(5)

# Load YOLO model
print(f">>> Loading YOLO Model from {MODEL_PATH}...")
try:
    model = YOLO(MODEL_PATH)
except Exception as e:
    print(f"Error loading model: {e}")
    exit()

def clear_inventory_records(branch):
    """Reset counts and status for the branch in Firestore."""
    print(f">>> Resetting inventory records for {branch}...")
    try:
        collection_suffix = branch.lower().replace(" ", "_")
        inventory_ref = db.collection(f"inventory_{collection_suffix}")
        docs = inventory_ref.stream()
        batch = db.batch()
        for doc in docs:
            batch.update(inventory_ref.document(doc.id), {
                "detected": 0,
                "status": "missing",
                "recorded": 0
            })
        batch.commit()
        print(f">>> {branch} inventory cleared.")
    except Exception as e:
        print(f"Error clearing inventory for {branch}: {e}")

def cooling_logic():
    """Background loop to simulate gradual cooling when fan is active."""
    global current_temp, is_fan_active, is_running
    while is_running:
        if is_fan_active:
            if current_temp > 22.0:
                current_temp -= COOLING_RATE
                new_temp = round(max(current_temp, 22.0), 1)
                db.collection("settings").document("global_config").update({
                    "currentTemperature": new_temp
                })
                print(f"[Cooling] Current Temperature: {new_temp}°C")
            else:
                is_fan_active = False
                now = datetime.now().strftime("%b %d, %Y • %I:%M %p")
                db.collection("settings").document("global_config").update({
                    "isFanActive": False,
                    "currentTemperature": 22.0
                })
                print("[Cooling] Safe temperature reached. Fan deactivated.")
                
                # Log fan stop
                log_ref = db.collection("system_logs").document()
                log_ref.set({
                    "type": "fan_stop",
                    "description": "Cooling system deactivated. Safe temperature reached.",
                    "user": "Smart_Shelf_Hardware",
                    "timestamp": now,
                    "createdAt": int(time.time() * 1000)
                })
        time.sleep(2)

def process_branch_scenario(branch):
    """Processes the current scenario for a specific branch."""
    global branch_indices, is_running
    
    idx = branch_indices[branch]
    img_name, description = SCENARIOS[idx]
    
    # Branch-specific folder
    branch_folder = branch.lower().replace(" ", "_")
    image_path = os.path.join(IMAGE_DIR, branch_folder, img_name)
    
    if not os.path.exists(image_path):
        print(f"!!! Error: Image {image_path} not found.")
        return False

    now = datetime.now().strftime("%b %d, %Y • %I:%M %p")
    print(f"\n--- Processing {branch}: {description} ---")
    print(f"--- Using image: {image_path} ---")
    
    # Run YOLO Prediction
    results = model.predict(source=image_path, conf=0.25, verbose=False)
    
    # Logic: Count total bottles and track misplacement
    total_counts = {code: 0 for code in REQUIRED_PERFUMES}
    misplaced_codes = set()
    misplacement_details = []
    
    boxes = results[0].boxes
    for box in boxes:
        cls_id = int(box.cls[0])
        detected_code = model.names[cls_id].upper().strip()
        
        if detected_code not in REQUIRED_PERFUMES:
            continue
            
        total_counts[detected_code] += 1
        
        # Physical Zone check
        x1, y1, x2, y2 = box.xyxyn[0].tolist()
        x_center = (x1 + x2) / 2
        actual_zone = "Unknown"
        for zone_name, bounds in SHELF_ZONES.items():
            if bounds["x_min"] <= x_center < bounds["x_max"]:
                actual_zone = zone_name
                break
        
        if detected_code != actual_zone:
            misplaced_codes.add(detected_code)
            detail = f"Perfume {detected_code} found in Area {actual_zone}"
            if detail not in misplacement_details:
                misplacement_details.append(detail)

    print(f"[{branch}] Detections: {total_counts}")
    if misplaced_codes:
        print(f"[{branch}] MISPLACED: {', '.join(misplaced_codes)}")

    # SYNC TO DATABASE
    try:
        collection_suffix = branch.lower().replace(" ", "_")
        inventory_ref = db.collection(f"inventory_{collection_suffix}")
        
        batch = db.batch()
        docs = inventory_ref.stream()
        
        # Build mappings for ID and Name
        code_to_doc_id = {}
        code_to_name = {}
        for doc in docs:
            data = doc.to_dict()
            c = data.get('perfumeCode')
            code_to_doc_id[c] = doc.id
            code_to_name[c] = data.get('name', f"Product {c}")
            
        for code, count in total_counts.items():
            doc_id = code_to_doc_id.get(code)
            if doc_id:
                item_ref = inventory_ref.document(doc_id)
                status = "misplaced" if code in misplaced_codes else ("missing" if count == 0 else ("low" if count <= 5 else "normal"))
                
                update_data = {"detected": count, "status": status, "lastUpdated": now}
                if description in ["Initial Full Restock", "Final Full Restock"]:
                    update_data["recorded"] = 10
                
                batch.update(item_ref, update_data)
        
        batch.commit()

        # SYSTEM LOGS & ALERTS
        log_desc = f"Inventory Check: {description}. Found {total_counts}."
        if misplaced_codes: log_desc += f" MISPLACED: {', '.join(misplacement_details)}"
            
        log_ref = db.collection("system_logs").document()
        log_ref.set({
            "type": "shelf_check", "description": log_desc, "user": "Smart_Shelf_Hardware",
            "branch": branch, "timestamp": now, "createdAt": int(time.time() * 1000)
        })
        
        for code, count in total_counts.items():
            alert_title, alert_desc, alert_type = "", "", ""
            product_name = code_to_name.get(code, f"Product {code}")
            
            if code in misplaced_codes:
                alert_title = f"Misplaced: {product_name}"
                detail = "Found in wrong zone"
                for d in misplacement_details:
                    if f"Perfume {code}" in d:
                        detail = d.replace(f"Perfume {code}", product_name)
                        break
                alert_desc = f"{detail}. Please return it to Area {code}."
                alert_type = "warning"
            elif count == 0:
                alert_title = f"Out of Stock: {product_name}"
                alert_desc = f"{product_name} is missing from the shelf!"
                alert_type = "critical"
            elif count <= 5:
                alert_title = f"Low Stock: {product_name}"
                alert_desc = f"Only {count} bottles of {product_name} remain."
                alert_type = "warning"
                
            if alert_title:
                db.collection(f"alerts_{collection_suffix}").document().set({
                    "title": alert_title, "desc": alert_desc, "branch": branch, "time": now, "type": alert_type
                })
        
        # Advance sequence for this branch
        branch_indices[branch] += 1
        if branch_indices[branch] >= len(SCENARIOS):
            print(f"\n>>> {branch.upper()} SEQUENCE COMPLETED.")
            choice = input(f">>> Restart {branch}? (y/n): ").lower()
            clear_inventory_records(branch)
            if choice == 'y':
                branch_indices[branch] = 0
                print(f"\n>>> {branch} restarted. Waiting for next trigger...")
            else:
                print(f">>> Stopping simulator for {branch}...")
                return "STOP"
        
        return True
    except Exception as e:
        print(f"Error updating {branch}: {e}")
        return False

# Firestore Listener
def on_snapshot(doc_snapshot, changes, read_time):
    global current_temp, is_fan_active
    for doc in doc_snapshot:
        if doc.exists:
            data = doc.to_dict()
            
            # 1. Check for manual inventory trigger
            if data and data.get("manualTriggerPending") == True:
                target = data.get("triggerBranch", "Both")
                print(f"\n[!] Manual Trigger Received: {target}")
                trigger_queue.put(target)
            
            # 2. Check for temperature changes (Spikes)
            new_temp = data.get("currentTemperature", 22.0)
            if new_temp > TEMP_THRESHOLD and not is_fan_active:
                current_temp = new_temp
                is_fan_active = True
                print(f"\n[!] High Temperature Detected ({current_temp}°C). Activating Cooling Fan...")
                db.collection("settings").document("global_config").update({"isFanActive": True})
            elif new_temp <= 22.1 and is_fan_active:
                # App manually reset or cooled down
                is_fan_active = False

if __name__ == "__main__":
    wait_for_products()
    
    # Start background cooling simulation
    cool_thread = threading.Thread(target=cooling_logic, daemon=True)
    cool_thread.start()
    
    print(f"\nSmart Shelf Listener Active. Waiting for trigger from App...")
    
    settings_ref = db.collection("settings").document("global_config")
    settings_ref.on_snapshot(on_snapshot)
    
    try:
        while is_running:
            try:
                target = trigger_queue.get(timeout=1.0)
                
                # Reset Trigger in Firestore immediately
                db.collection("settings").document("global_config").update({"manualTriggerPending": False})
                
                branches_to_process = ["Lipa", "San Pablo"] if target == "Both" else [target]
                
                for b in branches_to_process:
                    res = process_branch_scenario(b)
                    if res == "STOP":
                        is_running = False
                        break
                
                if is_running:
                    print("\nReady for next manual inventory.")
            except queue.Empty:
                continue
    except KeyboardInterrupt:
        print("\nStopping simulator...")
    is_running = False
    sys.exit()
