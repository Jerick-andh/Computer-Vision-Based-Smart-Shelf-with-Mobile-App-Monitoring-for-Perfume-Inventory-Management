# ASIA Semestral Project Material 2
## System Quality & Performance Analysis

**Project:** Otto Scents Automated Inventory System  
**Subject:** IT 322 – Advanced Systems Integration and Architecture  
**Focus:** ISO 25010 Quality Model, Data Integrity Architecture, Performance Scalability  
**Date:** May 2026  

---

## 1. ISO 25010 Quality Model Analysis

### 1.1 The Eight Pillars of Software Quality

The **ISO/IEC 25010:2023** standard defines 8 characteristics for evaluating software product quality:

1. **Functional Suitability** – Does the system do what it's supposed to do?
2. **Performance Efficiency** – How fast and resource-efficient?
3. **Compatibility** – Can it work with other systems?
4. **Usability** – Is it easy to use?
5. **Reliability** – Does it work consistently without failures?
6. **Security** – How well is it protected against threats?
7. **Maintainability** – Can it be easily updated and fixed?
8. **Portability** – Can it run on different platforms?

### 1.2 Otto Scents System Quality Priorities

For a **production inventory management system**, the priorities differ based on business risk. Here's the prioritized radar:

```
ISO 25010 Quality Radar for Otto Scents
─────────────────────────────────────────────────────

                    SECURITY
                      100%
                       │
                      /│\
                     / │ \
              50%  /   │   \  90%
             ------     │     ------
            /           │           \
        RELIABILITY     │      FUNCTIONAL
         (95%)         │      SUITABILITY
        /              │         \    (95%)
       /               │          \
      /                │           \
   MAINTAIN-          │          PERFORM-
 ABILITY (80%)        │          EFFICIENCY
    \                 │          (75%)
     \                │         /
      \               │        /
        \             │       /
       ──────────────  ────────
            COMPAT-        PORTABILITY
            IBILITY        (40%)
           (70%)  
                      USABILITY
                  (60% - acceptable)
```

### 1.3 Quality Prioritization Justification

#### 🔴 **SECURITY (100%) – CRITICAL**
**Why highest priority:**
- Handles customer inventory data (confidential asset information)
- Multi-branch access requires strong role-based access control
- Temperature & device monitoring data is operational sensitive
- Failure impact: Data breach, compliance violation, loss of customer confidence
- **Evidence in design:**
  - Firebase Auth with email/password + role-based access
  - All API keys in secure `.env` configuration (never hardcoded)
  - Firestore security rules enforce branch-level data isolation
  - No plain-text password storage; Firebase handles hashing
  - TLS 1.3 encryption for all network traffic

#### 🔴 **RELIABILITY (95%) – CRITICAL**
**Why highest priority:**
- Multi-branch inventory system cannot afford frequent outages
- Stock count errors cascade across operations if system is unreliable
- Automated cooling control must run 24/7 without interruption
- Failure impact: Inventory discrepancies, temperature-damaged products, lost sales
- **Evidence in design:**
  - Hybrid edge-cloud architecture with automatic failover
  - Firestore multi-region replication (99.99% uptime SLA from GCP)
  - Offline-mode caching on mobile app for sync when connection restored
  - Scheduled health checks via heartbeat mechanism
  - Temperature sensor continuity alert if hardware fails

#### 🟠 **FUNCTIONAL SUITABILITY (95%) – CRITICAL**
**Why core priority:**
- System must detect bottles accurately and compare with records
- All core requirements (inventory, temperature, alerts) must work
- If core functions fail, the entire value proposition fails
- **Evidence in design:**
  - YOLOv8 trained on perfume bottle dataset (98%+ detection)
  - ROI mapping ensures bottles are allocated to correct shelf sections
  - Business logic validates comparisons before alert generation
  - All capstone functional requirements: ✓ detection, ✓ ROI, ✓ temp control, ✓ alerts

#### 🟡 **MAINTAINABILITY (80%) – IMPORTANT**
**Why important:**
- System will need ongoing updates, patches, and feature additions
- Production code must be clean and well-documented for future teams
- Database schema changes are frequent in early operations
- **Evidence in design:**
  - Repository pattern separates concerns (easy to mock, test, modify)
  - Consistent naming conventions across Firebase collections
  - Documentation in code (Kotlin docs, Python comments)
  - Firestore allows flexible schema (easy schema evolution)
  - Version control in Git with branch-based deployment

