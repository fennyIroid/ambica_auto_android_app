package com.ambica.auto.app.ux.container.job.modules.repair_progress

import com.ambica.auto.app.model.domain.job.Job
import com.ambica.auto.app.model.domain.job.JobStage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class RepairProgressUiState(
    val stateFlow: StateFlow<RepairProgressDataState> = MutableStateFlow(RepairProgressDataState()),
    val event: (RepairProgressUiEvent) -> Unit = {},
)

data class RepairProgressDataState(
    val jobId: String? = null,
    val job: Job? = null,
    val note: String = "",
    val stageExpanded: Boolean = false,
    val previewPhotoIndex: Int? = null,
    val isWorking: Boolean = false,
)

sealed interface RepairProgressUiEvent {
    data class OnSetJobId(val jobId: String) : RepairProgressUiEvent
    data class OnNoteChange(val value: String) : RepairProgressUiEvent
    data class OnStageExpandedChange(val value: Boolean) : RepairProgressUiEvent
    data class OnStageSelected(val stage: JobStage) : RepairProgressUiEvent
    data class OnPreviewPhotoIndex(val index: Int?) : RepairProgressUiEvent
    data object OnUploadPhoto : RepairProgressUiEvent
    data object OnUpdateStatus : RepairProgressUiEvent
}

