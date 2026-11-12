"""
models/rating.py — Performance ratings for team members
"""
from datetime import datetime
from sqlalchemy import Column, Integer, String, DateTime, ForeignKey, Text, Float
from sqlalchemy.orm import relationship
from database import Base

class Rating(Base):
    __tablename__ = "ratings"

    id         = Column(Integer, primary_key=True, index=True)
    project_id = Column(Integer, ForeignKey("projects.id", ondelete="CASCADE"), nullable=False)
    rater_id   = Column(Integer, ForeignKey("users.id",    ondelete="CASCADE"), nullable=False)
    user_id    = Column(Integer, ForeignKey("users.id",    ondelete="CASCADE"), nullable=False) # The person being rated
    score      = Column(Float, nullable=False) # 1.0 to 5.0
    comment    = Column(Text, nullable=True)
    created_at = Column(DateTime, default=datetime.utcnow)

    project = relationship("Project")
    rater   = relationship("User", foreign_keys=[rater_id])
    user    = relationship("User", foreign_keys=[user_id])
