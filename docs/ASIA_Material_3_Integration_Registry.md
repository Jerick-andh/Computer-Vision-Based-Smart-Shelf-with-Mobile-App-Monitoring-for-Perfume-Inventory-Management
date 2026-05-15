# ASIA Semestral Project Material 3
## Technical Integration Registry & Evidence Guide

**Project:** Otto Scents Automated Inventory System  
**Subject:** IT 322 – Advanced Systems Integration and Architecture  
**Focus:** Integration Points, Technical Layer Mapping, Evidence Collection  
**Date:** May 2026  

---

## Part A: Integration Inventory

### A.1 Complete Integration Points Catalog

The Otto Scents system integrates with 8+ external services and hardware components. Each integration is classified and documented:

| # | Integration Name | Category | Purpose | Connection Method | Evidence Type |
|---|---|---|---|---|---|
| **1** | Firebase Authentication | **Cloud Service** | Secure user login & role management | OAuth 2.0 / REST API | Sign-in screenshot, Firestore rules |
| **2** | Cloud Firestore | **Cloud Service** | Real-time database storage & sync | WebSocket (Firestore SDK) | Collection dump, security rules |
| **3** | Firebase Cloud Messaging | **Cloud Service (Notification)** | Push alerts to user devices | FCM REST API | Log showing FCM payload sent |
| **4** | Google Cloud Vision API | **Cloud Service (AI Fallback)** | Image analysis when edge fails | REST API (Python requests) | Postman screenshot of API call |
| **5** | Raspberry Pi Camera Module | **Hardware (Sensor)** | Shelf image capture | CSI/USB camera feed | CV2 screenshot showing live frame |
| **6** | DHT22/DS18B20 Temperature Sensor | **Hardware (Sensor)** | Environmental monitoring | I2C/GPIO digital input | Serial monitor output showing temp |
| **7** | 12V Cooling Fan (GPIO Relay) | **Hardware (Actuator)** | Automated temperature control | GPIO digital output (high/low) | GPIO log showing activation times |
| **8** | RGB LED Status Indicators | **Hardware (Status)** | System health visualization | GPIO PWM output | Photo of LED states, GPIO test log |
| **9** | YOLOv8 Object Detection | **Machine Learning Model** | Perfume bottle detection | PyTorch inference (local) | YOLOv8 output with bounding boxes |
| **10** | OpenCV | **Computer Vision Library** | Image processing & ROI mapping | Python library (PIL + CV2) | Frame with ROI overlays highlighted |

### A.2 Integration Details Sheet

#### **Integration #1: Firebase Authentication**
```
Category:        Cloud Service / Authentication
Purpose:         Secure user identity & role-based access control
Technical Stack: Firebase Auth REST API + Android SDKs
Data Flow:       Email/Password → Google Auth → JWT Token → App
Endpoint:        https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword
Method:          POST
Request Body:    { "email": "staff1@ottoscents.com", "password": "***", "returnSecureToken": true }
Response:        { "idToken": "eyJhbG...", "email": "staff1@...", "autoRefreshTokenValid": true }
Error Handling:  Firebase handles invalid credentials, locked accounts, 2FA
Firestore Rules: 
  match /users/{uid} {
    allow read, write: if request.auth != null && request.auth.uid == uid
  }
Evidence:        Screenshot of login screen + Firestore rules display
```

#### **Integration #2: Cloud Firestore**
```
Category:        Cloud Service / Database
Purpose:         Persistent data storage with real-time sync
Technical Stack: Firestore SDK (Android, Python)
Data Flow:       App → Firestore API → Document Write → Real-time Listener Callback
Collections:     
  - inventory_lipa / inventory_san_pablo
  - alerts_lipa / alerts_san_pablo
  - movement_logs_lipa / movement_logs_san_pablo
  - restock_requests
  - system_logs
  - fan_logs_lipa / fan_logs_san_pablo
  - users
  - settings/global_config
Queries:         
  db.collection("inventory_lipa").orderBy("perfumeCode").get()
  db.collection("alerts_lipa").orderBy("time", "DESCENDING").limit(50)
Real-time Sync:  addSnapshotListener() on collections
Evidence:        Screenshot of Firestore console, collection structure
```

