package com.ambica.auto.app.ux.startup.auth.forgotpassword

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.material3.SheetState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ambica.auto.app.R
import com.ambica.auto.app.ui.compose.common.AmbicaTextField
import com.ambica.auto.app.ui.compose.common.PrimaryButton
import com.ambica.auto.app.ui.compose.common.bottomSheetFlingStabilizer
import com.ambica.auto.app.ui.theme.colorSplashOrange

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordBottomSheet(
    viewModel: ForgotPasswordViewModel = hiltViewModel(),
    sheetState: SheetState? = null,
) {
    val uiState = viewModel.uiState
    val state by uiState.stateFlow.collectAsStateWithLifecycle()
    val event = uiState.event
    val snackbarHostState = remember { SnackbarHostState() }
    val successSnackbarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current
    val configuration = LocalConfiguration.current
    val maxSheetHeight = (configuration.screenHeightDp * 0.82f).dp

    // Back press within the sheet navigates through steps
    BackHandler { event(ForgotPasswordUiEvent.OnBack) }

    LaunchedEffect(state.error) {
        state.error?.let {
            snackbarHostState.showSnackbar(it)
            event(ForgotPasswordUiEvent.OnDismissError)
        }
    }

    LaunchedEffect(state.successMessage) {
        state.successMessage?.let {
            successSnackbarHostState.showSnackbar(it, duration = SnackbarDuration.Short)
            event(ForgotPasswordUiEvent.OnDismissSuccess)
        }
    }

    Box(
        modifier = Modifier.pointerInput(Unit) {
            awaitEachGesture {
                awaitFirstDown(pass = PointerEventPass.Initial)
                focusManager.clearFocus()
            }
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = maxSheetHeight)
                .then(
                    if (sheetState != null) Modifier.bottomSheetFlingStabilizer(sheetState)
                    else Modifier
                )
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(bottom = 16.dp),
        ) {
            // Drag handle
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 12.dp, bottom = 20.dp)
                    .width(36.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(Color(0xFFDDDDDD)),
            )

            AnimatedContent(
                targetState = state.step,
                transitionSpec = {
                    val forward = targetState.ordinal > initialState.ordinal
                    if (forward) {
                        (slideInHorizontally(tween(280)) { it / 2 } + fadeIn(tween(280))) togetherWith
                            (slideOutHorizontally(tween(240)) { -it / 2 } + fadeOut(tween(240)))
                    } else {
                        (slideInHorizontally(tween(280)) { -it / 2 } + fadeIn(tween(280))) togetherWith
                            (slideOutHorizontally(tween(240)) { it / 2 } + fadeOut(tween(240)))
                    }
                },
                label = "forgot_step",
            ) { step ->
                when (step) {
                    ForgotPasswordStep.ENTER_EMAIL -> EnterEmailStep(state = state, event = event)
                    ForgotPasswordStep.VERIFY_OTP -> VerifyOtpStep(state = state, event = event)
                    ForgotPasswordStep.RESET_PASSWORD -> ResetPasswordStep(state = state, event = event)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp, start = 16.dp, end = 16.dp),
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
                .padding(bottom = 16.dp, start = 16.dp, end = 16.dp),
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

// ── Step 1: Enter Email ──────────────────────────────────────────────────────────────────────────

@Composable
private fun EnterEmailStep(
    state: ForgotPasswordDataState,
    event: (ForgotPasswordUiEvent) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        StepIconBadge(iconRes = R.drawable.ic_account_logout)

        Text(
            text = "Forgot Password?",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1D1D1D),
        )
        Text(
            text = "Enter your registered email to verify",
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF888888),
            textAlign = TextAlign.Center,
            lineHeight = 18.sp,
        )

        Spacer(modifier = Modifier.height(2.dp))

        AmbicaTextField(
            value = state.email,
            onValueChange = { event(ForgotPasswordUiEvent.OnEmailChange(it)) },
            placeholder = "Email address",
            errorText = state.emailError,
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            SecondaryButton(
                text = "Back to Login",
                onClick = { event(ForgotPasswordUiEvent.OnBack) },
                modifier = Modifier.weight(1f),
            )
            PrimaryButton(
                text = if (state.isLoading) "Sending..." else "Send Code",
                onClick = { event(ForgotPasswordUiEvent.OnSendCode) },
                enabled = !state.isLoading,
                modifier = Modifier.weight(1f),
            )
        }

        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(22.dp)
                    .padding(bottom = 4.dp),
                strokeWidth = 2.dp,
                color = colorSplashOrange,
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
    }
}

// ── Step 2: Verify OTP ───────────────────────────────────────────────────────────────────────────

