from ultralytics import YOLO
from collections import Counter

# Load YOLO model
model = YOLO("yolov8n.pt")

# Background / irrelevant classes
IGNORED_CLASSES = {
    "person",
    "chair",
    "dining table",
    "table",
    "couch",
    "bed",
    "tv",
    "laptop"
}

# Normalize confusing YOLO classes
CLASS_NORMALIZATION = {
    "cup": "can",
    "wine glass": "glass",
    "bottle": "bottle",
    "cell phone": "cell phone"
}

CONFIDENCE_THRESHOLD = 0.45

def detect_object(image_paths):
    """
    Detect object using multi-view voting with confidence filtering
    and background suppression.
    """

    detected_labels = []

    for image_path in image_paths:
        try:
            results = model(image_path)

            for r in results:
                if r.boxes is None:
                    continue

                for box in r.boxes:
                    cls_id = int(box.cls[0])
                    conf = float(box.conf[0])
                    label = model.names.get(cls_id)

                    # Skip weak detections
                    if conf < CONFIDENCE_THRESHOLD:
                        continue

                    # Skip background clutter
                    if not label or label in IGNORED_CLASSES:
                        continue

                    # Normalize label
                    label = CLASS_NORMALIZATION.get(label, label)

                    detected_labels.append(label)

        except Exception as e:
            print(f"[WARN] Detection failed on {image_path}: {e}")

    if not detected_labels:
        return None

    # Majority voting across views
    final_label = Counter(detected_labels).most_common(1)[0][0]
    return final_label
