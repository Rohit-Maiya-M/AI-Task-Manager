from fastapi.responses import JSONResponse
from fastapi.requests import Request
from fastapi import FastAPI

app = FastAPI()

@app.exception_handler(Exception)
async def global_exception_handler(request: Request, ex: Exception):
    return JSONResponse(
        status_code=500,
        content={
            "error": str(ex)
        }
    )
