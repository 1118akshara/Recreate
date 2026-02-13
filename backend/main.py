from fastapi import FastAPI, UploadFile, File, Form
import shutil
import os
from typing import List, Dict
from backend.object_analysis import analyze_single_object

# 🔹 NEW IMPORTS FOR AI
from backend.llm_service import generate_reuse_ideas
from backend.step_service import generate_steps

app = FastAPI()

UPLOAD_DIR = "uploads"
os.makedirs(UPLOAD_DIR, exist_ok=True)


# =====================================================
# EXISTING ENDPOINT (UNCHANGED)
# =====================================================
@app.post("/analyze-session")
async def analyze_session(
    files: List[UploadFile] = File(...),
    object_ids: List[str] = Form(...),
    view_types: List[str] = Form(...)
):
    print("\n===== ANALYZE SESSION CALLED =====")
    print(f"Files received: {len(files)}")

    object_groups: Dict[str, List[str]] = {}

    for file, obj_id, view in zip(files, object_ids, view_types):
        obj_dir = os.path.join(UPLOAD_DIR, obj_id)
        os.makedirs(obj_dir, exist_ok=True)

        filename = f"{view}_{file.filename}"
        path = os.path.join(obj_dir, filename)

        with open(path, "wb") as buffer:
            shutil.copyfileobj(file.file, buffer)

        object_groups.setdefault(obj_id, []).append(path)

    print("Received objects:")
    for obj_id, paths in object_groups.items():
        print(obj_id, len(paths))
        for p in paths:
            print(" ", p)

    analyzed_objects = []

    for obj_id, paths in object_groups.items():
        analysis = analyze_single_object(paths)
        if analysis:
            analysis["object_id"] = obj_id
            analyzed_objects.append(analysis)

    return {
        "objects": analyzed_objects,
       
    }


# =====================================================
# NEW AI ENDPOINT 1 — LLM Categorized Ideas
# =====================================================
@app.post("/generate-reuse-ai")
async def generate_reuse_ai(data: dict):

    print("\n===== LLM REUSE GENERATION CALLED =====")

    result = generate_reuse_ideas(data)

    return result


# =====================================================
# NEW AI ENDPOINT 2 — Detailed Step Generation
# =====================================================
@app.post("/generate-steps")
async def generate_reuse_steps(data: dict):

    idea = data.get("idea")

    if not idea:
        return {"error": "Idea title required"}

    print(f"\n===== GENERATING STEPS FOR: {idea} =====")

    steps = generate_steps(idea)

    return steps


# =====================================================
# HEALTH CHECK
# =====================================================
@app.get("/")
def health_check():
    return {"status": "Backend is running"}
