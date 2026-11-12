package com.simats.skillexchange.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.skillexchange.data.models.*
import com.simats.skillexchange.data.repository.ProjectRepository
import com.simats.skillexchange.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DashboardViewModel : ViewModel() {
    private val projectRepo = ProjectRepository()
    private val userRepo    = UserRepository()

    private val _recommendations = MutableStateFlow<List<RecommendedProject>>(emptyList())
    val recommendations: StateFlow<List<RecommendedProject>> = _recommendations

    private val _myProjects = MutableStateFlow<List<ProjectOut>>(emptyList())
    val myProjects: StateFlow<List<ProjectOut>> = _myProjects

    private val _profile = MutableStateFlow<UserProfile?>(null)
    val profile: StateFlow<UserProfile?> = _profile

    private val _unreadCount = MutableStateFlow(0)
    val unreadCount: StateFlow<Int> = _unreadCount

    private val _fundingStats = MutableStateFlow<FundingStats?>(null)
    val fundingStats: StateFlow<FundingStats?> = _fundingStats

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadDashboard(role: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _profile.value    = userRepo.getMe()
                _unreadCount.value = userRepo.unreadCount().unread_count
                when (role) {
                    "freelancer" -> {
                        _recommendations.value = projectRepo.getRecommendations()
                        _myProjects.value      = projectRepo.myProjects()
                    }
                    "idea_owner" -> {
                        _myProjects.value = projectRepo.myProjects()
                    }
                    "investor" -> {
                        _fundingStats.value = com.simats.skillexchange.data.repository.FundingRepository().fundingStats()
                    }
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refreshUnreadCount() {
        viewModelScope.launch {
            try { _unreadCount.value = userRepo.unreadCount().unread_count } catch (_: Exception) {}
        }
    }
}

