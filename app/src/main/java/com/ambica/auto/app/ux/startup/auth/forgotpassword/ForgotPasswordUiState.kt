package com.ambica.auto.app.ux.startup.auth.forgotpassword

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

enum class ForgotPasswordStep { ENTER_EMAIL, VERIFY_OTP, RESET_PASSWORD }

data class ForgotPasswordUiState(
    val stateFlow: StateFlow<ForgotPasswordDataState> = MutableStateFlow(ForgotPasswordDataState()),
    val event: (ForgotPasswordUiEvent) -> Unit = {},
)

data class ForgotPasswordDataState(
    val step: ForgotPasswordStep = ForgotPasswordStep.ENTER_EMAIL,

    // Step 1 — Enter Email
    val email: String = "",
    val emailError: String? = null,

    // Step 2 — Verify OTP
    val otp: String = "",
    val otpError: String? = null,
    val resendCooldownSeconds: Int = 0,

    // Step 3 — Reset Password
    val newPassword: String = "",
    val confirmPassword: String = "",
    val newPasswordError: String? = null,
    val newPasswordVisible: Boolean = false,
    val confirmPasswordVisible: Boolean = false,

    // Common
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,
    /** Set to true when the flow is complete or cancelled so LoginScreen can close the sheet. */
    val isDismissed: Boolean = false,
)

sealed interface ForgotPasswordUiEvent {
    // Step 1
    data class OnEmailChange(val value: String) : ForgotPasswordUiEvent
    data object OnSendCode : ForgotPasswordUiEvent

    // Step 2
    data class OnOtpChange(val otp: String) : ForgotPasswordUiEvent
    data object OnVerify : ForgotPasswordUiEvent
    data object OnResend : ForgotPasswordUiEvent

    // Step 3
    data class OnNewPasswordChange(val value: String) : ForgotPasswordUiEvent
    data class OnConfirmPasswordChange(val value: String) : ForgotPasswordUiEvent
    data object OnToggleNewPasswordVisibility : ForgotPasswordUiEvent
    data object OnToggleConfirmPasswordVisibility : ForgotPasswordUiEvent
    data object OnResetPassword : ForgotPasswordUiEvent

    // Common
    data object OnBack : ForgotPasswordUiEvent
    data object OnDismissError : ForgotPasswordUiEvent
    data object OnDismissSuccess : ForgotPasswordUiEvent
    data object OnReset : ForgotPasswordUiEvent
}
