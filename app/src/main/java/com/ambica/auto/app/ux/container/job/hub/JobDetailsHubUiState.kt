package com.ambica.auto.app.ux.container.job.hub

import com.ambica.auto.app.data.source.local.session.Session
import com.ambica.auto.app.model.domain.job.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class JobDetailsHubUiState(
    val stateFlow: StateFlow<JobDetailsHubDataState> = MutableStateFlow(JobDetailsHubDataState()),
    val event: (JobDetailsHubUiEvent) -> Unit = {},
)

data class JobDetailsHubDataState(
    val jobId: String? = null,
    val job: Job? = null,
    val session: Session? = null,
    val visibleSections: List<JobDetailSection> = emptyList(),
)

sealed interface JobDetailsHubUiEvent {
    data class OnSetJobId(val jobId: String) : JobDetailsHubUiEvent
}

