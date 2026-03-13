package com.ambica.auto.app.data.source.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import com.ambica.auto.app.data.source.Constants.DataStore.PREFERENCE_NAME
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppPreferenceDataStore @Inject constructor(
    private val applicationContext: Context
) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
        name = PREFERENCE_NAME,
        corruptionHandler = ReplaceFileCorruptionHandler { emptyPreferences() }
    )

    suspend fun getUserTokenData(): String? {
        return applicationContext.dataStore.data.first()[Keys.USER_TOKEN_DATA]
    }

    suspend fun saveUserTokenData(token: String) {
        applicationContext.dataStore.edit { it[Keys.USER_TOKEN_DATA] = token }
    }

    suspend fun getSelectedBranchId(): String? {
        return applicationContext.dataStore.data.first()[Keys.SELECTED_BRANCH_ID]
    }

    suspend fun saveSelectedBranchId(branchId: String) {
        applicationContext.dataStore.edit { it[Keys.SELECTED_BRANCH_ID] = branchId }
    }

    suspend fun getSelectedRole(): String? {
        return applicationContext.dataStore.data.first()[Keys.SELECTED_ROLE]
    }

    suspend fun saveSelectedRole(role: String) {
        applicationContext.dataStore.edit { it[Keys.SELECTED_ROLE] = role }
    }

    suspend fun getSessionExpiryMillis(): Long? {
        return applicationContext.dataStore.data.first()[Keys.SESSION_EXPIRY_MILLIS]
    }

    suspend fun saveSessionExpiryMillis(expiryMillis: Long) {
        applicationContext.dataStore.edit { it[Keys.SESSION_EXPIRY_MILLIS] = expiryMillis }
    }

    suspend fun setHasSeenOnboarding(seen: Boolean) {
        applicationContext.dataStore.edit { it[Keys.HAS_SEEN_ONBOARDING] = seen }
    }

    suspend fun getHasSeenOnboarding(): Boolean {
        return applicationContext.dataStore.data.first()[Keys.HAS_SEEN_ONBOARDING] ?: false
    }

    suspend fun clearAll() {
        applicationContext.dataStore.edit { it.clear() }
    }

    private object Keys {
        val USER_TOKEN_DATA = stringPreferencesKey("userTokenData")
        val HAS_SEEN_ONBOARDING = booleanPreferencesKey("hasSeenOnboarding")
        val SELECTED_BRANCH_ID = stringPreferencesKey("selectedBranchId")
        val SELECTED_ROLE = stringPreferencesKey("selectedRole")
        val SESSION_EXPIRY_MILLIS = longPreferencesKey("sessionExpiryMillis")
    }
}
