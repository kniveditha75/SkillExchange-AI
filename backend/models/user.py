"""
models/user.py — SQLAlchemy ORM models (mapped to MySQL tables)
"""
from datetime import datetime
from sqlalchemy import Column, Integer, String, Boolean, DateTime, ForeignKey, Text
from sqlalchemy.orm import relationship
from database import Base


class User(Base):
    __tablename__ = "users"

    id             = Column(Integer, primary_key=True, index=True)
    full_name      = Column(String(150), nullable=False)
    email          = Column(String(255), unique=True, index=True, nullable=False)
    hashed_password = Column(String(255), nullable=False)
    role           = Column(String(50), nullable=True)   # freelancer / investor / idea_owner
    gender         = Column(String(20), nullable=True)
    phone          = Column(String(20), nullable=True)
    profile_picture = Column(String(500), nullable=True)
    is_active      = Column(Boolean, default=True)
    created_at     = Column(DateTime, default=datetime.utcnow)

    # Relationships
    skills    = relationship("UserSkill",    back_populates="user", cascade="all, delete")
    interests = relationship("UserInterest", back_populates="user", cascade="all, delete")
    goals     = relationship("UserGoal",     back_populates="user", cascade="all, delete")


class UserSkill(Base):
    __tablename__ = "user_skills"

    id         = Column(Integer, primary_key=True, index=True)
    user_id    = Column(Integer, ForeignKey("users.id", ondelete="CASCADE"), nullable=False)
    skill_name = Column(String(100), nullable=False)

    user = relationship("User", back_populates="skills")


class UserInterest(Base):
    __tablename__ = "user_interests"

    id            = Column(Integer, primary_key=True, index=True)
    user_id       = Column(Integer, ForeignKey("users.id", ondelete="CASCADE"), nullable=False)
    interest_name = Column(String(100), nullable=False)

    user = relationship("User", back_populates="interests")


class UserGoal(Base):
    __tablename__ = "user_goals"

    id        = Column(Integer, primary_key=True, index=True)
    user_id   = Column(Integer, ForeignKey("users.id", ondelete="CASCADE"), nullable=False)
    goal_name = Column(String(150), nullable=False)

    user = relationship("User", back_populates="goals")
