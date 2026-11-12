"""
routers/ratings.py — Member performance ratings
"""
from typing import List, Optional
from datetime import datetime
from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from pydantic import BaseModel

from database import get_db
from models.user import User
from models.project import Project, ProjectMember
from models.rating import Rating
from models.notification import Notification
from utils.deps import get_current_user

router = APIRouter(prefix="/ratings", tags=["Ratings"])

class RatingCreate(BaseModel):
    project_id: int
    user_id: int  # person being rated
    score: float  # 1.0 to 5.0
    comment: Optional[str] = None

class RatingOut(BaseModel):
    id: int
    project_id: int
    rater_id: int
    user_id: int
    score: float
    comment: Optional[str]
    created_at: datetime
    project_title: Optional[str] = None
    rater_name: Optional[str] = None

    class Config:
        from_attributes = True

@router.post("/", response_model=RatingOut)
def create_rating(payload: RatingCreate, db: Session = Depends(get_db), current_user: User = Depends(get_current_user)):
    # Only idea owners or project owners should rate? 
    # Usually the project owner rates team members.
    project = db.query(Project).filter(Project.id == payload.project_id).first()
    if not project:
        raise HTTPException(status_code=404, detail="Project not found.")
    
    if project.owner_id != current_user.id:
        raise HTTPException(status_code=403, detail="Only project owners can rate members.")
    
    # Check if user is a member of the project
    is_member = db.query(ProjectMember).filter(
        ProjectMember.project_id == payload.project_id,
        ProjectMember.user_id == payload.user_id
    ).first()
    if not is_member:
        raise HTTPException(status_code=400, detail="User is not a member of this project.")
    
    # Check if already rated for this project
    existing = db.query(Rating).filter(
        Rating.project_id == payload.project_id,
        Rating.user_id == payload.user_id,
        Rating.rater_id == current_user.id
    ).first()
    if existing:
        # Update existing rating
        existing.score = payload.score
        existing.comment = payload.comment
        existing.created_at = datetime.utcnow()
        db.commit()
        db.refresh(existing)
        return existing

    new_rating = Rating(
        project_id = payload.project_id,
        rater_id   = current_user.id,
        user_id    = payload.user_id,
        score      = payload.score,
        comment    = payload.comment
    )
    db.add(new_rating)
    
    # Notify the user they were rated
    db.add(Notification(
        user_id = payload.user_id,
        title = "New Performance Rating! ⭐",
        body = f"You received a {payload.score}/5 rating for your work on '{project.title}'.",
        notif_type = "rating"
    ))
    
    db.commit()
    db.refresh(new_rating)
    return new_rating

@router.get("/user/{user_id}", response_model=List[RatingOut])
def get_user_ratings(user_id: int, db: Session = Depends(get_db)):
    ratings = db.query(Rating).filter(Rating.user_id == user_id).order_by(Rating.created_at.desc()).all()
    # Add project titles and rater names
    out = []
    for r in ratings:
        item = RatingOut.from_orm(r)
        item.project_title = r.project.title if r.project else "Deleted Project"
        item.rater_name = r.rater.full_name if r.rater else "Unknown"
        out.append(item)
    return out

@router.get("/stats/{user_id}")
def get_user_rating_stats(user_id: int, db: Session = Depends(get_db)):
    ratings = db.query(Rating).filter(Rating.user_id == user_id).all()
    if not ratings:
        return {"average_rating": 0.0, "total_ratings": 0}
    
    avg = sum(r.score for r in ratings) / len(ratings)
    return {"average_rating": round(avg, 1), "total_ratings": len(ratings)}
