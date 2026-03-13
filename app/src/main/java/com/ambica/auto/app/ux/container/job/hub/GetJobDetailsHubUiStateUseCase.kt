package com.ambica.auto.app.ux.container.job.hub

import android.content.Context
import com.ambica.auto.app.data.source.local.session.SessionStore
import com.ambica.auto.app.domain.repository.JobService
import com.ambica.auto.app.model.domain.job.JobId
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GetJobDetailsHubUiStateUseCase @Inject constructor(
    private val jobService: JobService,
    private val sessionStore: SessionStore,
) {
    private val state = MutableStateFlow(JobDetailsHubDataState())
    private var observeJob: Job? = null
    private var observeSession: Job? = null

    operator fun invoke(
        context: Context,
        coroutineScope: CoroutineScope,
    ): JobDetailsHubUiState {
        if (observeSession == null) {
            observeSession = coroutineScope.launch {
                sessionStore.sessionFlow.collect { session ->
                    state.update { s ->
                        val role = session.role
                        s.copy(
                            session = session,
                            visibleSections = JobDetailSection.entries.filter { it.isVisibleTo(role) },
                        )
                    }
                }
            }
        }

        return JobDetailsHubUiState(
            stateFlow = state,
            event = { event ->
                when (event) {
                    is JobDetailsHubUiEvent.OnSetJobId -> bindJob(coroutineScope, event.jobId)
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

