package com.ambica.auto.app.ux.container.branches

import android.content.Context
import com.ambica.auto.app.data.source.local.session.SessionStore
import com.ambica.auto.app.data.source.remote.helper.NetworkResult
import com.ambica.auto.app.data.source.remote.repository.ApiRepository
import com.ambica.auto.app.navigation.NavigationAction
import com.ambica.auto.app.ux.container.branches.create.CreateBranchRoute
import com.ambica.auto.app.ux.container.branches.detail.BranchDetailRoute
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class GetBranchesUiStateUseCase @Inject constructor(
    private val apiRepository: ApiRepository,
    private val sessionStore: SessionStore,
) {
    private val state = MutableStateFlow(BranchesDataState())

    operator fun invoke(
        context: Context,
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
    ): BranchesUiState {
        sessionStore.sessionFlow.onEach { session ->
            state.update { it.copy(canCreateBranch = session.role?.canManageBranches() == true) }
        }.launchIn(coroutineScope)

        fetchBranches(coroutineScope)

        return BranchesUiState(
            stateFlow = state,
            event = { event ->
                when (event) {
                    is BranchesUiEvent.OnSearchChange -> {
                        state.update { it.copy(search = event.value) }
                        fetchBranches(coroutineScope)
                    }
                    is BranchesUiEvent.OnStatusFilterChange -> {
                        state.update { it.copy(statusFilter = event.filter) }
                        fetchBranches(coroutineScope)
                    }
                    BranchesUiEvent.OnRetry -> fetchBranches(coroutineScope)
                    is BranchesUiEvent.OnShowSuccess -> state.update { it.copy(successMessage = event.message) }
                    BranchesUiEvent.OnDismissError -> state.update { it.copy(error = null) }
                    BranchesUiEvent.OnDismissSuccess -> state.update { it.copy(successMessage = null) }
                    BranchesUiEvent.OnBack -> navigate(NavigationAction.Pop())
                    BranchesUiEvent.OnCreateBranch ->
                        navigate(NavigationAction.Navigate(CreateBranchRoute.createRoute()))
                    is BranchesUiEvent.OnDeleteBranch -> deleteBranch(event.branchId, coroutineScope)
                    is BranchesUiEvent.OnBranchClick ->
                        navigate(NavigationAction.Navigate(BranchDetailRoute.createRoute(event.branchId)))
                }
            },
        )
    }

    private fun fetchBranches(coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            val s = state.value
            state.update { it.copy(isLoading = true, error = null) }
            val search = s.search.trim().ifBlank { null }
            val status = s.statusFilter.apiValue

            val result = apiRepository.getBranches(search = search, status = status)
                .first { it !is NetworkResult.Loading }

            when (result) {
                is NetworkResult.Success ->
                    state.update {
                        it.copy(
                            isLoading = false,
                            branches = result.data?.data.orEmpty(),
                        )
                    }
                is NetworkResult.Error ->
                    state.update { it.copy(isLoading = false, error = result.message ?: "Failed to load branches") }
                is NetworkResult.UnAuthenticated ->
                    state.update { it.copy(isLoading = false, error = "Session expired. Please log in again.") }
                else -> state.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun deleteBranch(branchId: String, coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            state.update { it.copy(deletingBranchId = branchId, error = null) }
            val result = apiRepository.deleteBranch(branchId)
                .first { it !is NetworkResult.Loading }

            when (result) {
                is NetworkResult.Success -> {
                    state.update {
                        it.copy(
                            deletingBranchId = null,
                            successMessage = "Branch deleted successfully.",
                        )
                    }
                    fetchBranches(coroutineScope)
                }
                is NetworkResult.Error ->
                    state.update {
                        it.copy(
                            deletingBranchId = null,
                            error = result.message ?: "Failed to delete branch.",
                        )
                    }
                is NetworkResult.UnAuthenticated ->
                    state.update {
                        it.copy(
                            deletingBranchId = null,
                            error = "Session expired. Please log in again.",
                        )
                    }
                else -> state.update { it.copy(deletingBranchId = null) }
            }
        }
    }
}
