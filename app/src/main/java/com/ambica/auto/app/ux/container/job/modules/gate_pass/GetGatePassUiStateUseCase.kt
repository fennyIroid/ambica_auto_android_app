package com.ambica.auto.app.ux.container.job.modules.gate_pass

import android.content.Context
import com.ambica.auto.app.domain.repository.JobService
import com.ambica.auto.app.model.domain.job.JobId
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GetGatePassUiStateUseCase @Inject constructor(
    private val jobService: JobService,
) {
    private val state = MutableStateFlow(GatePassDataState())
    private var observeJob: Job? = null

    operator fun invoke(
        context: Context,
        coroutineScope: CoroutineScope,
    ): GatePassUiState {
        return GatePassUiState(
            stateFlow = state,
            event = { event ->
                when (event) {
                    is GatePassUiEvent.OnSetJobId -> bindJob(coroutineScope, event.jobId)
                    is GatePassUiEvent.OnVehicleModelChange -> state.update { it.copy(vehicleModel = event.value) }
                    is GatePassUiEvent.OnRepairOrderNoChange -> state.update { it.copy(repairOrderNo = event.value) }
                    is GatePassUiEvent.OnInvoiceNoChange -> state.update { it.copy(invoiceNo = event.value) }
                    is GatePassUiEvent.OnDateDeliveredChange -> state.update { it.copy(dateDelivered = event.value) }
                    is GatePassUiEvent.OnTimeDeliveredChange -> state.update { it.copy(timeDelivered = event.value) }
                    is GatePassUiEvent.OnTestedSatisfiedChange -> state.update { it.copy(testedAndSatisfied = event.value) }
                    is GatePassUiEvent.OnWorkSummaryChange -> state.update { it.copy(workSummary = event.value) }
                    is GatePassUiEvent.OnRemarksChange -> state.update { it.copy(remarks = event.value) }
                    GatePassUiEvent.OnGenerateGatePass -> generate(coroutineScope)
                    GatePassUiEvent.OnConfirmGateOut -> gateOut(coroutineScope)
                }
            },
        )
    }

    private fun bindJob(coroutineScope: CoroutineScope, jobId: String) {
        if (state.value.jobId == jobId) return
        state.update { it.copy(jobId = jobId) }
        observeJob?.cancel()
        observeJob = coroutineScope.launch {
            jobService.jobFlow(JobId(jobId)).collect { job ->
                state.update { s ->
                    val shouldPrefill = s.repairOrderNo.isBlank() && s.vehicleModel.isBlank()
                    s.copy(
                        job = job,
                        repairOrderNo = if (shouldPrefill) job?.jobCardNumber.orEmpty() else s.repairOrderNo,
                    )
                }
            }
        }
    }

    private fun generate(coroutineScope: CoroutineScope) {
        val jobId = state.value.jobId ?: return
        coroutineScope.launch {
            state.update { it.copy(isWorking = true, errorText = null) }
            jobService.generateGatePass(JobId(jobId))
            state.update { it.copy(isWorking = false) }
        }
    }

    private fun gateOut(coroutineScope: CoroutineScope) {
        val jobId = state.value.jobId ?: return
        coroutineScope.launch {
            val err = validateForGateOut()
            if (err != null) {
                state.update { it.copy(errorText = err) }
                return@launch
            }
            state.update { it.copy(isWorking = true, errorText = null) }
            jobService.confirmGateOut(JobId(jobId))
            state.update { it.copy(isWorking = false) }
        }
    }

    private fun validateForGateOut(): String? {
        val s = state.value
        if (s.invoiceNo.trim().isBlank()) return "Invoice no. is required"
        if (s.dateDelivered.trim().isBlank()) return "Date of delivered is required"
        if (s.timeDelivered.trim().isBlank()) return "Time is required"
        return null
    }
}

