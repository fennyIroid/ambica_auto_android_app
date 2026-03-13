package com.ambica.auto.app.ux.container.job.create

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.ambica.auto.app.ui.theme.colorSplashOrange

@Composable
fun GateEntryScreen(
    navController: NavController,
    viewModel: GateEntryViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState
    val state by uiState.stateFlow.collectAsStateWithLifecycle()

    GateEntryContent(state = state, event = uiState.event)
    HandleNavigation(viewModelNav = viewModel, navController = navController)
}

@Composable
private fun GateEntryContent(
    state: GateEntryDataState,
    event: (GateEntryUiEvent) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .padding(bottom = 24.dp)
            .statusBarsPadding(),
        verticalArrangement = Arrangement.Top
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF2F2F2))
                    .clickable { event(GateEntryUiEvent.OnBack) },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.back_arrow),
                    contentDescription = "Back",
                    tint = Color(0xFF555555),
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.size(10.dp))
            Column {
                Text(text = "Gate Entry", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(text = "Vehicle check-in", style = MaterialTheme.typography.bodySmall, color = colorSplashOrange)
            }
        }

        // Scrollable content so all fields are reachable on small screens.
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            SectionCard(title = "Vehicle Details") {
                AmbicaTextField(state.vehicleNumber, { event(GateEntryUiEvent.OnVehicleNumberChange(it)) }, "Vehicle number")
                Spacer(modifier = Modifier.height(10.dp))
                AmbicaTextField(state.customerName, { event(GateEntryUiEvent.OnCustomerNameChange(it)) }, "Customer name")
                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    Box(modifier = Modifier.weight(1f)) {
                        AmbicaTextField(state.model, { event(GateEntryUiEvent.OnModelChange(it)) }, "Model")
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        AmbicaTextField(state.jobCardNumber, { event(GateEntryUiEvent.OnJobCardChange(it)) }, "Job card no")
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                AmbicaTextField(state.claimNumber, { event(GateEntryUiEvent.OnClaimChange(it)) }, "Claim no (optional)")
            }

            Spacer(modifier = Modifier.height(12.dp))

            SectionCard(title = "Customer Details") {
                AmbicaTextField(state.customerPhone, { event(GateEntryUiEvent.OnCustomerPhoneChange(it)) }, "Phone number")
            }

            Spacer(modifier = Modifier.height(12.dp))

            SectionCard(title = "Damage Notes") {
                AmbicaTextField(
                    value = state.damageNotes,
                    onValueChange = { event(GateEntryUiEvent.OnDamageNotesChange(it)) },
                    placeholder = "Record any visible damage or customer requests...",
                )
                Spacer(modifier = Modifier.height(10.dp))
                AmbicaTextField(
                    value = state.missingItem,
                    onValueChange = { event(GateEntryUiEvent.OnMissingItemChange(it)) },
                    placeholder = "Missing items (e.g. spare wheel, tools)...",
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            SectionCard(
                title = "Photo Capture",
                trailing = {
                    Text(text = "${state.photos.count { it != null }}/4 captured", style = MaterialTheme.typography.bodySmall, color = Color(0xFF7A7A7A))
                }
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    PhotoSlot(
                        modifier = Modifier.weight(1f),
                        label = "Front view",
                        filled = state.photos[0] != null
                    ) { event(GateEntryUiEvent.OnPhotoSlotClick(0)) }
                    PhotoSlot(
                        modifier = Modifier.weight(1f),
                        label = "Back view",
                        filled = state.photos[1] != null
                    ) { event(GateEntryUiEvent.OnPhotoSlotClick(1)) }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    PhotoSlot(
                        modifier = Modifier.weight(1f),
                        label = "Left side",
                        filled = state.photos[2] != null
                    ) { event(GateEntryUiEvent.OnPhotoSlotClick(2)) }
                    PhotoSlot(
                        modifier = Modifier.weight(1f),
                        label = "Right side",
                        filled = state.photos[3] != null
                    ) { event(GateEntryUiEvent.OnPhotoSlotClick(3)) }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            AnimatedVisibility(
                visible = !state.error.isNullOrBlank(),
                enter = fadeIn(tween(160)),
                exit = fadeOut(tween(160))
            ) {
                Text(text = state.error.orEmpty(), color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = { event(GateEntryUiEvent.OnCancel) },
                enabled = !state.isLoading,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.5.dp, colorSplashOrange),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = colorSplashOrange,
                ),
            ) {
                Text(text = "Cancel", style = MaterialTheme.typography.titleMedium)
            }
            PrimaryButton(
                text = if (state.isLoading) "Saving..." else "Save Job Card",
                onClick = { event(GateEntryUiEvent.OnSubmit) },
                enabled = !state.isLoading,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    trailing: @Composable (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text(text = title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.weight(1f))
                trailing?.invoke()
            }
            Spacer(modifier = Modifier.height(10.dp))
            content()
        }
    }
}

@Composable
private fun PhotoSlot(modifier: Modifier, label: String, filled: Boolean, onClick: () -> Unit) {
    Card(
        modifier = modifier
            .height(72.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F7F7)),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (filled) colorSplashOrange else Color(0xFFEAEAEA))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (filled) colorSplashOrange.copy(alpha = 0.12f) else Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_plus),
                    contentDescription = null,
                    tint = if (filled) colorSplashOrange else Color(0xFFB0B0B0),
                    modifier = Modifier.size(16.dp)
                )
            }
            Column {
                Text(text = label.uppercase(), style = MaterialTheme.typography.labelSmall, color = Color(0xFF7A7A7A))
                Text(text = if (filled) "Captured" else "Tap to capture", style = MaterialTheme.typography.bodySmall, color = Color(0xFF444444))
            }
        }
    }
}

