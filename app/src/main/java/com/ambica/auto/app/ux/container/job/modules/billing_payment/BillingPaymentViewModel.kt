package com.ambica.auto.app.ux.container.job.modules.billing_payment

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.ambica.auto.app.model.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class BillingPaymentViewModel @Inject constructor(
    @ApplicationContext context: Context,
    getBillingPaymentUiStateUseCase: GetBillingPaymentUiStateUseCase,
) : BaseViewModel() {
    val uiState: BillingPaymentUiState =
        getBillingPaymentUiStateUseCase(context = context, coroutineScope = viewModelScope)
}

