package com.simats.skillexchange.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.skillexchange.data.models.*
import com.simats.skillexchange.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class ProfileViewModel : ViewModel() {
    private val repo = UserRepository()

    private val _profile = MutableStateFlow<UserProfile?>(null)
    val profile: StateFlow<UserProfile?> = _profile

    private val _notifications = MutableStateFlow<List<NotificationOut>>(emptyList())
    val notifications: StateFlow<List<NotificationOut>> = _notifications

    private val _unreadCount = MutableStateFlow(0)
    val unreadCount: StateFlow<Int> = _unreadCount

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            try { _profile.value = repo.getMe() }
            catch (e: Exception) { _error.value = e.message }
            finally { _isLoading.value = false }
        }
    }

    fun loadNotifications() {
        viewModelScope.launch {
            try {
                _notifications.value = repo.listNotifications()
                _unreadCount.value   = repo.unreadCount().unread_count
            }
            catch (e: Exception) { _error.value = e.message }
        }
    }

    fun markRead(id: Int) {
        viewModelScope.launch {
            try {
                repo.markRead(id)
                _notifications.value = _notifications.value.map {
                    if (it.id == id) it.copy(is_read = true) else it
                }
                _unreadCount.value = _notifications.value.count { !it.is_read }
            }
            catch (e: Exception) { _error.value = e.message }
        }
    }

    fun markAllRead() {
        viewModelScope.launch {
            try {
                repo.markAllRead()
                _notifications.value = _notifications.value.map { it.copy(is_read = true) }
                _unreadCount.value = 0
            }
            catch (e: Exception) { _error.value = e.message }
        }
    }

    fun updateProfile(body: UserUpdate, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repo.updateProfile(body)
                loadProfile()
                onSuccess()
            }
            catch (e: Exception) { _error.value = e.message }
            finally { _isLoading.value = false }
        }
    }

    fun uploadAvatar(file: MultipartBody.Part, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repo.uploadAvatar(file)
                loadProfile()
                onSuccess()
            }
            catch (e: Exception) { _error.value = e.message }
            finally { _isLoading.value = false }
        }
    }
}

