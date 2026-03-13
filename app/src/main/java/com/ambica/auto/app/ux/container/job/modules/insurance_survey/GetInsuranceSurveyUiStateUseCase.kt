package com.ambica.auto.app.ux.container.job.modules.insurance_survey

import android.content.Context
import com.ambica.auto.app.data.source.remote.helper.NetworkResult
import com.ambica.auto.app.data.source.remote.model.document_checklist.DocumentChecklistItemResponse
import com.ambica.auto.app.data.source.remote.repository.ApiRepository
import com.ambica.auto.app.domain.repository.JobService
import com.ambica.auto.app.model.domain.job.JobId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.util.UUID

/** applicable_for: 1=Insurance, 2=Self, 3=Both */
private const val APPLICABLE_INSURANCE = 1

class GetInsuranceSurveyUiStateUseCase @Inject constructor(
    private val jobService: JobService,
    private val apiRepository: ApiRepository,
) {
    private val state = MutableStateFlow(InsuranceSurveyDataState())
    private var observeJob: Job? = null

    operator fun invoke(
        context: Context,
        coroutineScope: CoroutineScope,
    ): InsuranceSurveyUiState {
        return InsuranceSurveyUiState(
            stateFlow = state,
            event = { event ->
                when (event) {
                    is InsuranceSurveyUiEvent.OnSetJobId -> bindJob(coroutineScope, event.jobId)
                    is InsuranceSurveyUiEvent.OnSurveyorChange -> state.update { it.copy(surveyor = event.value) }
                    is InsuranceSurveyUiEvent.OnClaimNoChange -> state.update { it.copy(claimNo = event.value) }
                    is InsuranceSurveyUiEvent.OnCompanyChange -> state.update { it.copy(company = event.value) }
                    is InsuranceSurveyUiEvent.OnVehInDateChange -> state.update { it.copy(vehInDate = event.value) }
                    is InsuranceSurveyUiEvent.OnVehInTimeChange -> state.update { it.copy(vehInTime = event.value) }
                    is InsuranceSurveyUiEvent.OnNotesChange -> state.update { it.copy(notes = event.value) }
                    is InsuranceSurveyUiEvent.OnRemarksChange -> state.update { it.copy(remarks = event.value) }
                    is InsuranceSurveyUiEvent.OnApprovalStatusChange -> state.update { it.copy(approvalStatus = event.value) }
                    is InsuranceSurveyUiEvent.OnToggleChecklist -> toggleChecklist(event.item)
                    is InsuranceSurveyUiEvent.OnUploadClick -> addMockUpload(event.item)
                    InsuranceSurveyUiEvent.OnRetryChecklist -> fetchDocumentChecklists(coroutineScope)
                    InsuranceSurveyUiEvent.OnNext -> next()
                    InsuranceSurveyUiEvent.OnBack -> back()
                    InsuranceSurveyUiEvent.OnSubmit -> submit(coroutineScope)
                }
            },
        )
    }

    private fun bindJob(coroutineScope: CoroutineScope, jobId: String) {
        if (state.value.jobId == jobId) return
        state.update { it.copy(jobId = jobId) }
        fetchDocumentChecklists(coroutineScope)
        observeJob?.cancel()
        observeJob = coroutineScope.launch {
            jobService.jobFlow(JobId(jobId))
                .collect { job ->
                    state.update { s ->
                        val shouldPrefill = s.surveyor.isBlank() && s.notes.isBlank() && s.company.isBlank() && s.claimNo.isBlank()
                        s.copy(
                            job = job,
                            surveyor = if (shouldPrefill) job?.insuranceInfo?.surveyorName.orEmpty() else s.surveyor,
                            company = if (shouldPrefill) job?.insuranceInfo?.company.orEmpty() else s.company,
                            notes = if (shouldPrefill) job?.insuranceInfo?.notes.orEmpty() else s.notes,
                        )
                    }
                }
        }
    }

    private fun fetchDocumentChecklists(coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            state.update { it.copy(isChecklistLoading = true, checklistError = null) }
            val result = apiRepository.getDocumentChecklists(
                applicableFor = APPLICABLE_INSURANCE,
                ordering = "order",
                perPage = 100,
            ).first { it !is NetworkResult.Loading }

            when (result) {
                is NetworkResult.Success -> {
                    val items = result.data?.results.orEmpty()
                    val initialChecklist = items.associate { (it.id ?: "") to false }
                    state.update {
                        it.copy(
                            isChecklistLoading = false,
                            documentChecklistItems = items,
                            checklist = it.checklist.ifEmpty { initialChecklist },
                        )
                    }
                }
                is NetworkResult.Error ->
                    state.update {
                        it.copy(
                            isChecklistLoading = false,
                            checklistError = result.message ?: "Failed to load document checklist",
                        )
                    }
                is NetworkResult.UnAuthenticated ->
                    state.update {
                        it.copy(
                            isChecklistLoading = false,
                            checklistError = "Session expired. Please log in again.",
                        )
                    }
                else -> state.update { it.copy(isChecklistLoading = false) }
            }
        }
    }

    private fun toggleChecklist(item: DocumentChecklistItemResponse) {
        val id = item.id ?: return
        state.update { s ->
            s.copy(
                checklist = s.checklist.toMutableMap().apply {
                    this[id] = !(this[id] ?: false)
                },
            )
        }
    }

    private fun addMockUpload(item: DocumentChecklistItemResponse) {
        val id = item.id ?: return
        state.update { s ->
            val current = s.uploads[id].orEmpty()
            s.copy(
                uploads = s.uploads.toMutableMap().apply {
                    this[id] = current + "demo://upload/${item.name.orEmpty().replace(" ", "_")}/${UUID.randomUUID()}"
                }
            )
        }
    }

    private fun next() {
        val current = state.value.step
        val error = validateStep(current)
        if (error != null) {
            state.update { it.copy(stepError = error) }
            return
        }
        state.update { it.copy(step = (it.step + 1).coerceAtMost(MAX_STEP), stepError = null) }
    }

    private fun back() {
        state.update { it.copy(step = (it.step - 1).coerceAtLeast(0), stepError = null) }
    }

    private fun validateStep(step: Int): String? {
        val s = state.value
        return when (step) {
            0 -> {
                if (s.claimNo.trim().isBlank()) "Claim number is required"
                else if (s.surveyor.trim().isBlank()) "Surveyor name is required"
                else null
            }
            1 -> {
                val mandatoryIds = s.documentChecklistItems
                    .filter { it.isMandatory == true }
                    .mapNotNull { it.id }
                val missingRequired = mandatoryIds.filter { (s.checklist[it] ?: false).not() }
                if (mandatoryIds.isNotEmpty() && missingRequired.isNotEmpty()) {
                    "Please tick required documents before continuing"
                } else null
            }
            else -> null
        }
    }

    private fun submit(coroutineScope: CoroutineScope) {
        val jobId = state.value.jobId ?: return
        val surveyor = state.value.surveyor.trim().takeIf { it.isNotBlank() }
        val notes = buildString {
            val base = state.value.notes.trim()
            if (base.isNotBlank()) append(base)

            val itemIds = state.value.checklist.filterValues { it }.keys
            val itemNames = state.value.documentChecklistItems
                .filter { it.id in itemIds }
                .mapNotNull { it.name }
            val checklistSummary = itemNames.joinToString()
            if (checklistSummary.isNotBlank()) {
                if (isNotEmpty()) append("\n\n")
                append("Checklist:\n")
                append(checklistSummary)
            }

            val uploadsSummary = state.value.uploads
                .filterValues { it.isNotEmpty() }
                .entries
                .joinToString(separator = "\n") { (id, uploads) ->
                    val name = state.value.documentChecklistItems.find { it.id == id }?.name ?: id
                    "$name: ${uploads.size} file(s)"
                }
            if (uploadsSummary.isNotBlank()) {
                if (isNotEmpty()) append("\n\n")
                append("Uploads:\n")
                append(uploadsSummary)
            }

            val remarks = state.value.remarks.trim()
            if (remarks.isNotBlank()) {
                if (isNotEmpty()) append("\n\n")
                append("Remarks:\n")
                append(remarks)
            }
        }
        val status = state.value.approvalStatus
        coroutineScope.launch {
            state.update { it.copy(isSaving = true) }
            jobService.updateInsurance(JobId(jobId), surveyor, status, notes)
            state.update { it.copy(isSaving = false) }
        }
    }

    private companion object {
        const val MAX_STEP: Int = 3
    }
}

