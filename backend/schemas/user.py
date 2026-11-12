"""
schemas/user.py — Pydantic models for request validation & response serialization
"""
from datetime import datetime
from typing import List, Optional
from pydantic import BaseModel, EmailStr


# ── Auth Schemas ───────────────────────────────────────────────────────────────

class RegisterRequest(BaseModel):
    full_name: str
    email: EmailStr
    password: str
    confirm_password: str
    gender: Optional[str] = None
    phone: Optional[str] = None

    class Config:
        json_schema_extra = {
            "example": {
                "full_name": "Alex Chen",
                "email": "alex.chen@example.com",
                "password": "password123"
            }
        }


class LoginRequest(BaseModel):
    email: EmailStr
    password: str

    class Config:
        json_schema_extra = {
            "example": {
                "email": "alex.chen@example.com",
                "password": "password123"
            }
        }


class TokenResponse(BaseModel):
    access_token: str
    token_type: str = "bearer"
    user_id: int
    full_name: str
    email: str


# ── Onboarding Schemas ─────────────────────────────────────────────────────────

class RoleRequest(BaseModel):
    role: str   # "freelancer" | "investor" | "idea_owner"

    class Config:
        json_schema_extra = {"example": {"role": "freelancer"}}


class SkillsRequest(BaseModel):
    skills: List[str]

    class Config:
        json_schema_extra = {"example": {"skills": ["Python", "React", "Kotlin"]}}


class InterestsRequest(BaseModel):
    interests: List[str]

    class Config:
        json_schema_extra = {"example": {"interests": ["HealthTech", "EdTech"]}}


class GoalsRequest(BaseModel):
    goals: List[str]

    class Config:
        json_schema_extra = {"example": {"goals": ["Earn Income", "Learn & Grow"]}}


# ── User Profile Response ──────────────────────────────────────────────────────

class UserProfile(BaseModel):
    id: int
    full_name: str
    email: str
    role: Optional[str]
    gender: Optional[str]
    phone: Optional[str]
    profile_picture: Optional[str]
    is_active: bool
    created_at: datetime
    skills: List[str]
    interests: List[str]
    goals: List[str]

    class Config:
        from_attributes = True


class UserUpdateRequest(BaseModel):
    full_name: Optional[str] = None
    gender: Optional[str] = None
    phone: Optional[str] = None
    profile_picture: Optional[str] = None


# ── Generic Response ───────────────────────────────────────────────────────────

class MessageResponse(BaseModel):
    message: str
    success: bool = True
