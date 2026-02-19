import os
import cv2
from .real_plenoxels import run_real_plenoxels_reconstruction  # ✅ REAL PLENOXELS
from .extract_dimensions import estimate_dimensions  # Fallback
from .material_inference import infer_material

def run_plenoxels_reconstruction(image_folder: str):
    """
    Real Plenoxels reconstruction.
    """
    # 1. Load images
    images = []
    valid_extensions = {'.jpg', '.jpeg', '.png', '.bmp'}
    
    for img_name in os.listdir(image_folder):
        if os.path.splitext(img_name)[1].lower() in valid_extensions:
            img_path = os.path.join(image_folder, img_name)
            img = cv2.imread(img_path)
            if img is not None:
                # Resize for faster processing
                img = cv2.resize(img, (256, 256))
                images.append(img)
    
    if len(images) < 2:
        # Fallback to mock
        return {
            "model_path": "",
            "dimensions": {"width": 10.0, "height": 20.0, "depth": 10.0},
            "material": infer_material(images) if images else "unknown",
            "volume_cm3": 2000.0,
            "status": "insufficient_images"
        }
    
    # 2. ✅ RUN REAL PLENOXELS
    output_path = "output/reconstructed.ply"
    dimensions, volume = run_real_plenoxels_reconstruction(images, output_path)
    
    # 3. Get material
    material = infer_material(images)
    
    return {
        "model_path": output_path if os.path.exists(output_path) else "",
        "dimensions": dimensions,
        "material": material,
        "volume_cm3": volume,
        "status": "success" if os.path.exists(output_path) else "partial"
    }