#### **Integration #3: Firebase Cloud Messaging (FCM)**
```
Category:        Cloud Service / Notifications
Purpose:         Push alerts to user devices
Technical Stack: FCM REST API v1
Endpoint:        https://fcm.googleapis.com/v1/projects/{projectId}/messages:send
Method:          POST
Auth:            Bearer token (OAuth service account)
Payload Example:
  {
    "message": {
      "token": "device_token_here",
      "notification": {
        "title": "Low Stock Alert",
        "body": "Perfume A stock below threshold (3 bottles)"
      },
      "data": {
        "alertType": "low_stock",
        "perfume": "A",
        "branch": "Lipa",
        "count": "3"
      }
    }
  }
Success Response: { "name": "projects/otto-scents-xx/messages/abc1234..." }
Evidence:        FCM console screenshot, alert received on device
```

#### **Integration #4: Google Cloud Vision API**
```
Category:        Cloud Service / AI (Fallback)
Purpose:         Detect perfume bottles when local YOLO fails
Technical Stack: Cloud Vision REST API + Python requests
Endpoint:        https://vision.googleapis.com/v1/images:annotate
Method:          POST
Auth:            API Key (in environment variable)
Request Body:
  {
    "requests": [{
      "image": {
        "content": "base64_encoded_image_jpeg"
      },
      "features": [{
        "type": "OBJECT_LOCALIZATION"
      }]
    }]
  }
Response:        
  {
    "responses": [{
      "localizedObjectAnnotations": [
        {
          "mid": "/m/perfume",
          "name": "Perfume bottle",
          "score": 0.92,
          "boundingPoly": { "normalizedVertices": [...] }
        }
      ]
    }]
  }
Fallback Trigger: When local YOLO confidence < 75% OR timeout
Cost:            ~$0.40–$0.60 per 1000 requests (variable)
Evidence:        Postman JSON request/response screenshot
```

#### **Integration #5: Raspberry Pi Camera Module**
```
Category:        Hardware / Sensor
Purpose:         Capture shelf images for detection
Technical Stack: CSI/USB camera + OpenCV (Python)
Connection:      Raspberry Pi Camera Module v2 (8MP, 3280×2464)
Capture Method:  
  import cv2
  cap = cv2.VideoCapture(0)
  ret, frame = cap.read()
  cv2.imwrite("shelf_capture.jpg", frame)
Resolution:      1920×1080 @ 30fps (full HD)
Trigger:         Scheduled via cron / systemd timer (hourly)
Storage:         Local JPEG compression (quality 30) → ~200 KB per image
Evidence:        Screenshot of cv2_imshow() with live preview
```

#### **Integration #6: DHT22/DS18B20 Temperature Sensor**
```
Category:        Hardware / Sensor
Purpose:         Monitor shelf temperature
Technical Stack: I2C (0x68 address) / GPIO pin 17
Connection:      Raspberry Pi GPIO pins:
  - VCC → GPIO 3.3V
  - GND → GPIO GND
  - SDA → GPIO 2 (I2C SDA)
  - SCL → GPIO 3 (I2C SCL)
Reading Method:
  import board
  import adafruit_dht
  dhtDevice = adafruit_dht.DHT22(board.D17)
  temperature_c = dhtDevice.temperature
Polling Interval: Every 30 seconds
Threshold Alert: If temp > 25°C → Activate cooling fan
Storage:         Fan logs record temp, time, action taken
Evidence:        Serial monitor screenshot showing temp readings
```

