from typing import List, Optional
import google.generativeai as genai
from app.models.ai_models import Task
import os
from app.config import settings

# Configure Gemini with API key from environment variable
genai.configure(api_key=settings.gemini_api_key)

import google.generativeai as genai
from app.models.ai_models import Task
import os

def summarize_individual_task(task: Task) -> str:
    if not task:
        return "No task provided."

    print("DEBUG: Received task:", task.dict())

    prompt = f"""
    Summarize this task clearly and completely.
    Title: {task.title}
    Description: {task.description or 'No description'}
    Due Date: {task.dueDate or 'No due date'}
    Priority: {task.priority or 'None'}
    """

    try:
        model = genai.GenerativeModel("gemini-2.5-flash")
        response = model.generate_content(prompt)
        print("DEBUG: Gemini response:", response)
        return response.text if response and response.text else "Summary unavailable."
    except Exception as e:
        print("ERROR in summarize_individual_task:", e)
        return f"Error: {str(e)}"


def summarize_group_tasks(tasks: list[Task]) -> str:
    if not tasks:
        return "No tasks provided."

    task_descriptions = "\n".join(
        [f"- {t.title} (due: {t.dueDate or 'No due date'}, priority: {t.priority or 'None'})"
         for t in tasks]
    )

    prompt = f"""
    Provide a group-level summary of the workload.
    Highlight:
    - Total number of tasks
    - How many are urgent or overdue
    - General priorities and deadlines
    - A concise overview of the team's workload

    Tasks:
    {task_descriptions}
    """

    model = genai.GenerativeModel("gemini-2.5-flash")
    response = model.generate_content(prompt)
    return response.text if response and response.text else "Summary unavailable."



def prioritize_single_task(task: Task) -> dict:
    if not task:
        return {"priority": "None", "reasoning": "No task provided."}

    prompt = f"""
    Analyze the following task and decide its priority level (HIGH, MEDIUM, LOW).
    Provide reasoning based on due date, description, and importance.

    Task:
    - Title: {task.title}
    - Description: {task.description or 'No description'}
    - Due Date: {task.dueDate or 'No due date'}
    - Current Priority: {task.priority or 'None'}
    """

    model = genai.GenerativeModel("gemini-2.5-flash")
    response = model.generate_content(prompt)

    return {
        "priority": task.priority or "Unspecified",
        "reasoning": response.text if response and response.text else "Reasoning unavailable."
    }


def prioritize_multiple_tasks(tasks: List[Task]) -> dict:
    if not tasks:
        return {"prioritized": [], "reasoning": "No tasks provided."}

    task_descriptions = "\n".join(
        [f"- {t.title}: {t.description or 'No description'} (due: {t.dueDate or 'No due date'}, priority: {t.priority or 'None'})"
         for t in tasks]
    )

    prompt = f"""
    You are an AI assistant. Order the following tasks by urgency and importance.
    For each task, explain why it is placed in that order (consider due date, description, and priority).

    Tasks:
    {task_descriptions}
    """

    model = genai.GenerativeModel("gemini-2.5-flash")
    response = model.generate_content(prompt)

    return {
        "prioritized": [t.dict() for t in tasks],  # keep original task data
        "reasoning": response.text if response and response.text else "Reasoning unavailable."
    }



import en_core_web_sm
from datetime import datetime, timedelta
from app.models.ai_models import Task

# Load English model (run: python -m spacy download en_core_web_sm)
nlp = en_core_web_sm.load()

def parse_nlp_task(command: str) -> dict:
    doc = nlp(command)

    # Default values
    title = command
    description = "Generated from NLP command"
    due_date = None

    # Extract named entities (dates, times, etc.)
    for ent in doc.ents:
        if ent.label_ in ["DATE", "TIME"]:
            # Simple handling: if "tomorrow" → set due date to tomorrow
            if ent.text.lower() == "tomorrow":
                due_date = datetime.now() + timedelta(days=1)
            elif ent.text.lower() == "today":
                due_date = datetime.now()
            # You can expand this with dateparser for more robust parsing

    # Extract verbs/nouns for title
    verbs = [token.lemma_ for token in doc if token.pos_ == "VERB"]
    nouns = [token.text for token in doc if token.pos_ == "NOUN"]

    if verbs or nouns:
        title = " ".join(verbs + nouns)

    return {
        "title": title.capitalize(),
        "description": description,
        "dueDate": due_date.isoformat() if due_date else None
    }
