package com.ambica.auto.app.ux.startup.splash

import android.content.Context
import android.os.Bundle
import androidx.navigation.navOptions
import com.ambica.auto.app.data.source.local.datastore.AppPreferenceDataStore
import com.ambica.auto.app.data.source.local.session.SessionStore
import com.ambica.auto.app.data.source.remote.helper.NetworkResult
import com.ambica.auto.app.data.source.remote.repository.ApiRepository
import com.ambica.auto.app.navigation.NavigationAction
import com.ambica.auto.app.ux.startup.auth.login.LoginRoute
import com.ambica.auto.app.ux.main.MainRoute
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetSplashUiStateUseCase @Inject constructor(
    private val appPreferenceDataStore: AppPreferenceDataStore,
    private val apiRepository: ApiRepository,
    private val sessionStore: SessionStore,
) {

    private val splashData = MutableStateFlow(SplashData())
    private val bundle = MutableStateFlow<Bundle>(Bundle.EMPTY)

    operator fun invoke(
        context: Context,
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
    ): SplashUiState {
        return SplashUiState(
            splashStateFlow = splashData,
            event = { uiEvent ->
                when (uiEvent) {
                    is SplashUiEvent.GetIntentData -> {
                        bundle.value = uiEvent.bundle ?: Bundle.EMPTY
                    }
                    is SplashUiEvent.NavigateAfterSplash -> {
                        coroutineScope.launch {
                            val token = appPreferenceDataStore.getUserTokenData().orEmpty()
                            val expiry = appPreferenceDataStore.getSessionExpiryMillis() ?: 0L
                            val isExpired = expiry != 0L && System.currentTimeMillis() > expiry

                            val options = navOptions {
                                popUpTo(SplashRoute.routeDefinition.value) { inclusive = true }
                            }

                            val nextRoute = if (token.isBlank() || isExpired) {
                                LoginRoute.createRoute()
                            } else {
                                val profileResult = apiRepository.getProfile()
                                    .first { it !is NetworkResult.Loading }
                                when (profileResult) {
                                    is NetworkResult.Success -> {
                                        profileResult.data?.let { sessionStore.updateFromProfile(it) }
                                        MainRoute.createRoute()
                                    }
                                    is NetworkResult.UnAuthenticated -> LoginRoute.createRoute()
                                    else -> MainRoute.createRoute()
                                }
                            }

                            navigate(NavigationAction.NavigateWithOptions(nextRoute, options))
                        }
                    }
                }
            }
        )
    }
}
