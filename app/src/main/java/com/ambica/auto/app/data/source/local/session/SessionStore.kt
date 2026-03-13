package com.ambica.auto.app.data.source.local.session

import com.ambica.auto.app.data.source.remote.model.auth.UserProfileResponse
import com.ambica.auto.app.model.domain.Branch
import com.ambica.auto.app.model.domain.StaffRole
import kotlinx.coroutines.flow.StateFlow

data class Session(
    val isLoggedIn: Boolean,
    val staffName: String? = null,
    val role: StaffRole? = null,
    val branch: Branch? = null,
)

interface SessionStore {
    val sessionFlow: StateFlow<Session>

    suspend fun updateFromProfile(profile: UserProfileResponse)
    suspend fun clear()
}
