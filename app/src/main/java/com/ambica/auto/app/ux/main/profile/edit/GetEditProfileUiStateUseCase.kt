package com.ambica.auto.app.ux.main.profile.edit

import android.content.Context
import com.ambica.auto.app.data.source.local.session.SessionStore
import com.ambica.auto.app.data.source.remote.helper.NetworkResult
import com.ambica.auto.app.data.source.remote.model.auth.UpdateProfileRequest
import com.ambica.auto.app.data.source.remote.repository.ApiRepository
import com.ambica.auto.app.navigation.NavigationAction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class GetEditProfileUiStateUseCase @Inject constructor(
    private val apiRepository: ApiRepository,
    private val sessionStore: SessionStore,
) {
    private val state = MutableStateFlow(EditProfileDataState())

    operator fun invoke(
        context: Context,
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
    ): EditProfileUiState {
        coroutineScope.launch {
            loadProfile()
        }

        return EditProfileUiState(
            stateFlow = state,
            event = { event ->
                when (event) {
                    is EditProfileUiEvent.OnFirstNameChange ->
                        state.update { it.copy(firstName = event.value, error = null) }

                    is EditProfileUiEvent.OnLastNameChange ->
                        state.update { it.copy(lastName = event.value, error = null) }

                    is EditProfileUiEvent.OnPhoneChange ->
                        state.update { it.copy(phone = event.value, error = null) }

                    is EditProfileUiEvent.OnDateOfBirthChange ->
                        state.update { it.copy(dateOfBirth = event.value, error = null) }

                    is EditProfileUiEvent.OnGenderChange ->
                        state.update { it.copy(gender = event.value, error = null) }

                    is EditProfileUiEvent.OnAddressChange ->
                        state.update { it.copy(address = event.value, error = null) }

                    is EditProfileUiEvent.OnEmergencyContactNameChange ->
                        state.update { it.copy(emergencyContactName = event.value, error = null) }

                    is EditProfileUiEvent.OnEmergencyContactPhoneChange ->
                        state.update { it.copy(emergencyContactPhone = event.value, error = null) }

                    is EditProfileUiEvent.OnEmergencyContactRelationshipChange ->
                        state.update { it.copy(emergencyContactRelationship = event.value, error = null) }

                    EditProfileUiEvent.OnDismissError ->
                        state.update { it.copy(error = null) }

                    EditProfileUiEvent.OnBack ->
                        navigate(NavigationAction.Pop())

                    EditProfileUiEvent.OnSaveClick -> {
                        coroutineScope.launch { saveProfile(navigate) }
                    }
                }
            }
        )
    }

    private suspend fun loadProfile() {
        state.update { it.copy(isLoading = true) }
        val result = apiRepository.getProfile().first { it !is NetworkResult.Loading }
        when (result) {
            is NetworkResult.Success -> {
                val profile = result.data
                state.update {
                    it.copy(
                        isLoading = false,
                        firstName = profile?.firstName.orEmpty(),
                        lastName = profile?.lastName.orEmpty(),
                        email = profile?.email.orEmpty(),
                        phone = profile?.phone.orEmpty(),
                        dateOfBirth = profile?.dateOfBirth.orEmpty(),
                        gender = profile?.gender,
                        address = profile?.address.orEmpty(),
                        emergencyContactName = profile?.emergencyContactName.orEmpty(),
                        emergencyContactPhone = profile?.emergencyContactPhone.orEmpty(),
                        emergencyContactRelationship = profile?.emergencyContactRelationship.orEmpty(),
                    )
                }
            }
            is NetworkResult.Error -> {
                state.update { it.copy(isLoading = false, error = result.message ?: "Failed to load profile") }
            }
            is NetworkResult.UnAuthenticated -> {
                state.update { it.copy(isLoading = false, error = "Session expired. Please login again.") }
            }
            else -> Unit
        }
    }

    private suspend fun saveProfile(navigate: (NavigationAction) -> Unit) {
        val current = state.value

        if (current.firstName.isBlank()) {
            state.update { it.copy(error = "First name is required") }
            return
        }

        state.update { it.copy(isSaving = true, error = null) }

        val request = UpdateProfileRequest(
            firstName = current.firstName.trim().takeIf { it.isNotBlank() },
            lastName = current.lastName.trim().takeIf { it.isNotBlank() },
            phone = current.phone.trim().takeIf { it.isNotBlank() },
            dateOfBirth = current.dateOfBirth.trim().takeIf { it.isNotBlank() },
            gender = current.gender,
            address = current.address.trim().takeIf { it.isNotBlank() },
            emergencyContactName = current.emergencyContactName.trim().takeIf { it.isNotBlank() },
            emergencyContactPhone = current.emergencyContactPhone.trim().takeIf { it.isNotBlank() },
            emergencyContactRelationship = current.emergencyContactRelationship.trim().takeIf { it.isNotBlank() },
        )

        val result = apiRepository.updateProfile(request).first { it !is NetworkResult.Loading }
        when (result) {
            is NetworkResult.Success -> {
                result.data?.let { sessionStore.updateFromProfile(it) }
                state.update { it.copy(isSaving = false, saveSuccess = true) }
                navigate(NavigationAction.Pop())
            }
            is NetworkResult.Error -> {
                state.update { it.copy(isSaving = false, error = result.message ?: "Failed to update profile") }
            }
            is NetworkResult.UnAuthenticated -> {
                state.update { it.copy(isSaving = false, error = "Session expired. Please login again.") }
            }
            else -> Unit
        }
    }
}
