package com.ambica.auto.app.ux.main.profile

import android.content.Context
import com.ambica.auto.app.data.source.local.datastore.AppPreferenceDataStore
import com.ambica.auto.app.data.source.local.session.SessionStore
import com.ambica.auto.app.data.source.remote.helper.NetworkResult
import com.ambica.auto.app.data.source.remote.repository.ApiRepository
import com.ambica.auto.app.navigation.NavigationAction
import com.ambica.auto.app.ux.main.profile.changepassword.ChangePasswordRoute
import com.ambica.auto.app.ux.main.profile.edit.EditProfileRoute
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class GetProfileUiStateUseCase @Inject constructor(
    private val sessionStore: SessionStore,
    private val apiRepository: ApiRepository,
    private val appPreferenceDataStore: AppPreferenceDataStore,
) {
    private val state = MutableStateFlow(ProfileDataState())

    operator fun invoke(
        context: Context,
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
    ): ProfileUiState {
        sessionStore.sessionFlow.onEach { session ->
            val name = session.staffName?.ifBlank { "Staff" } ?: "Staff"
            state.update {
                it.copy(
                    name = name,
                    role = session.role?.label ?: "Staff",
                    branchName = session.branch?.name?.ifBlank { "-" } ?: "-",
                    initials = name
                        .split(" ")
                        .filter { w -> w.isNotBlank() }
                        .take(2)
                        .joinToString("") { w -> w.first().uppercase() }
                        .ifBlank { "U" },
                )
            }
        }.launchIn(coroutineScope)

        return ProfileUiState(
            stateFlow = state,
            event = { event ->
                when (event) {
                    ProfileUiEvent.OnEditProfileClick ->
                        navigate(NavigationAction.Navigate(EditProfileRoute.createRoute()))

                    ProfileUiEvent.OnChangePasswordClick ->
                        navigate(NavigationAction.Navigate(ChangePasswordRoute.createRoute()))

                    ProfileUiEvent.OnLogoutClick -> {
                        coroutineScope.launch {
                            state.update { it.copy(isLoggingOut = true) }
                            try {
                                apiRepository.logout().first { it !is NetworkResult.Loading }
                            } catch (_: Exception) {
                                // Local cleanup always happens
                            } finally {
                                appPreferenceDataStore.clearAll()
                                sessionStore.clear()
                                state.update { it.copy(isLoggingOut = false, logoutComplete = true) }
                            }
                        }
                    }

                    ProfileUiEvent.OnLogoutNavigated ->
                        state.update { it.copy(logoutComplete = false) }
                }
            }
        )
    }
}
