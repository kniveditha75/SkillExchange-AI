"""
main.py — FastAPI application entry point
Skill Exchange AI — Backend API
"""
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from fastapi.staticfiles import StaticFiles
import os

from database import engine, Base

# ── Import ALL models so SQLAlchemy creates all tables ─────────────────────────
import models.user        # noqa: F401
import models.project     # noqa: F401
import models.chat        # noqa: F401
import models.funding     # noqa: F401
import models.notification  # noqa: F401
import models.rating        # noqa: F401

# ── Import routers ─────────────────────────────────────────────────────────────
from routers import auth, onboarding, users
from routers import projects, chat, funding, ai, notifications, ratings

# ── Create all tables in MySQL ─────────────────────────────────────────────────
Base.metadata.create_all(bind=engine)

# ── FastAPI app ────────────────────────────────────────────────────────────────
app = FastAPI(
    title       = "Skill Exchange AI — Backend API",
    description = "Full REST API: auth, onboarding, projects, chat, funding, AI matching.",
    version     = "2.0.0",
    docs_url    = "/docs",
    redoc_url   = "/redoc",
)

# ── Static Files (for profile pictures) ────────────────────────────────────────
if not os.path.exists("uploads/profile_pics"):
    os.makedirs("uploads/profile_pics", exist_ok=True)

app.mount("/uploads", StaticFiles(directory="uploads"), name="uploads")

# ── CORS ───────────────────────────────────────────────────────────────────────
app.add_middleware(
    CORSMiddleware,
    allow_origins = [
        "http://localhost",
        "http://127.0.0.1",
        "http://10.0.2.2",
        "http://10.0.2.2:8000",
        "*",   # Allow all for development
    ],
    allow_credentials = True,
    allow_methods     = ["*"],
    allow_headers     = ["*"],
)

# ── Register routers ───────────────────────────────────────────────────────────
app.include_router(auth.router)
app.include_router(onboarding.router)
app.include_router(users.router)
app.include_router(projects.router)
app.include_router(chat.router)
app.include_router(funding.router)
app.include_router(ai.router)
app.include_router(notifications.router)
app.include_router(ratings.router)


# ── Root health check ──────────────────────────────────────────────────────────
@app.get("/", tags=["Health"])
def health_check():
    return {
        "status": "running",
        "app": "Skill Exchange AI Backend v2.0",
        "docs": "http://127.0.0.1:8000/docs",
        "endpoints": {
            "auth": "/auth",
            "onboarding": "/onboarding",
            "users": "/users",
            "projects": "/projects",
            "chat": "/chat",
            "funding": "/funding",
            "ai": "/ai",
            "notifications": "/notifications",
        }
    }
