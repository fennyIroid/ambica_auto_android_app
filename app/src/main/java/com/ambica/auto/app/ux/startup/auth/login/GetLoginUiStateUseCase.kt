package com.ambica.auto.app.ux.startup.auth.login

import android.content.Context
import androidx.navigation.navOptions
import com.ambica.auto.app.data.source.local.datastore.AppPreferenceDataStore
import com.ambica.auto.app.data.source.local.session.SessionStore
import com.ambica.auto.app.data.source.remote.helper.NetworkResult
import com.ambica.auto.app.data.source.remote.repository.ApiRepository
import com.ambica.auto.app.navigation.NavigationAction
import com.ambica.auto.app.ux.main.MainRoute
import com.ambica.auto.app.ux.startup.auth.setpassword.SetPasswordRoute
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

class GetLoginUiStateUseCase @Inject constructor(
    private val apiRepository: ApiRepository,
    private val appPreferenceDataStore: AppPreferenceDataStore,
    private val sessionStore: SessionStore,
) {
    private val state = MutableStateFlow(LoginDataState())

    operator fun invoke(
        context: Context,
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit
    ): LoginUiState {
        return LoginUiState(
            stateFlow = state,
            event = { event ->
                when (event) {
                    is LoginUiEvent.OnEmailChange ->
                        state.update { it.copy(email = event.value, emailError = null, generalError = null) }

                    is LoginUiEvent.OnPasswordChange ->
                        state.update { it.copy(password = event.value, passwordError = null, generalError = null) }

                    LoginUiEvent.OnTogglePasswordVisibility ->
                        state.update { it.copy(passwordVisible = !it.passwordVisible) }

                    is LoginUiEvent.OnRoleChange ->
                        state.update { it.copy(role = event.role, generalError = null) }

                    LoginUiEvent.OnClearError ->
                        state.update { it.copy(generalError = null, emailError = null, passwordError = null) }

                    LoginUiEvent.OnLoginClick -> {
                        val email = state.value.email.trim()
                        val password = state.value.password
                        val role = state.value.role

                        val emailErr = when {
                            email.isBlank() -> "Enter email"
                            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Enter a valid email"
                            else -> null
                        }
                        val passwordErr = if (password.isBlank()) "Enter password" else null

                        if (emailErr != null || passwordErr != null) {
                            state.update { it.copy(emailError = emailErr, passwordError = passwordErr) }
                            return@LoginUiState
                        }

                        coroutineScope.launch {
                            apiRepository.login(email, password, role).collect { result ->
                                when (result) {
                                    is NetworkResult.Loading -> {
                                        state.update { it.copy(isLoading = true, generalError = null) }
                                    }

                                    is NetworkResult.Success -> {
                                        val payload = result.data?.data
                                        val token = payload?.accessToken?.accessToken.orEmpty()
                                        val profile = payload?.userProfile
                                        val resolvedRole = profile?.role ?: role
                                        val expiresIn = payload?.accessToken?.expiresIn

                                        if (token.isBlank()) {
                                            state.update {
                                                it.copy(isLoading = false, generalError = "Login failed: no token received")
                                            }
                                            return@collect
                                        }

                                        appPreferenceDataStore.saveUserTokenData(token)
                                        appPreferenceDataStore.saveSelectedRole(resolvedRole.toString())
                                        expiresIn?.let {
                                            val expiryMillis = System.currentTimeMillis() + (it * 1000L)
                                            appPreferenceDataStore.saveSessionExpiryMillis(expiryMillis)
                                        }
                                        profile?.let { sessionStore.updateFromProfile(it) }

                                        val profileResult = apiRepository.getProfile()
                                            .first { it !is NetworkResult.Loading }
                                        if (profileResult is NetworkResult.Success && profileResult.data != null) {
                                            sessionStore.updateFromProfile(profileResult.data)
                                        }

                                        val options = navOptions {
                                            popUpTo(LoginRoute.routeDefinition.value) { inclusive = true }
                                        }
                                        if (payload?.passwordChangeRequired == true) {
                                            navigate(NavigationAction.NavigateWithOptions(SetPasswordRoute.createRoute(), options))
                                        } else {
                                            navigate(NavigationAction.NavigateWithOptions(MainRoute.createRoute(), options))
                                        }
                                    }

                                    is NetworkResult.Error -> {
                                        state.update {
                                            it.copy(isLoading = false, generalError = result.message ?: "Login failed")
                                        }
                                    }

                                    is NetworkResult.UnAuthenticated -> {
                                        state.update {
                                            it.copy(isLoading = false, generalError = "Invalid credentials or role mismatch")
                                        }
                                    }
                                }
                            }
                        }
                    }

                    is LoginUiEvent.NavigateNext -> {
                        val options = navOptions {
                            popUpTo(LoginRoute.routeDefinition.value) { inclusive = true }
                        }
                        navigate(NavigationAction.NavigateWithOptions(MainRoute.createRoute(), options))
                    }
                }
            }
        )
    }
}
