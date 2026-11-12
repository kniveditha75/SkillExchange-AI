package com.simats.skillexchange.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.skillexchange.data.models.*
import com.simats.skillexchange.data.repository.FundingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FundingViewModel : ViewModel() {
    private val repo = FundingRepository()

    private val _fundingProjects = MutableStateFlow<List<FundingProjectOut>>(emptyList())
    val fundingProjects: StateFlow<List<FundingProjectOut>> = _fundingProjects

    private val _portfolio = MutableStateFlow<List<InvestmentOut>>(emptyList())
    val portfolio: StateFlow<List<InvestmentOut>> = _portfolio

    private val _stats = MutableStateFlow<FundingStats?>(null)
    val stats: StateFlow<FundingStats?> = _stats

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _successMsg = MutableStateFlow<String?>(null)
    val successMsg: StateFlow<String?> = _successMsg

    fun loadFundingProjects() {
        viewModelScope.launch {
            _isLoading.value = true
            try { _fundingProjects.value = repo.fundingProjects() }
            catch (e: Exception) { _error.value = e.message }
            finally { _isLoading.value = false }
        }
    }

    fun loadPortfolio() {
        viewModelScope.launch {
            _isLoading.value = true
            try { _portfolio.value = repo.myPortfolio() }
            catch (e: Exception) { _error.value = e.message }
            finally { _isLoading.value = false }
        }
    }

    fun loadStats() {
        viewModelScope.launch {
            try { _stats.value = repo.fundingStats() }
            catch (e: Exception) { _error.value = e.message }
        }
    }

    fun invest(projectId: Int, amount: Double, note: String? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repo.invest(projectId, amount, note)
                _successMsg.value = "Invested ₹${result.amount} successfully! 🎉"
                loadFundingProjects()
                loadStats()
            }
            catch (e: Exception) { _error.value = e.message }
            finally { _isLoading.value = false }
        }
    }

    fun clearMessages() { _error.value = null; _successMsg.value = null }

    fun getProjectChatId(projectId: Int, onResult: (Int?) -> Unit) {
        viewModelScope.launch {
            try {
                val res = repo.getProjectConversation(projectId)
                onResult(res.id)
            } catch (e: Exception) {
                onResult(null)
            }
        }
    }
}

