package com.ambica.auto.app.ux.main.jobs

import com.ambica.auto.app.model.domain.Branch
import com.ambica.auto.app.model.domain.job.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/** Set by HomeScreen navigation actions before pushing to JobsScreen. */
object InitialJobsFilter {
    var filter: String? = null
}

data class JobsUiState(
    val stateFlow: StateFlow<JobsDataState> = MutableStateFlow(JobsDataState()),
    val event: (JobsUiEvent) -> Unit = {},
)

data class JobsDataState(
    val jobs: List<Job> = emptyList(),
    val branches: List<Branch> = emptyList(),
    val search: String = "",
    val filter: JobsFilter = JobsFilter.TODAY,
    val selectedBranchId: String? = null,
)

enum class JobsFilter { TODAY, PENDING, IN_PROGRESS, COMPLETED }

sealed interface JobsUiEvent {
    data class OnSearchChange(val value: String) : JobsUiEvent
    data class OnFilterChange(val filter: JobsFilter) : JobsUiEvent
    data class OnBranchSelect(val branchId: String?) : JobsUiEvent
    data class OnJobClick(val jobId: String) : JobsUiEvent
    data object OnCreateJobClick : JobsUiEvent
    data class OnApplyInitialFilter(val key: String?) : JobsUiEvent
}
