"""
routers/onboarding.py — Role selection and skill/interest/goal saving endpoints
"""
from typing import List
from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session

from database import get_db
from models.user import User, UserSkill, UserInterest, UserGoal
from schemas.user import RoleRequest, SkillsRequest, InterestsRequest, GoalsRequest, MessageResponse
from utils.deps import get_current_user

router = APIRouter(prefix="/onboarding", tags=["Onboarding"])

VALID_ROLES = {"freelancer", "investor", "idea_owner"}


# ── POST /onboarding/role ──────────────────────────────────────────────────────
@router.post("/role", response_model=MessageResponse)
def save_role(
    payload: RoleRequest,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    """
    Save the user's selected role (freelancer / investor / idea_owner).
    Called after the Role Selection screen.
    """
    if payload.role not in VALID_ROLES:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=f"Invalid role. Must be one of: {', '.join(VALID_ROLES)}"
        )

    current_user.role = payload.role
    db.commit()
    return MessageResponse(message=f"Role set to '{payload.role}' successfully.")


# ── POST /onboarding/skills ────────────────────────────────────────────────────
@router.post("/skills", response_model=MessageResponse)
def save_skills(
    payload: SkillsRequest,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    """
    Save the user's selected skills (Step 1 of onboarding).
    Replaces any previously saved skills.
    """
    # Clear existing skills first
    db.query(UserSkill).filter(UserSkill.user_id == current_user.id).delete()

    # Add new skills
    for skill in payload.skills:
        skill = skill.strip()
        if skill:
            db.add(UserSkill(user_id=current_user.id, skill_name=skill))

    db.commit()
    return MessageResponse(message=f"{len(payload.skills)} skill(s) saved successfully.")


# ── POST /onboarding/interests ─────────────────────────────────────────────────
@router.post("/interests", response_model=MessageResponse)
def save_interests(
    payload: InterestsRequest,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    """
    Save the user's selected interests (Step 2 of onboarding).
    Replaces any previously saved interests.
    """
    db.query(UserInterest).filter(UserInterest.user_id == current_user.id).delete()

    for interest in payload.interests:
        interest = interest.strip()
        if interest:
            db.add(UserInterest(user_id=current_user.id, interest_name=interest))

    db.commit()
    return MessageResponse(message=f"{len(payload.interests)} interest(s) saved successfully.")


# ── POST /onboarding/goals ─────────────────────────────────────────────────────
@router.post("/goals", response_model=MessageResponse)
def save_goals(
    payload: GoalsRequest,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    """
    Save the user's selected goals (Step 3 of onboarding).
    Replaces any previously saved goals.
    """
    db.query(UserGoal).filter(UserGoal.user_id == current_user.id).delete()

    for goal in payload.goals:
        goal = goal.strip()
        if goal:
            db.add(UserGoal(user_id=current_user.id, goal_name=goal))

    db.commit()
    return MessageResponse(message=f"{len(payload.goals)} goal(s) saved successfully.")
