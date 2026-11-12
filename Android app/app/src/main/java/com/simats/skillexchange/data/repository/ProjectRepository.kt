package com.simats.skillexchange.data.repository

import com.simats.skillexchange.data.models.*
import com.simats.skillexchange.data.network.RetrofitClient

class ProjectRepository {
    private val api = RetrofitClient.api

    suspend fun listProjects(q: String? = null, category: String? = null, seekingFunding: Boolean? = null, status: String? = null) =
        api.listProjects(q, category, seekingFunding, status)

    suspend fun myProjects() = api.myProjects()

    suspend fun getProject(id: Int) = api.getProject(id)

    suspend fun createProject(body: ProjectCreate) = api.createProject(body)

    suspend fun updateProject(id: Int, body: ProjectUpdate) = api.updateProject(id, body)

    suspend fun deleteProject(id: Int) = api.deleteProject(id)

    suspend fun applyToProject(id: Int, message: String?) =
        api.applyToProject(id, ApplicationRequest(message))

    suspend fun listApplicants(id: Int) = api.listApplicants(id)

    suspend fun acceptApplicant(projectId: Int, userId: Int) =
        api.updateApplicant(projectId, userId, ApplicationAction("accept"))

    suspend fun rejectApplicant(projectId: Int, userId: Int) =
        api.updateApplicant(projectId, userId, ApplicationAction("reject"))

    suspend fun getRecommendations() = api.getRecommendations()

    suspend fun getSkillAnalysis() = api.getSkillAnalysis()

    suspend fun getMatchScore(projectId: Int) = api.getMatchScore(projectId)

    suspend fun listProjectMembers(id: Int) = api.listProjectMembers(id)

    suspend fun removeProjectMember(id: Int, userId: Int) = api.removeProjectMember(id, userId)

    suspend fun getProjectConversation(projectId: Int) = api.getProjectConversation(projectId)

    suspend fun generateIdea(title: String, category: String) = api.generateIdea(title, category)

    suspend fun getProjectAnalysis(projectId: Int) = api.getProjectAnalysis(projectId)
}

