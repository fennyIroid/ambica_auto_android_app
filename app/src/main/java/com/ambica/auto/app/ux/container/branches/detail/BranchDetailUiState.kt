package com.ambica.auto.app.ux.container.branches.detail

import com.ambica.auto.app.data.source.remote.model.branch.BranchItemResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class BranchDetailUiState(
    val stateFlow: StateFlow<BranchDetailDataState> = MutableStateFlow(BranchDetailDataState()),
    val event: (BranchDetailUiEvent) -> Unit = {},
)

data class BranchDetailDataState(
    val branch: BranchItemResponse? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val canEditBranch: Boolean = false,
)

sealed interface BranchDetailUiEvent {
    data object OnBack : BranchDetailUiEvent
    data object OnRetry : BranchDetailUiEvent
    data object OnDismissError : BranchDetailUiEvent
    data object OnEditBranch : BranchDetailUiEvent
}