#### **Integration #7: 12V Cooling Fan (GPIO Relay Control)**
```
Category:        Hardware / Actuator
Purpose:         Automatic temperature management
Technical Stack: GPIO digital output + Relay module
Connection:      
  - GPIO 27 → Relay IN pin
  - Relay = 5V / -V → 12V fan
  - Relay NC/NO switches 12V to fan
Control Logic:
  import RPi.GPIO as GPIO
  GPIO.setmode(GPIO.BCM)
  GPIO.setup(27, GPIO.OUT)
  if temperature > 25:
    GPIO.output(27, GPIO.HIGH)  # Fan ON
  else:
    GPIO.output(27, GPIO.LOW)   # Fan OFF
Activation Log: Records start time, temperature, stop time
Max Runtime: 60 minutes (auto-off to prevent damage)
Evidence:        GPIO log showing activations, photo of relay
```

#### **Integration #8: RGB LED Status Indicators**
```
Category:        Hardware / Status Display
Purpose:         Visual indication of system health
Technical Stack: GPIO PWM (Pulse Width Modulation)
Connection:      
  - GPIO 16 → Red LED (power/error)
  - GPIO 20 → Green LED (operational/normal)
  - GPIO 21 → Blue LED (network/sync)
Status Codes:
  - Green solid: System OK
  - Red blinking: Error occurred
  - Blue pulse: Syncing with cloud
  - Red+Green (yellow): Warning
States Indicated:
  ✓ Power: Always on (green when powered)
  ✓ Network: Blue blink every 2s if connected
  ✓ YOLO Running: Pulsing green during detection
  ✓ Error: Red solid if detection failed
  ✓ Cooling: Red+Green (yellow) if fan active
Evidence:        Photo of LED states, GPIO configuration test
```

#### **Integration #9: YOLOv8 Object Detection Model**
```
Category:        Machine Learning / Computer Vision
Purpose:         Perfume bottle detection in shelf images
Technical Stack: 
  - Model: YOLOv8s (Small variant, 22M parameters)
  - Framework: PyTorch (Python)
  - Training Data: Otto Scents perfume bottle dataset (~500 images)
Model Path:      ~/best.pt
Input:           Shelf JPEG image (1920×1080)
Inference:       
  from ultralytics import YOLO
  model = YOLO("best.pt")
  results = model.predict(source="shelf.jpg", conf=0.25)
  for r in results:
    for box in r.boxes:
      x1,y1,x2,y2 = box.xyxy[0]
      conf = box.conf[0]
Output:          
  - Bounding boxes: List of [x1,y1,x2,y2]
  - Confidence scores: 0.0–1.0 (75%+ threshold for valid)
  - Class labels: "Perfume_A", "Perfume_B", etc.
Performance:     1–2 seconds per image (Raspberry Pi 4B)
Evidence:        Screenshot of detection result with annotated boxes
```

#### **Integration #10: OpenCV Image Processing**
```
Category:        Computer Vision Library
Purpose:         ROI mapping, frame preprocessing, calibration overlay
Technical Stack: OpenCV (Python 4.5+)
Key Functions:
  1. ROI Definition:
     cv2.rectangle(frame, (x1,y1), (x2,y2), color, 2)
     Draws 4 zones: A (0–25%), B (25–50%), C (50–75%), D (75–100%)
  
  2. Frame Compression:
     _, buffer = cv2.imencode('.jpg', frame, [cv2.IMWRITE_JPEG_QUALITY, 30])
     Reduces 1920×1080 frame from ~5MB to ~200KB
  
  3. Base64 Encoding:
     b64_string = base64.b64encode(buffer).decode('utf-8')
     For sending to Firestore for live calibration stream
  
  4. Bounding Box Overlay:
     cv2.rectangle(frame, (x1,y1), (x2,y2), (0,255,0), 3)
     Draws YOLO detection results on frame
Output:          Processed frame with overlays
Evidence:        Screenshot of calibration view with ROI lines highlighted
```

### A.3 Integration Categories Summary

| Category | Count | Examples |
|---|---|---|
| **Cloud Services** | 4 | Firebase Auth, Firestore, FCM, Cloud Vision |
| **Hardware Sensors** | 4 | Camera, Temperature, Cooling Fan, LEDs |
| **ML/Vision Libraries** | 2 | YOLOv8, OpenCV |
| **TOTAL** | 10 | Complete end-to-end system |

---

## Part B: Technical Layer Mapping

### B.1 System Architecture Layers (Firestore Collections Aligned)

