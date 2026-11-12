package com.simats.skillexchange.data.repository

import com.simats.skillexchange.data.models.*
import com.simats.skillexchange.data.network.RetrofitClient
import okhttp3.MultipartBody

class UserRepository {
    private val api = RetrofitClient.api

    suspend fun getMe()                                  = api.getMe()
    suspend fun listNotifications()                      = api.listNotifications()
    suspend fun unreadCount()                            = api.unreadCount()
    suspend fun markRead(id: Int)                        = api.markRead(id)
    suspend fun markAllRead()                            = api.markAllRead()
    suspend fun saveRole(role: String)                   = api.saveRole(RoleRequest(role))
    suspend fun saveSkills(skills: List<String>)         = api.saveSkills(SkillsRequest(skills))
    suspend fun saveInterests(interests: List<String>)   = api.saveInterests(InterestsRequest(interests))
    suspend fun saveGoals(goals: List<String>)           = api.saveGoals(GoalsRequest(goals))
    suspend fun updateProfile(body: UserUpdate)          = api.updateMe(body)
    suspend fun uploadAvatar(file: MultipartBody.Part)   = api.uploadAvatar(file)
}

