package com.ambica.auto.app.ux.startup.auth.login

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class LoginUiState(
    val stateFlow: StateFlow<LoginDataState> = MutableStateFlow(LoginDataState()),
    val event: (LoginUiEvent) -> Unit = {}
)

/**
 * role: 1=Owner, 2=System Admin, 3=Staff (default)
 */
data class LoginDataState(
    val email: String = "",
    val password: String = "",
    val passwordVisible: Boolean = false,
    val role: Int = 3,
    val isLoading: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val generalError: String? = null,
)

sealed interface LoginUiEvent {
    data class OnEmailChange(val value: String) : LoginUiEvent
    data class OnPasswordChange(val value: String) : LoginUiEvent
    data class OnRoleChange(val role: Int) : LoginUiEvent
    data object OnTogglePasswordVisibility : LoginUiEvent
    data object OnLoginClick : LoginUiEvent
    data object OnClearError : LoginUiEvent
    data class NavigateNext(val context: Context) : LoginUiEvent
}
