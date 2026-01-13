# object_config.py

# Map YOLO labels to project-specific object classes
YOLO_TO_PROJECT_MAP = {
    "bottle": "plastic_bottle",
    "vase": "plastic_bottle",
    "cup": "plastic_bottle",

    "can": "soda_can",

    "wine glass": "glass_bottle",
    "glass": "glass_bottle",

    "box": "cardboard_box",

    "tin": "tin_container"
}

OBJECT_MATERIAL_MAP = {
    "plastic_bottle": "plastic",
    "soda_can": "metal",
    "glass_bottle": "glass",
    "cardboard_box": "cardboard",
    "tin_container": "metal"
}
