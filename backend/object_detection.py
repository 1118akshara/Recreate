from ultralytics import YOLO
from collections import Counter
import os

MODEL_PATH = os.path.join(os.path.dirname(__file__), "yolov8l.pt")
model = YOLO(MODEL_PATH)

CONFIDENCE_THRESHOLD = 0.60

ALLOWED_OBJECTS = [
    "bottle", "cup", "bowl", "wine glass",
    "chair", "couch", "bed", "dining table",
    "tv", "laptop", "mouse", "remote", "keyboard",
    "cell phone", "microwave", "oven", "toaster",
    "refrigerator", "hair drier",
    "book", "clock", "vase", "scissors",
    "backpack", "suitcase", "umbrella", "handbag",
    "fork", "knife", "spoon"
]


def detect_object(image_paths):

    labels = []

    for image_path in image_paths:
        results = model(image_path, verbose=False)

        for r in results:
            if not r.boxes:
                continue

            for box in r.boxes:

                conf = float(box.conf[0])
                cls_id = int(box.cls[0])
                label = model.names.get(cls_id)

                if (
                    conf >= CONFIDENCE_THRESHOLD
                    and label in ALLOWED_OBJECTS
                ):
                    labels.append(label)

    if not labels:
        return "unknown"

    return Counter(labels).most_common(1)[0][0]
