package com.ambica.auto.app.ux.container.job.modules.timeline

import com.ambica.auto.app.model.domain.job.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class TimelineUiState(
    val stateFlow: StateFlow<TimelineDataState> = MutableStateFlow(TimelineDataState()),
    val event: (TimelineUiEvent) -> Unit = {},
)

data class TimelineDataState(
    val jobId: String? = null,
    val job: Job? = null,
)

sealed interface TimelineUiEvent {
    data class OnSetJobId(val jobId: String) : TimelineUiEvent
}

