package com.ambica.auto.app.ux.container.job.modules.insurance_survey

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.ambica.auto.app.data.source.remote.model.document_checklist.DocumentChecklistItemResponse
import com.ambica.auto.app.model.domain.job.ApprovalStatus
import com.ambica.auto.app.ui.compose.common.AmbicaTextField
import com.ambica.auto.app.ui.compose.common.FilterPillChip
import com.ambica.auto.app.ui.compose.common.PrimaryButton
import com.ambica.auto.app.ui.compose.common.SectionCard
import com.ambica.auto.app.ui.theme.colorSplashOrange
import com.ambica.auto.app.ux.container.job.modules.JobModuleScaffold

@Composable
fun InsuranceSurveyScreen(
    navController: NavController,
    jobId: String,
    viewModel: InsuranceSurveyViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState
    val state by uiState.stateFlow.collectAsStateWithLifecycle()

    LaunchedEffect(jobId) {
        uiState.event(InsuranceSurveyUiEvent.OnSetJobId(jobId))
    }

    JobModuleScaffold(title = "Insurance Survey", navController = navController) {
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(bottom = 24.dp)
                .background(Color.Transparent)
                .padding(horizontal = 2.dp, vertical = 6.dp)
                .clickable(enabled = false) { }
                .padding(0.dp),
        ) {
            StepHeader(
                step = state.step,
                errorText = state.stepError,
            )
            Spacer(Modifier.height(12.dp))

            when (state.step) {
                0 -> Step0SurveyDetails(state = state, uiState = uiState)
                1 -> Step1DocsChecklist(state = state, uiState = uiState)
                2 -> Step2ReportsAndPhotos(state = state, uiState = uiState)
                else -> Step3RemarksAndSubmit(state = state, uiState = uiState)
            }

            Spacer(Modifier.height(12.dp))
            StepFillerSummary(state = state)
            Spacer(Modifier.height(14.dp))
            StepActions(
                step = state.step,
                isSaving = state.isSaving,
                onBack = { uiState.event(InsuranceSurveyUiEvent.OnBack) },
                onNext = { uiState.event(InsuranceSurveyUiEvent.OnNext) },
                onSubmit = { uiState.event(InsuranceSurveyUiEvent.OnSubmit) },
            )
            Spacer(Modifier.height(18.dp))
        }
    }
}

@Composable
private fun StepHeader(
    step: Int,
    errorText: String?,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val steps = listOf("Details", "Checklist", "Reports", "Submit")
            steps.forEachIndexed { index, label ->
                FilterPillChip(
                    text = "${index + 1}. $label",
                    selected = index == step,
                    onClick = { /* locked: step changes through Next/Back to keep validation consistent */ },
                )
            }
        }
        if (!errorText.isNullOrBlank()) {
            Spacer(Modifier.height(10.dp))
            SectionCard(title = "Fix to continue") {
                androidx.compose.material3.Text(
                    text = errorText,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.error,
                    style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
private fun StepFillerSummary(state: InsuranceSurveyDataState) {
    val totalSteps = 4
    val progress = ((state.step + 1).toFloat() / totalSteps.toFloat()).coerceIn(0f, 1f)
    val checked = state.checklist.count { it.value }
    val mandatoryItems = state.documentChecklistItems.filter { it.isMandatory == true }
    val requiredChecked = mandatoryItems.count { state.checklist[it.id] == true }
    val requiredTotal = mandatoryItems.size
    val uploads = state.uploads.values.sumOf { it.size }

    SectionCard(title = "Step summary") {
        androidx.compose.material3.LinearProgressIndicator(
            progress = { progress },
            color = colorSplashOrange,
            trackColor = colorSplashOrange.copy(alpha = 0.12f),
            modifier = Modifier
                .fillMaxWidth()
                .clip(androidx.compose.foundation.shape.RoundedCornerShape(999.dp)),
        )
        Spacer(Modifier.height(10.dp))
        SummaryRow(label = "Progress", value = "Step ${state.step + 1} of $totalSteps")
        SummaryRow(label = "Checklist", value = "$checked selected ($requiredChecked/$requiredTotal required)")
        SummaryRow(label = "Uploads", value = if (uploads == 0) "None yet" else "$uploads file(s)")
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        androidx.compose.material3.Text(
            text = label,
            style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
            color = Color(0xFF7A7A7A),
        )
        androidx.compose.material3.Text(
            text = value,
            style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1D1D1D),
        )
    }
}

@Composable
private fun Step0SurveyDetails(
    state: InsuranceSurveyDataState,
    uiState: InsuranceSurveyUiState,
) {
    SectionCard(title = "Survey details") {
        AmbicaTextField(
            value = state.claimNo,
            onValueChange = { uiState.event(InsuranceSurveyUiEvent.OnClaimNoChange(it)) },
            placeholder = "Claim no.",
        )
        Spacer(Modifier.height(10.dp))
        AmbicaTextField(
            value = state.company,
            onValueChange = { uiState.event(InsuranceSurveyUiEvent.OnCompanyChange(it)) },
            placeholder = "Insurance company",
        )
        Spacer(Modifier.height(10.dp))
        AmbicaTextField(
            value = state.surveyor,
            onValueChange = { uiState.event(InsuranceSurveyUiEvent.OnSurveyorChange(it)) },
            placeholder = "Surveyor name",
        )
        Spacer(Modifier.height(10.dp))
        AmbicaTextField(
            value = state.notes,
            onValueChange = { uiState.event(InsuranceSurveyUiEvent.OnNotesChange(it)) },
            placeholder = "Notes (optional)",
        )
    }
}

@Composable
private fun Step1DocsChecklist(
    state: InsuranceSurveyDataState,
    uiState: InsuranceSurveyUiState,
) {
    SectionCard(title = "Documents checklist") {
        when {
            state.isChecklistLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    androidx.compose.material3.CircularProgressIndicator(
                        color = colorSplashOrange,
                        modifier = Modifier.size(32.dp),
                    )
                }
            }
            state.checklistError != null -> {
                androidx.compose.material3.Text(
                    text = state.checklistError,
                    style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.error,
                )
                Spacer(Modifier.height(8.dp))
                androidx.compose.material3.Text(
                    text = "Tap to retry",
                    style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = colorSplashOrange,
                    modifier = Modifier.clickable { uiState.event(InsuranceSurveyUiEvent.OnRetryChecklist) },
                )
            }
            else -> {
                val grouped = state.documentChecklistItems
                    .filter { it.documentCategory == 1 || it.documentCategory == 3 }
                    .groupBy { it.documentCategory }
                val categoryLabels = mapOf(
                    1 to "Vehicle",
                    2 to "Customer",
                    3 to "Insurance",
                    4 to "Legal",
                )
                grouped.entries.sortedBy { it.key ?: 0 }.forEach { (category, items) ->
                    if (items.isNotEmpty()) {
                        Spacer(Modifier.height(if (grouped.keys.first() == category) 0.dp else 14.dp))
                        ChecklistGroup(
                            title = categoryLabels[category] ?: "Documents",
                            items = items.sortedBy { it.displayOrder ?: it.order ?: 0 },
                            state = state,
                            uiState = uiState,
                        )
                    }
                }
                if (grouped.isEmpty()) {
                    androidx.compose.material3.Text(
                        text = "No document checklist available",
                        style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF7A7A7A),
                    )
                }
            }
        }
    }
}

