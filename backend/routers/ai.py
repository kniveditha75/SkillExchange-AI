"""
routers/ai.py — Local AI insights and matching
"""
from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session

from database import get_db
from models.user import User, UserSkill
from models.project import Project
from utils.deps import get_current_user
from utils.ai_logic import calculate_project_health

router = APIRouter(prefix="/ai", tags=["AI"])


def _skill_match_score(user_skills: list[str], project_skills_str: str) -> float:
    """Return overlap score 0.0–100.0 between user skills and project required_skills."""
    if not project_skills_str or not user_skills:
        return 0.0
    proj_skills = {s.strip().lower() for s in project_skills_str.split(",")}
    user_set    = {s.lower() for s in user_skills}
    if not proj_skills:
        return 0.0
    overlap = len(user_set & proj_skills)
    return round((overlap / len(proj_skills)) * 100, 1)


@router.get("/recommendations")
def get_recommendations(
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    """Return top AI-matched open projects for the current user based on skill overlap."""
    user_skills_rows = db.query(UserSkill).filter(UserSkill.user_id == current_user.id).all()
    user_skills = [s.skill_name for s in user_skills_rows]

    projects = db.query(Project).filter(Project.status == "open").all()

    scored = []
    for p in projects:
        score = _skill_match_score(user_skills, p.required_skills or "")
        scored.append({
            "id": p.id,
            "title": p.title,
            "description": p.description,
            "category": p.category,
            "required_skills": p.required_skills,
            "funding_goal": p.funding_goal,
            "amount_raised": p.amount_raised,
            "seeking_funding": p.seeking_funding,
            "team_size": p.team_size,
            "owner_name": p.owner.full_name if p.owner else None,
            "match_score": score,
            "created_at": p.created_at,
        })

    # Sort by match score descending, then by created_at
    scored.sort(key=lambda x: (-x["match_score"], x["created_at"]))
    return scored[:20]   # top 20


@router.get("/skill-analysis")
def skill_analysis(
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    """Analyze user's skills vs market demand across open projects."""
    user_skills_rows = db.query(UserSkill).filter(UserSkill.user_id == current_user.id).all()
    user_skills = [s.skill_name for s in user_skills_rows]

    projects = db.query(Project).filter(Project.status == "open").all()

    # Count demand per skill across all projects
    demand: dict[str, int] = {}
    for p in projects:
        if p.required_skills:
            for skill in p.required_skills.split(","):
                s = skill.strip()
                demand[s] = demand.get(s, 0) + 1

    total_projects = len(projects)
    user_skill_stats = []
    for skill in user_skills:
        count = demand.get(skill, 0)
        demand_pct = round((count / total_projects * 100), 1) if total_projects > 0 else 0
        user_skill_stats.append({
            "skill": skill,
            "demand_count": count,
            "demand_pct": demand_pct,
            "level": "high" if demand_pct >= 30 else "medium" if demand_pct >= 10 else "low"
        })

    # Top missing skills (high demand but user doesn't have them)
    top_demanded = sorted(demand.items(), key=lambda x: -x[1])[:10]
    suggested_skills = [
        {"skill": s, "demand_count": c}
        for s, c in top_demanded
        if s not in user_skills
    ][:5]

    overall_score = round(
        sum(s["demand_pct"] for s in user_skill_stats) / len(user_skill_stats), 1
    ) if user_skill_stats else 0.0

    return {
        "user_skills": user_skill_stats,
        "suggested_skills": suggested_skills,
        "overall_score": overall_score,
        "total_projects_analyzed": total_projects,
    }


@router.get("/project-analysis/{project_id}")
def project_analysis(
    project_id: int,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    """Feature 4: AI Investor Analytics (Local Algorithm)"""
    project = db.query(Project).filter(Project.id == project_id).first()
    if not project:
        raise HTTPException(status_code=404, detail="Project not found.")

    # Prepare data for the algorithm
    project_data = {
        "funding_goal": project.funding_goal,
        "amount_raised": project.amount_raised,
        "team_size": project.team_size,
        "created_at": project.created_at
    }
    
    return calculate_project_health(project_data)


@router.get("/match-score/{project_id}")
def match_score(
    project_id: int,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    """Get match score for the current user vs a specific project."""
    user_skills_rows = db.query(UserSkill).filter(UserSkill.user_id == current_user.id).all()
    user_skills = [s.skill_name for s in user_skills_rows]

    project = db.query(Project).filter(Project.id == project_id).first()
    if not project:
        return {"match_score": 0.0}

    score = _skill_match_score(user_skills, project.required_skills or "")
    return {
        "project_id": project_id,
        "match_score": score,
        "user_skill_count": len(user_skills),
        "project_skill_count": len(project.required_skills.split(",")) if project.required_skills else 0,
    }
