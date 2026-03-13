package com.ambica.auto.app.ux.main.dashboard

import android.content.Context
import com.ambica.auto.app.data.source.local.session.SessionStore
import com.ambica.auto.app.domain.repository.JobService
import com.ambica.auto.app.model.domain.job.Job
import com.ambica.auto.app.model.domain.job.JobStatus
import com.ambica.auto.app.navigation.NavigationAction
import com.ambica.auto.app.ux.container.job.hub.JobDetailsHubRoute
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import java.util.Locale
import javax.inject.Inject

class GetDashboardUiStateUseCase @Inject constructor(
    private val jobService: JobService,
    private val sessionStore: SessionStore,
) {
    private val state = MutableStateFlow(DashboardDataState())

    operator fun invoke(
        context: Context,
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
    ): DashboardUiState {
        combine(jobService.allJobsFlow, sessionStore.sessionFlow) { jobs, session ->
            state.value.copy(
                allJobs = jobs,
                filteredJobs = applyFilter(jobs, state.value.search, state.value.filter),
                branchName = session.branch?.name ?: "Main Workshop",
                branchCity = session.branch?.city?.ifBlank { "Surat, India" } ?: "Surat, India",
            )
        }.onEach { state.value = it }.launchIn(coroutineScope)

        return DashboardUiState(
            stateFlow = state,
            event = { event ->
                when (event) {
                    is DashboardUiEvent.OnSearchChange -> state.update {
                        it.copy(search = event.value, filteredJobs = applyFilter(it.allJobs, event.value, it.filter))
                    }
                    is DashboardUiEvent.OnFilterChange -> state.update {
                        it.copy(filter = event.filter, filteredJobs = applyFilter(it.allJobs, it.search, event.filter))
                    }
                    is DashboardUiEvent.OnJobClick ->
                        navigate(NavigationAction.Navigate(JobDetailsHubRoute.createRoute(event.jobId)))
                }
            }
        )
    }

    private fun applyFilter(jobs: List<Job>, search: String, filter: DashboardFilter): List<Job> {
        val q = search.trim().lowercase(Locale.getDefault())
        return jobs.filter { job ->
            val matchSearch = q.isBlank() ||
                job.vehicleNumber.lowercase(Locale.getDefault()).contains(q) ||
                job.customerName.lowercase(Locale.getDefault()).contains(q) ||
                job.jobCardNumber.lowercase(Locale.getDefault()).contains(q) ||
                (job.claimNumber?.lowercase(Locale.getDefault())?.contains(q) == true)
            val matchFilter = when (filter) {
                DashboardFilter.ALL -> true
                DashboardFilter.IN_REPAIR -> job.status == JobStatus.APPROVED_AND_WORK_IN_PROGRESS
                DashboardFilter.PENDING -> job.status in setOf(
                    JobStatus.AWAITING_DOCUMENTS, JobStatus.AWAITING_SURVEY,
                    JobStatus.AWAITING_INSURANCE_APPROVAL, JobStatus.AWAITING_PAYMENT,
                )
                DashboardFilter.COMPLETED -> job.status in setOf(
                    JobStatus.READY_FOR_DELIVERY, JobStatus.DELIVERED,
                )
            }
            matchSearch && matchFilter
        }
    }
}
