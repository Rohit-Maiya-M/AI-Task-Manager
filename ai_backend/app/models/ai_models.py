from pydantic import BaseModel
from typing import List, Optional
from datetime import datetime

class Task(BaseModel):
    title: str
    description: Optional[str] = None
    dueDate: Optional[datetime] = None
    priority: Optional[str] = None

class SummarizeMultipleRequest(BaseModel):
    tasks: List[Task]

class SummarizeSingleRequest(BaseModel):
    task: Task

class PrioritizeMultipleRequest(BaseModel):
    tasks: List[Task]

class PrioritizeSingleRequest(BaseModel):
    task: Task

class NLPTaskRequest(BaseModel):
    command: str
