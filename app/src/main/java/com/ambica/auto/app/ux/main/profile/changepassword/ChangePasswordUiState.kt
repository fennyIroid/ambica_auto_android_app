package com.ambica.auto.app.ux.main.profile.changepassword

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class ChangePasswordUiState(
    val stateFlow: StateFlow<ChangePasswordDataState> = MutableStateFlow(ChangePasswordDataState()),
    val event: (ChangePasswordUiEvent) -> Unit = {},
)

data class ChangePasswordDataState(
    val currentPassword: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val currentPasswordVisible: Boolean = false,
    val newPasswordVisible: Boolean = false,
    val confirmPasswordVisible: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,
)

sealed interface ChangePasswordUiEvent {
    data class OnCurrentPasswordChange(val value: String) : ChangePasswordUiEvent
    data class OnNewPasswordChange(val value: String) : ChangePasswordUiEvent
    data class OnConfirmPasswordChange(val value: String) : ChangePasswordUiEvent
    data object OnToggleCurrentPasswordVisibility : ChangePasswordUiEvent
    data object OnToggleNewPasswordVisibility : ChangePasswordUiEvent
    data object OnToggleConfirmPasswordVisibility : ChangePasswordUiEvent
    data object OnSubmit : ChangePasswordUiEvent
    data object OnDismissError : ChangePasswordUiEvent
    data object OnDismissSuccess : ChangePasswordUiEvent
    data object OnBack : ChangePasswordUiEvent
}
