"""
routers/users.py — User profile endpoint
"""
from fastapi import APIRouter, Depends, UploadFile, File, HTTPException
from sqlalchemy.orm import Session
import os
import uuid
import shutil

from database import get_db
from models.user import User
from schemas.user import UserProfile, UserUpdateRequest, MessageResponse
from utils.deps import get_current_user

router = APIRouter(prefix="/users", tags=["Users"])


# ── GET /users/me ──────────────────────────────────────────────────────────────
@router.get("/me", response_model=UserProfile)
def get_my_profile(current_user: User = Depends(get_current_user)):
    """
    Returns the full profile of the currently authenticated user,
    including their role, skills, interests, and goals.
    """
    # Ensure relationships are loaded and safe
    skills_list    = [s.skill_name    for s in (current_user.skills or [])]
    interests_list = [i.interest_name for i in (current_user.interests or [])]
    goals_list     = [g.goal_name     for g in (current_user.goals or [])]

    return UserProfile(
        id         = current_user.id,
        full_name  = current_user.full_name,
        email      = current_user.email,
        role       = current_user.role,
        gender     = current_user.gender,
        phone      = current_user.phone,
        profile_picture = current_user.profile_picture,
        is_active  = current_user.is_active,
        created_at = current_user.created_at,
        skills     = skills_list,
        interests  = interests_list,
        goals      = goals_list,
    )


# ── PUT /users/me ──────────────────────────────────────────────────────────────
@router.put("/me", response_model=MessageResponse)
def update_profile(
    payload: UserUpdateRequest,
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    """
    Updates the user's basic profile info.
    """
    if payload.full_name is not None:
        current_user.full_name = payload.full_name.strip()
    if payload.gender is not None:
        current_user.gender = payload.gender
    if payload.phone is not None:
        current_user.phone = payload.phone.strip()
    if payload.profile_picture is not None:
        current_user.profile_picture = payload.profile_picture

    db.add(current_user)
    db.commit()
    return MessageResponse(message="Profile updated successfully.")


# ── POST /users/avatar ─────────────────────────────────────────────────────────
@router.post("/avatar", response_model=MessageResponse)
async def upload_avatar(
    file: UploadFile = File(...),
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    """
    Uploads a new profile picture for the user.
    """
    # Validate file type
    if not file.content_type.startswith("image/"):
        raise HTTPException(status_code=400, detail="File must be an image.")

    # Create unique filename
    ext = os.path.splitext(file.filename)[1]
    filename = f"{uuid.uuid4()}{ext}"
    filepath = os.path.join("uploads/profile_pics", filename)

    # Save file
    try:
        with open(filepath, "wb") as buffer:
            shutil.copyfileobj(file.file, buffer)
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Could not save file: {e}")

    # Update user record (Relative URL)
    avatar_url = f"/uploads/profile_pics/{filename}"
    current_user.profile_picture = avatar_url
    db.add(current_user)
    db.commit()

    return MessageResponse(message="Avatar uploaded successfully.", success=True)
