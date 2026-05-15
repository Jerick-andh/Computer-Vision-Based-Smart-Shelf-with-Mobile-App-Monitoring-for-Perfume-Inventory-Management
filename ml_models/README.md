# ML Models - Machine Learning Assets

**YOLOv8 trained model and datasets for perfume bottle detection**

## Directory Contents

```
ml_models/
├── yolo_detector/          # Trained YOLOv8 model
├── datasets/               # Training and validation data
└── trained_models/         # Model version history
```

## YOLOv8 Detector Model

### File: `best.pt`

**Model Details:**
- **Architecture**: YOLOv8 Small (YOLOv8s)
- **Parameters**: ~22 million
- **Input Size**: 640×640 pixels
- **Training Data**: Perfume bottle dataset (~500 images)
- **Classes**: 4 (Perfume A, B, C, D)
- **Accuracy**: 98%+ on test set
- **Inference Time**: 1-2 seconds on Raspberry Pi 4B

### Performance Metrics

| Metric | Value | Notes |
|---|---|---|
| mAP50 | 0.95+ | Mean Average Precision at IoU 0.5 |
| mAP50-95 | 0.88+ | Stricter quality metric |
| Precision | 0.96+ | Few false positives |
| Recall | 0.94+ | Few missed detections |
| FPS (Pi 4B) | ~0.5 | 1-2 second inference |
| FPS (GPU) | ~100+ | On NVIDIA hardware |

### Usage

#### Python (Raspberry Pi)
```python
from ultralytics import YOLO
model = YOLO("ml_models/yolo_detector/best.pt")
results = model.predict(source="shelf.jpg", conf=0.25)

for r in results:
    for box in r.boxes:
        print(f"Class: {box.cls}, Confidence: {box.conf}")
```

#### Command Line
```bash
yolo detect predict model=ml_models/yolo_detector/best.pt source=shelf.jpg
```

## Dataset Information

### Location: `datasets/perfume_dataset/`

**Dataset Structure:**
```
perfume_dataset/
├── data.yaml                    # Dataset metadata
├── train/
│   ├── images/                  # ~400 training images
│   └── labels/                  # Corresponding YOLO format labels
└── val/
    ├── images/                  # ~100 validation images
    └── labels/
```

### Dataset Specifications

| Aspect | Detail |
|---|---|
| **Total Images** | ~500 (train: 400, val: 100) |
| **Resolution** | 1920×1080 pixels (multi-scale) |
| **Classes** | 4 (Perfume A, B, C, D) |
| **Format** | YOLO format (text annotations) |
| **Annotation Tool** | Roboflow |
| **Branches Covered** | Lipa, San Pablo |
| **Scenarios** | Full stock, sales, restock, misplaced |

### Class Distribution

```
Class A (Lavender): ~140 images (28%)
Class B (Rose):     ~130 images (26%)
Class C (Vanilla):  ~120 images (24%)
Class D (Jasmine):  ~110 images (22%)
```

### Real-World Scenarios

The dataset includes realistic scenarios:
- Full shelf stock
- Sales (partial depletion)
- Restocking (mixed stock)
- Misplaced items
- Challenging angles and lighting
- Both branch environments

## Model Training & Experimentation

### Trained Models Directory

Store different model versions:
```
trained_models/
├── v1_baseline.pt              # Initial training
├── v2_balanced_dataset.pt      # After dataset balancing
├── v3_augmentation.pt          # With advanced augmentation
└── v4_hyperparameter_tuned.pt  # Final optimized model
```

### Training Process

Original training configuration:
```bash
yolo detect train data=ml_models/datasets/data.yaml \
    model=yolov8s.pt \
    epochs=100 \
    imgsz=640 \
    batch=16 \
    device=0
```

### Why YOLOv8?

1. **Speed**: Real-time inference suitable for edge devices
2. **Accuracy**: State-of-the-art object detection (96%+ precision)
3. **Simplicity**: Easy to train and deploy
4. **Resource Efficiency**: Weighs ~22MB (compresses to 11MB)
5. **Framework**: PyTorch (portable, widely supported)
6. **Roboflow Integration**: Easy dataset management and versioning

## How to Update the Model

### 1. Collect New Training Data
```bash
# Add new images to datasets/perfume_dataset/train/images/
# Annotate with Roboflow (YOLO format)
```

### 2. Retrain Model
```bash
cd ml_models/datasets
yolo detect train data=data.yaml model=yolov8s.pt epochs=100
```

### 3. Validate Performance
```bash
yolo detect val model=runs/detect/train/weights/best.pt
```

### 4. Deploy New Model
```bash
# Copy to production location
cp runs/detect/train/weights/best.pt yolo_detector/best.pt
```

## Quality Assurance

### Confidence Threshold
- **Production**: 0.75 (75% confidence minimum)
- **Fallback**: 0.50 (lower threshold if edge fails)
- **Alert**: If avg confidence < 0.60, trigger cloud fallover

### Validation Process
1. Run inference on validation set
2. Check mAP (mean average precision) ≥ 0.88
3. Verify recall ≥ 0.92 (avoid missed bottles)
4. Check precision ≥ 0.94 (avoid false positives)
5. Test on real-world shelf scenarios

## Deployment Checklist

- [ ] Model file exists: `yolo_detector/best.pt`
- [ ] Model size < 50MB
- [ ] Inference time < 2 seconds on Raspberry Pi
- [ ] Confidence scores > 75% on test imagery
- [ ] All 4 bottle classes represented in training
- [ ] Model tested with both branches' shelf images
- [ ] Fallback cloud vision API configured
- [ ] Version tracked in Git (except .pt files)

## Troubleshooting

### Model predicts low confidence
```
→ Check image quality (resolution, lighting)
→ Verify model wasn't corrupted during download
→ Consider retraining with more diverse data
```

### Memory error on Raspberry Pi
```
→ Model too large or batch size too high
→ Use YOLOv8n (nano) instead of YOLOv8s
→ Reduce image size to 480×480
```

### Class imbalance (A heavily detected, D rarely)
```
→ Augment dataset with more D samples
→ Use weighted loss during training
→ Adjust confidence thresholds per class
```

## Integration with System

### Edge Processing
```python
# shelf_simulator.py uses this model
model = YOLO("ml_models/yolo_detector/best.pt")
results = model.predict(source=captured_frame, conf=0.25)
```

### Firestore Storage
Detections saved as:
```json
{
  "detect_source": "yolo_v8s",
  "model_version": "1.0",
  "detection_time_ms": 1250,
  "bottle_count": 52,
  "confidence_avg": 0.96,
  "fallback_used": false
}
```

### Mobile App Display
The app visualizes:
- Bounding boxes around detected bottles
- Confidence scores per detection
- Detection timestamp
- Fallback status (if cloud used)

---

**For more info:** 
- Training details: `README.roboflow.txt`
- Dataset info: `README.dataset.txt`
- Edge simulator: `backend/edge_simulator/README.md`

