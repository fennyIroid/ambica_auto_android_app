package com.ambica.auto.app.ux.container.job.modules.gate_pass

import com.ambica.auto.app.model.domain.job.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class GatePassUiState(
    val stateFlow: StateFlow<GatePassDataState> = MutableStateFlow(GatePassDataState()),
    val event: (GatePassUiEvent) -> Unit = {},
)

data class GatePassDataState(
    val jobId: String? = null,
    val job: Job? = null,
    val isWorking: Boolean = false,
    val vehicleModel: String = "",
    val repairOrderNo: String = "",
    val invoiceNo: String = "",
    val dateDelivered: String = "",
    val timeDelivered: String = "",
    val testedAndSatisfied: Boolean = false,
    val workSummary: String = "",
    val remarks: String = "",
    val errorText: String? = null,
)

sealed interface GatePassUiEvent {
    data class OnSetJobId(val jobId: String) : GatePassUiEvent
    data class OnVehicleModelChange(val value: String) : GatePassUiEvent
    data class OnRepairOrderNoChange(val value: String) : GatePassUiEvent
    data class OnInvoiceNoChange(val value: String) : GatePassUiEvent
    data class OnDateDeliveredChange(val value: String) : GatePassUiEvent
    data class OnTimeDeliveredChange(val value: String) : GatePassUiEvent
    data class OnTestedSatisfiedChange(val value: Boolean) : GatePassUiEvent
    data class OnWorkSummaryChange(val value: String) : GatePassUiEvent
    data class OnRemarksChange(val value: String) : GatePassUiEvent
    data object OnGenerateGatePass : GatePassUiEvent
    data object OnConfirmGateOut : GatePassUiEvent
}

