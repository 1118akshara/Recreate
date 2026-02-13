def reuse_prompt(analysis_json):
    return f"""
You are an AI expert in reuse and upcycling.

Detected objects with dimensions and materials:
{analysis_json}

TASK:
Generate NEW reuse ideas every time.

CATEGORIES:
- Easy
- Moderate
- Very Hard

RULES:
- Practical household projects only
- Difficulty based on steps, tools, precision
- No repeated ideas

Return ONLY JSON in this format:
{{
  "prompt_used": "summary of reasoning",
  "easy": [
    {{
      "title": "",
      "description": "",
      "youtube_query": ""
    }}
  ],
  "moderate": [],
  "very_hard": []
}}
"""
