# AI Task Manager

AI Task Manager is a full-stack task management system for personal and group workflows. It combines a Spring Boot REST API, a React frontend, PostgreSQL persistence, JWT authentication, and a separate FastAPI AI service for task summarization, prioritization, and natural-language task parsing.

## Features

- User signup and login with JWT-based authentication
- Personal task creation, editing, deletion, filtering, search, recents, due-date views, and library views
- Group administration flows for creating groups, adding members, assigning tasks, viewing members, and tracking assigned work
- Group member dashboards for assigned tasks, completion updates, search, filters, recents, and library views
- AI task summarization through a FastAPI service backed by Google Gemini
- AI endpoints for single-task summaries, group summaries, task prioritization, and NLP task parsing
- React protected routes with token injection through Axios interceptors

## Project Structure

```text
.
+-- aitaskmanager/          # Spring Boot backend API
+-- ai_backend/             # FastAPI AI microservice
+-- frontend/               # React client application
```

## Tech Stack

- Frontend: React, React Router, Axios, Carbon React, React Icons
- Backend: Java 21, Spring Boot, Spring Security, Spring Data JPA, JWT, WebClient
- Database: PostgreSQL
- AI service: Python, FastAPI, Uvicorn, Google Generative AI, spaCy
- Testing: Maven tests, React Testing Library, pytest

## Prerequisites

Install these before running the project:

- Java 21
- Node.js and npm
- Python 3.12 or newer
- PostgreSQL
- Git
- A Google Gemini API key

## Backend Setup

1. Create a PostgreSQL database:

```sql
CREATE DATABASE aitaskdb;
```

2. Review the Spring Boot configuration in `aitaskmanager/src/main/resources/application.properties`.

Current local defaults:

```properties
server.port=8080
spring.datasource.url=jdbc:postgresql://localhost:5432/aitaskdb
spring.datasource.username=postgres
spring.datasource.password=sole@2006
webclient.url=http://127.0.0.1:8000
```

3. Start the Spring Boot backend:

```powershell
cd aitaskmanager
.\mvnw.cmd spring-boot:run
```

The backend runs on:

```text
http://localhost:8080
```

## AI Service Setup

1. Create and activate a Python virtual environment:

```powershell
cd ai_backend
python -m venv .venv
.\.venv\Scripts\Activate.ps1
```

2. Install dependencies:

```powershell
pip install -r requirements.txt
pip install pydantic-settings
python -m spacy download en_core_web_sm
```

3. Create `ai_backend/.env`:

```env
GEMINI_API_KEY=your_gemini_api_key_here
```

4. Start the FastAPI service:

```powershell
uvicorn main:app --reload --host 127.0.0.1 --port 8000
```

The AI service runs on:

```text
http://127.0.0.1:8000
```

Swagger docs are available at:

```text
http://127.0.0.1:8000/docs
```

## Frontend Setup

1. Install dependencies:

```powershell
cd frontend
npm install
```

2. Start the React app:

```powershell
npm start
```

The frontend runs on:

```text
http://localhost:3000
```

The frontend API client points to:

```text
http://localhost:8080/aitaskmanager
```

## Running the Full App

The easiest way on Windows is to run the root launcher:

```powershell
.\run-all.bat
```

This starts:

- Spring Boot backend on port `8080`
- React frontend on port `3000`

Keep the launcher terminal open while using the app. Press `Ctrl+C` in that terminal to stop the services.

If you prefer to start each service manually, start them in this order:

1. PostgreSQL
2. Spring Boot backend on port `8080`
3. React frontend on port `3000`

Then open:

```text
http://localhost:3000
```

## Main API Areas

Authentication:

```text
POST /auth/signup
POST /auth/login
```

Personal tasks:

```text
POST   /aitaskmanager/personal/create
PATCH  /aitaskmanager/personal/edit/{taskId}
DELETE /aitaskmanager/personal/delete/{taskId}
GET    /aitaskmanager/personal/filter
GET    /aitaskmanager/personal/search
GET    /aitaskmanager/personal/library
```

Group admin:

```text
POST   /aitaskmanager/group/admin/createGroup
POST   /aitaskmanager/group/admin/createMember
GET    /aitaskmanager/group/admin/members/{groupId}
PUT    /aitaskmanager/group/admin/edit/{groupId}/{taskId}
PATCH  /aitaskmanager/group/admin/assign/{assignedUserId}/{groupId}/{taskId}
DELETE /aitaskmanager/group/admin/deleteMember
```

Group member:

```text
GET   /aitaskmanager/group/member/view/assignedTasks/{groupId}
PATCH /aitaskmanager/group/member/complete/{groupId}/{taskId}
GET   /aitaskmanager/group/member/filter/{groupId}
GET   /aitaskmanager/group/member/search/{groupId}
GET   /aitaskmanager/group/member/library
```

AI integration through Spring Boot:

```text
POST /aitaskmanager/ai/summarize
```

Direct FastAPI AI endpoints:

```text
POST /api/ai/summarize/individual
POST /api/ai/summarize/group
POST /api/ai/prioritize/single
POST /api/ai/prioritize/multiple
POST /api/ai/nlp-task
```

## Testing

Spring Boot tests:

```powershell
cd aitaskmanager
.\mvnw.cmd test
```

React tests:

```powershell
cd frontend
npm test
```

FastAPI tests:

```powershell
cd ai_backend
pytest
```

## Troubleshooting

- If login works but protected requests fail, check that the JWT token is present in browser local storage.
- If the frontend cannot call the backend, confirm Spring Boot is running on port `8080` and CORS allows `http://localhost:3000`.
- If AI summaries fail, confirm the FastAPI service is running on port `8000` and `GEMINI_API_KEY` is set in `ai_backend/.env`.
- If NLP parsing fails on startup, install the spaCy model with `python -m spacy download en_core_web_sm`.
- If database startup fails, confirm PostgreSQL is running and the `aitaskdb` database exists.

## Repository

```text
https://github.com/Rohit-Maiya-M/AI-Task-Manager
```
