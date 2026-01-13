def infer_material(object_name: str) -> str:
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
