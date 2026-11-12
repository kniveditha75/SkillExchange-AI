package com.simats.skillexchange.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.simats.skillexchange.data.network.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

class SessionManager(private val context: Context) {

    companion object {
        val KEY_TOKEN     = stringPreferencesKey("token")
        val KEY_USER_ID   = intPreferencesKey("user_id")
        val KEY_ROLE      = stringPreferencesKey("role")
        val KEY_FULL_NAME = stringPreferencesKey("full_name")
        val KEY_EMAIL     = stringPreferencesKey("email")
    }

    suspend fun saveSession(token: String, userId: Int, role: String, fullName: String, email: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_TOKEN]     = token
            prefs[KEY_USER_ID]   = userId
            prefs[KEY_ROLE]      = role
            prefs[KEY_FULL_NAME] = fullName
            prefs[KEY_EMAIL]     = email
        }
        RetrofitClient.setToken(token)
    }

    suspend fun updateRole(role: String) {
        context.dataStore.edit { it[KEY_ROLE] = role }
    }

    suspend fun clearSession() {
        context.dataStore.edit { it.clear() }
        RetrofitClient.setToken(null)
    }

    val tokenFlow: Flow<String?> = context.dataStore.data.map { it[KEY_TOKEN] }
    val roleFlow:  Flow<String?> = context.dataStore.data.map { it[KEY_ROLE] }
    val userIdFlow: Flow<Int?>   = context.dataStore.data.map { it[KEY_USER_ID] }
    val fullNameFlow: Flow<String?> = context.dataStore.data.map { it[KEY_FULL_NAME] }
    val emailFlow: Flow<String?>    = context.dataStore.data.map { it[KEY_EMAIL] }

    val sessionFlow: Flow<SessionData?> = context.dataStore.data.map { prefs ->
        val token = prefs[KEY_TOKEN] ?: return@map null
        SessionData(
            token    = token,
            userId   = prefs[KEY_USER_ID] ?: 0,
            role     = prefs[KEY_ROLE] ?: "",
            fullName = prefs[KEY_FULL_NAME] ?: "",
            email    = prefs[KEY_EMAIL] ?: ""
        )
    }
}

data class SessionData(
    val token: String,
    val userId: Int,
    val role: String,
    val fullName: String,
    val email: String
)

