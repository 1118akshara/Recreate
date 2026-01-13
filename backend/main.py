from fastapi import FastAPI, UploadFile, File
import shutil
import os
from typing import List
from reuse_engine import suggest_combined_reuse
from object_analysis import analyze_single_object

app = FastAPI()

UPLOAD_DIR = "uploads"
os.makedirs(UPLOAD_DIR, exist_ok=True)

@app.post("/analyze-session")
async def analyze_session(files: List[UploadFile] = File(...)):
    """
    TEMP STRUCTURE:
    Filenames should be like:
    object1_front.jpg
    object1_back.jpg
    object2_front.jpg
    object2_side.jpg
    """

    object_groups = {}

    # Group images by object ID (prefix before '_')
    for file in files:
        object_id = file.filename.split("_")[0]

        file_path = os.path.join(UPLOAD_DIR, file.filename)
        with open(file_path, "wb") as buffer:
            shutil.copyfileobj(file.file, buffer)

        object_groups.setdefault(object_id, []).append(file_path)

    analyzed_objects = []

    for object_id, image_paths in object_groups.items():
        analysis = analyze_single_object(image_paths)
        if analysis:
            analysis["object_id"] = object_id
            analyzed_objects.append(analysis)

    combined_suggestions = suggest_combined_reuse(analyzed_objects)

    return {
        "objects": analyzed_objects,
        "combined_reuse_suggestions": combined_suggestions
    }
