# Setting up YOLO Simulator on Ubuntu (VMware)

Follow these steps to get your "Smart Shelf" simulator running on your Ubuntu VM.

## 1. Prepare Files (on Windows)
Before moving to the VM, ensure you have these files ready in your project folder:
- `shelf_simulator.py` (Updated with A, B, C, D logic)
- `docs/shelf_captures/best.pt` (The letter-based weights)
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
        ├── scenario_full.jpg
        ├── scenario_sale.jpg
        └── ...
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
pip install ultralytics firebase-admin opencv-python-headless
```

## 4. Run the Simulator
While your virtual environment is active, run:

```bash
python shelf_simulator.py
```

### What to expect:
1. The script will load the YOLO model.
2. It will connect to your Firebase database.
3. It will wait for a **Manual Inventory** trigger from your Android App.
4. When you tap the button in the app, the VM will process the image, count the perfumes, and update the database!

---

## Troubleshooting
- **No serviceAccountKey.json?**: The script will run in **DRY RUN** mode. You can press `Enter` in the terminal to cycle through the sample images and see the counts without updating the database.
- **GL Errors?**: If you see errors about `libGL.so`, ensure you installed `libgl1-mesa-glx` as shown in step 3.
