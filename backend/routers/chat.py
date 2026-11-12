"""
routers/chat.py — Conversations and Messages (polling-based, 1s refresh)
"""
from datetime import datetime
from typing import Optional
from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from pydantic import BaseModel

from database import get_db
from models.user import User
from models.chat import Conversation, ConversationParticipant, Message
from models.funding import Investment
from models.notification import Notification
from utils.deps import get_current_user
from utils.ai_logic import extract_chat_highlights

router = APIRouter(prefix="/chat", tags=["Chat"])


# ── Schemas ────────────────────────────────────────────────────────────────────
class ConversationCreate(BaseModel):
    title: Optional[str] = None
    project_id: Optional[int] = None
    participant_ids: list[int]  # list of user IDs to add (besides self)

class MessageCreate(BaseModel):
    content: str


# ── POST /chat/conversations ──────────────────────────────────────────────────
@router.post("/conversations", status_code=201)
def create_conversation(
    payload: ConversationCreate,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    conv = Conversation(
        title      = payload.title,
        project_id = payload.project_id
    )
    db.add(conv)
    db.flush()

    # Add creator
    db.add(ConversationParticipant(conversation_id=conv.id, user_id=current_user.id))
    # Add other participants
    for uid in payload.participant_ids:
        if uid != current_user.id:
            db.add(ConversationParticipant(conversation_id=conv.id, user_id=uid))

    db.commit()
    db.refresh(conv)
    return {"id": conv.id, "title": conv.title, "project_id": conv.project_id}


# ── GET /chat/conversations ───────────────────────────────────────────────────
@router.get("/conversations")
def list_conversations(
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    participations = db.query(ConversationParticipant).filter(
        ConversationParticipant.user_id == current_user.id
    ).all()
    conv_ids = [p.conversation_id for p in participations]
    conversations = db.query(Conversation).filter(Conversation.id.in_(conv_ids)).all()

    result = []
    for conv in conversations:
        # last message
        last_msg = db.query(Message).filter(
            Message.conversation_id == conv.id
        ).order_by(Message.created_at.desc()).first()

        # unread count
        unread = db.query(Message).filter(
            Message.conversation_id == conv.id,
            Message.sender_id != current_user.id,
            Message.is_read == False
        ).count()

        result.append({
            "id": conv.id,
            "title": conv.title,
            "project_id": conv.project_id,
            "last_message": last_msg.content if last_msg else None,
            "last_message_at": last_msg.created_at if last_msg else conv.created_at,
            "unread_count": unread,
        })

    return sorted(result, key=lambda x: x["last_message_at"], reverse=True)


# ── GET /chat/project/{pid} ───────────────────────────────────────────────────
@router.get("/project/{project_id}")
def get_project_conversation(
    project_id: int,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    from models.project import Project, ProjectMember

    # Check if project exists
    project = db.query(Project).filter(Project.id == project_id).first()
    if not project:
        raise HTTPException(status_code=404, detail="Project not found.")

    # Check if user is owner or member
    is_member = db.query(ProjectMember).filter(
        ProjectMember.project_id == project_id,
        ProjectMember.user_id == current_user.id
    ).first()
    
    # Check if user is an investor
    is_investor = db.query(Investment).filter(
        Investment.project_id == project_id,
        Investment.investor_id == current_user.id
    ).first()
    
    if project.owner_id != current_user.id and not is_member and not is_investor:
        raise HTTPException(status_code=403, detail="You are not a member or investor of this project.")

    # Find conversation for this project
    conv = db.query(Conversation).filter(Conversation.project_id == project_id).first()
    
    if not conv:
        # Create it on the fly
        conv = Conversation(title=f"Chat: {project.title}", project_id=project_id)
        db.add(conv)
        db.flush()
        # Add owner
        db.add(ConversationParticipant(conversation_id=conv.id, user_id=project.owner_id))
        # Add all current members
        members = db.query(ProjectMember).filter(ProjectMember.project_id == project_id).all()
        for m in members:
            db.add(ConversationParticipant(conversation_id=conv.id, user_id=m.user_id))
        db.commit()
        db.refresh(conv)

    # Ensure current user is a participant (in case they were added to project but not chat)
    is_part = db.query(ConversationParticipant).filter(
        ConversationParticipant.conversation_id == conv.id,
        ConversationParticipant.user_id == current_user.id
    ).first()
    if not is_part:
        db.add(ConversationParticipant(conversation_id=conv.id, user_id=current_user.id))
        db.commit()

    return {"id": conv.id}


# ── GET /chat/conversations/{id}/messages ─────────────────────────────────────
@router.get("/conversations/{conv_id}/messages")
def get_messages(
    conv_id: int,
    since_id: Optional[int] = None,   # for polling: only return messages after this ID
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    # Verify participation
    participation = db.query(ConversationParticipant).filter(
        ConversationParticipant.conversation_id == conv_id,
        ConversationParticipant.user_id == current_user.id
    ).first()
    if not participation:
        raise HTTPException(status_code=403, detail="Not a participant.")

    query = db.query(Message).filter(Message.conversation_id == conv_id)
    if since_id:
        query = query.filter(Message.id > since_id)

    messages = query.order_by(Message.created_at.asc()).all()

    # Mark all as read
    db.query(Message).filter(
        Message.conversation_id == conv_id,
        Message.sender_id != current_user.id,
        Message.is_read == False
    ).update({"is_read": True})
    db.commit()

    return [
        {
            "id": m.id,
            "conversation_id": m.conversation_id,
            "sender_id": m.sender_id,
            "sender_name": m.sender.full_name,
            "content": m.content,
            "is_read": m.is_read,
            "created_at": m.created_at,
            "is_mine": m.sender_id == current_user.id,
        }
        for m in messages
    ]


# ── GET /chat/conversations/{id}/highlights ───────────────────────────────────
@router.get("/conversations/{conv_id}/highlights")
def get_chat_highlights(
    conv_id: int,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    """Feature 6: AI Chat Highlights (Local Extraction)"""
    # Verify participation
    participation = db.query(ConversationParticipant).filter(
        ConversationParticipant.conversation_id == conv_id,
        ConversationParticipant.user_id == current_user.id
    ).first()
    if not participation:
        raise HTTPException(status_code=403, detail="Not a participant.")

    messages = db.query(Message).filter(Message.conversation_id == conv_id).order_by(Message.created_at.desc()).limit(30).all()
    # Reverse to get chronological order for the extractor
    msg_list = [{"content": m.content, "sender_name": m.sender.full_name} for m in reversed(messages)]
    
    return {"highlights": extract_chat_highlights(msg_list)}


# ── POST /chat/conversations/{id}/messages ────────────────────────────────────
@router.post("/conversations/{conv_id}/messages", status_code=201)
def send_message(
    conv_id: int,
    payload: MessageCreate,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    participation = db.query(ConversationParticipant).filter(
        ConversationParticipant.conversation_id == conv_id,
        ConversationParticipant.user_id == current_user.id
    ).first()
    if not participation:
        raise HTTPException(status_code=403, detail="Not a participant.")

    msg = Message(
        conversation_id = conv_id,
        sender_id       = current_user.id,
        content         = payload.content.strip(),
    )
    db.add(msg)

    # Notify other participants
    other_participants = db.query(ConversationParticipant).filter(
        ConversationParticipant.conversation_id == conv_id,
        ConversationParticipant.user_id != current_user.id
    ).all()
    for p in other_participants:
        db.add(Notification(
            user_id    = p.user_id,
            title      = f"New message from {current_user.full_name}",
            body       = payload.content[:100],
            notif_type = "message"
        ))

    db.commit()
    db.refresh(msg)
    return {
        "id": msg.id,
        "conversation_id": msg.conversation_id,
        "sender_id": msg.sender_id,
        "sender_name": current_user.full_name,
        "content": msg.content,
        "is_read": msg.is_read,
        "created_at": msg.created_at,
        "is_mine": True,
    }
