package com.ambica.auto.app.ux.container.branches

import com.ambica.auto.app.data.source.remote.model.branch.BranchItemResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class BranchesUiState(
    val stateFlow: StateFlow<BranchesDataState> = MutableStateFlow(BranchesDataState()),
    val event: (BranchesUiEvent) -> Unit = {},
)

data class BranchesDataState(
    val branches: List<BranchItemResponse> = emptyList(),
    val search: String = "",
    val statusFilter: BranchStatusFilter = BranchStatusFilter.ALL,
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,
    val canCreateBranch: Boolean = false,
    val deletingBranchId: String? = null,
)

enum class BranchStatusFilter(val apiValue: Int?) {
    ALL(null),
    ACTIVE(1),
    INACTIVE(2),
}

sealed interface BranchesUiEvent {
    data class OnSearchChange(val value: String) : BranchesUiEvent
    data class OnStatusFilterChange(val filter: BranchStatusFilter) : BranchesUiEvent
    data object OnRetry : BranchesUiEvent
    data class OnShowSuccess(val message: String) : BranchesUiEvent
    data object OnBack : BranchesUiEvent
    data object OnDismissError : BranchesUiEvent
    data object OnDismissSuccess : BranchesUiEvent
    data object OnCreateBranch : BranchesUiEvent
    data class OnDeleteBranch(val branchId: String) : BranchesUiEvent
    data class OnBranchClick(val branchId: String) : BranchesUiEvent
}
