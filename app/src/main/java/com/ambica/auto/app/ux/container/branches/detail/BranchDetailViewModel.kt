package com.ambica.auto.app.ux.container.branches.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.ambica.auto.app.model.base.BaseViewModel
import com.ambica.auto.app.navigation.ViewModelNav
import com.ambica.auto.app.navigation.ViewModelNavImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BranchDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getBranchDetailUiStateUseCase: GetBranchDetailUiStateUseCase,
) : BaseViewModel(), ViewModelNav by ViewModelNavImpl() {
    private val branchId: String = savedStateHandle.get<String>(BranchDetailRoute.ARG_BRANCH_ID).orEmpty()

    val uiState: BranchDetailUiState =
        getBranchDetailUiStateUseCase(branchId = branchId, coroutineScope = viewModelScope) { navigate(it) }
}
