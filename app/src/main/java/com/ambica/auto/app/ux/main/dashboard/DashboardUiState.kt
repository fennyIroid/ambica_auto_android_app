package com.ambica.auto.app.ux.main.dashboard

import com.ambica.auto.app.model.domain.job.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class DashboardUiState(
    val stateFlow: StateFlow<DashboardDataState> = MutableStateFlow(DashboardDataState()),
    val event: (DashboardUiEvent) -> Unit = {},
)

data class DashboardDataState(
    val allJobs: List<Job> = emptyList(),
    val filteredJobs: List<Job> = emptyList(),
    val branchName: String = "Main Workshop",
    val branchCity: String = "Surat, India",
    val search: String = "",
    val filter: DashboardFilter = DashboardFilter.ALL,
)

enum class DashboardFilter { ALL, IN_REPAIR, PENDING, COMPLETED }

sealed interface DashboardUiEvent {
    data class OnSearchChange(val value: String) : DashboardUiEvent
    data class OnFilterChange(val filter: DashboardFilter) : DashboardUiEvent
    data class OnJobClick(val jobId: String) : DashboardUiEvent
}
