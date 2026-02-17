from fastapi import FastAPI, Request
from fastapi.responses import JSONResponse
import shutil
import os

from backend.object_analysis import analyze_single_object
from backend.llm_service import generate_reuse_ideas
from backend.step_service import generate_steps

print("🔥 THIS BACKEND VERSION IS RUNNING")

app = FastAPI()

UPLOAD_DIR = "uploads"
os.makedirs(UPLOAD_DIR, exist_ok=True)


# =====================================================
# UNLIMITED OBJECTS ANALYZE SESSION
# =====================================================
@app.post("/analyze-session")
async def analyze_session(request: Request):

    print("\n===== ANALYZE SESSION CALLED =====")

    try:
        form = await request.form()
        files = form.getlist("files")

        print("Raw form keys:", list(form.keys()))
        print("Files received:", len(files))

        if len(files) == 0:
            return JSONResponse(
                status_code=200,
                content={
                    "success": False,
                    "objects": [],
                    "error": "No files received"
                }
            )

        analyzed_objects = []

        for i, file in enumerate(files):

            object_id = f"object_{i+1}"
            obj_dir = os.path.join(UPLOAD_DIR, object_id)
            os.makedirs(obj_dir, exist_ok=True)

            file_path = os.path.join(obj_dir, file.filename)

            with open(file_path, "wb") as buffer:
                buffer.write(await file.read())

            print("Saved:", file_path)

            height = form.get(f"height_cm_{i}")
            width = form.get(f"width_cm_{i}")
            depth = form.get(f"depth_cm_{i}")

            print("Height:", height)
            print("Width:", width)
            print("Depth:", depth)

            analysis = analyze_single_object([file_path])

            if analysis:
                analysis["object_id"] = object_id
                analysis["dimensions"] = {
                    "height_cm": height,
                    "width_cm": width,
                    "depth_cm": depth
                }
                analyzed_objects.append(analysis)

        return {
            "success": True,
            "objects": analyzed_objects
        }

    except Exception as e:
        print("🔥 BACKEND ERROR:", str(e))
        return JSONResponse(
            status_code=200,
            content={
                "success": False,
                "objects": [],
                "error": str(e)
            }
        )


# =====================================================
# AI REUSE IDEAS
# =====================================================
@app.post("/generate-reuse-ai")
async def generate_reuse_ai(data: dict):

    print("\n===== LLM REUSE GENERATION CALLED =====")

    result = generate_reuse_ideas(data)
    return result


# =====================================================
# STEP GENERATION
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
