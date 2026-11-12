package com.simats.skillexchange.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.skillexchange.data.models.*
import com.simats.skillexchange.data.repository.ChatRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ChatViewModel : ViewModel() {
    private val repo = ChatRepository()

    private val _conversations = MutableStateFlow<List<ConversationOut>>(emptyList())
    val conversations: StateFlow<List<ConversationOut>> = _conversations

    private val _messages = MutableStateFlow<List<MessageOut>>(emptyList())
    val messages: StateFlow<List<MessageOut>> = _messages

    private val _highlights = MutableStateFlow<List<String>>(emptyList())
    val highlights: StateFlow<List<String>> = _highlights

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private var pollingJob: Job? = null
    private var currentConvId: Int? = null

    // ── Load all conversations ────────────────────────────────────────────────
    fun loadConversations() {
        viewModelScope.launch {
            _isLoading.value = true
            try { _conversations.value = repo.listConversations() }
            catch (e: Exception) { _error.value = e.message }
            finally { _isLoading.value = false }
        }
    }

    // ── Open a conversation + start 1s polling ────────────────────────────────
    fun openConversation(convId: Int) {
        currentConvId = convId
        stopPolling()
        // Initial load
        viewModelScope.launch {
            try { 
                _messages.value = repo.getMessages(convId)
                loadHighlights(convId)
            }
            catch (e: Exception) { _error.value = e.message }
        }
        // Poll every 1 second for new messages
        pollingJob = viewModelScope.launch {
            while (isActive) {
                delay(1_000)
                try {
                    val lastId = _messages.value.lastOrNull()?.id
                    val newMsgs = repo.getMessages(convId, sinceId = lastId)
                    if (newMsgs.isNotEmpty()) {
                        _messages.value = _messages.value + newMsgs
                        // Refresh highlights if new messages arrive
                        loadHighlights(convId)
                    }
                } catch (_: Exception) {}
            }
        }
    }

    private fun loadHighlights(convId: Int) {
        viewModelScope.launch {
            try {
                _highlights.value = repo.getChatHighlights(convId).highlights
            } catch (_: Exception) {}
        }
    }

    // ── Send a message ────────────────────────────────────────────────────────
    fun sendMessage(content: String) {
        val convId = currentConvId ?: return
        viewModelScope.launch {
            try {
                val msg = repo.sendMessage(convId, content)
                _messages.value = _messages.value + msg
                loadHighlights(convId)
            }
            catch (e: Exception) { _error.value = e.message }
        }
    }

    // ── Create a new conversation ─────────────────────────────────────────────
    fun createConversation(title: String?, projectId: Int?, participantIds: List<Int>, onCreated: (ConversationOut) -> Unit) {
        viewModelScope.launch {
            try {
                val conv = repo.createConversation(title, projectId, participantIds)
                loadConversations()
                onCreated(conv)
            }
            catch (e: Exception) { _error.value = e.message }
        }
    }

    fun stopPolling() { pollingJob?.cancel(); pollingJob = null }

    override fun onCleared() { stopPolling(); super.onCleared() }
}

