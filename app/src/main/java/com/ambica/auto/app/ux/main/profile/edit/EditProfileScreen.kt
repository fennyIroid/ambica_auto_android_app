package com.ambica.auto.app.ux.main.profile.edit

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.ambica.auto.app.R
import com.ambica.auto.app.navigation.HandleNavigation
import com.ambica.auto.app.ui.compose.common.PrimaryButton
import com.ambica.auto.app.ui.theme.colorSplashOrange

@Composable
fun EditProfileScreen(
    navController: NavController,
    viewModel: EditProfileViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState
    val state by uiState.stateFlow.collectAsStateWithLifecycle()
    val event = uiState.event
    val snackbarHostState = remember { SnackbarHostState() }

    HandleNavigation(viewModelNav = viewModel, navController = navController)

    LaunchedEffect(state.error) {
        state.error?.let {
            snackbarHostState.showSnackbar(it)
            event(EditProfileUiEvent.OnDismissError)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF6F7F9))
                .statusBarsPadding(),
        ) {
            TopBar(
                onBack = { event(EditProfileUiEvent.OnBack) }
            )

            if (state.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = colorSplashOrange)
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp),
                ) {
                    Spacer(Modifier.height(20.dp))

                    ProfileAvatar(
                        initials = getInitials(state.firstName, state.lastName)
                    )

                    Spacer(Modifier.height(24.dp))

                    SectionHeader("Personal Information")
                    Spacer(Modifier.height(12.dp))

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                ProfileTextField(
                                    value = state.firstName,
                                    onValueChange = { event(EditProfileUiEvent.OnFirstNameChange(it)) },
                                    label = "First Name",
                                    placeholder = "Enter first name",
                                    modifier = Modifier.weight(1f),
                                )
                                ProfileTextField(
                                    value = state.lastName,
                                    onValueChange = { event(EditProfileUiEvent.OnLastNameChange(it)) },
                                    label = "Last Name",
                                    placeholder = "Enter last name",
                                    modifier = Modifier.weight(1f),
                                )
                            }

                            ProfileTextField(
                                value = state.email,
                                onValueChange = {},
                                label = "Email",
                                placeholder = "",
                                enabled = false,
                            )

                            ProfileTextField(
                                value = state.phone,
                                onValueChange = { event(EditProfileUiEvent.OnPhoneChange(it)) },
                                label = "Phone",
                                placeholder = "+91 9876543210",
                                keyboardType = KeyboardType.Phone,
                            )

                            ProfileTextField(
                                value = state.dateOfBirth,
                                onValueChange = { event(EditProfileUiEvent.OnDateOfBirthChange(it)) },
                                label = "Date of Birth",
                                placeholder = "YYYY-MM-DD",
                            )

                            GenderSelector(
                                selectedGender = state.gender,
                                onGenderSelected = { event(EditProfileUiEvent.OnGenderChange(it)) },
                            )

                            ProfileTextField(
                                value = state.address,
                                onValueChange = { event(EditProfileUiEvent.OnAddressChange(it)) },
                                label = "Address",
                                placeholder = "Enter your address",
                                singleLine = false,
                                maxLines = 3,
                            )
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    SectionHeader("Emergency Contact")
                    Spacer(Modifier.height(12.dp))

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            ProfileTextField(
                                value = state.emergencyContactName,
                                onValueChange = { event(EditProfileUiEvent.OnEmergencyContactNameChange(it)) },
                                label = "Contact Name",
                                placeholder = "Enter contact name",
                            )

                            ProfileTextField(
                                value = state.emergencyContactPhone,
                                onValueChange = { event(EditProfileUiEvent.OnEmergencyContactPhoneChange(it)) },
                                label = "Contact Phone",
                                placeholder = "+91 9876543210",
                                keyboardType = KeyboardType.Phone,
                            )

                            ProfileTextField(
                                value = state.emergencyContactRelationship,
                                onValueChange = { event(EditProfileUiEvent.OnEmergencyContactRelationshipChange(it)) },
                                label = "Relationship",
                                placeholder = "e.g. Spouse, Parent, Friend",
                            )
                        }
                    }

                    Spacer(Modifier.height(32.dp))

                    PrimaryButton(
                        text = if (state.isSaving) "Saving..." else "Save Changes",
                        onClick = { event(EditProfileUiEvent.OnSaveClick) },
                        enabled = !state.isSaving,
                    )

                    Spacer(Modifier.height(32.dp))
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter),
        ) { data ->
            Snackbar(
                snackbarData = data,
                containerColor = Color(0xFFB00020),
                contentColor = Color.White,
            )
        }
    }
}

@Composable
private fun TopBar(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFF2F2F2))
                .clickable(onClick = onBack),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.back_arrow),
                contentDescription = "Back",
                tint = Color(0xFF555555),
                modifier = Modifier.size(20.dp),
            )
        }
        Text(
            text = "Edit Profile",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1D1D1D),
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp),
        )
        Spacer(Modifier.size(36.dp))
    }
}

@Composable
private fun ProfileAvatar(initials: String) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(colorSplashOrange.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = initials,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = colorSplashOrange,
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Tap to change photo",
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF888888),
        )
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.SemiBold,
        color = Color(0xFF5E5E5E),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    maxLines: Int = 1,
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF555555),
        )
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = placeholder,
                    color = Color(0xFFAAAAAA),
                )
            },
            enabled = enabled,
            singleLine = singleLine,
            maxLines = maxLines,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colorSplashOrange,
                unfocusedBorderColor = Color(0xFFE0E0E0),
                disabledBorderColor = Color(0xFFE0E0E0),
                disabledTextColor = Color(0xFF888888),
                cursorColor = colorSplashOrange,
            ),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        )
    }
}

@Composable
private fun GenderSelector(
    selectedGender: Int?,
    onGenderSelected: (Int?) -> Unit,
) {
    Column {
        Text(
            text = "Gender",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF555555),
        )
        Spacer(Modifier.height(8.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            GenderChip(
                label = "Male",
                isSelected = selectedGender == 1,
                onClick = { onGenderSelected(1) },
                modifier = Modifier.weight(1f),
            )
            GenderChip(
                label = "Female",
                isSelected = selectedGender == 2,
                onClick = { onGenderSelected(2) },
                modifier = Modifier.weight(1f),
            )
            GenderChip(
                label = "Other",
                isSelected = selectedGender == 3,
                onClick = { onGenderSelected(3) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun GenderChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = if (isSelected) colorSplashOrange else Color.White
    val textColor = if (isSelected) Color.White else Color(0xFF555555)
    val borderColor = if (isSelected) colorSplashOrange else Color(0xFFE0E0E0)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = textColor,
        )
    }
}

private fun getInitials(firstName: String, lastName: String): String {
    val parts = listOf(firstName, lastName).filter { it.isNotBlank() }
    return if (parts.isEmpty()) {
        "U"
    } else {
        parts.take(2).joinToString("") { it.first().uppercase() }
    }
}
