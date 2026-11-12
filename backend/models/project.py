"""
models/project.py — Project, ProjectApplication, ProjectMember ORM models
"""
from datetime import datetime
from sqlalchemy import Column, Integer, String, Boolean, DateTime, ForeignKey, Text, Float
from sqlalchemy.orm import relationship
from database import Base


class Project(Base):
    __tablename__ = "projects"

    id              = Column(Integer, primary_key=True, index=True)
    title           = Column(String(200), nullable=False)
    description     = Column(Text, nullable=False)
    category        = Column(String(100), nullable=True)        # HealthTech, EdTech, etc.
    required_skills = Column(Text, nullable=True)               # comma-separated
    funding_goal    = Column(Float, default=0.0)
    amount_raised   = Column(Float, default=0.0)
    status          = Column(String(50), default="open")        # open / in_progress / completed / closed
    owner_id        = Column(Integer, ForeignKey("users.id", ondelete="CASCADE"), nullable=False)
    team_size       = Column(Integer, default=1)
    seeking_funding = Column(Boolean, default=False)
    created_at      = Column(DateTime, default=datetime.utcnow)
    updated_at      = Column(DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)

    # Relationships
    owner        = relationship("User", foreign_keys=[owner_id])
    applications = relationship("ProjectApplication", back_populates="project", cascade="all, delete")
    members      = relationship("ProjectMember", back_populates="project", cascade="all, delete")
    investments  = relationship("Investment", back_populates="project", cascade="all, delete")


class ProjectApplication(Base):
    __tablename__ = "project_applications"

    id           = Column(Integer, primary_key=True, index=True)
    project_id   = Column(Integer, ForeignKey("projects.id", ondelete="CASCADE"), nullable=False)
    applicant_id = Column(Integer, ForeignKey("users.id",    ondelete="CASCADE"), nullable=False)
    message      = Column(Text, nullable=True)
    status       = Column(String(30), default="pending")  # pending / accepted / rejected
    created_at   = Column(DateTime, default=datetime.utcnow)

    project   = relationship("Project", back_populates="applications")
    applicant = relationship("User", foreign_keys=[applicant_id])


class ProjectMember(Base):
    __tablename__ = "project_members"

    id         = Column(Integer, primary_key=True, index=True)
    project_id = Column(Integer, ForeignKey("projects.id", ondelete="CASCADE"), nullable=False)
    user_id    = Column(Integer, ForeignKey("users.id",    ondelete="CASCADE"), nullable=False)
    role       = Column(String(100), nullable=True)
    joined_at  = Column(DateTime, default=datetime.utcnow)

    project = relationship("Project", back_populates="members")
    user    = relationship("User", foreign_keys=[user_id])
