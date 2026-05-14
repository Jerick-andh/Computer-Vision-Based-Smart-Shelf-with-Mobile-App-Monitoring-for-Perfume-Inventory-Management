import os
import time
from datetime import datetime
from flask import Flask, request, jsonify
from ultralytics import YOLO
import firebase_admin
from firebase_admin import credentials, firestore

# --- CONFIGURATION ---
MODEL_PATH = "docs/shelf_captures/best.pt"
TRACKED_PERFUMES = ["A", "B", "C", "D"]

app = Flask(__name__)

# --- SETUP ---
# On Cloud Run, it's best to use the default service account
try:
    firebase_admin.initialize_app()
    db = firestore.client()
    print(">>> Connected to Firestore (Cloud Run)")
except Exception as e:
    print(f"Error connecting to Firebase: {e}")
    # For local testing, fallback to credentials file if present
    if os.path.exists("serviceAccountKey.json"):
        cred = credentials.Certificate("serviceAccountKey.json")
        firebase_admin.initialize_app(cred)
        db = firestore.client()

# Load model into memory once on startup
print(">>> Loading YOLO Model...")
model = YOLO(MODEL_PATH)

@app.route('/detect', methods=['POST'])
def detect_and_sync():
    """
    Endpoint to receive an image, run YOLO, and update Firestore.
    Expects JSON: { "image_url": "...", "branch": "Lipa", "description": "Cloud Failover" }
    Or multipart/form-data for direct image upload.
    """
    branch = request.form.get('branch', 'Lipa')
    description = request.form.get('description', 'Cloud Failover Trigger')

    if 'image' not in request.files:
        return jsonify({"error": "No image provided"}), 400

    img_file = request.files['image']
    img_path = "/tmp/temp_capture.jpg"
    img_file.save(img_path)

    now = datetime.now().strftime("%b %d, %Y • %I:%M %p")

    # Run YOLO Prediction
    results = model.predict(source=img_path, conf=0.25, verbose=False)

    # Process Results
    detected_counts = {code: 0 for code in TRACKED_PERFUMES}
    for box in results[0].boxes:
        cls_id = int(box.cls[0])
        cls_name = model.names[cls_id].upper().strip()
        
        if cls_name in detected_counts:
            detected_counts[cls_name] += 1

    # --- SYNC TO DATABASE ---
    try:
        inventory_ref = db.collection(f"inventory_{branch.lower()}")
        batch = db.batch()
        count_updated = 0

        for code, count in detected_counts.items():
            docs = inventory_ref.where("perfumeCode", "==", code).stream()
            for doc in docs:
                item_ref = inventory_ref.document(doc.id)
                batch.update(item_ref, {
                    "detected": count,
                    "status": "normal" if count > 2 else "low",
                    "lastUpdated": now
                })
                count_updated += 1

        batch.commit()

        # Log Activity
        log_ref = db.collection("system_logs").document()
        log_summary = ", ".join([f"{code}:{count}" for code, count in detected_counts.items()])
        log_ref.set({
            "type": "cloud_failover",
            "description": f"Cloud Failover Detection: {log_summary}",
            "branch": branch,
            "timestamp": now,
            "createdAt": int(time.time() * 1000)
        })

        return jsonify({
            "status": "success",
            "updated_records": count_updated,
            "detections": detected_counts
        })

    except Exception as e:
        return jsonify({"error": str(e)}), 500

if __name__ == '__main__':
    port = int(os.environ.get('PORT', 8080))
    app.run(debug=False, host='0.0.0.0', port=port)
