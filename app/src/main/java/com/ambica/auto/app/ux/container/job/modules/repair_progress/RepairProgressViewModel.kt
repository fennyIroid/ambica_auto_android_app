package com.ambica.auto.app.ux.container.job.modules.repair_progress

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.ambica.auto.app.model.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class RepairProgressViewModel @Inject constructor(
    @ApplicationContext context: Context,
    getRepairProgressUiStateUseCase: GetRepairProgressUiStateUseCase,
) : BaseViewModel() {
    val uiState: RepairProgressUiState =
        getRepairProgressUiStateUseCase(context = context, coroutineScope = viewModelScope)
}

