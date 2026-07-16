from fastapi import FastAPI

app = FastAPI(title="DOC FAST 4.0 Python Services")


@app.get("/health")
def health():
    return {"status": "ok"}


@app.get("/search/ping")
def search_ping():
    return {"service": "search", "status": "ready"}

