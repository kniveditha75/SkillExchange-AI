"""
utils/ai_logic.py — Local AI algorithms (no API keys required)
"""
import re
from datetime import datetime

def generate_project_milestones(category: str, title: str) -> list:
    """Feature 3: AI Project Architect (Template-based Generation)"""
    templates = {
        "HealthTech": [
            "Market Research & Patient Need Analysis",
            "Regulatory Compliance & HIPAA Planning",
            "MVP Development (Core Health Features)",
            "Clinical Beta Testing",
            "Full Launch & Data Security Audit"
        ],
        "EdTech": [
            "Curriculum & Content Strategy",
            "Platform UI/UX Design for Learning",
            "Beta Launch with 100 Students",
            "Tutor/Teacher Integration",
            "Global Scaling & Certification"
        ],
        "FinTech": [
            "Financial Regulation & License Mapping",
            "Security & Encryption Architecture",
            "Transaction Pipeline Development",
            "User Trust & Verification Launch",
            "Partnership Expansion"
        ],
        "AI/ML": [
            "Dataset Collection & Cleaning",
            "Model Architecture Selection",
            "Training & Accuracy Optimization",
            "API Integration & Frontend Development",
            "Live Inference Scaling"
        ],
        "E-commerce": [
            "Supplier & Inventory Strategy",
            "Shopping Engine & Payment Logic",
            "Mobile App Beta",
            "Logistics & Shipping Partnership",
            "Grand Opening & Marketing Campaign"
        ]
    }
    
    # Default milestones if category is not found
    default = [
        "Concept Refinement & Research",
        "Technical Architecture Design",
        "MVP Prototype Development",
        "User Feedback & Iteration",
        "Official Launch & Scaling"
    ]
    
    return templates.get(category, default)

def calculate_project_health(project_data: dict) -> dict:
    """Feature 4: AI Investor Analytics (Scoring Algorithm)"""
    score = 70.0  # Base score
    insights = []
    
    # 1. Funding Progress
    goal = project_data.get("funding_goal", 0)
    raised = project_data.get("amount_raised", 0)
    if goal > 0:
        pct = (raised / goal) * 100
        if pct >= 50:
            score += 15
            insights.append("High funding momentum.")
        elif pct > 0:
            score += 5
            insights.append("Initial funding secured.")
        else:
            score -= 10
            insights.append("No funding yet; high risk.")
            
    # 2. Team Size
    team_size = project_data.get("team_size", 1)
    if team_size >= 5:
        score += 10
        insights.append("Solid team established.")
    elif team_size >= 2:
        score += 5
        insights.append("Co-founding team present.")
    else:
        score -= 5
        insights.append("Solo founder; execution risk.")
        
    # 3. Project Maturity
    created_at = project_data.get("created_at")
    if created_at:
        # Check if project is > 30 days old but still active
        days_old = (datetime.utcnow() - created_at).days
        if days_old > 30 and raised > 0:
            score += 5
            insights.append("Proven project longevity.")

    # Clamp score
    final_score = max(0.0, min(100.0, score))
    
    risk_level = "Low" if final_score >= 80 else "Medium" if final_score >= 50 else "High"
    
    return {
        "health_score": final_score,
        "risk_level": risk_level,
        "insights": insights
    }

def extract_chat_highlights(messages: list) -> list:
    """Feature 6: AI Chat Highlights (Simple Extraction)"""
    highlights = []
    
    # Keywords indicating action items
    action_keywords = ["todo", "fix", "deadline", "submit", "call", "meeting", "update"]
    
    for msg in messages[-20:]:  # Scan last 20 messages
        content = msg.get("content", "").lower()
        
        # Highlight questions (often needing answers)
        if "?" in content:
            highlights.append(f"Question: {msg['content']}")
        
        # Highlight action items
        for kw in action_keywords:
            if kw in content:
                highlights.append(f"Action: {msg['content']}")
                break
                
        if len(highlights) >= 5:
            break
            
    return highlights
