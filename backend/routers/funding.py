"""
routers/funding.py — Investment endpoints for Investors
"""
from datetime import datetime
from typing import Optional
from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from pydantic import BaseModel

from database import get_db
from models.user import User
from models.project import Project
from models.funding import Investment
from models.notification import Notification
from utils.deps import get_current_user

router = APIRouter(prefix="/funding", tags=["Funding"])


class InvestPayload(BaseModel):
    amount: float
    note: Optional[str] = None


@router.get("/projects")
def funding_projects(db: Session = Depends(get_db), current_user: User = Depends(get_current_user)):
    projects = db.query(Project).filter(Project.seeking_funding == True, Project.status == "open").all()
    return [
        {
            "id": p.id, "title": p.title, "description": p.description,
            "category": p.category, "funding_goal": p.funding_goal,
            "amount_raised": p.amount_raised,
            "pct_funded": round((p.amount_raised / p.funding_goal * 100), 1) if p.funding_goal > 0 else 0,
            "owner_name": p.owner.full_name if p.owner else None,
            "team_size": p.team_size, "created_at": p.created_at,
        }
        for p in projects
    ]


@router.post("/{project_id}/invest", status_code=201)
def invest_in_project(project_id: int, payload: InvestPayload, db: Session = Depends(get_db), current_user: User = Depends(get_current_user)):
    if current_user.role != "investor":
        raise HTTPException(status_code=403, detail="Only Investors can invest.")
    project = db.query(Project).filter(Project.id == project_id).first()
    if not project:
        raise HTTPException(status_code=404, detail="Project not found.")
    if not project.seeking_funding:
        raise HTTPException(status_code=400, detail="This project is not seeking funding.")
    if payload.amount <= 0:
        raise HTTPException(status_code=400, detail="Amount must be positive.")
    
    remaining_budget = project.funding_goal - (project.amount_raised or 0)
    if payload.amount > remaining_budget:
        raise HTTPException(status_code=400, detail=f"Investment exceeds remaining budget. Max allowed: ₹{remaining_budget:,.0f}")

    inv = Investment(project_id=project_id, investor_id=current_user.id, amount=payload.amount, note=payload.note)
    db.add(inv)
    project.amount_raised = (project.amount_raised or 0) + payload.amount
    project.updated_at = datetime.utcnow()
    db.add(Notification(user_id=project.owner_id, title="New Investment! 🎉",
        body=f"{current_user.full_name} invested ₹{payload.amount:,.0f} in '{project.title}'.", notif_type="investment"))
    db.commit()
    db.refresh(inv)
    return {"id": inv.id, "project_id": project_id, "amount": inv.amount, "new_total": project.amount_raised}


@router.get("/portfolio")
def my_portfolio(db: Session = Depends(get_db), current_user: User = Depends(get_current_user)):
    investments = db.query(Investment).filter(Investment.investor_id == current_user.id).order_by(Investment.created_at.desc()).all()
    return [
        {
            "id": inv.id, "project_id": inv.project_id,
            "project_title": inv.project.title if inv.project else None,
            "project_status": inv.project.status if inv.project else None,
            "amount": inv.amount, "note": inv.note, "created_at": inv.created_at,
            "pct_funded": round((inv.project.amount_raised / inv.project.funding_goal * 100), 1) if inv.project and inv.project.funding_goal > 0 else 0,
        }
        for inv in investments
    ]


@router.get("/stats")
def funding_stats(db: Session = Depends(get_db), current_user: User = Depends(get_current_user)):
    investments = db.query(Investment).filter(Investment.investor_id == current_user.id).all()
    total_invested = sum(i.amount for i in investments)
    active_count = sum(1 for i in investments if i.project and i.project.status in ("open", "in_progress"))
    completed_count = sum(1 for i in investments if i.project and i.project.status == "completed")
    simulated_value = sum(i.amount * 1.25 if i.project and i.project.status == "completed" else i.amount for i in investments)
    roi_pct = round((simulated_value / total_invested - 1) * 100, 1) if total_invested > 0 else 0.0
    return {
        "total_invested": total_invested, "portfolio_value": simulated_value, "roi_pct": roi_pct,
        "active_investments": active_count, "completed_investments": completed_count, "total_investments": len(investments),
    }