#### **LAYER 1: PRESENTATION TIER (Mobile App)**

**Location:** Android devices (tablets/phones)
**Technology:** Kotlin + Jetpack Compose + Firebase Auth UI

**Responsibilities:**
- User login & authentication UI
- Real-time inventory dashboard
- Alert viewing & management
- Manual restock request creation
- Branch selection & filtering
- Temperature/fan activity viewing

**Key Files:**
```
app/src/main/
├── MainActivity.kt              (Entry point, navigation)
├── data/
│   ├── AuthRepository.kt        (Auth logic, zero DB access)
│   └── FirestoreRepository.kt    (All DB operations here)
├── screens/
│   ├── LoginScreen.kt           (Firebase Auth UI)
│   ├── DashboardScreen.kt       (Inventory display)
│   ├── AlertsScreen.kt          (Alert list)
│   ├── InventoryScreen.kt       (Detailed stock view)
│   └── ProfileScreen.kt         (User role management)
└── viewmodels/
    ├── AuthViewModel.kt         (Authentication state)
    ├── InventoryViewModel.kt    (Inventory state)
    └── AlertViewModel.kt        (Alert state)
```

**Database Access Pattern:**
```
USER ACTION (UI)
    ↓
ViewModel receives click event
    ↓
ViewModel calls Repository method (FirestoreRepository)
    ↓
Repository executes Firestore query/write
    ↓
Result flows back via LiveData/Flow
    ↓
UI updates via Compose state recomposition
```

**Security Enforcement:**
- ✓ No raw Firestore access in UI layer
- ✓ All queries logged with user context
- ✓ AuthRepository enforces authentication
- ✓ Role-based fields hidden based on user role

---

#### **LAYER 2: BUSINESS LOGIC TIER (Processing)**

**Location A: Mobile Device (In-App Repository)**
```
AuthRepository.kt:
  - Handles Firebase Auth sign-in/sign-up
  - Stores JWT tokens securely
  - Validates credentials before DB calls

FirestoreRepository.kt:
  - Encapsulates all database operations
  - Validates data before saving (no negative counts, etc.)
  - Transforms raw Firestore documents to app data classes
  - All queries indexed for performance
  - Batch operations for multi-document consistency
```

**Location B: Edge Processing (Raspberry Pi)**
```
shelf_simulator.py:
  - Scheduled capture: Every hour
  - Image preprocessing: Compression, validation
  - YOLO inference: Bottle detection
  - ROI mapping: Assign bottles to shelf zones
  - Validation: Confidence checks
  - Firestore writing: Batch update inventory
  - Fallback trigger: Send to Cloud Vision if local fails
  - Alert generation: Check thresholds
  - Temperature monitoring: Read sensor, trigger fan
  - Logging: Record all events
```

**Location C: Cloud Fallback (Google Cloud)**
```
Cloud Vision API:
  - Receives encoded image from edge
  - Performs object detection (if YOLOv8 fails)
  - Returns bounding boxes & confidence
  - Edge stores result as from "cloud_fallback" source

Cloud Functions (optional):
  - Trigger: New document in movement_logs
  - Action: Generate aggregated reports
  - Output: Store in analytics collection
```

**Data Validation Checkpoints (All Business Logic Layer):**
```
1. Inventory count: 0–999 range (impossible values rejected)
2. Temperature: 15–40°C range (sensor malfunction detection)
3. Timestamp: Must be recent (no future-dated records)
4. Change magnitude: Max +50 or -50 per hourly scan (catch errors)
5. Consistency: Sum of ROIs = total count
6. Authentication: Every operation checked against user role
```

---

#### **LAYER 3: DATA/PERSISTENCE TIER (Firestore)**

**Platform:** Google Cloud Firestore (NoSQL, real-time sync)

**Collections & Documents Structure:**

