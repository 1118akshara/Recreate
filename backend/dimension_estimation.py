def estimate_dimensions(bbox_height_px, ref_px=300, ref_height_cm=20):
    scale = ref_height_cm / ref_px
    height_cm = bbox_height_px * scale

    return {
        "height_cm": round(height_cm, 2)
    }
