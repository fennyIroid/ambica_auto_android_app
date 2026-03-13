package com.ambica.auto.app.ux.container.branches.create

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class CreateBranchUiState(
    val stateFlow: StateFlow<CreateBranchDataState> = MutableStateFlow(CreateBranchDataState()),
    val event: (CreateBranchUiEvent) -> Unit = {},
)

data class CreateBranchDataState(
    val code: String = "",
    val name: String = "",
    val location: String = "",
    val tagline: String = "",
    val address: String = "",
    val phone: String = "",
    val email: String = "",
    val status: Int = 1,
    val isSaving: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,
)

sealed interface CreateBranchUiEvent {
    data class OnCodeChange(val value: String) : CreateBranchUiEvent
    data class OnNameChange(val value: String) : CreateBranchUiEvent
    data class OnLocationChange(val value: String) : CreateBranchUiEvent
    data class OnTaglineChange(val value: String) : CreateBranchUiEvent
    data class OnAddressChange(val value: String) : CreateBranchUiEvent
    data class OnPhoneChange(val value: String) : CreateBranchUiEvent
    data class OnEmailChange(val value: String) : CreateBranchUiEvent
    data class OnStatusChange(val status: Int) : CreateBranchUiEvent
    data object OnSubmit : CreateBranchUiEvent
    data object OnBack : CreateBranchUiEvent
    data object OnDismissError : CreateBranchUiEvent
    data object OnDismissSuccess : CreateBranchUiEvent
}
