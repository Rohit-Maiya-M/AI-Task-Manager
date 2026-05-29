from fastapi import FastAPI
from app.api import ai_endpoints

app = FastAPI(title="AI Task Service", version="1.0")

# include routers
app.include_router(ai_endpoints.router, prefix="/api/ai", tags=["AI Endpoints"])