@Composable
private fun VerifyOtpStep(
    state: ForgotPasswordDataState,
    event: (ForgotPasswordUiEvent) -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        StepIconBadge(iconRes = R.drawable.ic_insurence)

        Text(
            text = "Verify Email",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1D1D1D),
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "A verification code has been sent to",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF888888),
                textAlign = TextAlign.Center,
            )
            Text(
                text = state.email,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = colorSplashOrange,
                textAlign = TextAlign.Center,
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // OTP digit boxes backed by a hidden BasicTextField
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            BasicTextField(
                value = state.otp,
                onValueChange = { event(ForgotPasswordUiEvent.OnOtpChange(it)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                cursorBrush = SolidColor(colorSplashOrange),
                modifier = Modifier
                    .size(1.dp)
                    .focusRequester(focusRequester),
            )
            // fillMaxWidth + weight(1f) + aspectRatio(1f) ensures all 6 boxes are perfectly
            // equal and square regardless of screen width, preventing the last box from being crushed.
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                for (i in 0..5) {
                    val digit = state.otp.getOrNull(i)
                    val isCurrent = state.otp.length == i
                    val hasError = state.otpError != null
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                when {
                                    digit != null -> colorSplashOrange.copy(alpha = 0.08f)
                                    else -> Color(0xFFF5F5F5)
                                }
                            )
                            .border(
                                width = if (isCurrent || hasError) 1.5.dp else 1.dp,
                                color = when {
                                    hasError -> MaterialTheme.colorScheme.error
                                    isCurrent -> colorSplashOrange
                                    digit != null -> colorSplashOrange.copy(alpha = 0.4f)
                                    else -> Color(0xFFE0E0E0)
                                },
                                shape = RoundedCornerShape(12.dp),
                            )
                            .clickable { focusRequester.requestFocus() },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = digit?.toString() ?: "",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1D1D1D),
                        )
                    }
                }
            }
        }

        if (state.otpError != null) {
            Text(
                text = state.otpError,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
            )
        }

        PrimaryButton(
            text = if (state.isLoading) "Verifying..." else "Verify",
            onClick = { event(ForgotPasswordUiEvent.OnVerify) },
            enabled = !state.isLoading,
            modifier = Modifier.fillMaxWidth(),
        )

        // Resend row
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Didn't receive code? ",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF999999),
            )
            if (state.resendCooldownSeconds > 0) {
                val minutes = state.resendCooldownSeconds / 60
                val seconds = state.resendCooldownSeconds % 60
                Text(
                    text = "%02d:%02d".format(minutes, seconds),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF999999),
                )
            } else {
                Text(
                    text = "Resend",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = colorSplashOrange,
                    modifier = Modifier.clickable { event(ForgotPasswordUiEvent.OnResend) },
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
    }
}

// ── Step 3: Reset Password ───────────────────────────────────────────────────────────────────────

@Composable
private fun ResetPasswordStep(
    state: ForgotPasswordDataState,
    event: (ForgotPasswordUiEvent) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        StepIconBadge(iconRes = R.drawable.ic_eye_open)

        Text(
            text = "Reset Password",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1D1D1D),
        )
        Text(
            text = "Choose a strong password to secure your account",
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF888888),
            textAlign = TextAlign.Center,
            lineHeight = 18.sp,
        )

        Spacer(modifier = Modifier.height(2.dp))

        PasswordResetField(
            label = "New Password",
            value = state.newPassword,
            visible = state.newPasswordVisible,
            onValueChange = { event(ForgotPasswordUiEvent.OnNewPasswordChange(it)) },
            onToggleVisibility = { event(ForgotPasswordUiEvent.OnToggleNewPasswordVisibility) },
            errorText = state.newPasswordError,
        )

        PasswordResetField(
            label = "Confirm New Password",
            value = state.confirmPassword,
            visible = state.confirmPasswordVisible,
            onValueChange = { event(ForgotPasswordUiEvent.OnConfirmPasswordChange(it)) },
            onToggleVisibility = { event(ForgotPasswordUiEvent.OnToggleConfirmPasswordVisibility) },
        )

        if (state.error != null) {
            Text(
                text = state.error,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        PrimaryButton(
            text = if (state.isLoading) "Resetting..." else "Reset Password",
            onClick = { event(ForgotPasswordUiEvent.OnResetPassword) },
            enabled = !state.isLoading,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(4.dp))
    }
}

// ── Shared composables ───────────────────────────────────────────────────────────────────────────

@Composable
private fun StepIconBadge(iconRes: Int) {
    Box(
        modifier = Modifier
            .size(60.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(colorSplashOrange.copy(alpha = 0.1f)),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = colorSplashOrange,
            modifier = Modifier.size(28.dp),
        )
    }
}

@Composable
private fun PasswordResetField(
    label: String,
    value: String,
    visible: Boolean,
    onValueChange: (String) -> Unit,
    onToggleVisibility: () -> Unit,
    errorText: String? = null,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF5E5E5E),
        )
        androidx.compose.material3.OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            isError = errorText != null,
            trailingIcon = {
                Icon(
                    painter = painterResource(if (visible) R.drawable.ic_eye_open else R.drawable.ic_eye_closed),
                    contentDescription = null,
                    tint = Color(0xFF9E9E9E),
                    modifier = Modifier
                        .size(22.dp)
                        .clickable { onToggleVisibility() },
                )
            },
            shape = RoundedCornerShape(12.dp),
            colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colorSplashOrange,
                unfocusedBorderColor = Color(0xFFE0E0E0),
                errorBorderColor = MaterialTheme.colorScheme.error,
                focusedTextColor = Color(0xFF1D1D1D),
                unfocusedTextColor = Color(0xFF1D1D1D),
                cursorColor = colorSplashOrange,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
            ),
        )
        if (errorText != null) {
            Text(
                text = errorText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
            )
        }
    }
}

@Composable
private fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .height(50.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF2F2F2))
            .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF555555),
        )
    }
}
