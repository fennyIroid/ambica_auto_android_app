package com.ambica.auto.app.ux.startup.auth.setpassword

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.ambica.auto.app.model.base.BaseViewModel
import com.ambica.auto.app.navigation.ViewModelNav
import com.ambica.auto.app.navigation.ViewModelNavImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class SetPasswordViewModel @Inject constructor(
    @ApplicationContext context: Context,
    getSetPasswordUiStateUseCase: GetSetPasswordUiStateUseCase,
) : BaseViewModel(), ViewModelNav by ViewModelNavImpl() {
    val uiState: SetPasswordUiState =
        getSetPasswordUiStateUseCase(context = context, coroutineScope = viewModelScope) { navigate(it) }
}
