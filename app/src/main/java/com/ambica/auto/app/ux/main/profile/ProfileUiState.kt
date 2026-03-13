package com.ambica.auto.app.ux.main.profile

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class ProfileUiState(
    val stateFlow: StateFlow<ProfileDataState> = MutableStateFlow(ProfileDataState()),
    val event: (ProfileUiEvent) -> Unit = {},
)

data class ProfileDataState(
    val name: String = "Staff",
    val role: String = "Staff",
    val branchName: String = "-",
    val initials: String = "U",
    val isLoggingOut: Boolean = false,
    /** Set to true once logout cleanup is complete so the Screen can navigate. */
    val logoutComplete: Boolean = false,
)

sealed interface ProfileUiEvent {
    data object OnLogoutClick : ProfileUiEvent
    data object OnEditProfileClick : ProfileUiEvent
    data object OnChangePasswordClick : ProfileUiEvent
    data object OnLogoutNavigated : ProfileUiEvent
}
