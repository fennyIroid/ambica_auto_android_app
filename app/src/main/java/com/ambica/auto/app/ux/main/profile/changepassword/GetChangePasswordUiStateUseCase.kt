package com.ambica.auto.app.ux.main.profile.changepassword

import android.content.Context
import com.ambica.auto.app.data.source.remote.helper.NetworkResult
import com.ambica.auto.app.data.source.remote.model.auth.ChangePasswordRequest
import com.ambica.auto.app.data.source.remote.repository.ApiRepository
import com.ambica.auto.app.navigation.NavigationAction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class GetChangePasswordUiStateUseCase @Inject constructor(
    private val apiRepository: ApiRepository,
) {
    private val state = MutableStateFlow(ChangePasswordDataState())

    operator fun invoke(
        context: Context,
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
    ): ChangePasswordUiState {
        return ChangePasswordUiState(
            stateFlow = state,
            event = { event ->
                when (event) {
                    is ChangePasswordUiEvent.OnCurrentPasswordChange ->
                        state.update { it.copy(currentPassword = event.value, error = null) }
                    is ChangePasswordUiEvent.OnNewPasswordChange ->
                        state.update { it.copy(newPassword = event.value, error = null) }
                    is ChangePasswordUiEvent.OnConfirmPasswordChange ->
                        state.update { it.copy(confirmPassword = event.value, error = null) }
                    ChangePasswordUiEvent.OnToggleCurrentPasswordVisibility ->
                        state.update { it.copy(currentPasswordVisible = !it.currentPasswordVisible) }
                    ChangePasswordUiEvent.OnToggleNewPasswordVisibility ->
                        state.update { it.copy(newPasswordVisible = !it.newPasswordVisible) }
                    ChangePasswordUiEvent.OnToggleConfirmPasswordVisibility ->
                        state.update { it.copy(confirmPasswordVisible = !it.confirmPasswordVisible) }
                    ChangePasswordUiEvent.OnDismissError ->
                        state.update { it.copy(error = null) }
                    ChangePasswordUiEvent.OnDismissSuccess -> {
                        state.update { it.copy(successMessage = null) }
                        navigate(NavigationAction.Pop())
                    }
                    ChangePasswordUiEvent.OnBack ->
                        navigate(NavigationAction.Pop())

                    ChangePasswordUiEvent.OnSubmit -> {
                        val s = state.value
                        when {
                            s.currentPassword.isBlank() ->
                                state.update { it.copy(error = "Current password is required") }
                            s.newPassword.isBlank() ->
                                state.update { it.copy(error = "New password is required") }
                            s.newPassword.length < 8 ->
                                state.update { it.copy(error = "New password must be at least 8 characters") }
                            !s.newPassword.any { it.isUpperCase() } ->
                                state.update { it.copy(error = "New password must contain at least one uppercase letter") }
                            !s.newPassword.any { it.isDigit() } ->
                                state.update { it.copy(error = "New password must contain at least one number") }
                            s.confirmPassword.isBlank() ->
                                state.update { it.copy(error = "Please confirm your new password") }
                            s.newPassword != s.confirmPassword ->
                                state.update { it.copy(error = "Passwords do not match") }
                            else -> coroutineScope.launch {
                                state.update { it.copy(isSaving = true, error = null) }
                                val result = apiRepository.changePassword(
                                    ChangePasswordRequest(
                                        currentPassword = s.currentPassword,
                                        newPassword = s.newPassword,
                                        confirmPassword = s.confirmPassword,
                                    )
                                ).first { it !is NetworkResult.Loading }

                                when (result) {
                                    is NetworkResult.Success ->
                                        state.update {
                                            it.copy(
                                                isSaving = false,
                                                successMessage = "Password changed successfully!",
                                            )
                                        }
                                    is NetworkResult.Error ->
                                        state.update { it.copy(isSaving = false, error = result.message ?: "Failed to change password") }
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
}