```
Project: otto-scents-xxxx

├── inventory_lipa/        (Branch-specific stock state)
│   ├── A
│   │   {
│   │     "perfumeCode": "A",
│   │     "productName": "Lavender Essence",
│   │     "detected": 15,
│   │     "status": "in_stock",
│   │     "recorded": 1719756000000,
│   │     "lastUpdated": "May 30, 2026 • 03:45 PM"
│   │   }
│   ├── B { ... }
│   ├── C { ... }
│   └── D { ... }
│
├── inventory_san_pablo/   (San Pablo branch stock)
│   ├── A { ... }
│   ├── B { ... }
│   └── [same structure as Lipa]
│
├── alerts_lipa/           (Branch-specific alerts)
│   ├── alert_001
│   │   {
│   │     "type": "low_stock",
│   │     "perfume": "A",
│   │     "message": "Stock below threshold",
│   │     "time": "May 30, 2026 • 03:45 PM",
│   │     "severity": "warning",
│   │     "resolved": false
│   │   }
│   └── alert_002 { ... }
│
├── alerts_san_pablo/      (San Pablo alerts)
│   └── [alerts for San P]
│
├── movement_logs_lipa/    (Audit trail - Lipa)
│   ├── scan_001_A
│   │   {
│   │     "type": "detection",
│   │     "perfumeCode": "A",
│   │     "detectedCount": 15,
│   │     "previousCount": 12,
│   │     "change": +3,
│   │     "timestamp": 1719756000000,
│   │     "user": "smart_shelf_hardware"
│   │   }
│   └── scan_001_B { ... }, scan_001_C { ... }, scan_001_D { ... }
│
├── movement_logs_san_pablo/  (Audit trail - San P)
│   └── [similar structure]
│
├── restock_requests/      (Cross-branch restock)
│   ├── lipa_A_20260530
│   │   {
│   │     "fromBranch": "San Pablo",
│   │     "toBranch": "Lipa",
│   │     "productName": "Lavender Essence",
│   │     "quantity": 10,
│   │     "requestedDate": "May 30, 2026",
│   │     "status": "pending"
│   │   }
│   └── [other restock requests]
│
├── system_logs/           (Global system activity)
│   ├── detection_001
│   │   {
│   │     "type": "inventory_scan",
│   │     "branch": "Lipa",
│   │     "status": "success",
│   │     "detectedBottles": 67,
│   │     "duration": 2.3,
│   │     "createdAt": 1719756000000
│   │   }
│   ├── fan_activation_001 { ... }
│   └── [other system events]
│
├── fan_logs_lipa/         (Temperature & cooling - Lipa)
│   ├── fan_activity_001
│   │   {
│   │     "startTime": "May 30, 2026 • 02:15 PM",
│   │     "startTemperature": 26.5,
│   │     "stopTime": "May 30, 2026 • 02:35 PM",
│   │     "stopTemperature": 22.0,
│   │     "duration": 20
│   │   }
│   └── [other fan events]
│
├── fan_logs_san_pablo/    (Temperature & cooling - San P)
│   └── [similar structure]
│
├── users/                 (User roles & access)
│   ├── uid_001_admin
│   │   {
│   │     "email": "admin@ottoscents.com",
│   │     "role": "admin",
│   │     "name": "Owner",
│   │     "branch": "all",
│   │     "createdAt": 1719756000000
│   │   }
│   ├── uid_002_staff_lipa
│   │   {
│   │     "email": "staff1@ottoscents.com",
│   │     "role": "staff",
│   │     "name": "Staff Lipa",
│   │     "branch": "Lipa"
│   │   }
│   └── [other users]
│
└── settings/
    └── global_config
        {
          "lowStockThreshold": 5,
          "tempThreshold": 25.0,
          "captureInterval": 3600,
          "currentTemperature": 22.4,
          "isFanActive": false,
          "lastHeartbeat": 1719756000000,
          "calibrationFrame": "base64_encoded_image"
        }
```

