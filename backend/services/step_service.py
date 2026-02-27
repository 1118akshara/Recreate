import json
from backend.services.llm_core import call_llm


def extract_json(text: str):
    """
    Safely extract JSON object from LLM output.
    """
    try:
        return json.loads(text)
    except:
        start = text.find("{")
        end = text.rfind("}") + 1
        if start != -1 and end != -1:
            try:
                return json.loads(text[start:end])
            except:
                return {}
        return {}


def generate_steps(idea_title, objects):

    prompt = f"""
You are a professional DIY Craft Instructor and Sustainability Expert.

Provide detailed, beginner-friendly instructions for the following reuse idea.

Idea Title:
{idea_title}

Scanned Objects:
{json.dumps(objects, indent=2)}

REQUIREMENTS:

1. Provide required_materials as a simple list.
2. Provide clear step-by-step instructions.
3. Steps must be simple and practical.
4. Avoid technical language.
5. Avoid dangerous tools.
6. Focus on eco-friendly usage.
7. Make it easy for anyone to follow at home.

Return STRICT JSON:

{{
  "required_materials": [],
  "steps": [
    {{
      "step_number": 1,
      "instruction": ""
    }}
  ]
}}

Do NOT include anything outside JSON.
"""

    output = call_llm(prompt)
    result = extract_json(output)

    return {
        "required_materials": result.get("required_materials", []),
        "steps": result.get("steps", [])
    }