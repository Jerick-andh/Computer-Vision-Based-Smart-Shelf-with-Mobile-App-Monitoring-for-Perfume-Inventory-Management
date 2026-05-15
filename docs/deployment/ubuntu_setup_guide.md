# Setting up Smart Shelf Edge on Ubuntu (VMware)

Follow these steps to get your "Smart Shelf" edge hardware (simulator) running on your Ubuntu VM.

## 1. Prepare Files (on Windows)
Before moving to the VM, ensure you have these files ready in your project folder:
- `shelf_simulator.py` (Final version with A, B, C, D logic)
- `docs/shelf_captures/best.pt` (The letter-based AI weights)
- `serviceAccountKey.json` (Your Firebase credentials)
- `docs/shelf_captures/` folder (Contains all scenario images)

## 2. Transfer Files to Ubuntu
Place everything in `~/smart_shelf`. Your folder structure on Ubuntu should look like this:
```
~/smart_shelf/
├── shelf_simulator.py
├── serviceAccountKey.json
├── venv/
└── docs/
    └── shelf_captures/
        ├── best.pt
        ├── lipa/
        │   └── scenario_*.jpg
        └── san_pablo/
            └── scenario_*.jpg
```

## 3. Setup Environment (on Ubuntu Terminal)
Open your terminal in Ubuntu and run the following:

```bash
# Update system
sudo apt update
sudo apt install -y python3-pip python3-venv libgl1-mesa-glx libglib2.0-0

# Navigate to your folder
cd ~/smart_shelf

# Create and activate Virtual Environment
python3 -m venv venv
source venv/bin/activate

# Install dependencies
# NOTE: Using 'opencv-python' (not headless) to support the Calibration window
pip install ultralytics firebase-admin opencv-python
```

## 4. Run the Simulator
While your virtual environment is active, run:

```bash
python shelf_simulator.py
```

### What to expect:
1. The script will load the YOLO AI model.
2. It will connect to your Firebase database and start the **Heartbeat** (Green light in app).
3. It will wait for triggers (**Calibration**, **Live Scan**, or **Simulated Scans**) from your Android App.
4. **Live Calibration**: Opens a window on your desktop to help align your webcam with the shelf ROI zones.

---

## Troubleshooting
- **No serviceAccountKey.json?**: The script will fail to connect. Ensure you've downloaded the key from Firebase Console.
- **GL Errors?**: Ensure you installed `libgl1-mesa-glx`.
- **Camera Access**: Ensure your VMware settings allow the VM to "Connect" to your host's USB webcam.

---
© 2026 Otto Scents.
