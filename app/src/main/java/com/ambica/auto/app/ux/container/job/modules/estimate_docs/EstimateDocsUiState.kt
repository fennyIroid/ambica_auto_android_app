package com.ambica.auto.app.ux.container.job.modules.estimate_docs

import com.ambica.auto.app.model.domain.job.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class EstimateDocsUiState(
    val stateFlow: StateFlow<EstimateDocsDataState> = MutableStateFlow(EstimateDocsDataState()),
    val event: (EstimateDocsUiEvent) -> Unit = {},
)

data class EstimateDocsDataState(
    val jobId: String? = null,
    val job: Job? = null,
    val items: List<EstimateItem> = listOf(
        EstimateItem("Front Bumper Replacement", "450.00"),
        EstimateItem("Paint & Refinishing", "320.00"),
        EstimateItem("", "0.00"),
    ),
    val approval: InsuranceApproval = InsuranceApproval.PENDING,
    val isSaving: Boolean = false,
)

data class EstimateItem(
    val name: String,
    val cost: String,
)

enum class InsuranceApproval { PENDING, APPROVED, REJECTED }

sealed interface EstimateDocsUiEvent {
    data class OnSetJobId(val jobId: String) : EstimateDocsUiEvent
    data class OnItemNameChange(val index: Int, val value: String) : EstimateDocsUiEvent
    data class OnItemCostChange(val index: Int, val value: String) : EstimateDocsUiEvent
    data object OnAddItem : EstimateDocsUiEvent
    data class OnApprovalChange(val value: InsuranceApproval) : EstimateDocsUiEvent
    data object OnSave : EstimateDocsUiEvent
}

