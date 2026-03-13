package com.ambica.auto.app.ux.startup.auth.login

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.ambica.auto.app.model.base.BaseViewModel
import com.ambica.auto.app.navigation.ViewModelNav
import com.ambica.auto.app.navigation.ViewModelNavImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    @ApplicationContext context: Context,
    getLoginUiStateUseCase: GetLoginUiStateUseCase
) : BaseViewModel(), ViewModelNav by ViewModelNavImpl() {

    val uiState: LoginUiState =
        getLoginUiStateUseCase(context = context, coroutineScope = viewModelScope) { navigate(it) }
}

