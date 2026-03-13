package com.ambica.auto.app.ux.container.branches.edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.ambica.auto.app.model.base.BaseViewModel
import com.ambica.auto.app.navigation.ViewModelNav
import com.ambica.auto.app.navigation.ViewModelNavImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EditBranchViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getEditBranchUiStateUseCase: GetEditBranchUiStateUseCase,
) : BaseViewModel(), ViewModelNav by ViewModelNavImpl() {
    private val branchId: String = savedStateHandle.get<String>(EditBranchRoute.ARG_BRANCH_ID).orEmpty()

    val uiState: EditBranchUiState =
        getEditBranchUiStateUseCase(branchId = branchId, coroutineScope = viewModelScope) { navigate(it) }
}