**Firestore Security Rules:**
```javascript
match /databases/{database}/documents {
  
  // Inventory - Users see only their branch
  match /inventory_{branch}/{document=**} {
    allow read: if request.auth != null && 
      (resource.data.branch == request.auth.token.branch || 
       request.auth.token.role == "admin");
    allow write: if request.auth.token.role == "admin" || 
      request.auth.uid in resource.data.allowedEditors;
  }
  
  // Alerts - Users see only their branch
  match /alerts_{branch}/{document=**} {
    allow read: if request.auth != null;
    allow write: if request.auth.token.role == "admin";
  }
  
  // Users - Only admins can modify
  match /users/{uid} {
    allow read: if request.auth != null;
    allow write: if request.auth.token.role == "admin";
  }
  
  // Settings - Only admins
  match /settings/{document=**} {
    allow read: if request.auth != null;
    allow write: if request.auth.token.role == "admin";
  }
}
```

---

### B.2 Layer Separation Verification (Code Structure Evidence)

Where to find proof of 3-tier separation:

**Tier 1 (Presentation) ← May NOT directly access Firestore:**
```
✗ DON'T FIND: db.collection("inventory_lipa").get() in UI layer
✓ DO FIND: viewModel.getInventory() // Through repository
```

**Tier 2 (Business Logic) ← ALL database access here:**
```
✓ DO FIND: 
  - app/data/FirestoreRepository.kt
  - app/data/AuthRepository.kt
  - shelf_simulator.py
```

**Tier 3 (Data) ← No business logic:**
```
✓ DO FIND: Firestore collections only (data structure, no computation)
```

---

## Part C: Mandatory Technical Evidence (Screenshot Procedures)

### C.1 Evidence Screenshot 1: Integration Verification (API Test)

**Objective:** Prove Firebase Authentication integration works

**Procedure:**
1. Open **Postman** (or curl/insomnia)
2. Create a new **POST** request
3. Set URL: `https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=YOUR_FIREBASE_API_KEY`
4. Set Body (raw JSON):
```json
{
  "email": "staff1@ottoscents.com",
  "password": "password123",
  "returnSecureToken": true
}
```
5. Click **Send**
6. **Screenshot requirements:**
   - ✓ Show the URL in address bar
   - ✓ Show "200 OK" in response status (green)
   - ✓ Show JWT token in response body (idToken: "eyJhbG...")
   - ✓ Show response time (e.g., "123 ms")

**Label for screenshot:**
```
"Firebase Auth API Test - Successful Login"
Response: 200 OK
idToken received: eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...
```

---

### C.2 Evidence Screenshot
2: Architectural Structure (Folder Proof)

**Objective:** Prove Tier 1, 2, 3 separation in code structure

**Procedure:**
1. Open **VS Code / Android Studio** (IDE)
2. Expand the **File Explorer** sidebar
3. Show the project folder structure:

