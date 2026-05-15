# Otto Scents: Smart Shelf Inventory Management System

A professional computer-vision-based prototype for managing perfume inventory across multiple branches (Lipa and San Pablo). This project integrates YOLOv8 AI, Firebase Cloud synchronization, and real-time edge hardware monitoring.

## 🚀 Key Features

- **AI-Powered Inventory**: Real-time detection of perfume bottles (A, B, C, D) using YOLOv8.
- **Multi-Branch Support**: Isolated inventory tracking and unique scenario simulations for Lipa and San Pablo.
- **Live Webcam Integration**: Support for real-time camera captures and AI processing on edge hardware.
- **Remote ROI Calibration**: In-app live stream with green zone overlays for perfect hardware alignment.
- **Automated Cooling**: Simulated temperature monitoring and automatic fan activation to protect high-value inventory.
- **Professional Alerts**: Modern, color-coded notifications (Low Stock, Misplaced, Out of Stock) with real product name lookups.
- **Real-time Status**: Live monitoring of "Cloud Link" and "Edge Device" connectivity via a 30s heartbeat system.

## 📁 Project Structure

```
~/otto-scents-smart-shelf/
├── app/                  # Native Android App (Kotlin / Jetpack Compose)
├── shelf_simulator.py    # Python Edge Simulator (AI, Camera, Firestore Sync)
├── docs/
│   ├── shelf_captures/   # Realistic branch-specific scenario images
│   ├── VM_FILES_MANIFEST.md # Guide for setting up the Edge hardware
│   └── ...
├── ubuntu_setup_guide.md # Step-by-step instructions for Ubuntu VM setup
└── ...
```

## 📱 Mobile App Setup (Android)

1. Open the project in **Android Studio**.
2. Wait for Gradle sync to complete.
3. Run the app on an emulator or a physical device.
4. **Login Credentials**:
   - **Admin**: `admin@ottoscents.com` / `password123` (Manages San Pablo)
   - **Staff**: `staff1@ottoscents.com` / `password123` (Manages Lipa)

## 📡 Edge Hardware Setup (Ubuntu/Raspberry Pi)

Follow the detailed instructions in [ubuntu_setup_guide.md](ubuntu_setup_guide.md) to set up your virtual hardware.

### Quick Start:
```bash
# 1. Activate Environment
source venv/bin/activate

# 2. Install Dependencies
pip install ultralytics firebase-admin opencv-python

# 3. Run Simulator
python shelf_simulator.py
```

## 🛠️ Calibration & Demonstration

1. Open the **Shelf & ROI Tester** in the app.
2. Tap **Start Live Calibration** to see the real-time webcam feed and align your shelf zones.
3. Trigger a **Manual Inventory Check** to see the AI detect stock levels and update your dashboard instantly.

---
© 2026 Otto Scents. Developed for Capstone Research.
