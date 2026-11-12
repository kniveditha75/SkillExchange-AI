package com.simats.skillexchange.data.models

import com.google.gson.annotations.SerializedName

// ── Auth ──────────────────────────────────────────────────────────────────────
data class RegisterRequest(
    val full_name: String, 
    val email: String, 
    val password: String,
    val confirm_password: String,
    val gender: String? = null,
    val phone: String? = null
)
data class LoginRequest(val email: String, val password: String)
data class RoleRequest(val role: String)
data class SkillsRequest(val skills: List<String>)
data class InterestsRequest(val interests: List<String>)
data class GoalsRequest(val goals: List<String>)

data class TokenResponse(
    val access_token: String,
    val token_type: String,
    val user_id: Int,
    val full_name: String,
    val email: String
)

data class MessageResponse(val message: String, val success: Boolean = true)

// ── User ──────────────────────────────────────────────────────────────────────
data class UserProfile(
    val id: Int,
    val full_name: String,
    val email: String,
    val role: String?,
    val gender: String?,
    val phone: String?,
    val is_active: Boolean,
    val created_at: String,
    val skills: List<String>,
    val interests: List<String>,
    val goals: List<String>,
    val profile_picture: String? = null
)

data class UserUpdate(
    val full_name: String? = null,
    val gender: String? = null,
    val phone: String? = null,
    val profile_picture: String? = null
)

// ── Projects ──────────────────────────────────────────────────────────────────
data class ProjectOut(
    val id: Int,
    val title: String,
    val description: String,
    val category: String?,
    val required_skills: String?,
    val funding_goal: Double,
    val amount_raised: Double,
    val status: String,
    val owner_id: Int,
    val team_size: Int,
    val seeking_funding: Boolean,
    val created_at: String,
    val owner_name: String?,
    val member_count: Int,
    val application_status: String?
)

data class ProjectMemberOut(
    val user_id: Int,
    val full_name: String,
    val email: String,
    val role: String,
    val joined_at: String,
    val profile_picture: String? = null
)

data class ProjectCreate(
    val title: String,
    val description: String,
    val category: String? = null,
    val required_skills: String? = null,
    val funding_goal: Double = 0.0,
    val seeking_funding: Boolean = false,
    val team_size: Int = 1
)

data class ProjectUpdate(
    val title: String? = null,
    val description: String? = null,
    val category: String? = null,
    val required_skills: String? = null,
    val funding_goal: Double? = null,
    val seeking_funding: Boolean? = null,
    val status: String? = null,
    val team_size: Int? = null
)

data class ApplicationRequest(val message: String? = null)
data class ApplicationAction(val action: String)  // "accept" or "reject"

data class ApplyResponse(val id: Int, val status: String, val message: String)

data class ApplicantOut(
    val id: Int,
    val applicant_id: Int,
    val applicant_name: String,
    val applicant_email: String,
    val applicant_skills: List<String> = emptyList(),
    val message: String?,
    val status: String,
    val created_at: String
)

data class GeneratedProject(
    val title: String,
    val category: String,
    val description: String,
    val suggested_milestones: List<String>
)

// ── Chat ──────────────────────────────────────────────────────────────────────
data class ConversationOut(
    val id: Int,
    val title: String?,
    val project_id: Int?,
    val last_message: String?,
    val last_message_at: String?,
    val unread_count: Int
)

data class ConversationCreate(
    val title: String? = null,
    val project_id: Int? = null,
    val participant_ids: List<Int>
)

data class MessageOut(
    val id: Int,
    val conversation_id: Int,
    val sender_id: Int,
    val sender_name: String,
    val content: String,
    val is_read: Boolean,
    val created_at: String,
    val is_mine: Boolean
)

data class MessageCreate(val content: String)

data class ChatHighlights(val highlights: List<String>)

// ── Funding ───────────────────────────────────────────────────────────────────
data class FundingProjectOut(
    val id: Int,
    val title: String,
    val description: String,
    val category: String?,
    val funding_goal: Double,
    val amount_raised: Double,
    val pct_funded: Double,
    val owner_name: String?,
    val team_size: Int,
    val created_at: String
)

data class InvestRequest(val amount: Double, val note: String? = null)

data class InvestResponse(
    val id: Int,
    val project_id: Int,
    val amount: Double,
    val new_total: Double
)

data class InvestmentOut(
    val id: Int,
    val project_id: Int,
    val project_title: String?,
    val project_status: String?,
    val amount: Double,
    val note: String?,
    val created_at: String,
    val pct_funded: Double
)

data class FundingStats(
    val total_invested: Double,
    val portfolio_value: Double,
    val roi_pct: Double,
    val active_investments: Int,
    val completed_investments: Int,
    val total_investments: Int
)

// ── AI ────────────────────────────────────────────────────────────────────────
data class RecommendedProject(
    val id: Int,
    val title: String,
    val description: String,
    val category: String?,
    val required_skills: String?,
    val funding_goal: Double,
    val amount_raised: Double,
    val seeking_funding: Boolean,
    val team_size: Int,
    val owner_name: String?,
    val match_score: Double,
    val created_at: String
)

data class SkillStat(val skill: String, val demand_count: Int, val demand_pct: Double, val level: String)
data class SuggestedSkill(val skill: String, val demand_count: Int)

data class SkillAnalysis(
    val user_skills: List<SkillStat>,
    val suggested_skills: List<SuggestedSkill>,
    val overall_score: Double,
    val total_projects_analyzed: Int
)

data class MatchScore(val project_id: Int, val match_score: Double, val user_skill_count: Int, val project_skill_count: Int)

data class ProjectHealth(
    val health_score: Double,
    val risk_level: String,
    val insights: List<String>
)

// ── Notifications ─────────────────────────────────────────────────────────────
data class NotificationOut(
    val id: Int,
    val title: String,
    val body: String?,
    val notif_type: String?,
    val is_read: Boolean,
    val created_at: String
)

data class UnreadCount(val unread_count: Int)

data class IdResponse(val id: Int)

// ── Ratings ───────────────────────────────────────────────────────────────────
data class RatingCreate(
    val project_id: Int,
    val user_id: Int,
    val score: Float,
    val comment: String? = null
)

data class RatingOut(
    val id: Int,
    val project_id: Int,
    val rater_id: Int,
    val user_id: Int,
    val score: Float,
    val comment: String?,
    val created_at: String,
    val project_title: String? = null,
    val rater_name: String? = null
)

data class RatingStats(
    val average_rating: Double,
    val total_ratings: Int
)