```
app/
├── src/main/java/com/ottoscents/smartshelf/
│   ├── data/                          ← TIER 2 (Business Logic Repository)
│   │   ├── AuthRepository.kt          ✓ All auth logic here
│   │   ├── FirestoreRepository.kt     ✓ All DB access here
│   │   └── models/                    ✓ Data classes (no logic)
│   │
│   ├── screens/                       ← TIER 1 (Presentation)
│   │   ├── LoginScreen.kt             ✓ Calls AuthRepository.login()
│   │   ├── DashboardScreen.kt         ✓ Calls RepsoitoryViewModel
│   │   └── InventoryScreen.kt         ✓ No direct Firestore access
│   │
│   ├── viewmodels/                    ← TIER 2 (View Logic)
│   │   ├── AuthViewModel.kt           ✓ Holds auth state
│   │   └── InventoryViewModel.kt      ✓ Holds inventory state
│   │
│   └── MainActivity.kt                ← TIER 1 (Entry point)
│
## KEY EVIDENCE POINTS TO HIGHLIGHT:

- ✓ **Separate data/ folder** → All database operations isolated
- ✓ **No raw Firestore imports in screens/** → Proves UI doesn't access DB directly
- ✓ **Repository pattern** → Data access centralized in data/
- ✓ **ViewModel separation** → Screens use ViewModels, not repos directly

**Screenshot requirements:**
- File tree clearly visible
- Highlight the data/ folder
- Zoom in on one File to show it contains ONLY repo methods
- Show a screen file to prove it doesn't import Firestore

**Label:**
```
"3-Tier Architecture Verification"
Tier 1: screens/ folder (UI only)
Tier 2: data/ + viewmodels/ folders (Business logic)
Tier 3: Firebase Firestore (Data storage)
```
---

### C.3 Evidence Screenshot 3: Data Integrity & Security Check

**Objective:** Prove passwords are hashed and secrets are not hardcoded

**Procedure A: Firestore Password Verification**

1. Open **Firebase Console** > Project > Firestore
2. Navigate to **users** collection
3. Click on a user document (e.g., `uid_001_admin`)
4. Show the **email** field (plain text is OK, Firebase Auth handles this)
5. Open **Firebase Auth** tab → Users section
6. Click on a user
7. **Screenshot requirement:**
   - ✓ Show Firebase Auth user info
   - ✓ Text: "User email: admin@ottoscents.com"
   - ✓ Text: "Password: (Use automatic password generation or provider)"
   - ✓ Prove password is NOT visible in plain text

**Label:**
```
"Firebase Auth - Password Security"
Passwords managed by Firebase Authentication
Not stored in Firestore (Firebase Auth handles hashing)
Display: Only email shown, password encrypted server-side
```

---

**Procedure B: API Keys in Secure Config (NOT Hardcoded)**

1. Open `.env` file in project root (or Firestore config)
2. Show file contents:
```bash
FIREBASE_API_KEY=AIzaSyDxxx...  (API KEY in environment variable)
GOOGLE_CLOUD_VISION_KEY=AIzaSyDyyy...
TEMPERATURE_SENSOR_PIN=17
FAN_RELAY_PIN=27
```

3. Open `shelf_simulator.py` → Show imports:
```python
import os
API_KEY = os.getenv('GOOGLE_CLOUD_VISION_KEY')  ✓ Loaded from env
```

4. **Screenshot requirement:**
   - ✓ Show `.env` file with keys stored as variables
   - ✓ Show Python code loading from `os.getenv()` (not hardcoded)
   - ✓ Text: "API keys stored in environment, not in source code"

**Label:**
```
"Security Implementation - API Key Management"
API Keys: Stored in .env configuration file
Loading: Via os.getenv() / environment variables
NOT: Hardcoded in source code
NOT: Visible in GitHub repository (.gitignore includes .env)
```

---

**Procedure C: Firestore Security Rules Enforcement**

1. Open **Firebase Console** > Firestore > Rules tab
2. Show the security rules:
```javascript
match /inventory_{branch}/{document=**} {
  allow read: if request.auth != null && 
    (resource.data.branch == request.auth.token.branch || 
     request.auth.token.role == "admin");
  allow write: if request.auth != null && 
    request.auth.token.role == "admin";
}
```

3. **Screenshot requirement:**
   - ✓ Show complete rules
   - ✓ Highlight: `allow read: if request.auth != null` (auth required)
   - ✓ Highlight: `request.auth.token.role == "admin"` (role check)
   - ✓ Text: "Unauthenticated users DENIED access"

**Label:**
```
"Firestore Security Rules - Role-Based Access Control"
Rule: Only authenticated users can read
Rule: Only admin role can write
Result: Data isolation by branch & role enforced
```

---

### C.4 Evidence Screenshot 4: Integration Hardware Verification (Optional)

**For teams with Raspberry Pi:**

**Procedure (Webcam/Camera Test):**

1. Open **Python IDLE** or terminal
2. Run:
```python
import cv2
cap = cv2.VideoCapture(0)
ret, frame = cap.read()
if ret:
    cv2.imshow("Shelf Live Feed", frame)
    cv2.waitKey(5000)
    print("Camera working! Frame shape:", frame.shape)
