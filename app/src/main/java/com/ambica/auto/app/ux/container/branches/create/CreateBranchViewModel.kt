package com.ambica.auto.app.ux.container.branches.create

import androidx.lifecycle.viewModelScope
import com.ambica.auto.app.model.base.BaseViewModel
import com.ambica.auto.app.navigation.ViewModelNav
import com.ambica.auto.app.navigation.ViewModelNavImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CreateBranchViewModel @Inject constructor(
    getCreateBranchUiStateUseCase: GetCreateBranchUiStateUseCase,
) : BaseViewModel(), ViewModelNav by ViewModelNavImpl() {
    val uiState: CreateBranchUiState =
        getCreateBranchUiStateUseCase(coroutineScope = viewModelScope) { navigate(it) }
}
