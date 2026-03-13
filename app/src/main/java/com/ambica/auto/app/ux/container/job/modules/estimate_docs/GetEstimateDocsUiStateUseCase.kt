package com.ambica.auto.app.ux.container.job.modules.estimate_docs

import android.content.Context
import com.ambica.auto.app.data.source.local.demo.DemoRepository
import com.ambica.auto.app.model.domain.job.JobId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class GetEstimateDocsUiStateUseCase @Inject constructor(
    private val demoRepository: DemoRepository,
) {
    private val state = MutableStateFlow(EstimateDocsDataState())
    private var observeJob: Job? = null

    operator fun invoke(
        context: Context,
        coroutineScope: CoroutineScope,
    ): EstimateDocsUiState {
        return EstimateDocsUiState(
            stateFlow = state,
            event = { event ->
                when (event) {
                    is EstimateDocsUiEvent.OnSetJobId -> bindJob(coroutineScope, event.jobId)
                    is EstimateDocsUiEvent.OnItemNameChange -> {
                        state.update { s ->
                            val list = s.items.toMutableList()
                            val idx = event.index.coerceIn(0, list.lastIndex.coerceAtLeast(0))
                            if (list.isNotEmpty()) list[idx] = list[idx].copy(name = event.value)
                            s.copy(items = list)
                        }
                    }
                    is EstimateDocsUiEvent.OnItemCostChange -> {
                        state.update { s ->
                            val list = s.items.toMutableList()
                            val idx = event.index.coerceIn(0, list.lastIndex.coerceAtLeast(0))
                            if (list.isNotEmpty()) list[idx] = list[idx].copy(cost = event.value)
                            s.copy(items = list)
                        }
                    }
                    EstimateDocsUiEvent.OnAddItem -> state.update { it.copy(items = it.items + EstimateItem("", "0.00")) }
                    is EstimateDocsUiEvent.OnApprovalChange -> state.update { it.copy(approval = event.value) }
                    EstimateDocsUiEvent.OnSave -> saveEstimate(coroutineScope)
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
                .map { it }
                .distinctUntilChanged()
                .collect { job ->
                    state.update { it.copy(job = job) }
                }
        }
    }

    private fun saveEstimate(coroutineScope: CoroutineScope) {
        val jobId = state.value.jobId ?: return
        val amount = state.value.items.sumOf { it.cost.toDoubleOrNull() ?: 0.0 } * 1.18
        coroutineScope.launch {
            state.update { it.copy(isSaving = true) }
            demoRepository.updateEstimate(JobId(jobId), amount, pdfName = "EST-${amount.toInt()}.pdf")
            state.update { it.copy(isSaving = false) }
        }
    }
}

