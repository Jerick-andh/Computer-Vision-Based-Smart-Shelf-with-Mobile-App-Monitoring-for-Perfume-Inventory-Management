# VM Files Manifest

These are the files you need to copy to your Ubuntu VM folder (e.g., `~/smart_shelf`).

### 1. The Simulator Script
- File: `shelf_simulator.py` (located in the project root)
- Description: The main listener script that handles database checks and image simulation.

### 2. The YOLO Model
- File: `docs/shelf_captures/best.pt`
- **IMPORTANT**: Copy this to your VM folder. The script expects it at `docs/shelf_captures/best.pt` or you can move it to the same folder as the script and update the `MODEL_PATH` in the script.

### 3. Firebase Credentials
- File: `serviceAccountKey.json`
- Description: Your private key from Firebase Console.

### 4. Scenario Images
- Folder: `docs/shelf_captures/`
- Description: All `.jpg` files inside this folder are needed for the simulation.

---

## Quick Setup Commands (on Ubuntu)

```bash
# 1. Create directory
mkdir -p ~/smart_shelf/docs/shelf_captures
cd ~/smart_shelf

# 2. Setup Venv
python3 -m venv venv
source venv/bin/activate

# 3. Install Requirements
pip install ultralytics firebase-admin opencv-python-headless
```

*After copying the files, run:*
`python3 shelf_simulator.py`
