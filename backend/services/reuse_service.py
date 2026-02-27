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


def generate_reuse(objects):

    multiple_objects = len(objects) > 1

    prompt = f"""
You are a professional Sustainable Product Designer.

Generate creative, practical, eco-friendly reuse ideas.

Scanned Objects:
{json.dumps(objects, indent=2)}

STRICT RULES:

EASY:
- Use ONLY one scanned object.
- Simple and beginner friendly.

MODERATE:
- MUST combine ALL scanned objects together.
- Practical and realistic for home use.

ADVANCED:
- MUST combine ALL scanned objects.
- MUST include at least one extra simple material (rope, glue, LED, wood, cloth, etc).

ALL IDEAS MUST BE:
- Safe
- Eco-friendly
- Budget-friendly
- Realistic
- Useful in daily life

Each idea must include:
- title (short and attractive)
- short_description (1-2 simple sentences)
- sustainability_note (1 short sentence)

Return STRICT JSON:

{{
  "easy": [
    {{
      "title": "",
      "short_description": "",
      "sustainability_note": ""
    }}
  ],
  "moderate": [],
  "advanced": []
}}

Do NOT include anything outside JSON.
Ensure moderate and advanced are not empty if multiple objects exist.
"""

    output = call_llm(prompt)
    result = extract_json(output)

    easy = result.get("easy", [])
    moderate = result.get("moderate", [])
    advanced = result.get("advanced", [])

    # Safety fallback
    if not multiple_objects:
        moderate = []
        if not advanced:
            advanced = [
                {
                    "title": "Creative Decorative Upgrade",
                    "short_description": "Enhance the object using simple craft materials to give it a new purpose.",
                    "sustainability_note": "Extends product life and reduces landfill waste."
                }
            ]
    
    return {
        "easy": easy,
        "moderate": moderate,
        "advanced": advanced
    }