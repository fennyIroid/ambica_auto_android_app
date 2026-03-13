package com.ambica.auto.app.ux.container.branches.detail

import com.ambica.auto.app.data.source.local.session.SessionStore
import com.ambica.auto.app.data.source.remote.helper.NetworkResult
import com.ambica.auto.app.data.source.remote.repository.ApiRepository
import com.ambica.auto.app.navigation.NavigationAction
import com.ambica.auto.app.ux.container.branches.edit.EditBranchRoute
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class GetBranchDetailUiStateUseCase @Inject constructor(
    private val apiRepository: ApiRepository,
    private val sessionStore: SessionStore,
) {
    private val state = MutableStateFlow(BranchDetailDataState())

    operator fun invoke(
        branchId: String,
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
    ): BranchDetailUiState {
        sessionStore.sessionFlow.onEach { session ->
            state.update { it.copy(canEditBranch = session.role?.canManageBranches() == true) }
        }.launchIn(coroutineScope)

        fetchBranchDetail(branchId, coroutineScope)

        return BranchDetailUiState(
            stateFlow = state,
            event = { event ->
                when (event) {
                    BranchDetailUiEvent.OnBack -> navigate(NavigationAction.Pop())
                    BranchDetailUiEvent.OnRetry -> fetchBranchDetail(branchId, coroutineScope)
                    BranchDetailUiEvent.OnDismissError -> state.update { it.copy(error = null) }
                    BranchDetailUiEvent.OnEditBranch -> navigate(NavigationAction.Navigate(EditBranchRoute.createRoute(branchId)))
                }
            },
        )
    }

    private fun fetchBranchDetail(branchId: String, coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            state.update { it.copy(isLoading = true, error = null) }
            val result = apiRepository.getBranchDetail(branchId)
                .first { it !is NetworkResult.Loading }

            when (result) {
                is NetworkResult.Success ->
                    state.update {
                        it.copy(
                            isLoading = false,
                            branch = result.data?.data,
                        )
                    }
                is NetworkResult.Error ->
                    state.update { it.copy(isLoading = false, error = result.message ?: "Failed to load branch details") }
                is NetworkResult.UnAuthenticated ->
                    state.update { it.copy(isLoading = false, error = "Session expired. Please log in again.") }
                else -> state.update { it.copy(isLoading = false) }
            }
        }
    }
}
