# Deployment - Infrastructure & Operations

**Infrastructure as Code, deployment scripts, and operational procedures**

This directory contains everything needed to deploy Otto Scents to production environments.

## Directory Structure

```
deployment/
├── docker/                      # Container definitions
│   ├── Dockerfile              # Edge simulator container
│   └── docker-compose.yml       # Multi-container orchestration
│
├── gcp/                         # Google Cloud Platform
│   ├── firestore_config.json   # Firestore configuration
│   ├── cloud_functions_deploy.sh
│   └── README.md               # GCP setup guide
│
└── scripts/                     # Deployment automation
    ├── deploy_edge.sh          # Deploy to Raspberry Pi
    ├── setup_firebase.sh       # Initialize Firebase
    └── health_check.sh         # System health verification
```

## Quick Deployment Guide

### For Raspberry Pi (Edge Device)

```bash
# 1. SSH into Raspberry Pi
ssh pi@192.168.1.100

# 2. Clone repository
git clone https://github.com/yourorg/otto-scents.git
cd otto-scents

# 3. Run deployment script
chmod +x deployment/scripts/deploy_edge.sh
./deployment/scripts/deploy_edge.sh
```

### For Google Cloud Platform

```bash
# 1. Authenticate with GCP
gcloud auth login
gcloud config set project otto-scents-xxxx

# 2. Deploy Firestore
chmod +x deployment/gcp/setup_firestore.sh
./deployment/gcp/setup_firestore.sh

# 3. Deploy Cloud Functions
chmod +x deployment/gcp/cloud_functions_deploy.sh
./deployment/gcp/cloud_functions_deploy.sh
```

### For Android App

```bash
# 1. Open in Android Studio
android-studio app/

# 2. Connect device or start emulator
adb devices

# 3. Build and run
./gradlew installDebug
```

## Docker Deployment

### Build Container
```bash
cd deployment/docker
docker build -t otto-scents-edge:latest .
```

### Run Locally
```bash
docker run --device /dev/video0 \
           -e GOOGLE_APPLICATION_CREDENTIALS=/credentials.json \
           -v /credentials.json:/credentials.json \
           otto-scents-edge:latest
```

### Deploy to Raspberry Pi
```bash
# Push to registry
docker tag otto-scents-edge:latest ghcr.io/yourorg/otto-scents:latest
docker push ghcr.io/yourorg/otto-scents:latest

# On Raspberry Pi:
docker pull ghcr.io/yourorg/otto-scents:latest
docker run --device /dev/video0 \
           -e GOOGLE_APPLICATION_CREDENTIALS=/credentials.json \
           ghcr.io/yourorg/otto-scents:latest
```

## GCP Setup

### Prerequisites
- Google Cloud project created
- Firestore database enabled
- Firebase Authentication configured
- Service account with appropriate permissions

### Initialize Firestore
```bash
# Set up collections and indexes
gcloud firestore databases create \
    --region=us-central1 \
    --type=firestore-native

# Load initial security rules
gcloud firestore databases configure-rules \
    deployment/gcp/firestore_rules.json
```

### Deploy Cloud Functions
```bash
gcloud functions deploy detectionFailover \
    --runtime python311 \
    --trigger-topic detection-fallback \
    --source backend/cloud_functions
```

## Production Deployment Checklist

Before deploying to production:

### Security
- [ ] `.env` file contains all credentials (never committed)
- [ ] Firebase service account key secured
- [ ] Firestore security rules configured correctly
- [ ] API keys rotated
- [ ] SSL/TLS certificates valid
- [ ] Database backups enabled

### Configuration
- [ ] Environment variables set correctly
- [ ] Database collections created
- [ ] Indexes configured for optimal queries
- [ ] Alert thresholds tuned
- [ ] Log levels appropriate (not too verbose)

### Testing
- [ ] Unit tests passing (90%+ coverage)
- [ ] Integration tests passing
- [ ] Load testing completed
- [ ] Failover tested
- [ ] Recovery procedures verified

### Monitoring
- [ ] Logging configured (Stackdriver/Cloud Logging)
- [ ] Alerts set up for critical events
- [ ] Health check endpoint working
- [ ] Dashboard created for key metrics

### Rollback Plan
- [ ] Previous version available
- [ ] Rollback procedure documented
- [ ] Database backups current
- [ ] Team trained on rollback

