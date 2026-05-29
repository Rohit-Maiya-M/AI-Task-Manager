from fastapi import APIRouter
from app.models.ai_models import SummarizeMultipleRequest, SummarizeSingleRequest, PrioritizeSingleRequest, PrioritizeMultipleRequest, NLPTaskRequest
from app.services.ai_service import (
    summarize_individual_task,
    summarize_group_tasks,
    prioritize_single_task,
    prioritize_multiple_tasks,
    parse_nlp_task
)

router = APIRouter()

# Summarization
@router.post("/summarize/individual")
def summarize_individual(request: SummarizeSingleRequest):
    return {"summary": summarize_individual_task(request.task)}

@router.post("/summarize/group")
def summarize_group(request: SummarizeMultipleRequest):  # reuse PrioritizeRequest since it has tasks list
    return {"summary": summarize_group_tasks(request.tasks)}

# Prioritization
@router.post("/prioritize/single")
def prioritize_single(request: PrioritizeSingleRequest):
    return prioritize_single_task(request.task)

@router.post("/prioritize/multiple")
def prioritize_multiple(request: PrioritizeMultipleRequest):
    return prioritize_multiple_tasks(request.tasks)

# NLP Parsing
@router.post("/nlp-task")
def nlp_task(request: NLPTaskRequest):
    return {"task": parse_nlp_task(request.command)}
