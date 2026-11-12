package com.simats.skillexchange.data.repository

import com.simats.skillexchange.data.models.*
import com.simats.skillexchange.data.network.RetrofitClient

class ChatRepository {
    private val api = RetrofitClient.api

    suspend fun listConversations() = api.listConversations()

    suspend fun createConversation(title: String?, projectId: Int?, participantIds: List<Int>) =
        api.createConversation(ConversationCreate(title, projectId, participantIds))

    suspend fun getMessages(convId: Int, sinceId: Int? = null) = api.getMessages(convId, sinceId)

    suspend fun sendMessage(convId: Int, content: String) =
        api.sendMessage(convId, MessageCreate(content))

    suspend fun getChatHighlights(convId: Int) = api.getChatHighlights(convId)
}

