package com.ambica.auto.app.ux.startup.splash

import android.content.Context
import android.os.Bundle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class SplashUiState(
    val splashStateFlow: StateFlow<SplashData?> = MutableStateFlow(null),
    val event: (SplashUiEvent) -> Unit = {}
)

data class SplashData(
    val isLoading: Boolean = false,
)

sealed interface SplashUiEvent {
    data class GetIntentData(val bundle: Bundle? = null) : SplashUiEvent
    data class NavigateAfterSplash(val context: Context) : SplashUiEvent
}