## Deployment Scripts

### `deploy_edge.sh`
Deploys the edge simulator to Raspberry Pi:
1. Install Python dependencies
2. Configure systemd service for auto-start
3. Set up GPIO permissions
4. Configure Firestore credentials
5. Verify camera and sensors
6. Start monitoring service

### `setup_firebase.sh`
Initializes Firebase services:
1. Create Firestore collections
2. Deploy security rules
3. Set up authentication
4. Configure Cloud Messaging
5. Create service accounts

### `health_check.sh`
Verifies system health:
1. Check Firestore connectivity
2. Verify camera access
3. Test temperature sensor
4. Confirm cooling fan
5. Check internet connectivity
6. Alert if issues found

## Environment Configuration

Create `.env` file (never commit):
```bash
# Firebase
FIREBASE_API_KEY=AIzaSyDxxx...
FIREBASE_PROJECT_ID=otto-scents-xxxx
GOOGLE_APPLICATION_CREDENTIALS=/path/to/serviceAccountKey.json

# Sensors & Hardware
CAMERA_DEVICE=/dev/video0
TEMP_SENSOR_GPIO=17
FAN_RELAY_GPIO=27

# System Parameters
LOW_STOCK_THRESHOLD=5
TEMP_THRESHOLD=25.0
CAPTURE_INTERVAL=3600
HEARTBEAT_INTERVAL=30

# Monitoring
LOG_LEVEL=INFO
LOG_FILE=/var/log/otto-scents/simulator.log
```

## Monitoring & Healthchecks

### Real-Time Monitoring
```bash
# SSH into edge device
ssh pi@192.168.1.100

# View logs
sudo journalctl -u otto-scents -f

# Check system metrics
ps aux | grep shelf_simulator
free -h
df -h
```

### Firestore Monitoring
```bash
# View detection logs
gcloud firestore query system_logs \
    --order-by=createdAt \
    --direction=descending \
    --limit=50
```

### Health Check API
```bash
# Check edge device health
curl http://192.168.1.100:8080/health

# Response:
# {
#   "status": "healthy",
#   "camera": "ok",
#   "firestore": "connected",
#   "temp_sensor": "ok",
#   "last_detection": "2 minutes ago",
#   "uptime": "14 days 2 hours"
# }
```

## Troubleshooting Deployments

### Firestore Connection Failed
```
Error: PERMISSION_DENIED
→ Verify service account has Firestore editor role
→ Check security rules allow writes from Pi IP
→ Confirm credentials file accessible
```

### Camera Not Found
```
Error: Cannot access /dev/video0
→ Ensure camera connected
→ Check ownership: sudo chown pi:pi /dev/video0
→ Test with: raspistill -o test.jpg
```

### Out of Disk Space
```
Error: No space left on device
→ Check storage: df -h
→ Clean Docker images: docker system prune
→ Archive old logs to storage
```

### High Memory Usage
```
Error: OOM Killer triggered
→ Check memory: free -h
→ Reduce model size (use YOLOv8n instead)
→ Restart service: sudo systemctl restart otto-scents
```

## Scaling for Multiple Branches

### Add New Branch
```bash
# 1. Create new collections in Firestore
firestore_create_branch.py --branch "New Branch"

# 2. Deploy edge simulator to new location
# Configure with correct branch ID:
BRANCH_NAME="new_branch"
./deploy_edge.sh --branch $BRANCH_NAME

# 3. Add to monitoring dashboard
# Update dashboard to include new branch metrics
```

### Database Scaling
- Firestore auto-scales automatically
- Partition data by branch (already configured)
- Add composite indexes for multi-branch queries
- Consider BigQuery for analytics on large history

## References

- **Ubuntu Setup**: See `docs/deployment/ubuntu_setup_guide.md`
- **GCP Guide**: See `deployment/gcp/README.md`
- **Docker Hub**: See https://hub.docker.com/r/yourorg/otto-scents
- **GCP Dashboard**: https://console.cloud.google.com/

## Support

For deployment issues:
1. Check this README
2. Review `docs/guides/troubleshooting.md`
3. Check deployment logs
4. Run health check script
5. Contact DevOps team

---

**Last Updated:** May 2026  
**Deployment Status:** Production-Ready

