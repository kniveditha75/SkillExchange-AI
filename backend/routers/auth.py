"""
routers/auth.py — Register and Login endpoints
"""
from fastapi import APIRouter, Depends, HTTPException, status
from fastapi.security import OAuth2PasswordRequestForm
from sqlalchemy.orm import Session

from database import get_db
from models.user import User
from schemas.user import RegisterRequest, LoginRequest, TokenResponse, MessageResponse
from utils.auth import hash_password, verify_password, create_access_token

router = APIRouter(prefix="/auth", tags=["Authentication"])


# ── POST /auth/register ────────────────────────────────────────────────────────
@router.post("/register", response_model=TokenResponse, status_code=201)
def register(payload: RegisterRequest, db: Session = Depends(get_db)):
    """
    Create a new user account.
    - Validates email is unique
    - Hashes the password with bcrypt
    - Returns a JWT access token immediately (no separate login needed)
    """
    # Check if email already exists
    existing = db.query(User).filter(User.email == payload.email).first()
    if existing:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="An account with this email already exists."
        )

    # Check if passwords match
    if payload.password != payload.confirm_password:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Passwords do not match."
        )

    # Create user
    new_user = User(
        full_name       = payload.full_name.strip(),
        email           = payload.email.lower().strip(),
        hashed_password = hash_password(payload.password),
        gender          = payload.gender,
        phone           = payload.phone,
    )
    db.add(new_user)
    db.commit()
    db.refresh(new_user)

    # Return token so the app can proceed to onboarding immediately
    token = create_access_token({"sub": str(new_user.id)})
    return TokenResponse(
        access_token = token,
        user_id      = new_user.id,
        full_name    = new_user.full_name,
        email        = new_user.email,
    )


# ── POST /auth/login ───────────────────────────────────────────────────────────
@router.post("/login", response_model=TokenResponse)
def login(payload: LoginRequest, db: Session = Depends(get_db)):
    """
    Login with email and password.
    Returns a JWT access token on success.
    """
    user = db.query(User).filter(User.email == payload.email.lower().strip()).first()

    if not user or not verify_password(payload.password, user.hashed_password):
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Incorrect email or password."
        )

    if not user.is_active:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="This account has been deactivated."
        )

    token = create_access_token({"sub": str(user.id)})
    return TokenResponse(
        access_token = token,
        user_id      = user.id,
        full_name    = user.full_name,
        email        = user.email,
    )


# ── POST /auth/login-form — OAuth2 compatible (for Swagger UI "Authorize") ─────
@router.post("/login-form", response_model=TokenResponse, include_in_schema=False)
def login_form(
    form_data: OAuth2PasswordRequestForm = Depends(),
    db: Session = Depends(get_db)
):
    user = db.query(User).filter(User.email == form_data.username.lower()).first()
    if not user or not verify_password(form_data.password, user.hashed_password):
        raise HTTPException(status_code=401, detail="Incorrect email or password.")

    token = create_access_token({"sub": str(user.id)})
    return TokenResponse(
        access_token = token,
        user_id      = user.id,
        full_name    = user.full_name,
        email        = user.email,
    )