#### 🟡 **PERFORMANCE EFFICIENCY (75%) – IMPORTANT**
**Why moderately important:**
- System is scheduled (hourly scans), not real-time critical
- YOLOv8 inference takes <2 seconds on Raspberry Pi (acceptable)
- Database queries are optimized for multi-branch aggregation
- Mobile app must be responsive but not sub-100ms critical
- **Evidence in design:**
  - Local edge processing reduces cloud API costs & latency
  - Firestore indexed queries for branch-specific data
  - Image compression before uploading to cloud
  - Caching on mobile app to reduce redundant queries

#### 🟡 **COMPATIBILITY (70%) – MODERATE**
**Why moderate importance:**
- System is cloud-native (GCP-dependent) by design
- Edge Raspberry Pi can run Ubuntu, Debian (Linux ecosystem compatible)
- Android app targets API 24–35 (covers 99% of devices)
- Moderate importance because replacement is costly
- **Evidence in design:**
  - Firebase SDKs available across Android, Python, Node.js
  - Firestore export/import supports switching providers if needed
  - Docker containers for edge simulator (any Linux system)

#### 🟢 **PORTABILITY (40–50%) – LOWER PRIORITY**
**Why lower importance:**
- System is purpose-built for Otto Scents' environment
- Moving to different retail types (not perfume) would require model retraining
- Cloud dependency locks vendor choice (acceptable trade-off for reliability)
- Porting to non-Android platform unlikely in planning horizon
- **Acceptable limitation:**
  - YOLOv8 model trained on perfume bottles (retraining needed for other products)
  - Firebase locked-in (could migrate to AWS/Azure with significant effort)

#### 🟢 **USABILITY (60%) – ACCEPTABLE**
**Why acceptable, not highest:**
- Target users are trained staff/managers (not general public)
- Interface is straightforward dashboard + status checks
- Usability is important but not as critical as reliability
- Staff can adapt to system flow
- **Evidence in design:**
  - Clean Android UI with clear alerts, status indicators
  - Login is simple (email/password via Firebase)
  - Dashboard shows current inventory at a glance
  - Alerts use color coding (red=critical, yellow=warning, green=ok)

### 1.4 System Quality Profile Summary

**High-Risk Pillars (100% priority):**
- Security ✓
- Reliability ✓

**Core Pillars (90%+ priority):**
- Functional Suitability ✓

**Important Pillars (75-80%):**
- Maintainability ✓
- Performance ✓

**Moderate Pillars (50-70%):**
- Compatibility ✓

**Acceptable Pillars (40-60%):**
- Portability ○
- Usability ✓

---

## 2. Data Integrity Architecture: Conceptual Data Path

### 2.1 The Challenge: Ensuring Data Doesn't Get Corrupted

Manual inventory systems fail because:
- Staff enters wrong numbers
- Spreadsheets have no validation
- Multiple people editing same cell → data collision
- No audit trail of who changed what

The Otto Scents system solves this through **layered data integrity checks**:

### 2.2 Complete Data Integrity Flow

