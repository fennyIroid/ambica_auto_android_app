package com.ambica.auto.app.ux.main.profile

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.ambica.auto.app.model.base.BaseViewModel
import com.ambica.auto.app.navigation.ViewModelNav
import com.ambica.auto.app.navigation.ViewModelNavImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    @ApplicationContext context: Context,
    getProfileUiStateUseCase: GetProfileUiStateUseCase,
) : BaseViewModel(), ViewModelNav by ViewModelNavImpl() {
    val uiState: ProfileUiState =
        getProfileUiStateUseCase(context = context, coroutineScope = viewModelScope) { navigate(it) }
}
