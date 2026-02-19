# plenoxels/material_inference.py
import cv2
import numpy as np

def detect_object_name(image):
    """
    Placeholder: Use YOLO or another model to detect object name from image.
    For now, returns a mock object name.
    """
    # TODO: Replace with actual YOLO detection
    # Example: 
    # from ultralytics import YOLO
    # model = YOLO("yolov8n.pt")
    # results = model(image)
    # return results[0].names[0]  # top detected class
    
    # Mock detection for testing
    mock_objects = ["bottle", "cell phone", "can", "glass", "cardboard", "box"]
    return np.random.choice(mock_objects)

def infer_material(images):
    """
    Infer material from list of images (multiple views).
    Uses object detection + your material mapping logic.
    """
    # Use the first image for detection (or combine results from all)
    if images:
        object_name = detect_object_name(images[0])
    else:
        return "unknown"
    
    object_name = object_name.lower()

    if "bottle" in object_name:
        return "plastic"
    
    if "cell phone" in object_name:
        return "plastic + metal"
    
    if "can" in object_name:
        return "metal"
    
    if "glass" in object_name:
        return "glass"
    
    if "cardboard" in object_name or "box" in object_name:
        return "cardboard"
    
    return "unknown"

# Optional: If you already have YOLO set up, replace detect_object_name with:
# from ultralytics import YOLO
# model = YOLO("yolov8n.pt")
# def detect_object_name(image):
#     results = model(image)
#     return results[0].names[results[0].probs.top1]