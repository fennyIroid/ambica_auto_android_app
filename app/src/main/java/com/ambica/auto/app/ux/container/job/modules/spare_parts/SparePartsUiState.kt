package com.ambica.auto.app.ux.container.job.modules.spare_parts

import com.ambica.auto.app.model.domain.job.Job
import com.ambica.auto.app.model.domain.job.PartItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class SparePartsUiState(
    val stateFlow: StateFlow<SparePartsDataState> = MutableStateFlow(SparePartsDataState()),
    val event: (SparePartsUiEvent) -> Unit = {},
)

data class SparePartsDataState(
    val jobId: String? = null,
    val job: Job? = null,
    val isWorking: Boolean = false,
)

sealed interface SparePartsUiEvent {
    data class OnSetJobId(val jobId: String) : SparePartsUiEvent
    data object OnAddQuickPart : SparePartsUiEvent
    data class OnCycleStatus(val part: PartItem) : SparePartsUiEvent
}

