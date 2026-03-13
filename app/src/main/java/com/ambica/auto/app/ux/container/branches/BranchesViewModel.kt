package com.ambica.auto.app.ux.container.branches

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.ambica.auto.app.model.base.BaseViewModel
import com.ambica.auto.app.navigation.ViewModelNav
import com.ambica.auto.app.navigation.ViewModelNavImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class BranchesViewModel @Inject constructor(
    @ApplicationContext context: Context,
    getBranchesUiStateUseCase: GetBranchesUiStateUseCase,
) : BaseViewModel(), ViewModelNav by ViewModelNavImpl() {
    val uiState: BranchesUiState =
        getBranchesUiStateUseCase(context = context, coroutineScope = viewModelScope) { navigate(it) }
}
