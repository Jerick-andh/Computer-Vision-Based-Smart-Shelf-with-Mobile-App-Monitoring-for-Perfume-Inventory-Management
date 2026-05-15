# Use Python Slim for a smaller image
FROM python:3.10-slim

# Install system dependencies for OpenCV and YOLO
RUN apt-get update && apt-get install -y \
    libgl1-mesa-glx \
    libglib2.0-0 \
    && rm -rf /var/lib/apt/lists/*

# Set working directory
WORKDIR /app

# Copy requirements and install
# Note: Using ultralytics-cpu for cloud run (saves space and usually no GPU available)
RUN pip install flask gunicorn ultralytics firebase-admin opencv-python-headless

# Copy project files
COPY cloud_run_failover.py main.py
COPY best.pt best.pt

# Expose port
EXPOSE 8080

# Run with Gunicorn for production
CMD ["gunicorn", "--bind", ":8080", "--workers", "1", "--threads", "8", "--timeout", "0", "main:app"]
