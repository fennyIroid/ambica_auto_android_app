package com.ambica.auto.app.ux.startup.auth.forgotpassword

import android.content.Context
import com.ambica.auto.app.data.source.remote.helper.NetworkResult
import com.ambica.auto.app.data.source.remote.model.auth.ForgotPasswordRequest
import com.ambica.auto.app.data.source.remote.model.auth.ResetPasswordRequest
import com.ambica.auto.app.data.source.remote.model.auth.VerifyOtpRequest
import com.ambica.auto.app.data.source.remote.repository.ApiRepository
import com.ambica.auto.app.navigation.NavigationAction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class GetForgotPasswordUiStateUseCase @Inject constructor(
    private val apiRepository: ApiRepository,
) {
    private val state = MutableStateFlow(ForgotPasswordDataState())

    operator fun invoke(
        context: Context,
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
    ): ForgotPasswordUiState {
        return ForgotPasswordUiState(
            stateFlow = state,
            event = { event ->
                when (event) {
                    // ── Field changes ──────────────────────────────────────────────────────────
                    is ForgotPasswordUiEvent.OnEmailChange ->
                        state.update { it.copy(email = event.value, emailError = null, error = null) }
                    is ForgotPasswordUiEvent.OnOtpChange ->
                        state.update { it.copy(otp = event.otp.filter { c -> c.isDigit() }.take(6), otpError = null) }
                    is ForgotPasswordUiEvent.OnNewPasswordChange ->
                        state.update { it.copy(newPassword = event.value, newPasswordError = null, error = null) }
                    is ForgotPasswordUiEvent.OnConfirmPasswordChange ->
                        state.update { it.copy(confirmPassword = event.value, error = null) }
                    ForgotPasswordUiEvent.OnToggleNewPasswordVisibility ->
                        state.update { it.copy(newPasswordVisible = !it.newPasswordVisible) }
                    ForgotPasswordUiEvent.OnToggleConfirmPasswordVisibility ->
                        state.update { it.copy(confirmPasswordVisible = !it.confirmPasswordVisible) }
                    ForgotPasswordUiEvent.OnDismissError ->
                        state.update { it.copy(error = null) }
                    ForgotPasswordUiEvent.OnDismissSuccess -> {
                        val shouldDismiss = state.value.step == ForgotPasswordStep.RESET_PASSWORD
                        state.update { it.copy(successMessage = null, isDismissed = shouldDismiss) }
                    }

                    // ── Navigation within steps ────────────────────────────────────────────────
                    ForgotPasswordUiEvent.OnBack -> when (state.value.step) {
                        ForgotPasswordStep.ENTER_EMAIL -> state.update { it.copy(isDismissed = true) }
                        ForgotPasswordStep.VERIFY_OTP ->
                            state.update { it.copy(step = ForgotPasswordStep.ENTER_EMAIL, otp = "", otpError = null) }
                        ForgotPasswordStep.RESET_PASSWORD ->
                            state.update { it.copy(step = ForgotPasswordStep.VERIFY_OTP) }
                    }

                    ForgotPasswordUiEvent.OnReset ->
                        state.value = ForgotPasswordDataState()

                    // ── Step 1: Send OTP ───────────────────────────────────────────────────────
                    ForgotPasswordUiEvent.OnSendCode -> {
                        val email = state.value.email.trim()
                        val emailErr = when {
                            email.isBlank() -> "Enter your email address"
                            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                                "Enter a valid email address"
                            else -> null
                        }
                        if (emailErr != null) {
                            state.update { it.copy(emailError = emailErr) }
                            return@ForgotPasswordUiState
                        }
                        coroutineScope.launch {
                            state.update { it.copy(isLoading = true, error = null) }
                            val result = apiRepository.forgotPassword(ForgotPasswordRequest(email))
                                .first { it !is NetworkResult.Loading }
                            when (result) {
                                is NetworkResult.Success -> {
                                    state.update {
                                        it.copy(
                                            isLoading = false,
                                            step = ForgotPasswordStep.VERIFY_OTP,
                                            successMessage = "OTP sent! Check your inbox.",
                                        )
                                    }
                                    startCooldown(coroutineScope)
                                }
                                is NetworkResult.Error ->
                                    state.update {
                                        it.copy(isLoading = false, error = result.message ?: "Failed to send OTP. Please try again.")
                                    }
                                is NetworkResult.UnAuthenticated ->
                                    state.update { it.copy(isLoading = false, error = "Account not found or inactive.") }
                                else -> state.update { it.copy(isLoading = false) }
                            }
                        }
                    }

                    // ── Step 2: Verify OTP via API ─────────────────────────────────────────────
                    ForgotPasswordUiEvent.OnVerify -> {
                        if (state.value.otp.length < 6) {
                            state.update { it.copy(otpError = "Enter the complete 6-digit OTP") }
                            return@ForgotPasswordUiState
                        }
                        coroutineScope.launch {
                            state.update { it.copy(isLoading = true, otpError = null, error = null) }
                            val result = apiRepository.verifyOtp(
                                VerifyOtpRequest(email = state.value.email, otp = state.value.otp)
                            ).first { it !is NetworkResult.Loading }

                            when (result) {
                                is NetworkResult.Success ->
                                    state.update { it.copy(isLoading = false, step = ForgotPasswordStep.RESET_PASSWORD) }
                                is NetworkResult.Error ->
                                    state.update {
                                        it.copy(
                                            isLoading = false,
                                            otpError = result.message ?: "Invalid or expired OTP. Please try again.",
                                        )
                                    }
                                is NetworkResult.UnAuthenticated ->
                                    state.update {
                                        it.copy(
                                            isLoading = false,
                                            otpError = "Invalid or expired OTP. Please request a new one.",
                                        )
                                    }
                                else -> state.update { it.copy(isLoading = false) }
                            }
                        }
                    }

                    ForgotPasswordUiEvent.OnResend -> {
                        if (state.value.resendCooldownSeconds > 0) return@ForgotPasswordUiState
                        coroutineScope.launch {
                            state.update { it.copy(isLoading = true, error = null, otp = "") }
                            val result = apiRepository.forgotPassword(ForgotPasswordRequest(state.value.email))
                                .first { it !is NetworkResult.Loading }
                            when (result) {
                                is NetworkResult.Success -> {
                                    state.update {
                                        it.copy(isLoading = false, successMessage = "OTP resent! Check your inbox.")
                                    }
                                    startCooldown(coroutineScope)
                                }
                                is NetworkResult.Error ->
                                    state.update { it.copy(isLoading = false, error = result.message ?: "Failed to resend OTP") }
                                else -> state.update { it.copy(isLoading = false) }
                            }
                        }
                    }

                    // ── Step 3: Reset Password ─────────────────────────────────────────────────
                    ForgotPasswordUiEvent.OnResetPassword -> {
                        val s = state.value
                        when {
                            s.newPassword.isBlank() ->
                                state.update { it.copy(newPasswordError = "Enter new password") }
                            s.newPassword.length < 8 ->
                                state.update { it.copy(newPasswordError = "Must be at least 8 characters") }
                            !s.newPassword.any { it.isUpperCase() } ->
                                state.update { it.copy(newPasswordError = "Must include one uppercase letter") }
                            !s.newPassword.any { it.isDigit() } ->
                                state.update { it.copy(newPasswordError = "Must include one number") }
                            s.confirmPassword.isBlank() ->
                                state.update { it.copy(error = "Please confirm your password") }
                            s.newPassword != s.confirmPassword ->
                                state.update { it.copy(error = "Passwords do not match") }
                            else -> coroutineScope.launch {
                                state.update { it.copy(isLoading = true, error = null) }
                                val result = apiRepository.resetPassword(
                                    ResetPasswordRequest(
                                        email = s.email,
                                        otp = s.otp,
                                        newPassword = s.newPassword,
                                        confirmPassword = s.confirmPassword,
                                    )
                                ).first { it !is NetworkResult.Loading }

                                when (result) {
                                    is NetworkResult.Success ->
                                        state.update {
                                            it.copy(
                                                isLoading = false,
                                                successMessage = "Password reset successfully! You can now log in.",
                                            )
                                        }
                                    is NetworkResult.Error -> {
                                        val msg = result.message ?: "Something went wrong. Please try again."
                                        if (msg.contains("otp", ignoreCase = true) ||
                                            msg.contains("expired", ignoreCase = true) ||
                                            msg.contains("invalid", ignoreCase = true)
                                        ) {
                                            // OTP-related error → go back to step 2 so the user can re-enter OTP
                                            state.update {
                                                it.copy(
                                                    isLoading = false,
                                                    step = ForgotPasswordStep.VERIFY_OTP,
                                                    otp = "",
                                                    otpError = msg,
                                                )
                                            }
                                        } else {
                                            state.update { it.copy(isLoading = false, error = msg) }
                                        }
                                    }
                                    is NetworkResult.UnAuthenticated ->
                                        state.update {
                                            it.copy(
                                                isLoading = false,
                                                step = ForgotPasswordStep.VERIFY_OTP,
                                                otp = "",
                                                otpError = "Invalid or expired OTP. Please request a new one.",
                                            )
                                        }
                                    else -> state.update { it.copy(isLoading = false) }
                                }
                            }
                        }
                    }
                }
            }
        )
    }

    private fun startCooldown(coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            for (seconds in 59 downTo 0) {
                state.update { it.copy(resendCooldownSeconds = seconds + 1) }
                delay(1000)
            }
            state.update { it.copy(resendCooldownSeconds = 0) }
        }
    }
}
