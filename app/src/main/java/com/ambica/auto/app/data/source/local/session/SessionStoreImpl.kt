package com.ambica.auto.app.data.source.local.session

import com.ambica.auto.app.data.source.remote.model.auth.UserProfileResponse
import com.ambica.auto.app.model.domain.Branch
import com.ambica.auto.app.model.domain.StaffRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionStoreImpl @Inject constructor() : SessionStore {

    private val _sessionFlow = MutableStateFlow(Session(isLoggedIn = false))
    override val sessionFlow: StateFlow<Session> = _sessionFlow.asStateFlow()

    override suspend fun updateFromProfile(profile: UserProfileResponse) {
        val displayName = listOfNotNull(profile.firstName?.trim(), profile.lastName?.trim())
            .filter { it.isNotBlank() }
            .joinToString(" ")
            .ifBlank { profile.email.orEmpty().ifBlank { "Staff" } }

        val role = when (profile.role) {
            1 -> StaffRole.OWNER
            2 -> StaffRole.SYSTEM_ADMIN
            3 -> when (profile.staffType) {
                1 -> StaffRole.SECURITY
                2 -> StaffRole.SUPERVISOR
                3 -> StaffRole.MANAGER
                4 -> StaffRole.BILLING
                5 -> StaffRole.SPARE_PARTS_TEAM
                else -> StaffRole.MANAGER
            }
            else -> null
        }

        val branchName = profile.branch?.trim().orEmpty()
        val branch = if (branchName.isNotBlank()) {
            Branch(
                id = branchName.lowercase(Locale.getDefault()).replace(" ", "_"),
                name = branchName,
                city = "",
            )
        } else {
            null
        }

        _sessionFlow.value = Session(
            isLoggedIn = true,
            staffName = displayName,
            role = role,
            branch = branch,
        )
    }

    override suspend fun clear() {
        _sessionFlow.value = Session(isLoggedIn = false)
    }
}
