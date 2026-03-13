package com.ambica.auto.app.ux.container.job.create

import android.content.Context
import androidx.navigation.navOptions
import com.ambica.auto.app.data.source.local.datastore.AppPreferenceDataStore
import com.ambica.auto.app.data.source.local.session.SessionStore
import com.ambica.auto.app.domain.repository.JobService
import com.ambica.auto.app.model.domain.job.Job
import com.ambica.auto.app.model.domain.job.JobDocument
import com.ambica.auto.app.model.domain.job.JobId
import com.ambica.auto.app.model.domain.job.JobStatus
import com.ambica.auto.app.navigation.NavigationAction
import com.ambica.auto.app.ux.container.job.hub.JobDetailsHubRoute
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

class GetGateEntryUiStateUseCase @Inject constructor(
    private val sessionStore: SessionStore,
    private val jobService: JobService,
    private val appPreferenceDataStore: AppPreferenceDataStore,
) {
    private val state = MutableStateFlow(GateEntryDataState())

    operator fun invoke(
        context: Context,
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit
    ): GateEntryUiState {
        return GateEntryUiState(
            stateFlow = state,
            event = { event ->
                when (event) {
                    is GateEntryUiEvent.OnVehicleNumberChange -> state.update { it.copy(vehicleNumber = event.value, error = null) }
                    is GateEntryUiEvent.OnModelChange -> state.update { it.copy(model = event.value, error = null) }
                    is GateEntryUiEvent.OnJobCardChange -> state.update { it.copy(jobCardNumber = event.value, error = null) }
                    is GateEntryUiEvent.OnClaimChange -> state.update { it.copy(claimNumber = event.value, error = null) }
                    is GateEntryUiEvent.OnCustomerNameChange -> state.update { it.copy(customerName = event.value, error = null) }
                    is GateEntryUiEvent.OnCustomerPhoneChange -> state.update { it.copy(customerPhone = event.value, error = null) }
                    is GateEntryUiEvent.OnDamageNotesChange -> state.update { it.copy(damageNotes = event.value, error = null) }
                    is GateEntryUiEvent.OnMissingItemChange -> state.update { it.copy(missingItem = event.value, error = null) }

                    is GateEntryUiEvent.OnPhotoSlotClick -> {
                        val idx = event.index.coerceIn(0, 3)
                        state.update { s ->
                            val list = s.photos.toMutableList()
                            list[idx] = list[idx] ?: "demo://photo/${UUID.randomUUID()}"
                            s.copy(photos = list)
                        }
                    }

                    GateEntryUiEvent.OnBack -> navigate(NavigationAction.Pop())
                    GateEntryUiEvent.OnCancel -> navigate(NavigationAction.Pop())

                    GateEntryUiEvent.OnSubmit -> {
                        val s = state.value
                        val missingPhotos = s.photos.any { it == null }
                        if (s.vehicleNumber.isBlank()) {
                            state.update { it.copy(error = "Enter vehicle number") }
                            return@GateEntryUiState
                        }
                        if (s.jobCardNumber.isBlank()) {
                            state.update { it.copy(error = "Enter job card number") }
                            return@GateEntryUiState
                        }
                        if (s.customerName.isBlank()) {
                            state.update { it.copy(error = "Enter customer name") }
                            return@GateEntryUiState
                        }
                        if (missingPhotos) {
                            state.update { it.copy(error = "Please capture all 4 entry photos") }
                            return@GateEntryUiState
                        }

                        coroutineScope.launch {
                            state.update { it.copy(isLoading = true) }
                            val branchId = appPreferenceDataStore.getSelectedBranchId() ?: "hazira"
                            val staffName = sessionStore.sessionFlow.value.staffName ?: "Staff"
                            val jobId = JobId.random()
                            val job = Job(
                                id = jobId,
                                vehicleNumber = s.vehicleNumber.trim(),
                                customerName = s.customerName.trim(),
                                customerPhone = s.customerPhone.trim().ifBlank { null },
                                branchId = branchId,
                                jobCardNumber = s.jobCardNumber.trim(),
                                claimNumber = s.claimNumber.trim().ifBlank { null },
                                createdBy = staffName,
                                gateEntryAtMillis = System.currentTimeMillis(),
                                status = JobStatus.NEW_ENTRY,
                                stage = com.ambica.auto.app.model.domain.job.JobStage.GATE_ENTRY_COMPLETED,
                                visibleNotes = listOf(
                                    s.damageNotes.trim().takeIf { it.isNotBlank() }?.let { "Damage: $it" },
                                    s.missingItem.trim().takeIf { it.isNotBlank() }?.let { "Missing items: $it" },
                                ).filterNotNull().joinToString("\n").ifBlank { "" },
                                entryPhotoUris = s.photos.filterNotNull(),
                                documents = listOf(
                                    JobDocument(id = "rc", title = "Registration Certificate (RC)", received = false),
                                    JobDocument(id = "insurance", title = "Insurance Policy", received = false),
                                    JobDocument(id = "kyc", title = "KYC Documents", received = false),
                                    JobDocument(id = "claimForm", title = "Claim Form (Optional)", required = false, received = false),
                                )
                            )
                            val result = jobService.createGateEntryJob(job)
                            if (result.isSuccess) {
                                val options = navOptions {
                                    popUpTo(GateEntryRoute.routeDefinition.value) { inclusive = true }
                                }
                                navigate(NavigationAction.NavigateWithOptions(JobDetailsHubRoute.createRoute(jobId.value), options))
                            } else {
                                state.update { it.copy(isLoading = false, error = "Failed to create job") }
                            }
                        }
                    }
                }
            }
        )
    }
}

