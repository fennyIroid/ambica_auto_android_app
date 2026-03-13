package com.ambica.auto.app.ux.main.profile.changepassword

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
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
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.ambica.auto.app.R
import com.ambica.auto.app.navigation.HandleNavigation
import com.ambica.auto.app.ui.compose.common.PrimaryButton
import com.ambica.auto.app.ui.theme.colorSplashOrange

@Composable
fun ChangePasswordScreen(
    navController: NavController,
    viewModel: ChangePasswordViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState
    val state by uiState.stateFlow.collectAsStateWithLifecycle()
    val event = uiState.event
    val snackbarHostState = remember { SnackbarHostState() }
    val successSnackbarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current

    HandleNavigation(viewModelNav = viewModel, navController = navController)

    LaunchedEffect(state.error) {
        state.error?.let {
            snackbarHostState.showSnackbar(it)
            event(ChangePasswordUiEvent.OnDismissError)
        }
    }

    LaunchedEffect(state.successMessage) {
        state.successMessage?.let {
            successSnackbarHostState.showSnackbar(it, duration = SnackbarDuration.Short)
            event(ChangePasswordUiEvent.OnDismissSuccess)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                awaitEachGesture {
                    awaitFirstDown(pass = PointerEventPass.Initial)
                    focusManager.clearFocus()
                }
            },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF6F7F9))
                .statusBarsPadding(),
        ) {
            TopBar(onBack = { event(ChangePasswordUiEvent.OnBack) })

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                // Info card
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = colorSplashOrange.copy(alpha = 0.08f)),
                    elevation = CardDefaults.cardElevation(0.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_insurence),
                            contentDescription = null,
                            tint = colorSplashOrange,
                            modifier = Modifier
                                .size(20.dp)
                                .padding(top = 2.dp),
                        )
                        Text(
                            text = "New password must be at least 8 characters, include one uppercase letter and one number.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF7A4500),
                            lineHeight = 18.sp,
                        )
                    }
                }

                // Password fields card
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        PasswordField(
                            label = "Current Password",
                            value = state.currentPassword,
                            visible = state.currentPasswordVisible,
                            onValueChange = { event(ChangePasswordUiEvent.OnCurrentPasswordChange(it)) },
                            onToggleVisibility = { event(ChangePasswordUiEvent.OnToggleCurrentPasswordVisibility) },
                        )
                        FieldDivider()
                        PasswordField(
                            label = "New Password",
                            value = state.newPassword,
                            visible = state.newPasswordVisible,
                            onValueChange = { event(ChangePasswordUiEvent.OnNewPasswordChange(it)) },
                            onToggleVisibility = { event(ChangePasswordUiEvent.OnToggleNewPasswordVisibility) },
                        )
                        FieldDivider()
                        PasswordField(
                            label = "Confirm New Password",
                            value = state.confirmPassword,
                            visible = state.confirmPasswordVisible,
                            onValueChange = { event(ChangePasswordUiEvent.OnConfirmPasswordChange(it)) },
                            onToggleVisibility = { event(ChangePasswordUiEvent.OnToggleConfirmPasswordVisibility) },
                        )
                    }
                }

                // Inline validation hint
                AnimatedVisibility(
                    visible = state.newPassword.isNotBlank(),
                    enter = fadeIn(tween(160)),
                    exit = fadeOut(tween(160)),
                ) {
                    PasswordStrengthHints(password = state.newPassword)
                }

                PrimaryButton(
                    text = if (state.isSaving) "Changing Password..." else "Change Password",
                    onClick = { event(ChangePasswordUiEvent.OnSubmit) },
                    enabled = !state.isSaving,
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(64.dp))
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
        ) { data ->
            Snackbar(
                snackbarData = data,
                containerColor = Color(0xFF2D2D2D),
                contentColor = Color.White,
                shape = RoundedCornerShape(12.dp),
            )
        }

        SnackbarHost(
            hostState = successSnackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
        ) { data ->
            Snackbar(
                snackbarData = data,
                containerColor = Color(0xFF2E7D32),
                contentColor = Color.White,
                shape = RoundedCornerShape(12.dp),
            )
        }
    }
}

@Composable
private fun TopBar(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
                .clickable { onBack() },
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
            text = "Change Password",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1D1D1D),
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp),
        )
    }
}

@Composable
private fun PasswordField(
    label: String,
    value: String,
    visible: Boolean,
    onValueChange: (String) -> Unit,
    onToggleVisibility: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF5E5E5E),
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                Icon(
                    painter = painterResource(if (visible) R.drawable.ic_eye_open else R.drawable.ic_eye_closed),
                    contentDescription = if (visible) "Hide password" else "Show password",
                    tint = Color(0xFF9E9E9E),
                    modifier = Modifier
                        .size(22.dp)
                        .clickable { onToggleVisibility() },
                )
            },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colorSplashOrange,
                unfocusedBorderColor = Color(0xFFE0E0E0),
                focusedTextColor = Color(0xFF1D1D1D),
                unfocusedTextColor = Color(0xFF1D1D1D),
                cursorColor = colorSplashOrange,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
            ),
        )
    }
}

@Composable
private fun FieldDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Color(0xFFF0F0F0)),
    )
}

@Composable
private fun PasswordStrengthHints(password: String) {
    val hasLength = password.length >= 8
    val hasUpper = password.any { it.isUpperCase() }
    val hasDigit = password.any { it.isDigit() }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = "Password requirements",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF9E9E9E),
            )
            StrengthHintRow(label = "At least 8 characters", met = hasLength)
            StrengthHintRow(label = "At least one uppercase letter", met = hasUpper)
            StrengthHintRow(label = "At least one number", met = hasDigit)
        }
    }
}

@Composable
private fun StrengthHintRow(label: String, met: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            modifier = Modifier
                .size(18.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(if (met) colorSplashOrange.copy(alpha = 0.15f) else Color(0xFFF0F0F0)),
            contentAlignment = Alignment.Center,
        ) {
            if (met) {
                Icon(
                    painter = painterResource(R.drawable.ic_check),
                    contentDescription = null,
                    tint = colorSplashOrange,
                    modifier = Modifier.size(10.dp),
                )
            }
        }
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = if (met) Color(0xFF1D1D1D) else Color(0xFF9E9E9E),
        )
    }
}
