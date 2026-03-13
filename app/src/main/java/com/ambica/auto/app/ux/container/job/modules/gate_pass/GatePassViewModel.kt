package com.ambica.auto.app.ux.container.job.modules.gate_pass

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.ambica.auto.app.model.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class GatePassViewModel @Inject constructor(
    @ApplicationContext context: Context,
    getGatePassUiStateUseCase: GetGatePassUiStateUseCase,
) : BaseViewModel() {
    val uiState: GatePassUiState =
        getGatePassUiStateUseCase(context = context, coroutineScope = viewModelScope)
}

