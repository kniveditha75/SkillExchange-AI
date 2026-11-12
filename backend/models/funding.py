"""
models/funding.py — Investment ORM model
"""
from datetime import datetime
from sqlalchemy import Column, Integer, DateTime, ForeignKey, Float, String
from sqlalchemy.orm import relationship
from database import Base


class Investment(Base):
    __tablename__ = "investments"

    id          = Column(Integer, primary_key=True, index=True)
    project_id  = Column(Integer, ForeignKey("projects.id", ondelete="CASCADE"), nullable=False)
    investor_id = Column(Integer, ForeignKey("users.id",    ondelete="CASCADE"), nullable=False)
    amount      = Column(Float, nullable=False)
    note        = Column(String(300), nullable=True)
    created_at  = Column(DateTime, default=datetime.utcnow)

    project  = relationship("Project", back_populates="investments")
    investor = relationship("User", foreign_keys=[investor_id])
