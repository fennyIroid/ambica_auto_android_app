package com.ambica.auto.app.ux.container.job.create

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class GateEntryUiState(
    val stateFlow: StateFlow<GateEntryDataState> = MutableStateFlow(GateEntryDataState()),
    val event: (GateEntryUiEvent) -> Unit = {}
)

data class GateEntryDataState(
    val vehicleNumber: String = "",
    val model: String = "",
    val jobCardNumber: String = "",
    val claimNumber: String = "",
    val customerName: String = "",
    val customerPhone: String = "",
    val damageNotes: String = "",
    val missingItem: String = "",
    val photos: List<String?> = listOf(null, null, null, null),
    val isLoading: Boolean = false,
    val error: String? = null,
)

sealed interface GateEntryUiEvent {
    data class OnVehicleNumberChange(val value: String) : GateEntryUiEvent
    data class OnModelChange(val value: String) : GateEntryUiEvent
    data class OnJobCardChange(val value: String) : GateEntryUiEvent
    data class OnClaimChange(val value: String) : GateEntryUiEvent
    data class OnCustomerNameChange(val value: String) : GateEntryUiEvent
    data class OnCustomerPhoneChange(val value: String) : GateEntryUiEvent
    data class OnDamageNotesChange(val value: String) : GateEntryUiEvent
    data class OnMissingItemChange(val value: String) : GateEntryUiEvent
    data class OnPhotoSlotClick(val index: Int) : GateEntryUiEvent
    data object OnSubmit : GateEntryUiEvent
    data object OnCancel : GateEntryUiEvent
    data object OnBack : GateEntryUiEvent
}

