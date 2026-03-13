package com.ambica.auto.app.ux.container.job.modules.timeline

import android.content.Context
import com.ambica.auto.app.domain.repository.JobService
import com.ambica.auto.app.model.domain.job.JobId
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GetTimelineUiStateUseCase @Inject constructor(
    private val jobService: JobService,
) {
    private val state = MutableStateFlow(TimelineDataState())
    private var observeJob: Job? = null

    operator fun invoke(
        context: Context,
        coroutineScope: CoroutineScope,
    ): TimelineUiState {
        return TimelineUiState(
            stateFlow = state,
            event = { event ->
                when (event) {
                    is TimelineUiEvent.OnSetJobId -> bindJob(coroutineScope, event.jobId)
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
                state.update { it.copy(job = job) }
            }
        }
    }
}

