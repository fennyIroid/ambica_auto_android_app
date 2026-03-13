package com.ambica.auto.app.ux.container.branches.edit

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class EditBranchUiState(
    val stateFlow: StateFlow<EditBranchDataState> = MutableStateFlow(EditBranchDataState()),
    val event: (EditBranchUiEvent) -> Unit = {},
)

data class EditBranchDataState(
    val branchId: String = "",
    val code: String = "",
    val name: String = "",
    val location: String = "",
    val tagline: String = "",
    val address: String = "",
    val phone: String = "",
    val email: String = "",
    val status: Int = 1,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,
)

sealed interface EditBranchUiEvent {
    data class OnCodeChange(val value: String) : EditBranchUiEvent
    data class OnNameChange(val value: String) : EditBranchUiEvent
    data class OnLocationChange(val value: String) : EditBranchUiEvent
    data class OnTaglineChange(val value: String) : EditBranchUiEvent
    data class OnAddressChange(val value: String) : EditBranchUiEvent
    data class OnPhoneChange(val value: String) : EditBranchUiEvent
    data class OnEmailChange(val value: String) : EditBranchUiEvent
    data class OnStatusChange(val status: Int) : EditBranchUiEvent
    data object OnSubmit : EditBranchUiEvent
    data object OnBack : EditBranchUiEvent
    data object OnDismissError : EditBranchUiEvent
    data object OnDismissSuccess : EditBranchUiEvent
}
