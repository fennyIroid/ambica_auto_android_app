package com.ambica.auto.app.ux.main.profile.edit

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class EditProfileUiState(
    val stateFlow: StateFlow<EditProfileDataState> = MutableStateFlow(EditProfileDataState()),
    val event: (EditProfileUiEvent) -> Unit = {},
)

data class EditProfileDataState(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val phone: String = "",
    val dateOfBirth: String = "",
    val gender: Int? = null,
    val address: String = "",
    val emergencyContactName: String = "",
    val emergencyContactPhone: String = "",
    val emergencyContactRelationship: String = "",
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val error: String? = null,
    val saveSuccess: Boolean = false,
)

sealed interface EditProfileUiEvent {
    data class OnFirstNameChange(val value: String) : EditProfileUiEvent
    data class OnLastNameChange(val value: String) : EditProfileUiEvent
    data class OnPhoneChange(val value: String) : EditProfileUiEvent
    data class OnDateOfBirthChange(val value: String) : EditProfileUiEvent
    data class OnGenderChange(val value: Int?) : EditProfileUiEvent
    data class OnAddressChange(val value: String) : EditProfileUiEvent
    data class OnEmergencyContactNameChange(val value: String) : EditProfileUiEvent
    data class OnEmergencyContactPhoneChange(val value: String) : EditProfileUiEvent
    data class OnEmergencyContactRelationshipChange(val value: String) : EditProfileUiEvent
    data object OnSaveClick : EditProfileUiEvent
    data object OnDismissError : EditProfileUiEvent
    data object OnBack : EditProfileUiEvent
}
