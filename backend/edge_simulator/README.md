# Backend - Edge Simulator

**Raspberry Pi-based edge processing layer for shelf monitoring**

This directory contains the Python edge simulator that runs on the Raspberry Pi at each branch location.

## What It Does

The edge simulator is responsible for:
- Capturing shelf images at scheduled intervals (hourly)
- Running YOLOv8 object detection locally
- Mapping bottles to Regions of Interest (ROI zones A, B, C, D)
- Synchronizing results to Firestore in real-time
- Monitoring temperature and controlling cooling fans
- Triggering cloud failover when local detection fails

## Files

| File | Purpose |
|---|---|
| `shelf_simulator.py` | Main edge processing script |
| `requirements.txt` | Python dependencies |
| `README.md` | This file |

## Quick Start

### 1. Setup Environment
```bash
# Create virtual environment
python3 -m venv venv
source venv/bin/activate  # or: venv\Scripts\activate on Windows

# Install dependencies
pip install -r requirements.txt
```

### 2. Configure Firebase
```bash
# Place your Firebase service account key
cp /path/to/serviceAccountKey.json ./serviceAccountKey.json
```

### 3. Run Simulator
```bash
python shelf_simulator.py
```

## Configuration

Edit the configuration section in `shelf_simulator.py`:

```python
MODEL_PATH = "path/to/best.pt"           # YOLOv8 model location
CREDENTIALS_PATH = "serviceAccountKey.json"
LOW_STOCK_THRESHOLD = 5                 # Alert when count drops below
TEMP_THRESHOLD = 25.0                   # Fan activation temperature (°C)
CAPTURE_INTERVAL = 3600                 # Capture every N seconds
```

## Key Functions

### `detect_and_update()` - Main Detection Loop
- Captures image from webcam
- Runs YOLOv8 inference
- Maps detections to ROI zones
- Updates Firestore collections
- Generates alerts if stock low

### `temperature_monitor()` - Environmental Control
- Reads temperature sensor (I2C GPIO pin 17)
- Activates cooling fan if `TEMP_THRESHOLD` exceeded
- Logs all temperature events

### `heartbeat()` - System Health
- Sends heartbeat to Firestore every 30 seconds
- Allows mobile app to detect connection status
- Triggers failover alert if heartbeat missing > 5 minutes

### `fallback_processing()` - Cloud Failover
- Triggered if local YOLO confidence < 75%
- Sends image to Google Cloud Vision API
- Falls back to cloud results if available

## Integration Points

| Integration | Purpose | Location |
|---|---|---|
| **Firestore** | Real-time database sync | inventory_*, alerts_*, system_logs |
| **YOLOv8 Model** | Bottle detection | ml_models/yolo_detector/best.pt |
| **Webcam** | Image capture | /dev/video0 (Linux) or USB camera |
| **Temperature Sensor** | Environmental monitoring | GPIO I2C address 0x68 |
| **Cooling Fan** | Temperature control | GPIO pin 27 relay control |
| **Cloud Vision** | Failover detection | Google Cloud API |

## Firestore Collections Updated

- `inventory_lipa` / `inventory_san_pablo` - Stock counts
- `alerts_lipa` / `alerts_san_pablo` - Low stock/temp alerts
- `system_logs` - Detection and processing events
- `fan_logs_lipa` / `fan_logs_san_pablo` - Cooling activations
- `settings/global_config` - Current status and config

## Hardware Requirements

- **Raspberry Pi** 4B (2GB+ RAM)
- **Camera Module** (CSI or USB, 1920×1080+)
- **Temperature Sensor** (DHT22/DS18B20)
- **Cooling Fan** with relay module
- **Network** connectivity (WiFi or Ethernet)

## Troubleshooting

### Model not found
```
Error: Model path incorrect
→ Check MODEL_PATH points to ml_models/yolo_detector/best.pt
```

### Firestore connection failed
```
Error: PERMISSION_DENIED
→ Verify serviceAccountKey.json loaded and credentials valid
→ Check Firestore security rules allow database writes
```

### Camera not detected
```
Error: Cannot access /dev/video0
→ Ensure camera connected via CSI or USB
→ Check permissions: `sudo usermod -a -G video pi`
```

### Temperature reading stuck
```
Error: I2C sensor offline
→ Verify DHT22 wired to GPIO 17
→ Run `i2cdetect -y 1` to check I2C devices
```

## Performance Notes

- **Inference time**: 1-2 seconds per image (Raspberry Pi 4B)
- **Firestore writes**: ~100ms per batch update
- **Memory usage**: ~400MB idle, ~600MB during inference
- **Network**: Requires at least 2 Mbps upload speed for reliable failover

## Security

- API keys stored in `.env` (never hardcoded)
- Firebase service account key in `.gitignore`
- Firestore security rules enforce database access control
- All network traffic encrypted in transit (TLS 1.3)

## Monitoring & Logging

View real-time logs:
```bash
tail -f simulator.log
```

Check Firestore for system_logs:
```javascript
db.collection("system_logs")
  .orderBy("createdAt", "desc")
  .limit(50)
  .onSnapshot(console.log)
```

---

**For more info:** See `docs/deployment/ubuntu_setup_guide.md`

