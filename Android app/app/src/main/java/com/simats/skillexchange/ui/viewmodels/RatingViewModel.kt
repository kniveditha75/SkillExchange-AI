package com.simats.skillexchange.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.skillexchange.data.models.*
import com.simats.skillexchange.data.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RatingViewModel : ViewModel() {
    private val api = RetrofitClient.api

    private val _userRatings = MutableStateFlow<List<RatingOut>>(emptyList())
    val userRatings: StateFlow<List<RatingOut>> = _userRatings

    private val _ratingStats = MutableStateFlow<RatingStats?>(null)
    val ratingStats: StateFlow<RatingStats?> = _ratingStats

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _successMsg = MutableStateFlow<String?>(null)
    val successMsg: StateFlow<String?> = _successMsg

    fun submitRating(projectId: Int, userId: Int, score: Float, comment: String?) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                api.submitRating(RatingCreate(projectId, userId, score, comment))
                _successMsg.value = "Rating submitted successfully! ⭐"
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadUserRatings(userId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _userRatings.value = api.getUserRatings(userId)
                _ratingStats.value = api.getUserRatingStats(userId)
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearMessages() {
        _error.value = null
        _successMsg.value = null
    }
}

