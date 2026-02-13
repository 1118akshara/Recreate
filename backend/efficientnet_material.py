import torch
from torchvision import models, transforms
from PIL import Image
import os

# ===============================
# ORIGINAL TRAINED MATERIAL CLASSES (DO NOT MODIFY COUNT)
# ===============================
MATERIAL_CLASSES = [
    "brick",
    "carpet",
    "ceramic",
    "fabric",
    "foliage",
    "food",
    "glass",
    "hair",
    "leather",
    "metal",
    "mirror",
    "other",
    "painted",
    "paper",
    "plastic",
    "polishedstone",
    "skin",
    "sky",
    "stone",
    "tile",
    "wallpaper",
    "water",
    "wood"
]

NUM_CLASSES = len(MATERIAL_CLASSES)

# ===============================
# ALLOWED MATERIALS FOR REUSE SYSTEM
# ===============================
ALLOWED_MATERIALS = [
    "brick",
    "carpet",
    "fabric",
    "foliage",
    "food",
    "glass",
    "hair",
    "leather",
    "metal",
    "mirror",
    "other",
    "painted",
    "paper",
    "plastic",
    "polishedstone",
    "stone",
    "wallpaper",
    "wood"
]

# ===============================
# DEVICE
# ===============================
device = torch.device("cuda" if torch.cuda.is_available() else "cpu")

# ===============================
# LOAD EFFICIENTNET
# ===============================
material_model = models.efficientnet_b0(weights=None)
material_model.classifier[1] = torch.nn.Linear(1280, NUM_CLASSES)

BASE_DIR = os.path.dirname(__file__)
MODEL_PATH = os.path.join(BASE_DIR, "efficientnet_material.pth")

state_dict = torch.load(MODEL_PATH, map_location=device)
material_model.load_state_dict(state_dict)

material_model.to(device)
material_model.eval()

# ===============================
# IMAGE PREPROCESSING
# ===============================
transform = transforms.Compose([
    transforms.Resize((224, 224)),
    transforms.ToTensor(),
    transforms.Normalize(
        mean=[0.485, 0.456, 0.406],
        std=[0.229, 0.224, 0.225]
    )
])

# ===============================
# MATERIAL PREDICTION
# ===============================
def predict_material(image_paths):

    votes = []

    for image_path in image_paths:

        if not os.path.exists(image_path):
            continue

        image = Image.open(image_path).convert("RGB")

        # 🔥 Use full image for stability
        cropped = image

        tensor = transform(cropped).unsqueeze(0).to(device)

        with torch.no_grad():
            outputs = material_model(tensor)
            probs = torch.softmax(outputs, dim=1)
            class_idx = torch.argmax(probs, dim=1).item()
            confidence = probs[0][class_idx].item()

        # 🔥 Stronger confidence threshold
        if confidence >= 0.60:
            predicted_material = MATERIAL_CLASSES[class_idx]
            votes.append((predicted_material, confidence))

    # No valid predictions
    if not votes:
        return {
            "material": "unknown",
            "confidence": 0.0
        }

    # Confidence-weighted voting
    score_map = {}
    for material, conf in votes:
        score_map[material] = score_map.get(material, 0) + conf

    final_material = max(score_map, key=score_map.get)
    final_confidence = score_map[final_material] / len(votes)

    # 🔥 Filter unwanted materials
    if final_material not in ALLOWED_MATERIALS:
        final_material = "unknown"

    return {
        "material": final_material,
        "confidence": round(final_confidence, 2)
    }