@Composable
private fun Step2ReportsAndPhotos(
    state: InsuranceSurveyDataState,
    uiState: InsuranceSurveyUiState,
) {
    SectionCard(title = "Reports & photos") {
        when {
            state.isChecklistLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    androidx.compose.material3.CircularProgressIndicator(
                        color = colorSplashOrange,
                        modifier = Modifier.size(32.dp),
                    )
                }
            }
            state.checklistError != null -> {
                androidx.compose.material3.Text(
                    text = state.checklistError,
                    style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.error,
                )
                Spacer(Modifier.height(8.dp))
                androidx.compose.material3.Text(
                    text = "Tap to retry",
                    style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = colorSplashOrange,
                    modifier = Modifier.clickable { uiState.event(InsuranceSurveyUiEvent.OnRetryChecklist) },
                )
            }
            else -> {
                val legalAndExtras = state.documentChecklistItems
                    .filter { it.documentCategory == 4 || it.documentCategory == 2 }
                val categoryLabels = mapOf(
                    1 to "Vehicle",
                    2 to "Customer",
                    3 to "Insurance",
                    4 to "Legal",
                )
                legalAndExtras.groupBy { it.documentCategory }.entries.sortedBy { it.key ?: 0 }.forEach { (category, items) ->
                    if (items.isNotEmpty()) {
                        Spacer(Modifier.height(14.dp))
                        ChecklistGroup(
                            title = categoryLabels[category] ?: "Documents",
                            items = items.sortedBy { it.displayOrder ?: it.order ?: 0 },
                            state = state,
                            uiState = uiState,
                        )
                    }
                }
                if (legalAndExtras.isEmpty()) {
                    androidx.compose.material3.Text(
                        text = "No additional documents for this step",
                        style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF7A7A7A),
                    )
                }
            }
        }
    }
}

