"""
models/chat.py — Conversation and Message ORM models
"""
from datetime import datetime
from sqlalchemy import Column, Integer, String, DateTime, ForeignKey, Text, Boolean
from sqlalchemy.orm import relationship
from database import Base


class Conversation(Base):
    __tablename__ = "conversations"

    id         = Column(Integer, primary_key=True, index=True)
    project_id = Column(Integer, ForeignKey("projects.id", ondelete="SET NULL"), nullable=True)
    title      = Column(String(200), nullable=True)
    created_at = Column(DateTime, default=datetime.utcnow)

    messages     = relationship("Message", back_populates="conversation", cascade="all, delete")
    participants = relationship("ConversationParticipant", back_populates="conversation", cascade="all, delete")


class ConversationParticipant(Base):
    __tablename__ = "conversation_participants"

    id              = Column(Integer, primary_key=True, index=True)
    conversation_id = Column(Integer, ForeignKey("conversations.id", ondelete="CASCADE"), nullable=False)
    user_id         = Column(Integer, ForeignKey("users.id",          ondelete="CASCADE"), nullable=False)
    joined_at       = Column(DateTime, default=datetime.utcnow)

    conversation = relationship("Conversation", back_populates="participants")
    user         = relationship("User", foreign_keys=[user_id])


class Message(Base):
    __tablename__ = "messages"

    id              = Column(Integer, primary_key=True, index=True)
    conversation_id = Column(Integer, ForeignKey("conversations.id", ondelete="CASCADE"), nullable=False)
    sender_id       = Column(Integer, ForeignKey("users.id",          ondelete="CASCADE"), nullable=False)
    content         = Column(Text, nullable=False)
    is_read         = Column(Boolean, default=False)
    created_at      = Column(DateTime, default=datetime.utcnow)

    conversation = relationship("Conversation", back_populates="messages")
    sender       = relationship("User", foreign_keys=[sender_id])
