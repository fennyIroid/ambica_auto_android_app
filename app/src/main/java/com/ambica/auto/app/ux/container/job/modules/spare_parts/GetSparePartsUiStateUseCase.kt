package com.ambica.auto.app.ux.container.job.modules.spare_parts

import android.content.Context
import com.ambica.auto.app.data.source.local.demo.DemoRepository
import com.ambica.auto.app.model.domain.job.JobId
import com.ambica.auto.app.model.domain.job.PartItem
import com.ambica.auto.app.model.domain.job.PartStatus
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GetSparePartsUiStateUseCase @Inject constructor(
    private val demoRepository: DemoRepository,
) {
    private val state = MutableStateFlow(SparePartsDataState())
    private var observeJob: Job? = null

    operator fun invoke(
        context: Context,
        coroutineScope: CoroutineScope,
    ): SparePartsUiState {
        return SparePartsUiState(
            stateFlow = state,
            event = { event ->
                when (event) {
                    is SparePartsUiEvent.OnSetJobId -> bindJob(coroutineScope, event.jobId)
                    SparePartsUiEvent.OnAddQuickPart -> addQuickPart(coroutineScope)
                    is SparePartsUiEvent.OnCycleStatus -> cycleStatus(coroutineScope, event.part)
                }
            },
        )
    }

    private fun bindJob(coroutineScope: CoroutineScope, jobId: String) {
        if (state.value.jobId == jobId) return
        state.update { it.copy(jobId = jobId) }
        observeJob?.cancel()
        observeJob = coroutineScope.launch {
            demoRepository.jobFlow(JobId(jobId))
                .collect { job ->
                    state.update { it.copy(job = job) }
                }
        }
    }

    private fun addQuickPart(coroutineScope: CoroutineScope) {
        val jobId = state.value.jobId ?: return
        coroutineScope.launch {
            state.update { it.copy(isWorking = true) }
            demoRepository.upsertPart(
                JobId(jobId),
                PartItem(
                    id = UUID.randomUUID().toString(),
                    name = "New Part",
                    status = PartStatus.REQUIRED,
                ),
            )
            state.update { it.copy(isWorking = false) }
        }
    }

    private fun cycleStatus(coroutineScope: CoroutineScope, part: PartItem) {
        val jobId = state.value.jobId ?: return
        val order = listOf(
            PartStatus.REQUIRED,
            PartStatus.ORDERED,
            PartStatus.RECEIVED,
            PartStatus.INSTALLED,
            PartStatus.NOT_AVAILABLE,
            PartStatus.ALTERNATIVE_USED,
        )
        val idx = order.indexOf(part.status).coerceAtLeast(0)
        val next = order[(idx + 1) % order.size]
        coroutineScope.launch {
            state.update { it.copy(isWorking = true) }
            demoRepository.setPartStatus(JobId(jobId), part.id, next)
            state.update { it.copy(isWorking = false) }
        }
    }
}

