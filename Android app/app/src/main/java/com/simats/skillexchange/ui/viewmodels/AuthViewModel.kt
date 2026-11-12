package com.simats.skillexchange.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.skillexchange.data.local.SessionManager
import com.simats.skillexchange.data.models.*
import com.simats.skillexchange.data.repository.UserRepository
import com.simats.skillexchange.data.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val sessionManager: SessionManager) : ViewModel() {
    private val repo = UserRepository()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun signup(
        fullName: String, 
        email: String, 
        password: String, 
        confirmPassword: String,
        gender: String,
        phone: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val res = RetrofitClient.api.register(
                    RegisterRequest(fullName, email, password, confirmPassword, gender, phone)
                )
                RetrofitClient.setToken(res.access_token)
                sessionManager.saveSession(res.access_token, res.user_id, "", res.full_name, res.email)
                onSuccess()
            } catch (e: Exception) {
                _error.value = getFriendlyErrorMessage(e, "Signup failed")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun login(email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val res = RetrofitClient.api.login(LoginRequest(email, password))
                RetrofitClient.setToken(res.access_token)
                val profile = RetrofitClient.api.getMe()
                sessionManager.saveSession(res.access_token, res.user_id, profile.role ?: "", res.full_name, res.email)
                onSuccess()
            } catch (e: Exception) {
                _error.value = getFriendlyErrorMessage(e, "Login failed")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun saveRole(role: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repo.saveRole(role)
                sessionManager.updateRole(role)
                onSuccess()
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun saveOnboardingData(skills: List<String>, interests: List<String>, goals: List<String>, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                if (skills.isNotEmpty()) repo.saveSkills(skills)
                if (interests.isNotEmpty()) repo.saveInterests(interests)
                if (goals.isNotEmpty()) repo.saveGoals(goals)
                onSuccess()
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() { _error.value = null }

    private fun getFriendlyErrorMessage(e: Exception, fallback: String): String {
        if (e is retrofit2.HttpException) {
            try {
                val errorBody = e.response()?.errorBody()?.string()
                if (!errorBody.isNullOrBlank()) {
                    val json = com.google.gson.JsonParser.parseString(errorBody).asJsonObject
                    if (json.has("detail")) {
                        return json.get("detail").asString
                    }
                }
            } catch (jsonEx: Exception) {
                // Ignore and fall back
            }
            if (e.code() == 401) {
                return "Incorrect email or password."
            }
        }
        return e.message ?: fallback
    }
}

