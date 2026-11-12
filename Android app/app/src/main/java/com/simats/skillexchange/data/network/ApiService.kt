package com.simats.skillexchange.data.network

import com.simats.skillexchange.data.models.*
import okhttp3.MultipartBody
import retrofit2.http.*

interface ApiService {

    // ── Auth ──────────────────────────────────────────────────────────────────
    @POST("auth/register")
    suspend fun register(@Body body: RegisterRequest): TokenResponse

    @POST("auth/login")
    suspend fun login(@Body body: LoginRequest): TokenResponse

    // ── Onboarding ────────────────────────────────────────────────────────────
    @POST("onboarding/role")
    suspend fun saveRole(@Body body: RoleRequest): MessageResponse

    @POST("onboarding/skills")
    suspend fun saveSkills(@Body body: SkillsRequest): MessageResponse

    @POST("onboarding/interests")
    suspend fun saveInterests(@Body body: InterestsRequest): MessageResponse

    @POST("onboarding/goals")
    suspend fun saveGoals(@Body body: GoalsRequest): MessageResponse

    // ── Users ─────────────────────────────────────────────────────────────────
    @GET("users/me")
    suspend fun getMe(): UserProfile

    @PUT("users/me")
    suspend fun updateMe(@Body body: UserUpdate): MessageResponse

    @Multipart
    @POST("users/avatar")
    suspend fun uploadAvatar(@Part file: MultipartBody.Part): MessageResponse

    // ── Projects ──────────────────────────────────────────────────────────────
    @GET("projects/")
    suspend fun listProjects(
        @Query("q") q: String? = null,
        @Query("category") category: String? = null,
        @Query("seeking_funding") seekingFunding: Boolean? = null,
        @Query("status") status: String? = null
    ): List<ProjectOut>

    @GET("projects/mine")
    suspend fun myProjects(): List<ProjectOut>

    @GET("projects/{id}")
    suspend fun getProject(@Path("id") id: Int): ProjectOut

    @POST("projects/")
    suspend fun createProject(@Body body: ProjectCreate): ProjectOut

    @PUT("projects/{id}")
    suspend fun updateProject(@Path("id") id: Int, @Body body: ProjectUpdate): ProjectOut

    @DELETE("projects/{id}")
    suspend fun deleteProject(@Path("id") id: Int)

    @POST("projects/{id}/apply")
    suspend fun applyToProject(@Path("id") id: Int, @Body body: ApplicationRequest): ApplyResponse

    @GET("projects/{id}/applicants")
    suspend fun listApplicants(@Path("id") id: Int): List<ApplicantOut>

    @PUT("projects/{id}/applicants/{uid}")
    suspend fun updateApplicant(
        @Path("id") id: Int,
        @Path("uid") uid: Int,
        @Body body: ApplicationAction
    ): MessageResponse

    @GET("projects/{id}/members")
    suspend fun listProjectMembers(@Path("id") id: Int): List<ProjectMemberOut>

    @DELETE("projects/{id}/members/{uid}")
    suspend fun removeProjectMember(@Path("id") id: Int, @Path("uid") uid: Int)

    @GET("projects/generate-idea")
    suspend fun generateIdea(
        @Query("title") title: String,
        @Query("category") category: String
    ): GeneratedProject

    // ── Chat ──────────────────────────────────────────────────────────────────
    @GET("chat/conversations")
    suspend fun listConversations(): List<ConversationOut>

    @POST("chat/conversations")
    suspend fun createConversation(@Body body: ConversationCreate): ConversationOut

    @GET("chat/project/{id}")
    suspend fun getProjectConversation(@Path("id") id: Int): IdResponse

    @GET("chat/conversations/{id}/messages")
    suspend fun getMessages(
        @Path("id") id: Int,
        @Query("since_id") sinceId: Int? = null
    ): List<MessageOut>

    @POST("chat/conversations/{id}/messages")
    suspend fun sendMessage(@Path("id") id: Int, @Body body: MessageCreate): MessageOut

    @GET("chat/conversations/{id}/highlights")
    suspend fun getChatHighlights(@Path("id") id: Int): ChatHighlights

    // ── Funding ───────────────────────────────────────────────────────────────
    @GET("funding/projects")
    suspend fun fundingProjects(): List<FundingProjectOut>

    @POST("funding/{id}/invest")
    suspend fun invest(@Path("id") id: Int, @Body body: InvestRequest): InvestResponse

    @GET("funding/portfolio")
    suspend fun myPortfolio(): List<InvestmentOut>

    @GET("funding/stats")
    suspend fun fundingStats(): FundingStats

    // ── AI ────────────────────────────────────────────────────────────────────
    @GET("ai/recommendations")
    suspend fun getRecommendations(): List<RecommendedProject>

    @GET("ai/skill-analysis")
    suspend fun getSkillAnalysis(): SkillAnalysis

    @GET("ai/match-score/{id}")
    suspend fun getMatchScore(@Path("id") id: Int): MatchScore

    @GET("ai/project-analysis/{id}")
    suspend fun getProjectAnalysis(@Path("id") id: Int): ProjectHealth

    // ── Notifications ─────────────────────────────────────────────────────────
    @GET("notifications/")
    suspend fun listNotifications(): List<NotificationOut>

    @GET("notifications/unread-count")
    suspend fun unreadCount(): UnreadCount

    @PUT("notifications/{id}/read")
    suspend fun markRead(@Path("id") id: Int): MessageResponse

    @PUT("notifications/read-all")
    suspend fun markAllRead(): MessageResponse

    // ── Ratings ───────────────────────────────────────────────────────────────
    @POST("ratings/")
    suspend fun submitRating(@Body body: RatingCreate): RatingOut

    @GET("ratings/user/{id}")
    suspend fun getUserRatings(@Path("id") id: Int): List<RatingOut>

    @GET("ratings/stats/{id}")
    suspend fun getUserRatingStats(@Path("id") id: Int): RatingStats
}

