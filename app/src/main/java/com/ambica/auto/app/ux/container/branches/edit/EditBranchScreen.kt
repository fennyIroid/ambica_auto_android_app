package com.ambica.auto.app.ux.container.branches.edit

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.ambica.auto.app.R
import com.ambica.auto.app.navigation.HandleNavigation
import com.ambica.auto.app.ui.compose.common.AmbicaTextField
import com.ambica.auto.app.ui.compose.common.PrimaryButton
import com.ambica.auto.app.ui.theme.BackgroundScreen
import com.ambica.auto.app.ui.theme.colorSplashOrange
import com.ambica.auto.app.ui.theme.colorText

@Composable
fun EditBranchScreen(
    navController: NavController,
    viewModel: EditBranchViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState
    val state by uiState.stateFlow.collectAsStateWithLifecycle()

    EditBranchContent(state = state, event = uiState.event)
    HandleNavigation(viewModelNav = viewModel, navController = navController)
}

@Composable
private fun EditBranchContent(
    state: EditBranchDataState,
    event: (EditBranchUiEvent) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val errorSnackbar = remember { SnackbarHostState() }

    LaunchedEffect(state.error) {
        state.error?.let {
            errorSnackbar.showSnackbar(it, duration = SnackbarDuration.Short)
            event(EditBranchUiEvent.OnDismissError)
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
                .statusBarsPadding()
                .background(BackgroundScreen),
        ) {
            TopBar(onBack = { event(EditBranchUiEvent.OnBack) })

            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(48.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(color = colorSplashOrange)
                    }
                }
                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 32.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
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
                                    painter = painterResource(R.drawable.ic_branch),
                                    contentDescription = null,
                                    tint = colorSplashOrange,
                                    modifier = Modifier
                                        .size(20.dp)
                                        .padding(top = 2.dp),
                                )
                                Text(
                                    text = "Update the branch details below. Fields marked with * are required.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF7A4500),
                                    lineHeight = 18.sp,
                                )
                            }
                        }

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
                                SectionLabel("Basic Information")

                                AmbicaTextField(
                                    value = state.code,
                                    onValueChange = { event(EditBranchUiEvent.OnCodeChange(it)) },
                                    placeholder = "Branch Code *",
                                )
                                AmbicaTextField(
                                    value = state.name,
                                    onValueChange = { event(EditBranchUiEvent.OnNameChange(it)) },
                                    placeholder = "Branch Name *",
                                )
                                AmbicaTextField(
                                    value = state.location,
                                    onValueChange = { event(EditBranchUiEvent.OnLocationChange(it)) },
                                    placeholder = "Location *",
                                )
                                AmbicaTextField(
                                    value = state.tagline,
                                    onValueChange = { event(EditBranchUiEvent.OnTaglineChange(it)) },
                                    placeholder = "Tagline",
                                )
                            }
                        }

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
                                SectionLabel("Address & Contact")

                                AmbicaTextField(
                                    value = state.address,
                                    onValueChange = { event(EditBranchUiEvent.OnAddressChange(it)) },
                                    placeholder = "Address *",
                                )
                                AmbicaTextField(
                                    value = state.phone,
                                    onValueChange = { event(EditBranchUiEvent.OnPhoneChange(it)) },
                                    placeholder = "Phone * (e.g. +919876543210)",
                                )
                                AmbicaTextField(
                                    value = state.email,
                                    onValueChange = { event(EditBranchUiEvent.OnEmailChange(it)) },
                                    placeholder = "Email",
                                )
                            }
                        }

                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                SectionLabel("Status")

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                ) {
                                    StatusOption(
                                        label = "Active",
                                        selected = state.status == 1,
                                        color = Color(0xFF2E7D32),
                                        bgColor = Color(0xFFE8F5E9),
                                        onClick = { event(EditBranchUiEvent.OnStatusChange(1)) },
                                        modifier = Modifier.weight(1f),
                                    )
                                    StatusOption(
                                        label = "Inactive",
                                        selected = state.status == 2,
                                        color = Color(0xFFC62828),
                                        bgColor = Color(0xFFFFEBEE),
                                        onClick = { event(EditBranchUiEvent.OnStatusChange(2)) },
                                        modifier = Modifier.weight(1f),
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        PrimaryButton(
                            text = if (state.isSaving) "Updating..." else "Update Branch",
                            onClick = { event(EditBranchUiEvent.OnSubmit) },
                            enabled = !state.isSaving,
                        )

                        if (state.isSaving) {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center,
                            ) {
                                CircularProgressIndicator(
                                    color = colorSplashOrange,
                                    modifier = Modifier.size(24.dp),
                                    strokeWidth = 2.dp,
                                )
                            }
                        }
                    }
                }
            }
        }

        SnackbarHost(
            hostState = errorSnackbar,
            modifier = Modifier.align(Alignment.BottomCenter),
        ) { data ->
            Snackbar(
                snackbarData = data,
                containerColor = Color(0xFF333333),
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
            .background(BackgroundScreen)
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
            text = "Edit Branch",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = colorText,
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp),
        )
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold,
        color = colorText,
    )
}

@Composable
private fun StatusOption(
    label: String,
    selected: Boolean,
    color: Color,
    bgColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val bg = if (selected) bgColor else Color(0xFFF5F5F5)
    val fg = if (selected) color else Color(0xFF999999)
    val borderShape = RoundedCornerShape(12.dp)

    Box(
        modifier = modifier
            .height(48.dp)
            .clip(borderShape)
            .background(bg)
            .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (selected) {
                Icon(
                    painter = painterResource(R.drawable.ic_check),
                    contentDescription = null,
                    tint = fg,
                    modifier = Modifier.size(16.dp),
                )
            }
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = fg,
            )
        }
    }
}
