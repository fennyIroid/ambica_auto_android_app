package com.ambica.auto.app.ux.main.jobs

import android.content.Context
import com.ambica.auto.app.domain.repository.JobService
import com.ambica.auto.app.model.domain.Branch
import com.ambica.auto.app.model.domain.job.Job
import com.ambica.auto.app.model.domain.job.JobStatus
import com.ambica.auto.app.navigation.NavigationAction
import com.ambica.auto.app.ux.container.job.create.GateEntryRoute
import com.ambica.auto.app.ux.container.job.hub.JobDetailsHubRoute
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import java.util.Locale
import javax.inject.Inject

class GetJobsUiStateUseCase @Inject constructor(
    private val jobService: JobService,
) {
    private val state = MutableStateFlow(JobsDataState())

    operator fun invoke(
        context: Context,
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
    ): JobsUiState {
        jobService.allJobsFlow.onEach { jobs ->
            val branches = jobs
                .map { it.branchId }
                .distinct()
                .map { id -> Branch(id = id, name = id.replaceFirstChar { it.uppercaseChar() }, city = "") }
            state.update { current ->
                current.copy(
                    branches = branches,
                    jobs = applyFilter(jobs, current.search, current.filter, current.selectedBranchId),
                )
            }
        }.launchIn(coroutineScope)

        return JobsUiState(
            stateFlow = state,
            event = { event ->
                when (event) {
                    is JobsUiEvent.OnSearchChange -> state.update {
                        it.copy(
                            search = event.value,
                            jobs = applyFilter(jobService.allJobsFlow.value, event.value, it.filter, it.selectedBranchId),
                        )
                    }
                    is JobsUiEvent.OnFilterChange -> state.update {
                        it.copy(
                            filter = event.filter,
                            jobs = applyFilter(jobService.allJobsFlow.value, it.search, event.filter, it.selectedBranchId),
                        )
                    }
                    is JobsUiEvent.OnBranchSelect -> state.update {
                        it.copy(
                            selectedBranchId = event.branchId,
                            jobs = applyFilter(jobService.allJobsFlow.value, it.search, it.filter, event.branchId),
                        )
                    }
                    is JobsUiEvent.OnApplyInitialFilter -> {
                        val newFilter = when (event.key) {
                            "pending" -> JobsFilter.PENDING
                            "in_progress" -> JobsFilter.IN_PROGRESS
                            "completed" -> JobsFilter.COMPLETED
                            else -> JobsFilter.TODAY
                        }
                        state.update {
                            it.copy(
                                filter = newFilter,
                                jobs = applyFilter(jobService.allJobsFlow.value, it.search, newFilter, it.selectedBranchId),
                            )
                        }
                    }
                    is JobsUiEvent.OnJobClick ->
                        navigate(NavigationAction.Navigate(JobDetailsHubRoute.createRoute(event.jobId)))
                    JobsUiEvent.OnCreateJobClick ->
                        navigate(NavigationAction.Navigate(GateEntryRoute.createRoute()))
                }
            }
        )
    }

    private fun applyFilter(
        jobs: List<Job>,
        search: String,
        filter: JobsFilter,
        branchId: String?,
    ): List<Job> {
        val q = search.trim().lowercase(Locale.getDefault())
        return jobs
            .filter { job ->
                val matchSearch = q.isBlank() ||
                    job.vehicleNumber.lowercase(Locale.getDefault()).contains(q) ||
                    job.customerName.lowercase(Locale.getDefault()).contains(q) ||
                    job.jobCardNumber.lowercase(Locale.getDefault()).contains(q) ||
                    (job.claimNumber?.lowercase(Locale.getDefault())?.contains(q) == true)
                val matchStatus = when (filter) {
                    JobsFilter.TODAY -> true
                    JobsFilter.PENDING -> job.status in setOf(
                        JobStatus.AWAITING_DOCUMENTS, JobStatus.AWAITING_SURVEY,
                        JobStatus.AWAITING_INSURANCE_APPROVAL, JobStatus.AWAITING_PAYMENT,
                    )
                    JobsFilter.IN_PROGRESS -> job.status in setOf(
                        JobStatus.NEW_ENTRY, JobStatus.APPROVED_AND_WORK_IN_PROGRESS, JobStatus.VEHICLE_READY,
                    )
                    JobsFilter.COMPLETED -> job.status in setOf(
                        JobStatus.READY_FOR_DELIVERY, JobStatus.DELIVERED,
                    )
                }
                val matchBranch = branchId == null || job.branchId == branchId
                matchSearch && matchStatus && matchBranch
            }
            .sortedByDescending { it.timeline.maxOfOrNull { e -> e.atMillis } ?: it.gateEntryAtMillis }
    }
}