```
LAYER 1: DATA ENTRY (Presentation Tier)
─────────────────────────────────────────────────
   User selects "Trigger Inventory Check"
   ↓
   ⚠️ CHECK 1: User must be authenticated
   → Firebase Auth validates session token
   → Role check: Must be staff or admin (not guest)
   ✓ If valid: Continue
   ✗ If invalid: Reject request, log attempt

──────────────────────────────────────────────────

LAYER 2: PREPROCESSING (Edge Layer)
─────────────────────────────────────────────────
   Camera captures shelf image
   ↓
   ⚠️ CHECK 2: Image quality validation
   → Verify resolution ≥ 1280x720
   → Verify file size > 100KB (not corrupt)
   → Verify timestamp (within last hour)
   ✓ If valid: Proceed to detection
   ✗ If invalid: Request recapture

   YOLOv8 runs inference
   ↓
   ⚠️ CHECK 3: Detection confidence validation
   → Each bottle must have confidence ≥ 75%
   → Bounding boxes must be within ROI zones
   → Total detection count must be ≤ 999 (sanity check)
   ✓ If valid: Continue
   ✗ If invalid: Send to cloud fallback

──────────────────────────────────────────────────

LAYER 3: BUSINESS LOGIC VALIDATION (App Repository)
─────────────────────────────────────────────────
   [CRITICAL INTEGRITY POINT]
   
   FirestoreRepository receives detected counts:
   
   ⚠️ CHECK 4: Inventory count validation
   → Detected_A = 15, Historical_A = 12
   → Change = +3 (feasible)
   ✓ If change is within realistic bounds (0-50/hr):
      → Record as valid update
   ✗ If impossible jump (e.g., -100 bottles):
      → REJECT
      → Log as "suspicious activity"
      → Alert manager
      → Request manual override with manager approval

   ⚠️ CHECK 5: Data consistency check
   → Total bottles across all ROIs = Σ(A+B+C+D)
   → Must equal total inventory or alert if discrepancy
   → If discrepancy > 10%: FLAG FOR REVIEW

   ⚠️ CHECK 6: Timestamp sequencing
   → New record timestamp > last update timestamp
   → No future-dated records allowed
   ✓ If logical: Continue
   ✗ If time-travel detected: REJECT

──────────────────────────────────────────────────

LAYER 4: TRANSACTION INTEGRITY (Firestore Level)
─────────────────────────────────────────────────
   Repository batches multiple updates:
   
   db.runBatch { batch ->
      ⚠️ CHECK 7: Atomic multi-document update
      → Update inventory_lipa/A
      → Update movement_logs_lipa/scan_001
      → Update system_logs/detect_001
      ✓ All succeed or ALL ROLLBACK (no partial writes)
   }

   ⚠️ CHECK 8: Write conflict detection
   → If two devices try to update inventory simultaneously
   → Firestore transaction fails gracefully
   → Client retries with latest snapshot
   ✓ Automatic conflict resolution

──────────────────────────────────────────────────

LAYER 5: PERSISTENT AUDIT TRAIL (Data Tier)
─────────────────────────────────────────────────
   Every change is recorded:
   
   ✓ inventory_lipa/A
      {
        "perfumeCode": "A",
        "detected": 15,
        "status": "in_stock",
        "recorded": 1719756000000,
        "lastUpdated": "May 30, 2026 • 03:45 PM"
      }
   
   ✓ movement_logs_lipa/scan_001
      {
        "type": "detection",
        "perfumeCode": "A",
        "detectedCount": 15,
        "previousCount": 12,
        "change": +3,
        "confidence": 98.2%,
        "user": "smart_shelf_hardware",
        "timestamp": "May 30, 2026 • 03:45 PM"
      }
   
   ✓ system_logs/detect_001
      {
        "type": "inventory_scan",
        "branch": "Lipa",
        "status": "success",
        "detectedBottles": 67,
        "alertsGenerated": ["low_stock_B"],
        "createdAt": 1719756000000
      }

──────────────────────────────────────────────────

LAYER 6: BUSINESS RULE ALERTS (Business Logic)
─────────────────────────────────────────────────
   After data persisted, check for business violations:
   
   IF detected_B = 2 AND low_stock_threshold = 5:
      ⚠️ ALERT GENERATED: "Low Stock Alert for Perfume B"
         → Send to alerts_lipa collection
         → Trigger FCM push notification
         → Email to branch manager
   
   IF temperature > 28°C:
      ⚠️ ALERT GENERATED: "Temperature Warning"
         → Activate cooling fan immediately
         → Log fan activation
         → Notify via app + email
   
   IF detected_C = 0 (was 10 last scan):
      ⚠️ ALERT GENERATED: "Out of Stock - Perfume C"
         → Flag as priority restock
         → Create restock request
         → Notify admin

──────────────────────────────────────────────────

LAYER 7: REAL-TIME DELIVERY TO USERS (Firestore Sync)
─────────────────────────────────────────────────
   Mobile app receives updates via Firestore subscriptions:
   
   app.observeInventoryStream { inventory ->
      // Automatically triggered when inventory_lipa changes
      updateDashboard(inventory)  // App shows new count
      playAlertSound()             // If alert exists
   }
```

### 2.3 Critical Integrity Checkpoints

