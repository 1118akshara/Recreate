from backend.object_detection import detect_object
from backend.efficientnet_material import predict_material
from backend.dimension_estimation import estimate_dimensions

def analyze_single_object(image_paths):
    # Object detection
    object_name = detect_object(image_paths)
    if not object_name:
        return None

    # Material detection
    material_result = predict_material(image_paths)

    # Dimensions (placeholder / AR later)
    dimensions = estimate_dimensions(bbox_height_px=250)


    return {
        "object": object_name,
        "material": material_result["material"],
        "confidence": material_result["confidence"],
        "dimensions": dimensions,
        
    }
