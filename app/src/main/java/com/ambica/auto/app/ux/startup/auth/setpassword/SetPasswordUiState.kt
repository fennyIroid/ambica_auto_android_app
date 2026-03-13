package com.ambica.auto.app.ux.startup.auth.setpassword

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class SetPasswordUiState(
    val stateFlow: StateFlow<SetPasswordDataState> = MutableStateFlow(SetPasswordDataState()),
    val event: (SetPasswordUiEvent) -> Unit = {},
)

data class SetPasswordDataState(
    val newPassword: String = "",
    val confirmPassword: String = "",
    val newPasswordVisible: Boolean = false,
    val confirmPasswordVisible: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,
)

sealed interface SetPasswordUiEvent {
    data class OnNewPasswordChange(val value: String) : SetPasswordUiEvent
    data class OnConfirmPasswordChange(val value: String) : SetPasswordUiEvent
    data object OnToggleNewPasswordVisibility : SetPasswordUiEvent
    data object OnToggleConfirmPasswordVisibility : SetPasswordUiEvent
    data object OnSubmit : SetPasswordUiEvent
    data object OnSkip : SetPasswordUiEvent
    data object OnDismissError : SetPasswordUiEvent
    data object OnDismissSuccess : SetPasswordUiEvent
}
