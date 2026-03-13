package com.ambica.auto.app.ux.startup.splash

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.ambica.auto.app.navigation.ViewModelNav
import com.ambica.auto.app.navigation.ViewModelNavImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    @ApplicationContext context: Context,
    getSplashUiStateUseCase: GetSplashUiStateUseCase
) : com.ambica.auto.app.model.base.BaseViewModel(),
    ViewModelNav by ViewModelNavImpl() {

    val splashUiState: SplashUiState = getSplashUiStateUseCase(
        context = context,
        coroutineScope = viewModelScope
    ) { navigate(it) }
}
