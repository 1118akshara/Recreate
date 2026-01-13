from object_detection import detect_object
from material_inference import infer_material
from dimension_estimation import estimate_dimensions
from reuse_engine import suggest_single_object_reuse


def analyze_single_object(image_paths):
    """
    Analyze one object given multiple images (different views)
    """

    # 1️⃣ Detect object type
    detected_object = detect_object(image_paths)
    if not detected_object:
        return None

    # ✅ FIX: define object_name
    object_name = detected_object

    # 2️⃣ Infer material
    material = infer_material(object_name)

    # 3️⃣ Estimate dimensions (temporary approximation)
    dimensions = estimate_dimensions(bbox_height_px=250)

    # ✅ SAFE height access
    height_cm = dimensions.get("height_cm", 0)

    # 4️⃣ Return full analysis including reuse ideas
    return {
        "object": object_name,
        "material": material,
        "dimensions": dimensions,
        "reuse_ideas": suggest_single_object_reuse(
            object_name,
            material,
            height_cm
        )
    }
