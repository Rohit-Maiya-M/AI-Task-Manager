import pytest
from fastapi.testclient import TestClient
from main import app

client = TestClient(app)

def test_summarize_single(monkeypatch):
    # Mock Gemini response
    def fake_summarize_individual_task(task):
        return "Mock summary for testing"

    from app.services import ai_service
    monkeypatch.setattr(ai_service, "summarize_individual_task", fake_summarize_individual_task)

    payload = {"task": {"title": "Finish backend module"}}
    response = client.post("/api/ai/summarize/individual", json=payload)
    assert response.status_code == 200
    assert "summary" in response.json()