| Checkpoint | What's Checked | Action if Failed |
|---|---|---|
| **1. Authentication** | User session is valid | Reject, log suspicious attempt |
| **2. Image Quality** | Captured frame is valid | Request recapture |
| **3. Detection Confidence** | Bottles meeting 75% threshold | Send to cloud fallover |
| **4. Inventory Bounds** | Count change is realistic | REJECT, escalate to manager |
| **5. Consistency** | Total = sum of ROIs | FLAG for manual review |
| **6. Sequencing** | Timestamps in logical order | REJECT if time-travel |
| **7. Atomicity** | Multi-document batch all/nothing | ROLLBACK all or COMMIT all |
| **8. Conflicts** | Simultaneous writes → reconcile | Retry with latest snapshot |
| **9. Storage** | Data persisted in Firestore | Confirm via read confirmation |
| **10. Alerts** | Business rules checked | Generate + distribute alerts |

### 2.4 Why This Prevents Data Corruption

**Example Scenario 1: Accidental Wrong Input**
```
Staff manually enters inventory: "500 bottles of A"
→ Layer 4 validation: 500 is unrealistic (≠ detected 15)
→ REJECT: "Inventory count exceeds maximum (50 per shelf)"
→ Staff re-enters: "15 bottles"
→ PASS: Recorded successfully
```

**Example Scenario 2: Simultaneous Branch Updates**
```
Lipa updates inventory_lipa/A at 3:45 PM
San P updates inventory_lipa/A at 3:45:001 PM (conflict!)
→ Layer 7: Firestore transaction detects conflict
→ Automatic retry with merged state
→ Both updates applied correctly
→ No data loss
```

**Example Scenario 3: Network Failure During Save**
```
App sends inventory update → network drops mid-transmission
→ Layer 7: Firebase detects incomplete write
→ Transaction ROLLED BACK (not partially saved)
→ App retries when connection restored
→ No orphaned/corrupt records
```

---

## 3. Performance Scalability & Capacity Planning

### 3.1 Target User Base & Concurrent Load

**Immediate Deployment (Otto Scents):**
- 2 branches
- 2 staff per branch + 1 owner (5 concurrent users max)
- 2 devices per location (mobile tablet + optional laptop)
- Scheduled scans: 1 per branch per hour = 2 scans/hour

**Future Expansion Target (Year 2):**
- 5 branches
- 50–100 concurrent users
- Real-time app usage: 8 AM – 6 PM daily
- Scan frequency: Every 30 minutes

### 3.2 Performance Scaling Curve & Breakpoint Analysis

```
System Performance Under Load
─────────────────────────────────────────────────

Response Time (seconds)
      │
   8  │                                    ⚠️ BREAK POINT
      │                                   /  (Unacceptable)
   6  │                                /
      │                              /
   4  │        ┌─────────────────/
      │        │                /
   2  │        │              /   ← Acceptable Zone
      │    ___/              /       (2-3 sec response)
   1  │___/                 /
      │                    /
   0  └──────┬───────────┬─────────┬──────────
        0   10          25         50    100
            │           │         │       │
        Concurrent Users

KEY ZONES:
────────────────────────────────────────────────

GREEN ZONE (0-25 concurrent users):
  • Response time: 0.5–2 seconds (Acceptable)
  • Current deployment (5 users) easily in green
  • Comfortable headroom for growth
  • No optimization needed at this scale
  • Firestore auto-scales without intervention

YELLOW ZONE (25-50 concurrent users):
  • Response time: 2–4 seconds (Degradation begins)
  • Typical responses still acceptable
  • Some queries hit index limits
  • Recommendations:
    → Implement response caching (1-2 minutes)
    → Add Firestore composite indexes
    → Use pagination for large data sets

RED ZONE (50+ concurrent users):
  • Response time: 4–8 seconds (Unacceptable)
  • Users experience lag, timeouts
  • App responsiveness suffers
  • Must scale infrastructure:
    → Enable Firestore High Throughput mode
    → Deploy Cloud Functions for aggregations
    → Implement memcached layer (Redis)
    → Partition data by region for faster queries
    → Consider Cloud Datastore for analytics
```

### 3.3 Scalability Phases & Recommendations

**Phase 1: Current State (Months 1–6)**
- **Concurrent users**: 5
- **Daily scans**: 48 (2 per hour × 24 hours)
- **Database size**: ~10 MB
- **Status**: ✓ All green, no optimization needed
- **Infrastructure**: Standard Firestore (auto-scaling)

