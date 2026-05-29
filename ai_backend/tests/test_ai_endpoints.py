import pytest
from fastapi.testclient import TestClient
from main import app


client = TestClient(app)

BASE_URL = "/api/ai"

def safe_print_response(label, response):
    print(f"\n--- {label} ---")
    print("Status:", response.status_code)
    try:
        print("JSON:", response.json())
    except Exception:
        print("Raw text:", response.text)


def test_summarize_single():
    payload = {
        "task": {
            "title": "Finish backend module",
            "description": "Implement TaskRepository and service layer",
            "dueDate": "2026-04-18T10:00:00",
            "priority": "HIGH"
        }
    }
    r = client.post(f"{BASE_URL}/summarize/individual", json=payload)
    assert r.status_code == 200
    safe_print_response("Summarize Single", r)


def test_summarize_group():
    payload = {
        "tasks": [
            { "title": "Finish backend module", "description": "Implement TaskRepository and service layer", "dueDate": "2026-04-18T10:00:00", "priority": "HIGH" },
            { "title": "Prepare group presentation", "description": "Slides on ethics and holistic technologies", "dueDate": "2026-04-20T09:00:00", "priority": "MEDIUM" }
        ]
    }
    r = client.post(f"{BASE_URL}/summarize/group", json=payload)
    assert r.status_code == 200
    safe_print_response("Summarize Group", r)


def test_prioritize_single():
    payload = {
        "task": {
            "title": "Update resume",
            "description": "Add backend project and IBM prep work",
            "priority": "LOW"
        }
    }
    r = client.post(f"{BASE_URL}/prioritize/single", json=payload)
    assert r.status_code == 200
    safe_print_response("Prioritize Single", r)


def test_prioritize_multiple():
    payload = {
        "tasks": [
            { "title": "Finish backend module", "dueDate": "2026-04-18T10:00:00", "priority": "HIGH" },
            { "title": "Prepare group presentation", "dueDate": "2026-04-20T09:00:00", "priority": "MEDIUM" },
            { "title": "Update resume", "priority": "LOW" }
        ]
    }
    r = client.post(f"{BASE_URL}/prioritize/multiple", json=payload)
    assert r.status_code == 200
    safe_print_response("Prioritize Multiple", r)


def test_nlp_task():
    payload = { "command": "Remind me to finish backend tomorrow" }
    r = client.post(f"{BASE_URL}/nlp-task", json=payload)
    assert r.status_code == 200
    safe_print_response("NLP Task", r)


if __name__ == "__main__":
    test_summarize_single()
    test_summarize_group()
    test_prioritize_single()
    test_prioritize_multiple()
    test_nlp_task()
