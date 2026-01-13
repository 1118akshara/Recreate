# ===============================
# Reuse Engine
# ===============================

def suggest_single_object_reuse(object_name: str, material: str, height_cm: float):
    """
    Suggest reuse ideas for a single detected object
    """

    object_name = object_name.lower()
    material = material.lower()

    ideas = []

    # -------------------------------
    # Object-specific rules (priority)
    # -------------------------------

    if "bottle" in object_name:
        ideas.extend([
            "Pen Stand",
            "Desk Organizer",
            "Bird Feeder",
            "Watering Can",
            "Cable Holder",
            "Indoor Plant Pot"
        ])

        if height_cm > 20:
            ideas.extend([
                "Water Storage Bottle",
                "Vertical Garden Planter",
                "Self-Watering Planter"
            ])

    elif "cell phone" in object_name or "phone" in object_name:
        ideas.extend([
            "Desk Clock using old phone",
            "Security Camera (IP cam reuse)",
            "Music Player for Kids",
            "Digital Photo Frame",
            "Smart Home Display (weather, clock)",
            "Emergency Backup Phone"
        ])

    elif "can" in object_name:
        ideas.extend([
            "Candle Holder",
            "Mini Planter",
            "Desk Organizer",
            "Cutlery Holder",
            "Paint Brush Holder"
        ])

    elif "glass" in object_name:
        ideas.extend([
            "Flower Vase",
            "Lamp Shade",
            "Terrarium Container",
            "Decorative Storage Jar"
        ])

    elif "box" in object_name or "cardboard" in object_name:
        ideas.extend([
            "Storage Box",
            "Drawer Organizer",
            "Stationery Holder",
            "File Organizer"
        ])

    # -------------------------------
    # Material-based fallback rules
    # -------------------------------

    if not ideas:
        if material == "plastic":
            ideas.extend([
                "Pen Stand",
                "Desk Organizer",
                "Cable Management Box"
            ])

        elif material == "metal":
            ideas.extend([
                "Tool Holder",
                "Lantern",
                "Key Holder",
                "Desk Paperweight"
            ])

        elif material == "glass":
            ideas.extend([
                "Flower Vase",
                "Decorative Container"
            ])

        elif material == "cardboard":
            ideas.extend([
                "Storage Box",
                "Drawer Organizer"
            ])

    if not ideas:
        ideas.append("No reuse ideas available")

    return ideas


def suggest_combined_reuse(objects):
    """
    Suggest reuse ideas when multiple objects are scanned together
    """

    if len(objects) < 2:
        return []

    object_names = [obj["object"].lower() for obj in objects]
    materials = [obj["material"].lower() for obj in objects]

    has_bottle = any("bottle" in name for name in object_names)
    has_phone = any("phone" in name for name in object_names)
    has_can = any("can" in name for name in object_names)
    has_box = any("box" in name for name in object_names)

    has_plastic = any("plastic" in mat for mat in materials)
    has_metal = any("metal" in mat for mat in materials)
    has_glass = any("glass" in mat for mat in materials)

    combined_ideas = []

    # -------------------------------
    # Object-based combinations
    # -------------------------------

    if has_bottle and has_phone:
        combined_ideas.extend([
            "Smart Plant Pot with Phone-based Watering Reminder",
            "Desk Organizer with Digital Timer",
            "Study Pomodoro Timer Stand"
        ])

    if has_bottle and has_can:
        combined_ideas.extend([
            "Modular Desk Organizer",
            "Vertical Mini Garden using Bottle and Can"
        ])

    if has_can and has_phone:
        combined_ideas.extend([
            "Phone Speaker Amplifier using Can",
            "DIY Desk Clock Stand"
        ])

    if has_box and has_phone:
        combined_ideas.extend([
            "Charging Station Organizer",
            "DIY Phone Storage Dock"
        ])

    # -------------------------------
    # Material-based combinations
    # -------------------------------

    if has_plastic and has_metal:
        combined_ideas.extend([
            "Mixed-Material Storage Organizer",
            "DIY Tool Stand"
        ])

    if has_glass and has_metal:
        combined_ideas.extend([
            "Decorative Candle Lantern",
            "Table Lamp using Glass and Metal"
        ])

    # -------------------------------
    # Multi-object (3+ items)
    # -------------------------------

    if len(objects) >= 3:
        combined_ideas.append(
            "Complete Desk Organization Kit using multiple reused items"
        )

    # -------------------------------
    # Deduplicate and fallback
    # -------------------------------

    combined_ideas = list(dict.fromkeys(combined_ideas))

    if not combined_ideas:
        combined_ideas.append("Creative combined reuse project")

    return combined_ideas
