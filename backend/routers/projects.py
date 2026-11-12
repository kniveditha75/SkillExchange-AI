"""
routers/projects.py — Project CRUD, apply, team management
"""
from typing import List, Optional
from datetime import datetime
from fastapi import APIRouter, Depends, HTTPException, status, Query
from sqlalchemy.orm import Session
from pydantic import BaseModel

from database import get_db
from models.user import User, UserSkill
from models.project import Project, ProjectApplication, ProjectMember
from models.notification import Notification
from models.chat import Conversation, ConversationParticipant
from utils.deps import get_current_user
from utils.ai_logic import generate_project_milestones

router = APIRouter(prefix="/projects", tags=["Projects"])


@router.get("/generate-idea")
def generate_idea(
    title: str,
    category: str,
    current_user: User = Depends(get_current_user)
):
    """Feature 3: AI Project Architect (Local Template Engine)"""
    milestones = generate_project_milestones(category, title)
    
    # Generate a sample description
    description = f"This is a high-impact {category} project focused on {title}. " \
                  f"The project aims to solve core challenges in the {category} space " \
                  f"by leveraging modern technology and community collaboration."
    
    return {
        "title": title,
        "category": category,
        "description": description,
        "suggested_milestones": milestones
    }


# ── Schemas ────────────────────────────────────────────────────────────────────
class ProjectCreate(BaseModel):
    title: str
    description: str
    category: Optional[str] = None
    required_skills: Optional[str] = None   # comma-separated
    funding_goal: float = 0.0
    seeking_funding: bool = False
    team_size: int = 1

class ProjectUpdate(BaseModel):
    title: Optional[str] = None
    description: Optional[str] = None
    category: Optional[str] = None
    required_skills: Optional[str] = None
    funding_goal: Optional[float] = None
    seeking_funding: Optional[bool] = None
    status: Optional[str] = None
    team_size: Optional[int] = None

class ApplicationCreate(BaseModel):
    message: Optional[str] = None

class ApplicationAction(BaseModel):
    action: str  # "accept" or "reject"

class ProjectOut(BaseModel):
    id: int
    title: str
    description: str
    category: Optional[str]
    required_skills: Optional[str]
    funding_goal: float
    amount_raised: float
    status: str
    owner_id: int
    team_size: int
    seeking_funding: bool
    created_at: datetime
    owner_name: Optional[str] = None
    member_count: int = 0
    application_status: Optional[str] = None   # for the calling user

    class Config:
        from_attributes = True


def _project_out(project: Project, current_user_id: int = None, db: Session = None) -> dict:
    app_status = None
    if current_user_id and db:
        app = db.query(ProjectApplication).filter(
            ProjectApplication.project_id == project.id,
            ProjectApplication.applicant_id == current_user_id
        ).first()
        app_status = app.status if app else None

    return {
        "id": project.id,
        "title": project.title,
        "description": project.description,
        "category": project.category,
        "required_skills": project.required_skills,
        "funding_goal": project.funding_goal,
        "amount_raised": project.amount_raised,
        "status": project.status,
        "owner_id": project.owner_id,
        "team_size": project.team_size,
        "seeking_funding": project.seeking_funding,
        "created_at": project.created_at,
        "owner_name": project.owner.full_name if project.owner else None,
        "member_count": len(project.members),
        "application_status": app_status,
    }


