import os
import json
from dotenv import load_dotenv
from openai import OpenAI

# Load environment variables
load_dotenv()

api_key = os.getenv("OPENAI_API_KEY")

if not api_key:
    raise ValueError("OPENAI_API_KEY not found. Check your .env file location.")

client = OpenAI(api_key=api_key)


def generate_steps(idea_title: str):
    """
    Generates structured step-by-step instructions
    for a selected reuse idea.
    """

    prompt = f"""
You are an expert DIY instructor.

Create detailed, beginner-friendly step-by-step instructions for:

{idea_title}

For each step include:
- step_number
- instruction
- tools_required
- image_prompt (clear description for generating illustration image)

Rules:
- Keep steps realistic
- Use simple tools
- Make it safe
- 5–8 steps maximum

Return JSON ONLY in this format:

[
  {{
    "step_number": 1,
    "instruction": "",
    "tools_required": "",
    "image_prompt": ""
  }}
]
"""

    response = client.chat.completions.create(
        model="gpt-4o-mini",
        messages=[
            {"role": "user", "content": prompt}
        ],
        temperature=0.7
    )

    content = response.choices[0].message.content

    # Ensure valid JSON is returned
    try:
        return json.loads(content)
    except json.JSONDecodeError:
        # If AI slightly misformats JSON, return raw text for debugging
        return {
            "error": "Invalid JSON returned by AI",
            "raw_response": content
        }
