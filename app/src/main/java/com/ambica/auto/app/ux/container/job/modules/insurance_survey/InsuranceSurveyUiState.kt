package com.ambica.auto.app.ux.container.job.modules.insurance_survey

import com.ambica.auto.app.data.source.remote.model.document_checklist.DocumentChecklistItemResponse
import com.ambica.auto.app.model.domain.job.ApprovalStatus
import com.ambica.auto.app.model.domain.job.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class InsuranceSurveyUiState(
    val stateFlow: StateFlow<InsuranceSurveyDataState> = MutableStateFlow(InsuranceSurveyDataState()),
    val event: (InsuranceSurveyUiEvent) -> Unit = {},
)

data class InsuranceSurveyDataState(
    val jobId: String? = null,
    val job: Job? = null,
    val step: Int = 0,
    val surveyor: String = "",
    val claimNo: String = "",
    val company: String = "",
    val vehInDate: String = "",
    val vehInTime: String = "",
    val notes: String = "",
    val remarks: String = "",
    val approvalStatus: ApprovalStatus = ApprovalStatus.PENDING,
    val documentChecklistItems: List<DocumentChecklistItemResponse> = emptyList(),
    val checklist: Map<String, Boolean> = emptyMap(),
    val uploads: Map<String, List<String>> = emptyMap(),
    val isChecklistLoading: Boolean = false,
    val checklistError: String? = null,
    val isSaving: Boolean = false,
    val stepError: String? = null,
)

sealed interface InsuranceSurveyUiEvent {
    data class OnSetJobId(val jobId: String) : InsuranceSurveyUiEvent
    data class OnSurveyorChange(val value: String) : InsuranceSurveyUiEvent
    data class OnClaimNoChange(val value: String) : InsuranceSurveyUiEvent
    data class OnCompanyChange(val value: String) : InsuranceSurveyUiEvent
    data class OnVehInDateChange(val value: String) : InsuranceSurveyUiEvent
    data class OnVehInTimeChange(val value: String) : InsuranceSurveyUiEvent
    data class OnNotesChange(val value: String) : InsuranceSurveyUiEvent
    data class OnRemarksChange(val value: String) : InsuranceSurveyUiEvent
    data class OnApprovalStatusChange(val value: ApprovalStatus) : InsuranceSurveyUiEvent
    data class OnToggleChecklist(val item: DocumentChecklistItemResponse) : InsuranceSurveyUiEvent
    data class OnUploadClick(val item: DocumentChecklistItemResponse) : InsuranceSurveyUiEvent
    data object OnRetryChecklist : InsuranceSurveyUiEvent
    data object OnNext : InsuranceSurveyUiEvent
    data object OnBack : InsuranceSurveyUiEvent
    data object OnSubmit : InsuranceSurveyUiEvent
}