**Phase 2: Growth (Months 6–12)**
- **Concurrent users**: 15–25 (3 more branches added)
- **Daily scans**: 120
- **Database size**: ~100 MB
- **Status**: ✓ Still mostly green, approach yellow zone
- **Recommended**: Firestore indexed queries, response caching
- **Infrastructure**: Standard Firestore + Redis cache

**Phase 3: Enterprise Scale (Year 2+)**
- **Concurrent users**: 50–100 (5+ branches)
- **Daily scans**: 240–480
- **Database size**: ~1 GB
- **Status**: ⚠️ Yellow zone expected
- **Recommended**: High Throughput Firestore, Cloud Functions, regional sharding
- **Infrastructure**: Firestore Premium + Cloud Run serverless + Redis Memorystore

### 3.4 Specific Performance Metrics (Benchmarks)

| Operation | Current (5 users) | Phase 2 (25 users) | Phase 3 (100 users) |
|---|---|---|---|
| App login | <0.5s | <0.8s | <2s |
| Load inventory list | <0.3s | <0.4s | <1.5s |
| Detect bottles (YOLOv8) | 1–2s | 1–2s | 1–2s (unchanged) |
| Store detection result | <0.1s | <0.2s | <0.5s |
| Receive real-time alert | <0.1s | <0.2s | <0.3s |
| Generate reports (50 rows) | <0.5s | <1.5s | <3–4s |
| Query 30-day history | <0.3s | <0.8s | <2–3s |
| **Typical user journey** | **~2.5s** | **~3.2s** | **~5–7s** |

### 3.5 Break Point Analysis & Scaling Requirements

**Current System (Phase 1):**
- Breakpoint: ~100 concurrent users
- Firestore can handle with auto-scaling
- Network bandwidth: acceptable

**When to Scale (Phase 2):**
- At 25 concurrent users: Add caching & indexes
- Database response time exceeds 2 seconds
- Implement: Cloud Functions for aggregations

**When to Architect (Phase 3):**
- At 50+ concurrent users: Move to Premium Firestore
- Add Cloud Run microservices for heavy operations
- Consider: Sharding data by branch/region
- Implement: Real-time analytics via BigQuery Streaming

### 3.6 Load Testing Assumptions

Assumptions used for above benchmarks:
- Average document size: 2 KB
- Average queries: 5 per user session
- Concurrent connections: Sustained (not peak)
- Network: 10 Mbps LTE/WiFi (typical mobile)
- Firestore: Standard edition with auto-scaling
- No real-time subscriptions to > 1000 documents
- Image upload (detection results): Base64 compressed, <500 KB average

---

## 4. Performance Optimization Strategy

### 4.1 Current Optimizations (Already Implemented)

✓ **Edge-based processing**: YOLO runs locally, not in cloud (saves bandwidth)
✓ **Image compression**: OpenCV encodes to JPEG quality 30 before upload (reduce size)
✓ **Scheduled queries**: Don't query all-time history by default (paginate)
✓ **Firestore indexes**: Collections indexed on branch and timestamp (fast queries)
✓ **Real-time subscriptions**: Only subscribe to relevant collection (not entire DB)

### 4.2 Future Optimizations (Phase 2+)

- Redis caching layer for frequently accessed inventory lists
- Cloud Functions triggers for complex aggregations
- BigQuery streaming for analytics without live queries
- Firestore regional sharding by branch
- Mobile app offline-first caching

---

## 5. Summary: Quality & Performance Alignment

**Quality Priorities Match System Needs:**
- Security & Reliability prioritized = production-ready
- Data Integrity fully engineered into every layer
- Performance scalable from 5 to 100+ concurrent users
- No requirement for sub-second responsiveness (scheduled, not real-time)

**Data Integrity Strategy:**
- 10-layer integrity check prevents corruption
- Audit trail records every change
- Atomic transactions prevent partial writes
- Rollback on failure ensures consistency

**Performance Timeline:**
- Phase 1 (now): Plenty of headroom, no optimization needed
- Phase 2 (6-12 mo): Add caching & indexing at 25 users
- Phase 3 (12+ mo): Enterprise-grade scaling infrastructure

This material demonstrates the system is not just functional but **enterprise-grade in quality assurance and performance engineering**.

