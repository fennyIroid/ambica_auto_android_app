package com.ambica.auto.app.ux.container.job.modules.estimate_docs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.ambica.auto.app.R
import com.ambica.auto.app.ui.compose.common.AmbicaTextField
import com.ambica.auto.app.ui.compose.common.PrimaryButton
import com.ambica.auto.app.ui.theme.colorSplashOrange
import com.ambica.auto.app.ux.container.job.modules.JobModuleScaffold

@Composable
fun EstimateDocsScreen(
    navController: NavController,
    jobId: String,
    viewModel: EstimateDocsViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState
    val state by uiState.stateFlow.collectAsStateWithLifecycle()

    LaunchedEffect(jobId) {
        uiState.event(EstimateDocsUiEvent.OnSetJobId(jobId))
    }

    EstimateDocsContent(
        state = state,
        onEvent = uiState.event,
        navController = navController,
    )
}

@Composable
private fun EstimateDocsContent(
    state: EstimateDocsDataState,
    onEvent: (EstimateDocsUiEvent) -> Unit,
    navController: NavController,
) {
    val scrollState = rememberScrollState()
    val subtotal = state.items.sumOf { it.cost.toDoubleOrNull() ?: 0.0 }
    val gst = subtotal * 0.18
    val grandTotal = subtotal + gst

    JobModuleScaffold(title = "Estimate", navController = navController) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
        ) {
            Text(
                text = "Repair Items",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF444444),
            )
            Spacer(Modifier.height(8.dp))

            state.items.forEachIndexed { index, item ->
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(
                                text = "Item Name",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF888888),
                            )
                            Spacer(Modifier.height(4.dp))
                            AmbicaTextField(
                                value = item.name,
                                onValueChange = { newName ->
                                    onEvent(EstimateDocsUiEvent.OnItemNameChange(index, newName))
                                },
                                placeholder = "New Item Name",
                            )
                        }
                        Column(Modifier.width(100.dp)) {
                            Text(
                                text = "Cost",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF888888),
                            )
                            Spacer(Modifier.height(4.dp))
                            AmbicaTextField(
                                value = item.cost,
                                onValueChange = { newCost ->
                                    onEvent(EstimateDocsUiEvent.OnItemCostChange(index, newCost))
                                },
                                placeholder = "$ 0.00",
                            )
                        }
                    }
                }
                Spacer(Modifier.height(10.dp))
            }

            Card(
                shape = RoundedCornerShape(999.dp),
                border = BorderStroke(1.dp, colorSplashOrange.copy(alpha = 0.3f)),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onEvent(EstimateDocsUiEvent.OnAddItem) },
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                        .align(Alignment.CenterHorizontally),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "+",
                        style = MaterialTheme.typography.titleMedium,
                        color = colorSplashOrange,
                    )
                    Spacer(Modifier.height(4.dp))
                    Spacer(Modifier.size(6.dp))
                    Text(
                        text = "Add Another Item",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = colorSplashOrange,
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(Modifier.padding(14.dp)) {
                    SummaryRow(label = "Subtotal", value = String.format("$%.2f", subtotal))
                    SummaryRow(label = "GST (18%)", value = String.format("$%.2f", gst))
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Grand Total",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF444444),
                        )
                        Text(
                            text = String.format("$%.2f", grandTotal),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = colorSplashOrange,
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            Text(
                text = "Insurance Approval",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF444444),
            )
            Spacer(Modifier.height(8.dp))

            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    ApprovalChip(
                        label = "Pending",
                        selected = state.approval == InsuranceApproval.PENDING,
                        onClick = { onEvent(EstimateDocsUiEvent.OnApprovalChange(InsuranceApproval.PENDING)) },
                        modifier = Modifier.weight(1f),
                    )
                    ApprovalChip(
                        label = "Approved",
                        selected = state.approval == InsuranceApproval.APPROVED,
                        onClick = { onEvent(EstimateDocsUiEvent.OnApprovalChange(InsuranceApproval.APPROVED)) },
                        modifier = Modifier.weight(1f),
                    )
                    ApprovalChip(
                        label = "Rejected",
                        selected = state.approval == InsuranceApproval.REJECTED,
                        onClick = { onEvent(EstimateDocsUiEvent.OnApprovalChange(InsuranceApproval.REJECTED)) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            Spacer(Modifier.height(10.dp))
            Text(
                text = "Note: Adjust estimate based on surveyor's assessment and final report.",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF777777),
            )

            Spacer(Modifier.height(16.dp))

            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column {
                    Image(
                        painter = painterResource(R.drawable.inspection),
                        contentDescription = "Reference photo",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp),
                        contentScale = ContentScale.FillWidth,
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            PrimaryButton(
                text = if (state.isSaving) "Saving..." else "Save Estimate",
                onClick = { onEvent(EstimateDocsUiEvent.OnSave) },
                enabled = !state.isSaving,
            )
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF777777),
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF1D1D1D),
        )
    }
}

@Composable
private fun ApprovalChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val bg = if (selected) colorSplashOrange.copy(alpha = 0.16f) else Color(0xFFF2F2F2)
    val fg = if (selected) Color.White else Color(0xFF555555)
    val border = if (selected) BorderStroke(1.dp, colorSplashOrange) else null
    Card(
        shape = RoundedCornerShape(999.dp),
        colors = CardDefaults.cardColors(containerColor = if (selected) colorSplashOrange else bg),
        border = border,
        modifier = modifier
            .clickable { onClick() },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = fg,
            )
        }
    }
}

