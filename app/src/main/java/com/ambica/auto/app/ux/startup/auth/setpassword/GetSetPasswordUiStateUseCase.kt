package com.ambica.auto.app.ux.startup.auth.setpassword

import android.content.Context
import androidx.navigation.navOptions
import com.ambica.auto.app.data.source.remote.helper.NetworkResult
import com.ambica.auto.app.data.source.remote.model.auth.SetPasswordRequest
import com.ambica.auto.app.data.source.remote.repository.ApiRepository
import com.ambica.auto.app.navigation.NavigationAction
import com.ambica.auto.app.ux.main.MainRoute
import com.ambica.auto.app.ux.startup.auth.setpassword.SetPasswordRoute
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class GetSetPasswordUiStateUseCase @Inject constructor(
    private val apiRepository: ApiRepository,
) {
    private val state = MutableStateFlow(SetPasswordDataState())

    operator fun invoke(
        context: Context,
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
    ): SetPasswordUiState {
        return SetPasswordUiState(
            stateFlow = state,
            event = { event ->
                when (event) {
                    is SetPasswordUiEvent.OnNewPasswordChange ->
                        state.update { it.copy(newPassword = event.value, error = null) }
                    is SetPasswordUiEvent.OnConfirmPasswordChange ->
                        state.update { it.copy(confirmPassword = event.value, error = null) }
                    SetPasswordUiEvent.OnToggleNewPasswordVisibility ->
                        state.update { it.copy(newPasswordVisible = !it.newPasswordVisible) }
                    SetPasswordUiEvent.OnToggleConfirmPasswordVisibility ->
                        state.update { it.copy(confirmPasswordVisible = !it.confirmPasswordVisible) }
                    SetPasswordUiEvent.OnDismissError ->
                        state.update { it.copy(error = null) }
                    SetPasswordUiEvent.OnDismissSuccess -> {
                        state.update { it.copy(successMessage = null) }
                        goToMain(navigate)
                    }

                    // Skip — no API call, navigate directly to the main app
                    SetPasswordUiEvent.OnSkip -> goToMain(navigate)

                    SetPasswordUiEvent.OnSubmit -> {
                        val s = state.value
                        when {
                            s.newPassword.isBlank() ->
                                state.update { it.copy(error = "Enter your new password") }
                            s.newPassword.length < 8 ->
                                state.update { it.copy(error = "Password must be at least 8 characters") }
                            !s.newPassword.any { it.isUpperCase() } ->
                                state.update { it.copy(error = "Password must contain at least one uppercase letter") }
                            !s.newPassword.any { it.isDigit() } ->
                                state.update { it.copy(error = "Password must contain at least one number") }
                            s.confirmPassword.isBlank() ->
                                state.update { it.copy(error = "Please confirm your password") }
                            s.newPassword != s.confirmPassword ->
                                state.update { it.copy(error = "Passwords do not match") }
                            else -> coroutineScope.launch {
                                state.update { it.copy(isSaving = true, error = null) }
                                val result = apiRepository.setPassword(
                                    SetPasswordRequest(
                                        newPassword = s.newPassword,
                                        confirmPassword = s.confirmPassword,
                                    )
                                ).first { it !is NetworkResult.Loading }

                                when (result) {
                                    is NetworkResult.Success ->
                                        state.update {
                                            it.copy(
                                                isSaving = false,
                                                successMessage = "Password set successfully! Welcome aboard.",
                                            )
                                        }
                                    is NetworkResult.Error ->
                                        state.update {
                                            it.copy(isSaving = false, error = result.message ?: "Failed to set password. Please try again.")
                                        }
                                    is NetworkResult.UnAuthenticated ->
                                        state.update { it.copy(isSaving = false, error = "Session expired. Please log in again.") }
                                    else -> state.update { it.copy(isSaving = false) }
                                }
                            }
                        }
                    }
                }
            }
        )
    }

    private fun goToMain(navigate: (NavigationAction) -> Unit) {
        val options = navOptions {
            popUpTo(SetPasswordRoute.routeDefinition.value) { inclusive = true }
        }
        navigate(NavigationAction.NavigateWithOptions(MainRoute.createRoute(), options))
    }
}