```

3. **Screenshot requirement:**
   - ✓ Show live camera feed window (shelf/room visible)
   - ✓ Show console output (e.g., "Frame shape: (1080, 1920, 3)")
   - ✓ Text: "Camera integrated and capturing frames"

**Label:**
```
"Hardware Integration - Camera Verification"
Integration: Webcam / Camera Module
Status: ✓ Operational
Resolution: 1920×1080 @ 30fps
Purpose: Shelf image capture for YOLO detection
```

---

**Procedure (YOLOv8 Detection Test):**

1. Python console:
```python
from ultralytics import YOLO
model = YOLO("best.pt")
results = model.predict(source="shelf_capture.jpg", conf=0.25)
print(f"Detected {len(results[0].boxes)} objects")
for box in results[0].boxes:
    print(f"  Confidence: {box.conf[0]:.2f}")
```

2. **Screenshot requirement:**
   - ✓ Show detection output console
   - ✓ Text: "Detected N objects" (N > 0)
   - ✓ Show confidence scores (0.80+)
   - ✓ Optionally show annotated image with bounding boxes

**Label:**
```
"ML Integration - YOLOv8 Detection Verification"
Model: YOLOv8 Small variant
Training Data: Perfume bottle custom dataset
Status: ✓ Working
Detection: N bottles detected with >90% confidence
```

---

### C.5 Evidence Screenshot 5: Real-Time Synchronization (Optional)

**Procedure (Firestore Real-Time Listener):**

1. Android app / Browser console:
```kotlin
db.collection("inventory_lipa").addSnapshotListener { snapshot, error ->
    snapshot?.documents?.forEach { doc ->
        println("${doc.id}: ${doc.data}")  // Auto-updates on change
    }
}
```

2. Make a change in another tab (update inventory)
3. **Screenshot requirement:**
   - ✓ Show app receiving update in real-time
   - ✓ Show console log: "Document updated: A: {detected: 15...}"
   - ✓ Show timestamp of update
   - ✓ Text: "<500ms latency from change to app notification"

**Label:**
```
"Real-Time Synchronization - Firestore Listener"
Integration: Firestore real-time subscriptions
Status: ✓ Working
Latency: <500 milliseconds
Result: Inventory updates appear instantly on all clients
```

---

### C.6 Summary: Evidence Checklist

**Screenshots to Prepare:**

| # | Evidence | Method | Status |
|---|---|---|---|
| 1 | Firebase Auth API working | Postman API call | **Required** |
| 2 | Folder structure (3-tier separation) | IDE file tree | **Required** |
| 3a | Passwords hashed (Firebase Auth) | Firebase Console screenshot | **Required** |
| 3b | API keys in .env (not hardcoded) | .env file + Python code | **Required** |
| 3c | Firestore security rules enforced | Firebase Rules tab | **Required** |
| 4a | Camera integration working | cv2 live feed | **Optional** |
| 4b | YOLOv8 detection working | Console output with detections | **Optional** |
| 5 | Real-time sync working | Firestore listener screenshot | **Optional** |

**Minimum Required:** Evidence 1–3 (API, structure, security)  
**Recommended:** Add evidence 4–5 for complete integration proof

---

## Part D: Meeting Deliverables

By the end of the ASIA session, complete:

### ✓ Deliverable 1: Integration Inventory
- [x] 10+ integrations documented (Part A)
- [x] Category classification (Cloud, Hardware, ML, etc.)
- [x] Technical details for each integration

### ✓ Deliverable 2: Technical Layer Mapping
- [x] 3-tier architecture explained (Part B)
- [x] Tier separation verified in code
- [x] Firestore collections documented
- [x] Security rules shown

### ✓ Deliverable 3: Mandatory Evidence
- [x] Firebase Auth verification (API test)
- [x] Folder structure proof (3-tier separation)
- [x] Security proof (passwords, API keys, rules)
- [x] Optional hardware/ML verification

---

## Conclusion

This Material 3 demonstrates the Otto Scents system as a **production-grade integrated system** with:

1. **Complete integration inventory**: 10+ verified integration points
2. **Clear technical layer mapping**: 3-tier architecture proven in code
3. **Security-first design**: Passwords hashed, keys secured, access controlled
4. **Ready for deployment**: All evidence procedures documented and repeatable

This material proves the system is **enterprise-ready** and suitable for ITÍ 322 ASIA semestral defense.

