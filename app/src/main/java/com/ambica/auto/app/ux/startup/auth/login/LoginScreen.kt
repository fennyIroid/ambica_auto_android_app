package com.ambica.auto.app.ux.startup.auth.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.ambica.auto.app.R
import com.ambica.auto.app.navigation.HandleNavigation
import com.ambica.auto.app.ui.compose.common.AmbicaTextField
import com.ambica.auto.app.ui.compose.common.PrimaryButton
import com.ambica.auto.app.ui.theme.colorSplashOrange
import com.ambica.auto.app.ux.startup.auth.forgotpassword.ForgotPasswordBottomSheet
import com.ambica.auto.app.ux.startup.auth.forgotpassword.ForgotPasswordViewModel
import kotlinx.coroutines.launch

private val roleOptions = listOf(
    1 to "Owner",
    2 to "Admin",
    3 to "Staff",
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState
    val state by uiState.stateFlow.collectAsStateWithLifecycle()

    var showForgotPasswordSheet by remember { mutableStateOf(false) }
    val forgotPasswordViewModel: ForgotPasswordViewModel = hiltViewModel()
    val forgotPasswordState by forgotPasswordViewModel.uiState.stateFlow.collectAsStateWithLifecycle()

    // Wildflower pattern: sheetState with confirmValueChange to block partial-expand and
    // prevent dismissal while an API call is in progress.
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { newValue ->
            when {
                newValue == SheetValue.PartiallyExpanded -> false
                forgotPasswordState.isLoading && newValue == SheetValue.Hidden -> false
                else -> true
            }
        },
    )
    val scope = rememberCoroutineScope()

    // Wildflower pattern: hasBeenVisible guard — fires onDismiss only after the sheet was actually
    // shown, so initial composition doesn't trigger a false dismiss.
    var sheetHasBeenVisible by remember { mutableStateOf(false) }
    LaunchedEffect(sheetState.currentValue) {
        if (sheetState.currentValue == SheetValue.Expanded) sheetHasBeenVisible = true
        if (sheetHasBeenVisible && sheetState.currentValue == SheetValue.Hidden) {
            showForgotPasswordSheet = false
            sheetHasBeenVisible = false
            forgotPasswordViewModel.reset()
        }
    }

    // When the forgot-password flow signals completion, programmatically animate the sheet closed.
    LaunchedEffect(forgotPasswordState.isDismissed) {
        if (forgotPasswordState.isDismissed) {
            scope.launch { sheetState.hide() }.invokeOnCompletion {
                showForgotPasswordSheet = false
                sheetHasBeenVisible = false
                forgotPasswordViewModel.reset()
            }
        }
    }

    LoginScreenContent(
        state = state,
        event = uiState.event,
        onForgotPasswordClick = { showForgotPasswordSheet = true },
    )

    HandleNavigation(viewModelNav = viewModel, navController = navController)

    if (showForgotPasswordSheet) {
        ModalBottomSheet(
            onDismissRequest = { },
            sheetState = sheetState,
            containerColor = Color.White,
            dragHandle = null,
            // Prevent the sheet from consuming IME (keyboard) insets — without this, the sheet
            // expands to fill the whole screen when the keyboard opens.
            // The content handles keyboard offset with imePadding() + verticalScroll instead.
            contentWindowInsets = { WindowInsets(0) },
            properties = ModalBottomSheetProperties(
                shouldDismissOnBackPress = false,
            ),
        ) {
            ForgotPasswordBottomSheet(
                viewModel = forgotPasswordViewModel,
                sheetState = sheetState,
            )
        }
    }
}

@Composable
private fun LoginScreenContent(
    state: LoginDataState,
    event: (LoginUiEvent) -> Unit,
    onForgotPasswordClick: () -> Unit = {},
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.login_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x66000000)),
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            contentAlignment = Alignment.Center,
        ) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                tonalElevation = 4.dp,
                shadowElevation = 10.dp,
                modifier = Modifier
                    .fillMaxWidth(0.88f)
                    .widthIn(max = 360.dp),
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Image(
                        painter = painterResource(R.drawable.ambica_auto_bg_rem_logo),
                        contentDescription = null,
                        modifier = Modifier.size(120.dp),
                        contentScale = ContentScale.Fit,
                    )

                    Text(
                        text = "AMBICA AUTO GROUP",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFB00020),
                    )
                    Text(
                        text = "SALES & SERVICE PORTAL",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF333333),
                    )
                    Text(
                        text = "LOGIN",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF333333),
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Role selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        roleOptions.forEach { (roleValue, label) ->
                            val selected = state.role == roleValue
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (selected) colorSplashOrange else Color.Transparent)
                                    .border(
                                        width = 1.dp,
                                        color = if (selected) colorSplashOrange else Color(0xFFCCCCCC),
                                        shape = RoundedCornerShape(10.dp),
                                    )
                                    .clickable { event(LoginUiEvent.OnRoleChange(roleValue)) },
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (selected) Color.White else Color(0xFF555555),
                                )
                            }
                        }
                    }

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        AmbicaTextField(
                            value = state.email,
                            onValueChange = { event(LoginUiEvent.OnEmailChange(it)) },
                            placeholder = "Email",
                            errorText = state.emailError,
                        )
                        AmbicaTextField(
                            value = state.password,
                            onValueChange = { event(LoginUiEvent.OnPasswordChange(it)) },
                            placeholder = "Password",
                            isPassword = true,
                            passwordVisible = state.passwordVisible,
                            onTogglePasswordVisibility = { event(LoginUiEvent.OnTogglePasswordVisibility) },
                            errorText = state.passwordError,
                        )

                        // Forgot password link
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.CenterEnd,
                        ) {
                            Text(
                                text = "Forgot Password?",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = colorSplashOrange,
                                modifier = Modifier.clickable { onForgotPasswordClick() },
                            )
                        }

                        AnimatedVisibility(
                            visible = !state.generalError.isNullOrBlank(),
                            enter = fadeIn(tween(180)),
                            exit = fadeOut(tween(180)),
                        ) {
                            Text(
                                text = state.generalError.orEmpty(),
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }

                        PrimaryButton(
                            text = if (state.isLoading) "Signing in..." else "Sign in",
                            onClick = { event(LoginUiEvent.OnLoginClick) },
                            enabled = !state.isLoading,
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewLogin() {
    LoginScreenContent(state = LoginDataState(), event = {})
}