@Composable
private fun Step3RemarksAndSubmit(
    state: InsuranceSurveyDataState,
    uiState: InsuranceSurveyUiState,
) {
    SectionCard(title = "Vehicle in / remarks") {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            AmbicaTextField(
                value = state.vehInDate,
                onValueChange = { uiState.event(InsuranceSurveyUiEvent.OnVehInDateChange(it)) },
                placeholder = "Veh. in date",
                modifier = Modifier.weight(1f),
            )
            AmbicaTextField(
                value = state.vehInTime,
                onValueChange = { uiState.event(InsuranceSurveyUiEvent.OnVehInTimeChange(it)) },
                placeholder = "Time",
                modifier = Modifier.weight(1f),
            )
        }
        Spacer(Modifier.height(10.dp))
        AmbicaTextField(
            value = state.remarks,
            onValueChange = { uiState.event(InsuranceSurveyUiEvent.OnRemarksChange(it)) },
            placeholder = "Remarks (optional)",
        )
        Spacer(Modifier.height(14.dp))
        androidx.compose.material3.Text(
            text = "Approval status",
            style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1D1D1D),
        )
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterPillChip(
                text = "Pending",
                selected = state.approvalStatus == ApprovalStatus.PENDING,
                onClick = { uiState.event(InsuranceSurveyUiEvent.OnApprovalStatusChange(ApprovalStatus.PENDING)) },
            )
            FilterPillChip(
                text = "Approved",
                selected = state.approvalStatus == ApprovalStatus.APPROVED,
                onClick = { uiState.event(InsuranceSurveyUiEvent.OnApprovalStatusChange(ApprovalStatus.APPROVED)) },
            )
            FilterPillChip(
                text = "Re-inspection",
                selected = state.approvalStatus == ApprovalStatus.RE_INSPECTION,
                onClick = { uiState.event(InsuranceSurveyUiEvent.OnApprovalStatusChange(ApprovalStatus.RE_INSPECTION)) },
            )
        }
    }
}

@Composable
private fun ChecklistGroup(
    title: String,
    items: List<DocumentChecklistItemResponse>,
    state: InsuranceSurveyDataState,
    uiState: InsuranceSurveyUiState,
) {
    androidx.compose.material3.Text(
        text = title,
        style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.SemiBold,
        color = Color(0xFF1D1D1D),
    )
    Spacer(Modifier.height(10.dp))
    items.forEachIndexed { index, item ->
        val id = item.id ?: return@forEachIndexed
        ChecklistRow(
            label = item.displayName,
            required = item.isMandatory == true,
            checked = state.checklist[id] == true,
            uploadCount = state.uploads[id].orEmpty().size,
            showUpload = true,
            onUpload = { uiState.event(InsuranceSurveyUiEvent.OnUploadClick(item)) },
            onToggle = { uiState.event(InsuranceSurveyUiEvent.OnToggleChecklist(item)) },
        )
        if (index < items.lastIndex) Spacer(Modifier.height(10.dp))
    }
}

@Composable
private fun ChecklistRow(
    label: String,
    required: Boolean,
    checked: Boolean,
    uploadCount: Int,
    showUpload: Boolean,
    onUpload: () -> Unit,
    onToggle: () -> Unit,
) {
    val bg = if (checked) colorSplashOrange.copy(alpha = 0.10f) else Color(0xFFF6F6F6)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(14.dp))
            .background(bg)
            .clickable { onToggle() }
            .padding(horizontal = 12.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            androidx.compose.material3.Text(
                text = label,
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                color = Color(0xFF1D1D1D),
                fontWeight = if (required) FontWeight.SemiBold else FontWeight.Medium,
            )
            if (required) {
                Spacer(Modifier.height(2.dp))
                androidx.compose.material3.Text(
                    text = "Required",
                    style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                    color = Color(0xFF7A7A7A),
                )
            }
            if (showUpload) {
                Spacer(Modifier.height(4.dp))
                androidx.compose.material3.Text(
                    text = if (uploadCount == 0) "No upload" else "$uploadCount upload(s)",
                    style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                    color = Color(0xFF7A7A7A),
                )
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            if (showUpload) {
                Box(
                    modifier = Modifier
                        .clip(androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                        .background(Color.White)
                        .clickable { onUpload() }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    androidx.compose.material3.Text(
                        text = if (uploadCount == 0) "Upload" else "Add",
                        style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                        color = colorSplashOrange,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
            Box(
                modifier = Modifier
                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                    .background(if (checked) colorSplashOrange else Color.White)
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center,
            ) {
                androidx.compose.material3.Text(
                    text = if (checked) "Done" else "Tap",
                    style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                    color = if (checked) Color.White else Color(0xFF7A7A7A),
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
private fun StepActions(
    step: Int,
    isSaving: Boolean,
    onBack: () -> Unit,
    onNext: () -> Unit,
    onSubmit: () -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
        if (step > 0) {
            androidx.compose.material3.Text(
                text = "Back",
                modifier = Modifier
                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
                    .clickable(enabled = !isSaving) { onBack() }
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                color = Color(0xFF7A7A7A),
                fontWeight = FontWeight.SemiBold,
            )
        } else {
            Spacer(modifier = Modifier.height(0.dp))
        }

        if (step < 3) {
            PrimaryButton(
                text = "Next",
                enabled = !isSaving,
                onClick = onNext,
                modifier = Modifier.weight(1f),
            )
        } else {
            PrimaryButton(
                text = if (isSaving) "Saving..." else "Save Survey",
                enabled = !isSaving,
                onClick = onSubmit,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