# ── POST /projects/ ───────────────────────────────────────────────────────────
@router.post("/", status_code=201)
def create_project(
    payload: ProjectCreate,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    if current_user.role != "idea_owner":
        raise HTTPException(status_code=403, detail="Only Idea Owners can create projects.")

    project = Project(
        title           = payload.title.strip(),
        description     = payload.description.strip(),
        category        = payload.category,
        required_skills = payload.required_skills,
        funding_goal    = payload.funding_goal,
        seeking_funding = payload.seeking_funding,
        team_size       = payload.team_size,
        owner_id        = current_user.id,
    )
    db.add(project)
    db.commit()
    db.refresh(project)
    return _project_out(project, current_user.id, db)


# ── GET /projects/ — list/search ─────────────────────────────────────────────
@router.get("/")
def list_projects(
    q: Optional[str] = Query(None, description="Search title/description"),
    category: Optional[str] = Query(None),
    seeking_funding: Optional[bool] = Query(None),
    status: Optional[str] = Query(None),
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    query = db.query(Project)
    if q:
        query = query.filter(
            Project.title.ilike(f"%{q}%") | Project.description.ilike(f"%{q}%")
        )
    if category:
        query = query.filter(Project.category == category)
    if seeking_funding is not None:
        query = query.filter(Project.seeking_funding == seeking_funding)
    if status:
        query = query.filter(Project.status == status)

    projects = query.order_by(Project.created_at.desc()).all()
    return [_project_out(p, current_user.id, db) for p in projects]


# ── GET /projects/mine ────────────────────────────────────────────────────────
@router.get("/mine")
def my_projects(
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    if current_user.role == "idea_owner":
        projects = db.query(Project).filter(Project.owner_id == current_user.id).all()
    else:
        # freelancer — projects they're a member of
        memberships = db.query(ProjectMember).filter(ProjectMember.user_id == current_user.id).all()
        project_ids = [m.project_id for m in memberships]
        projects = db.query(Project).filter(Project.id.in_(project_ids)).all()

    return [_project_out(p, current_user.id, db) for p in projects]


# ── GET /projects/{id} ────────────────────────────────────────────────────────
@router.get("/{project_id}")
def get_project(
    project_id: int,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    project = db.query(Project).filter(Project.id == project_id).first()
    if not project:
        raise HTTPException(status_code=404, detail="Project not found.")
    return _project_out(project, current_user.id, db)


# ── PUT /projects/{id} ────────────────────────────────────────────────────────
@router.put("/{project_id}")
def update_project(
    project_id: int,
    payload: ProjectUpdate,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    project = db.query(Project).filter(Project.id == project_id).first()
    if not project:
        raise HTTPException(status_code=404, detail="Project not found.")
    if project.owner_id != current_user.id:
        raise HTTPException(status_code=403, detail="Not authorized.")

    for field, value in payload.dict(exclude_none=True).items():
        setattr(project, field, value)
    project.updated_at = datetime.utcnow()
    db.commit()
    db.refresh(project)
    return _project_out(project, current_user.id, db)


# ── DELETE /projects/{id} ─────────────────────────────────────────────────────
@router.delete("/{project_id}", status_code=204)
def delete_project(
    project_id: int,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    project = db.query(Project).filter(Project.id == project_id).first()
    if not project:
        raise HTTPException(status_code=404, detail="Project not found.")
    if project.owner_id != current_user.id:
        raise HTTPException(status_code=403, detail="Not authorized.")
    db.delete(project)
    db.commit()


# ── POST /projects/{id}/apply ─────────────────────────────────────────────────
@router.post("/{project_id}/apply", status_code=201)
def apply_to_project(
    project_id: int,
    payload: ApplicationCreate,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    if current_user.role != "freelancer":
        raise HTTPException(status_code=403, detail="Only Freelancers can apply to projects.")

    project = db.query(Project).filter(Project.id == project_id).first()
    if not project:
        raise HTTPException(status_code=404, detail="Project not found.")

    existing = db.query(ProjectApplication).filter(
        ProjectApplication.project_id == project_id,
        ProjectApplication.applicant_id == current_user.id
    ).first()
    if existing:
        raise HTTPException(status_code=400, detail="You have already applied to this project.")

    application = ProjectApplication(
        project_id   = project_id,
        applicant_id = current_user.id,
        message      = payload.message,
    )
    db.add(application)

    # Notify the idea owner
    notif = Notification(
        user_id    = project.owner_id,
        title      = "New Application",
        body       = f"{current_user.full_name} applied to your project '{project.title}'.",
        notif_type = "project_apply"
    )
    db.add(notif)
    db.commit()
    db.refresh(application)
    return {"id": application.id, "status": application.status, "message": "Application submitted."}


# ── GET /projects/{id}/applicants ─────────────────────────────────────────────
@router.get("/{project_id}/applicants")
def list_applicants(
    project_id: int,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    project = db.query(Project).filter(Project.id == project_id).first()
    if not project:
        raise HTTPException(status_code=404, detail="Project not found.")
    if project.owner_id != current_user.id:
        raise HTTPException(status_code=403, detail="Not authorized.")

    apps = db.query(ProjectApplication).filter(ProjectApplication.project_id == project_id).all()
    return [
        {
            "id": a.id,
            "applicant_id": a.applicant_id,
            "applicant_name": a.applicant.full_name,
            "applicant_email": a.applicant.email,
            "applicant_skills": [s.skill_name for s in a.applicant.skills] if a.applicant.skills else [],
            "message": a.message,
            "status": a.status,
            "created_at": a.created_at,
        }
        for a in apps
    ]


# ── PUT /projects/{id}/applicants/{uid} ───────────────────────────────────────
@router.put("/{project_id}/applicants/{applicant_id}")
def update_applicant(
    project_id: int,
    applicant_id: int,
    payload: ApplicationAction,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    project = db.query(Project).filter(Project.id == project_id).first()
    if not project or project.owner_id != current_user.id:
        raise HTTPException(status_code=403, detail="Not authorized.")

    app = db.query(ProjectApplication).filter(
        ProjectApplication.project_id == project_id,
        ProjectApplication.applicant_id == applicant_id
    ).first()
    if not app:
        raise HTTPException(status_code=404, detail="Application not found.")

    if payload.action == "accept":
        app.status = "accepted"
        # Add to project members
        already_member = db.query(ProjectMember).filter(
            ProjectMember.project_id == project_id,
            ProjectMember.user_id == applicant_id
        ).first()
        if not already_member:
            db.add(ProjectMember(project_id=project_id, user_id=applicant_id))
        
        # Automatically create/find a conversation for this project
        existing_conv = db.query(Conversation).filter(Conversation.project_id == project_id).first()
        if not existing_conv:
            new_conv = Conversation(title=f"Chat: {project.title}", project_id=project_id)
            db.add(new_conv)
            db.flush()
            # Add owner and new member
            db.add(ConversationParticipant(conversation_id=new_conv.id, user_id=project.owner_id))
            db.add(ConversationParticipant(conversation_id=new_conv.id, user_id=applicant_id))
        else:
            # Check if applicant is already a participant
            is_part = db.query(ConversationParticipant).filter(
                ConversationParticipant.conversation_id == existing_conv.id,
                ConversationParticipant.user_id == applicant_id
            ).first()
            if not is_part:
                db.add(ConversationParticipant(conversation_id=existing_conv.id, user_id=applicant_id))

        notif_body = f"Your application to '{project.title}' was accepted! 🎉"
    elif payload.action == "reject":
        app.status = "rejected"
        notif_body = f"Your application to '{project.title}' was not accepted this time."
    else:
        raise HTTPException(status_code=400, detail="Action must be 'accept' or 'reject'.")

    # Notify applicant
    db.add(Notification(
        user_id    = applicant_id,
        title      = "Application Update",
        body       = notif_body,
        notif_type = "project_apply"
    ))
    db.commit()
    return {"status": app.status, "message": f"Application {app.status}."}


# ── GET /projects/{id}/members ────────────────────────────────────────────────
@router.get("/{project_id}/members")
def list_project_members(
    project_id: int,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    project = db.query(Project).filter(Project.id == project_id).first()
    if not project:
        raise HTTPException(status_code=404, detail="Project not found.")
    
    # Authorized if owner or a member
    is_member = db.query(ProjectMember).filter(
        ProjectMember.project_id == project_id,
        ProjectMember.user_id == current_user.id
    ).first()
    
    if project.owner_id != current_user.id and not is_member:
        raise HTTPException(status_code=403, detail="Not authorized.")

    members = db.query(ProjectMember).filter(ProjectMember.project_id == project_id).all()
    return [
        {
            "user_id": m.user_id,
            "full_name": m.user.full_name,
            "email": m.user.email,
            "role": m.user.role,
            "joined_at": m.joined_at,
            "profile_picture": m.user.profile_picture
        }
        for m in members
    ]


# ── DELETE /projects/{id}/members/{uid} ───────────────────────────────────────
@router.delete("/{project_id}/members/{user_id}", status_code=204)
def remove_project_member(
    project_id: int,
    user_id: int,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    project = db.query(Project).filter(Project.id == project_id).first()
    if not project:
        raise HTTPException(status_code=404, detail="Project not found.")
    if project.owner_id != current_user.id:
        raise HTTPException(status_code=403, detail="Only the project owner can remove members.")

    member = db.query(ProjectMember).filter(
        ProjectMember.project_id == project_id,
        ProjectMember.user_id == user_id
    ).first()
    
    if not member:
        raise HTTPException(status_code=404, detail="Member not found.")

    db.delete(member)
    
    # Also mark application as rejected or just leave it? 
    # Usually we just remove the membership.
    
    db.add(Notification(
        user_id=user_id,
        title="Removed from Project",
        body=f"You have been removed from the project '{project.title}'.",
        notif_type="project_update"
    ))
    db.commit()
    return None
