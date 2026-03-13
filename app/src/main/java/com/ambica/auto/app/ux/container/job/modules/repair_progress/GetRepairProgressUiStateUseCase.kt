package com.ambica.auto.app.ux.container.job.modules.repair_progress

import android.content.Context
import com.ambica.auto.app.model.domain.job.JobId
import com.ambica.auto.app.model.domain.job.JobStage
import com.ambica.auto.app.model.domain.job.ProgressPhoto
import com.ambica.auto.app.domain.repository.JobService
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GetRepairProgressUiStateUseCase @Inject constructor(
    private val jobService: JobService,
) {
    private val state = MutableStateFlow(RepairProgressDataState())
    private var observeJob: Job? = null

    operator fun invoke(
        context: Context,
        coroutineScope: CoroutineScope,
    ): RepairProgressUiState {
        return RepairProgressUiState(
            stateFlow = state,
            event = { event ->
                when (event) {
                    is RepairProgressUiEvent.OnSetJobId -> bindJob(coroutineScope, event.jobId)
                    is RepairProgressUiEvent.OnNoteChange -> state.update { it.copy(note = event.value) }
                    is RepairProgressUiEvent.OnStageExpandedChange -> state.update { it.copy(stageExpanded = event.value) }
                    is RepairProgressUiEvent.OnStageSelected -> setStage(coroutineScope, event.stage)
                    is RepairProgressUiEvent.OnPreviewPhotoIndex -> state.update { it.copy(previewPhotoIndex = event.index) }
                    RepairProgressUiEvent.OnUploadPhoto -> addPhoto(coroutineScope)
                    RepairProgressUiEvent.OnUpdateStatus -> addPhoto(coroutineScope)
                }
            },
        )
    }

    private fun bindJob(coroutineScope: CoroutineScope, jobId: String) {
        if (state.value.jobId == jobId) return
        state.update { it.copy(jobId = jobId) }
        observeJob?.cancel()
        observeJob = coroutineScope.launch {
            jobService.jobFlow(JobId(jobId)).collect { job ->
                state.update { it.copy(job = job) }
            }
        }
    }

    private fun setStage(coroutineScope: CoroutineScope, stage: JobStage) {
        val jobId = state.value.jobId ?: return
        coroutineScope.launch {
            state.update { it.copy(isWorking = true, stageExpanded = false) }
            jobService.updateJobStage(JobId(jobId), stage)
            state.update { it.copy(isWorking = false) }
        }
    }

    private fun addPhoto(coroutineScope: CoroutineScope) {
        val jobId = state.value.jobId ?: return
        val stage = state.value.job?.stage ?: JobStage.DISMANTLING
        val note = state.value.note
        coroutineScope.launch {
            state.update { it.copy(isWorking = true) }
            jobService.addProgressPhoto(
                JobId(jobId),
                stage,
                ProgressPhoto(
                    id = UUID.randomUUID().toString(),
                    dateMillis = System.currentTimeMillis(),
                    note = note,
                    uri = "demo://progress/${UUID.randomUUID()}",
                ),
            )
            state.update { it.copy(isWorking = false, note = "") }
        }
    }
}

