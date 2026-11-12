package com.simats.skillexchange.data.repository

import com.simats.skillexchange.data.models.*
import com.simats.skillexchange.data.network.RetrofitClient

class FundingRepository {
    private val api = RetrofitClient.api

    suspend fun fundingProjects()              = api.fundingProjects()
    suspend fun invest(id: Int, amount: Double, note: String? = null) =
        api.invest(id, InvestRequest(amount, note))
    suspend fun myPortfolio()                  = api.myPortfolio()
    suspend fun fundingStats()                 = api.fundingStats()
    suspend fun getProjectConversation(id: Int) = api.getProjectConversation(id)
}

