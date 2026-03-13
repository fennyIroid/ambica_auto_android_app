package com.ambica.auto.app.ux.container.job.modules.billing_payment

import com.ambica.auto.app.model.domain.job.Job
import com.ambica.auto.app.model.domain.job.PaymentMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class BillingPaymentUiState(
    val stateFlow: StateFlow<BillingPaymentDataState> = MutableStateFlow(BillingPaymentDataState()),
    val event: (BillingPaymentUiEvent) -> Unit = {},
)

data class BillingPaymentDataState(
    val jobId: String? = null,
    val job: Job? = null,
    val paymentMode: PaymentMode = PaymentMode.ONLINE,
    val paymentMenuExpanded: Boolean = false,
    val isSaving: Boolean = false,
)

sealed interface BillingPaymentUiEvent {
    data class OnSetJobId(val jobId: String) : BillingPaymentUiEvent
    data class OnPaymentModeChange(val mode: PaymentMode) : BillingPaymentUiEvent
    data class OnPaymentMenuExpanded(val expanded: Boolean) : BillingPaymentUiEvent
    data class OnSubmit(val verified: Boolean) : BillingPaymentUiEvent
}

