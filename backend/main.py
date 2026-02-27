from fastapi import FastAPI, Request
from fastapi.responses import JSONResponse
import shutil
import os

from backend.object_analysis import analyze_single_object
from backend.services.reuse_service import generate_reuse
from backend.services.step_service import generate_steps

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

            object_type = form.get(f"object_type_{i}")
            height = form.get(f"height_cm_{i}")
            width = form.get(f"width_cm_{i}")
            depth = form.get(f"depth_cm_{i}")
            diameter = form.get(f"diameter_cm_{i}")

            analysis = analyze_single_object([file_path])

            if analysis:

                analysis["object_id"] = object_id
                analysis["object_type"] = object_type

                if object_type == "CYLINDRICAL":
                    analysis["dimensions"] = {
                        "height_cm": height,
                        "diameter_cm": diameter
                    }
                else:
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
# AI REUSE IDEAS (LLM)
# =====================================================
@app.post("/generate-reuse-ai")
async def generate_reuse_ai(data: dict):

    print("\n===== LLM REUSE GENERATION CALLED =====")

    objects = data.get("objects")

    if not objects:
        return {
            "success": False,
            "error": "No objects provided"
        }

    try:
        result = generate_reuse(objects)
        print("LLM RESULT:", result)
        response = {
            "success": True,
            "easy": result.get("easy", []),
            "moderate": result.get("moderate", []),
            "advanced": result.get("advanced", [])
        }

        print("Sending to Android:", response)

        return response

    except Exception as e:
        print("🔥 REUSE ERROR:", str(e))
        return {
            "success": False,
            "error": str(e)
        }
# =====================================================
# STEP GENERATION (LLM)
# =====================================================
@app.post("/generate-steps")
async def generate_reuse_steps(data: dict):

    print("\n===== STEP GENERATION CALLED =====")

    idea_title = data.get("idea_title")
    objects = data.get("objects")

    if not idea_title:
        return {
            "success": False,
            "error": "Idea title required"
        }

    try:
        steps = generate_steps(idea_title, objects)
        print("STEPS :", steps)
        return {
            "success": True,
            "steps": steps
        }

    except Exception as e:
        print("🔥 STEP ERROR:", str(e))
        return {
            "success": False,
            "error": str(e)
        }