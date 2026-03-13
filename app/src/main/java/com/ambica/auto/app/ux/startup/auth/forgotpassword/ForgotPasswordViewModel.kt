package com.ambica.auto.app.ux.startup.auth.forgotpassword

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.ambica.auto.app.model.base.BaseViewModel
import com.ambica.auto.app.navigation.ViewModelNav
import com.ambica.auto.app.navigation.ViewModelNavImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    @ApplicationContext context: Context,
    getForgotPasswordUiStateUseCase: GetForgotPasswordUiStateUseCase,
) : BaseViewModel(), ViewModelNav by ViewModelNavImpl() {
    val uiState: ForgotPasswordUiState =
        getForgotPasswordUiStateUseCase(context = context, coroutineScope = viewModelScope) { navigate(it) }

    /** Resets the flow back to step 1 with blank state (called after the sheet is dismissed). */
    fun reset() = uiState.event(ForgotPasswordUiEvent.OnReset)
}
