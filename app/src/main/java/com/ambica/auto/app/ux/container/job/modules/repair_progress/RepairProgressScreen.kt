package com.ambica.auto.app.ux.container.job.modules.repair_progress

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
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
import com.ambica.auto.app.model.domain.job.JobStage
import com.ambica.auto.app.ui.compose.common.PhotoGridWithUpload
import com.ambica.auto.app.ui.compose.common.PhotoPreviewModal
import com.ambica.auto.app.ui.compose.common.PrimaryButton
import com.ambica.auto.app.ui.theme.colorSplashOrange
import com.ambica.auto.app.ux.container.job.modules.JobModuleScaffold
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun RepairProgressScreen(
    navController: NavController,
    jobId: String,
    viewModel: RepairProgressViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState
    val state by uiState.stateFlow.collectAsStateWithLifecycle()

    LaunchedEffect(jobId) {
        uiState.event(RepairProgressUiEvent.OnSetJobId(jobId))
    }

    val scrollState = rememberScrollState()
    val job = state.job
    val currentStage = job?.stage ?: JobStage.DISMANTLING
    val allPhotoIds = remember(job) { job?.progress?.flatMap { it.photos }?.map { it.id } ?: emptyList() }

    JobModuleScaffold(title = "Update Repair Progress", navController = navController) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
        ) {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(Modifier.padding(14.dp)) {
                    Text(
                        text = "JOB CARD: #${job?.jobCardNumber ?: "—"}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = colorSplashOrange,
                    )
                    Spacer(Modifier.height(14.dp))

                    val stages = JobStage.repairStages
                    val currentIndex = stages.indexOf(currentStage)
                    stages.forEachIndexed { index, stage ->
                        val isCompleted = index < currentIndex
                        val isInProgress = index == currentIndex
                        StageTimelineRow(
                            stage = stage,
                            isCompleted = isCompleted,
                            isInProgress = isInProgress,
                            showConnector = index < stages.lastIndex,
                            completedAt = if (isCompleted && job != null) formatStageDate(job.gateEntryAtMillis, job.timeline.maxOfOrNull { it.atMillis }) else null,
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Select Current Stage",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF444444),
            )
            Spacer(Modifier.height(6.dp))
            StageDropdown(
                currentStage = currentStage,
                stageExpanded = state.stageExpanded,
                onExpandedChange = { uiState.event(RepairProgressUiEvent.OnStageExpandedChange(it)) },
                onStageSelected = { uiState.event(RepairProgressUiEvent.OnStageSelected(it)) },
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Progress Notes",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF444444),
            )
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = state.note,
                onValueChange = { uiState.event(RepairProgressUiEvent.OnNoteChange(it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                placeholder = { Text("Detail any findings or delays here...", color = Color(0xFF888888)) },
                minLines = 4,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colorSplashOrange,
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    cursorColor = colorSplashOrange,
                    focusedTextColor = Color(0xFF1D1D1D),
                    unfocusedTextColor = Color(0xFF1D1D1D),
                ),
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Progress Photos",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF444444),
            )
            Spacer(Modifier.height(8.dp))
            PhotoGridWithUpload(
                photoIds = allPhotoIds,
                modifier = Modifier.height(200.dp),
                onPhotoClick = { index -> uiState.event(RepairProgressUiEvent.OnPreviewPhotoIndex(index)) },
                onUploadClick = { uiState.event(RepairProgressUiEvent.OnUploadPhoto) },
            )
            val previewIndex = state.previewPhotoIndex
            if (previewIndex != null) {
                PhotoPreviewModal(
                    photoIndex = previewIndex,
                    totalCount = allPhotoIds.size,
                    onDismiss = { uiState.event(RepairProgressUiEvent.OnPreviewPhotoIndex(null)) },
                )
            }

            Spacer(Modifier.height(24.dp))

            PrimaryButton(
                text = if (state.isWorking) "Updating..." else "Update Status",
                enabled = !state.isWorking,
                onClick = { uiState.event(RepairProgressUiEvent.OnUpdateStatus) },
            )
            Spacer(Modifier.height(96.dp))
        }
    }
}

@Composable
private fun StageDropdown(
    currentStage: JobStage,
    stageExpanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onStageSelected: (JobStage) -> Unit,
) {
    Box {
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F8F8)),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onExpandedChange(true) },
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = currentStage.label,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF1D1D1D),
                )
                Text(
                    text = "▼",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFF666666),
                )
            }
        }
        DropdownMenu(
            expanded = stageExpanded,
            onDismissRequest = { onExpandedChange(false) },
        ) {
            JobStage.repairStages.forEach { stage ->
                DropdownMenuItem(
                    text = { Text(stage.label, color = Color(0xFF1D1D1D), style = MaterialTheme.typography.bodyMedium) },
                    onClick = { onStageSelected(stage) },
                )
            }
        }
    }
}

@Composable
private fun StageTimelineRow(
    stage: JobStage,
    isCompleted: Boolean,
    isInProgress: Boolean,
    showConnector: Boolean,
    completedAt: String?,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(
                        when {
                            isCompleted -> colorSplashOrange
                            isInProgress -> colorSplashOrange.copy(alpha = 0.9f)
                            else -> Color(0xFFE8E8E8)
                        },
                    ),
                contentAlignment = Alignment.Center,
            ) {
                if (isCompleted) {
                    Text("✓", color = Color.White, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                } else if (isInProgress) {
                    Text("⋯", color = Color.White, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                }
            }
            if (showConnector) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(32.dp)
                        .background(if (isCompleted) colorSplashOrange else Color(0xFFE8E8E8)),
                )
            }
        }
        Spacer(Modifier.size(12.dp))
        Column(modifier = Modifier.padding(bottom = if (showConnector) 0.dp else 4.dp)) {
            Text(
                text = stage.label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = when {
                    isCompleted || isInProgress -> colorSplashOrange
                    else -> Color(0xFF9A9A9A)
                },
            )
            Text(
                text = when {
                    isCompleted -> completedAt ?: "Completed"
                    isInProgress -> "Currently In Progress"
                    else -> "Pending"
                },
                style = MaterialTheme.typography.bodySmall,
                color = when {
                    isCompleted || isInProgress -> Color(0xFF888888)
                    else -> Color(0xFFB0B0B0)
                },
            )
        }
    }
}

private fun formatStageDate(gateEntryAt: Long, lastTimelineAt: Long?): String {
    val millis = lastTimelineAt ?: gateEntryAt
    val sdf = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())
    return "Completed on ${sdf.format(Date(millis))}"
}

