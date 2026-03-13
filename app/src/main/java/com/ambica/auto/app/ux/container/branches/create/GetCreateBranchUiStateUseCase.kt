package com.ambica.auto.app.ux.container.branches.create

import android.util.Patterns
import com.ambica.auto.app.data.source.remote.helper.NetworkResult
import com.ambica.auto.app.data.source.remote.model.branch.CreateBranchRequest
import com.ambica.auto.app.data.source.remote.repository.ApiRepository
import com.ambica.auto.app.navigation.NavigationAction
import com.ambica.auto.app.navigation.PopResultKeyValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class GetCreateBranchUiStateUseCase @Inject constructor(
    private val apiRepository: ApiRepository,
) {
    private val state = MutableStateFlow(CreateBranchDataState())

    operator fun invoke(
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
    ): CreateBranchUiState {
        return CreateBranchUiState(
            stateFlow = state,
            event = { event ->
                when (event) {
                    is CreateBranchUiEvent.OnCodeChange -> state.update { it.copy(code = event.value) }
                    is CreateBranchUiEvent.OnNameChange -> state.update { it.copy(name = event.value) }
                    is CreateBranchUiEvent.OnLocationChange -> state.update { it.copy(location = event.value) }
                    is CreateBranchUiEvent.OnTaglineChange -> state.update { it.copy(tagline = event.value) }
                    is CreateBranchUiEvent.OnAddressChange -> state.update { it.copy(address = event.value) }
                    is CreateBranchUiEvent.OnPhoneChange -> state.update { it.copy(phone = event.value) }
                    is CreateBranchUiEvent.OnEmailChange -> state.update { it.copy(email = event.value) }
                    is CreateBranchUiEvent.OnStatusChange -> state.update { it.copy(status = event.status) }
                    CreateBranchUiEvent.OnBack -> navigate(NavigationAction.Pop())
                    CreateBranchUiEvent.OnDismissError -> state.update { it.copy(error = null) }
                    CreateBranchUiEvent.OnDismissSuccess -> state.update { it.copy(successMessage = null) }
                    CreateBranchUiEvent.OnSubmit -> submit(coroutineScope, navigate)
                }
            },
        )
    }

    private fun submit(coroutineScope: CoroutineScope, navigate: (NavigationAction) -> Unit) {
        val s = state.value
        val validationError = validate(s)
        if (validationError != null) {
            state.update { it.copy(error = validationError) }
            return
        }

        coroutineScope.launch {
            state.update { it.copy(isSaving = true, error = null) }
            val request = CreateBranchRequest(
                code = s.code.trim(),
                name = s.name.trim(),
                location = s.location.trim(),
                tagline = s.tagline.trim().ifBlank { null },
                address = s.address.trim(),
                phone = s.phone.trim(),
                email = s.email.trim().ifBlank { null },
                status = s.status,
            )

            val result = apiRepository.createBranch(request)
                .first { it !is NetworkResult.Loading }

            when (result) {
                is NetworkResult.Success -> {
                    state.update { it.copy(isSaving = false) }
                    navigate(NavigationAction.PopWithResult(listOf(
                        PopResultKeyValue("refresh_branches", true),
                        PopResultKeyValue("branch_success_message", "Branch created successfully!"),
                    )))
                }
                is NetworkResult.Error ->
                    state.update { it.copy(isSaving = false, error = result.message ?: "Failed to create branch.") }
                is NetworkResult.UnAuthenticated ->
                    state.update { it.copy(isSaving = false, error = "Session expired. Please log in again.") }
                else -> state.update { it.copy(isSaving = false) }
            }
        }
    }

    private fun validate(s: CreateBranchDataState): String? {
        if (s.code.isBlank()) return "Branch code is required."
        if (s.code.length > 10) return "Branch code must be 10 characters or less."
        if (s.name.isBlank()) return "Branch name is required."
        if (s.name.length > 120) return "Branch name must be 120 characters or less."
        if (s.location.isBlank()) return "Location is required."
        if (s.address.isBlank()) return "Address is required."
        if (s.phone.isBlank()) return "Phone number is required."
        if (!s.phone.trim().matches(Regex("^\\+?[0-9]{7,15}$"))) return "Enter a valid phone number (e.g. +919876543210)."
        if (s.email.isNotBlank() && !Patterns.EMAIL_ADDRESS.matcher(s.email.trim()).matches()) {
            return "Enter a valid email address."
        }
        return null
    }
}
