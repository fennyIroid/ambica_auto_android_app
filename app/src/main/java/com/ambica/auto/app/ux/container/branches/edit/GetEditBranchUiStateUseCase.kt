package com.ambica.auto.app.ux.container.branches.edit

import android.util.Patterns
import com.ambica.auto.app.data.source.remote.helper.NetworkResult
import com.ambica.auto.app.data.source.remote.model.branch.CreateBranchRequest
import com.ambica.auto.app.data.source.remote.repository.ApiRepository
import com.ambica.auto.app.navigation.NavigationAction
import com.ambica.auto.app.navigation.PopResultKeyValue
import com.ambica.auto.app.ux.container.branches.BranchesRoute
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class GetEditBranchUiStateUseCase @Inject constructor(
    private val apiRepository: ApiRepository,
) {
    private val state = MutableStateFlow(EditBranchDataState())

    operator fun invoke(
        branchId: String,
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
    ): EditBranchUiState {
        loadBranch(branchId, coroutineScope)

        return EditBranchUiState(
            stateFlow = state,
            event = { event ->
                when (event) {
                    is EditBranchUiEvent.OnCodeChange -> state.update { it.copy(code = event.value) }
                    is EditBranchUiEvent.OnNameChange -> state.update { it.copy(name = event.value) }
                    is EditBranchUiEvent.OnLocationChange -> state.update { it.copy(location = event.value) }
                    is EditBranchUiEvent.OnTaglineChange -> state.update { it.copy(tagline = event.value) }
                    is EditBranchUiEvent.OnAddressChange -> state.update { it.copy(address = event.value) }
                    is EditBranchUiEvent.OnPhoneChange -> state.update { it.copy(phone = event.value) }
                    is EditBranchUiEvent.OnEmailChange -> state.update { it.copy(email = event.value) }
                    is EditBranchUiEvent.OnStatusChange -> state.update { it.copy(status = event.status) }
                    EditBranchUiEvent.OnBack -> navigate(NavigationAction.Pop())
                    EditBranchUiEvent.OnDismissError -> state.update { it.copy(error = null) }
                    EditBranchUiEvent.OnDismissSuccess -> state.update { it.copy(successMessage = null) }
                    EditBranchUiEvent.OnSubmit -> submit(branchId, coroutineScope, navigate)
                }
            },
        )
    }

    private fun loadBranch(branchId: String, coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            state.update { it.copy(isLoading = true, error = null) }
            val result = apiRepository.getBranchDetail(branchId)
                .first { it !is NetworkResult.Loading }

            when (result) {
                is NetworkResult.Success -> {
                    val branch = result.data?.data
                    state.update {
                        it.copy(
                            isLoading = false,
                            branchId = branchId,
                            code = branch?.code.orEmpty(),
                            name = branch?.name.orEmpty(),
                            location = branch?.location.orEmpty().ifBlank { branch?.city.orEmpty() },
                            tagline = branch?.tagline.orEmpty(),
                            address = branch?.address.orEmpty(),
                            phone = branch?.phone.orEmpty(),
                            email = branch?.email.orEmpty(),
                            status = branch?.status ?: 1,
                        )
                    }
                }
                is NetworkResult.Error ->
                    state.update { it.copy(isLoading = false, error = result.message ?: "Failed to load branch") }
                is NetworkResult.UnAuthenticated ->
                    state.update { it.copy(isLoading = false, error = "Session expired. Please log in again.") }
                else -> state.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun submit(branchId: String, coroutineScope: CoroutineScope, navigate: (NavigationAction) -> Unit) {
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

            val result = apiRepository.updateBranch(branchId, request)
                .first { it !is NetworkResult.Loading }

            when (result) {
                is NetworkResult.Success -> {
                    state.update { it.copy(isSaving = false) }
                    navigate(NavigationAction.PopWithResult(
                        resultValues = listOf(
                            PopResultKeyValue("refresh_branches", true),
                            PopResultKeyValue("branch_success_message", "Branch updated successfully!"),
                        ),
                        popToRouteDefinition = BranchesRoute.routeDefinition,
                        inclusive = false,
                    ))
                }
                is NetworkResult.Error ->
                    state.update { it.copy(isSaving = false, error = result.message ?: "Failed to update branch.") }
                is NetworkResult.UnAuthenticated ->
                    state.update { it.copy(isSaving = false, error = "Session expired. Please log in again.") }
                else -> state.update { it.copy(isSaving = false) }
            }
        }
    }

    private fun validate(s: EditBranchDataState): String? {
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
