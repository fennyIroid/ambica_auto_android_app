package com.ambica.auto.app.ux.container.job.modules.billing_payment

import android.content.Context
import com.ambica.auto.app.data.source.local.demo.DemoRepository
import com.ambica.auto.app.model.domain.job.JobId
import com.ambica.auto.app.model.domain.job.PaymentMode
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GetBillingPaymentUiStateUseCase @Inject constructor(
    private val demoRepository: DemoRepository,
) {
    private val state = MutableStateFlow(BillingPaymentDataState())
    private var observeJob: Job? = null

    operator fun invoke(
        context: Context,
        coroutineScope: CoroutineScope,
    ): BillingPaymentUiState {
        return BillingPaymentUiState(
            stateFlow = state,
            event = { event ->
                when (event) {
                    is BillingPaymentUiEvent.OnSetJobId -> bindJob(coroutineScope, event.jobId)
                    is BillingPaymentUiEvent.OnPaymentModeChange -> state.update { it.copy(paymentMode = event.mode, paymentMenuExpanded = false) }
                    is BillingPaymentUiEvent.OnPaymentMenuExpanded -> state.update { it.copy(paymentMenuExpanded = event.expanded) }
                    is BillingPaymentUiEvent.OnSubmit -> submit(coroutineScope, event.verified)
                }
            },
        )
    }

    private fun bindJob(coroutineScope: CoroutineScope, jobId: String) {
        if (state.value.jobId == jobId) return
        state.update { it.copy(jobId = jobId) }
        observeJob?.cancel()
        observeJob = coroutineScope.launch {
            demoRepository.jobFlow(JobId(jobId)).collect { job ->
                state.update { it.copy(job = job) }
            }
        }
    }

    private fun submit(coroutineScope: CoroutineScope, verified: Boolean) {
        val jobId = state.value.jobId ?: return
        val job = state.value.job ?: return
        val b = job.billing
        val mode: PaymentMode = state.value.paymentMode
        coroutineScope.launch {
            state.update { it.copy(isSaving = true) }
            demoRepository.updateBilling(
                JobId(jobId),
                b.insuranceLiability,
                b.customerLiability,
                b.shortfallAmount,
                mode,
                verified,
            )
            state.update { it.copy(isSaving = false) }
        }
    }
}

