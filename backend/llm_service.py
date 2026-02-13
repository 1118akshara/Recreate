import os
import json
from dotenv import load_dotenv
from openai import OpenAI

load_dotenv()

api_key = os.getenv("OPENAI_API_KEY")

if not api_key:
    raise ValueError("OPENAI_API_KEY not found")

client = OpenAI(api_key=api_key)


def generate_reuse_ideas(analysis_json: dict):

    prompt = f"""
You are an AI expert in reuse and sustainability.

Detected objects:
{analysis_json}

Generate NEW reuse ideas.

Categorize into:
- Easy
- Moderate
- Very Hard

Return JSON ONLY. No explanation text.

Format:

{{
 "easy": [{{"title":"","description":"","youtube_query":""}}],
 "moderate": [],
 "very_hard": []
}}
"""

    response = client.chat.completions.create(
        model="gpt-4o-mini",
        messages=[{"role": "user", "content": prompt}],
        temperature=0.9
    )

    content = response.choices[0].message.content

    try:
        return json.loads(content)
    except:
        return {
            "easy": [],
            "moderate": [],
            "very_hard": []
        }
