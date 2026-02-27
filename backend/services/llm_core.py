import requests

OLLAMA_URL = "http://localhost:11434/api/generate"
MODEL = "mistral:7b-instruct-q4_0"

def call_llm(prompt: str):
    response = requests.post(
        OLLAMA_URL,
        json={
            "model": MODEL,
            "prompt": prompt,
            "stream": False,
            "options": {
                "temperature": 0.7
            }
        }
    )

    if response.status_code != 200:
        raise Exception("LLM request failed")

    return response.json()["response"]