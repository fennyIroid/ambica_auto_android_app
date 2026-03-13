package com.ambica.auto.app.ux.container.job.modules.gate_pass

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.ambica.auto.app.R
import com.ambica.auto.app.ui.compose.common.AmbicaTextField
import com.ambica.auto.app.ui.compose.common.SectionCard
import com.ambica.auto.app.ui.theme.colorSplashOrange
import com.ambica.auto.app.ux.container.job.modules.JobModuleScaffold

@Composable
fun GatePassScreen(
    navController: NavController,
    jobId: String,
    viewModel: GatePassViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState
    val state by uiState.stateFlow.collectAsStateWithLifecycle()

    LaunchedEffect(jobId) {
        uiState.event(GatePassUiEvent.OnSetJobId(jobId))
    }

    val job = state.job
    val scrollState = rememberScrollState()

    JobModuleScaffold(title = "Vehicle Gate Pass", navController = navController) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "AMBICA BODYSHOP",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF777777),
                )
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFF2F2F2))
                        .clickable { },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "i",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF555555),
                    )
                }
            }
            Spacer(Modifier.height(12.dp))

            JobCardBlock(
                jobCardNumber = job?.jobCardNumber ?: "#8829",
                vehicleName = state.vehicleModel.ifBlank { "Vehicle model" },
                vehicleNumber = job?.vehicleNumber ?: "KA-01-MJ-1234",
                isReady = job?.gatePass?.generated == true,
            )
            Spacer(Modifier.height(20.dp))

            if (!state.errorText.isNullOrBlank()) {
                SectionCard(title = "Fix to continue") {
                    Text(
                        text = state.errorText!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                Spacer(Modifier.height(12.dp))
            }

            GatePassFormatCard(
                state = state,
                jobCardNumber = job?.jobCardNumber.orEmpty(),
                vehicleNumber = job?.vehicleNumber.orEmpty(),
                gatePassNo = job?.gatePass?.gatePassNumber,
                onVehicleModelChange = { uiState.event(GatePassUiEvent.OnVehicleModelChange(it)) },
                onRepairOrderNoChange = { uiState.event(GatePassUiEvent.OnRepairOrderNoChange(it)) },
                onInvoiceNoChange = { uiState.event(GatePassUiEvent.OnInvoiceNoChange(it)) },
                onDateDeliveredChange = { uiState.event(GatePassUiEvent.OnDateDeliveredChange(it)) },
                onTimeDeliveredChange = { uiState.event(GatePassUiEvent.OnTimeDeliveredChange(it)) },
                onTestedSatisfiedChange = { uiState.event(GatePassUiEvent.OnTestedSatisfiedChange(it)) },
                onWorkSummaryChange = { uiState.event(GatePassUiEvent.OnWorkSummaryChange(it)) },
                onRemarksChange = { uiState.event(GatePassUiEvent.OnRemarksChange(it)) },
            )

            Spacer(Modifier.height(18.dp))

            androidx.compose.material3.Button(
                onClick = { uiState.event(GatePassUiEvent.OnGenerateGatePass) },
                enabled = !state.isWorking,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colorSplashOrange),
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_gatepass),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = Color.White,
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = if (state.isWorking) "Working..." else "Generate Gate Pass",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                )
            }
            Spacer(Modifier.height(10.dp))
            OutlinedButton(
                onClick = { uiState.event(GatePassUiEvent.OnConfirmGateOut) },
                enabled = !state.isWorking,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.5.dp, colorSplashOrange),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = colorSplashOrange),
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_gatepass),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = colorSplashOrange,
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Confirm Gate Out",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun GatePassFormatCard(
    state: GatePassDataState,
    jobCardNumber: String,
    vehicleNumber: String,
    gatePassNo: String?,
    onVehicleModelChange: (String) -> Unit,
    onRepairOrderNoChange: (String) -> Unit,
    onInvoiceNoChange: (String) -> Unit,
    onDateDeliveredChange: (String) -> Unit,
    onTimeDeliveredChange: (String) -> Unit,
    onTestedSatisfiedChange: (Boolean) -> Unit,
    onWorkSummaryChange: (String) -> Unit,
    onRemarksChange: (String) -> Unit,
) {
    SectionCard(title = "Gate Pass (C.V.D.)", iconRes = R.drawable.ic_gatepass) {
        Text(
            text = "SHREE AMBICA AUTO SALES & SERVICE",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1D1D1D),
        )
        Text(
            text = "Format as per workshop gate pass slip",
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF7A7A7A),
        )
        Spacer(Modifier.height(14.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            AmbicaTextField(
                value = vehicleNumber.ifBlank { state.job?.vehicleNumber.orEmpty() },
                onValueChange = { /* read-only */ },
                placeholder = "Vehicle Reg. No.",
                modifier = Modifier.weight(1f),
            )
            AmbicaTextField(
                value = state.vehicleModel,
                onValueChange = onVehicleModelChange,
                placeholder = "Vehicle model",
                modifier = Modifier.weight(1f),
            )
        }
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            AmbicaTextField(
                value = state.repairOrderNo.ifBlank { jobCardNumber },
                onValueChange = onRepairOrderNoChange,
                placeholder = "Job Card / Repair order no.",
                modifier = Modifier.weight(1f),
            )
            AmbicaTextField(
                value = state.invoiceNo,
                onValueChange = onInvoiceNoChange,
                placeholder = "Invoice no.",
                modifier = Modifier.weight(1f),
            )
        }
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            AmbicaTextField(
                value = gatePassNo.orEmpty(),
                onValueChange = { /* read-only */ },
                placeholder = "Gate pass no.",
                modifier = Modifier.weight(1f),
            )
            AmbicaTextField(
                value = state.dateDelivered,
                onValueChange = onDateDeliveredChange,
                placeholder = "Date of delivered",
                modifier = Modifier.weight(1f),
            )
        }
        Spacer(Modifier.height(10.dp))
        AmbicaTextField(
            value = state.timeDelivered,
            onValueChange = onTimeDeliveredChange,
            placeholder = "Time",
        )
        Spacer(Modifier.height(14.dp))

        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAFA)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onTestedSatisfiedChange(!state.testedAndSatisfied) },
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        text = "Tested & satisfied",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1D1D1D),
                    )
                    Text(
                        text = "Has been tested by me/us and satisfied with the work done",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF7A7A7A),
                    )
                }
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .border(
                            width = 1.5.dp,
                            color = if (state.testedAndSatisfied) colorSplashOrange else Color(0xFFCCCCCC),
                            shape = RoundedCornerShape(6.dp),
                        )
                        .background(
                            if (state.testedAndSatisfied) colorSplashOrange else Color.Transparent,
                            RoundedCornerShape(6.dp),
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    if (state.testedAndSatisfied) {
                        Text(
                            text = "✓",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        AmbicaTextField(
            value = state.workSummary,
            onValueChange = onWorkSummaryChange,
            placeholder = "Repairs / replaced parts summary (optional)",
        )
        Spacer(Modifier.height(10.dp))
        AmbicaTextField(
            value = state.remarks,
            onValueChange = onRemarksChange,
            placeholder = "Remarks (optional)",
        )
    }
}

@Composable
private fun JobCardBlock(
    jobCardNumber: String,
    vehicleName: String,
    vehicleNumber: String,
    isReady: Boolean,
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAFA)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFEEEEEE)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ambica_auto_bg_rem_logo),
                    contentDescription = null,
                    modifier = Modifier.size(36.dp),
                    tint = Color(0xFF888888),
                )
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "JOB CARD #$jobCardNumber",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF777777),
                )
                Text(
                    text = vehicleName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1D1D1D),
                )
                Text(
                    text = vehicleNumber,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF666666),
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(colorSplashOrange.copy(alpha = 0.16f))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
            ) {
                Text(
                    text = if (isReady) "READY" else "PENDING",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = colorSplashOrange,
                )
            }
        }
    }
}

@Composable
private fun PreDeliveryChecklist(
    workCompleted: Boolean,
    onWorkCompletedChange: (Boolean) -> Unit,
    paymentReceived: Boolean,
    onPaymentReceivedChange: (Boolean) -> Unit,
    finalQcInspection: Boolean,
    onFinalQcInspectionChange: (Boolean) -> Unit,
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 10.dp),
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_repairprogress),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = colorSplashOrange,
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Pre-Delivery Checklist",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1D1D1D),
            )
        }
        ChecklistRow(
            label = "Work Completed",
            checked = workCompleted,
            onCheckedChange = onWorkCompletedChange,
            iconRes = R.drawable.inspection,
        )
        Spacer(Modifier.height(8.dp))
        ChecklistRow(
            label = "Payment Received",
            checked = paymentReceived,
            onCheckedChange = onPaymentReceivedChange,
            iconRes = R.drawable.ic_payment,
        )
        Spacer(Modifier.height(8.dp))
        ChecklistRow(
            label = "Final QC Inspection",
            checked = finalQcInspection,
            onCheckedChange = onFinalQcInspectionChange,
            iconRes = R.drawable.inspection,
        )
    }
}

@Composable
private fun ChecklistRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    iconRes: Int,
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAFA)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .border(
                        width = 1.5.dp,
                        color = if (checked) colorSplashOrange else Color(0xFFCCCCCC),
                        shape = RoundedCornerShape(6.dp),
                    )
                    .background(
                        if (checked) colorSplashOrange else Color.Transparent,
                        RoundedCornerShape(6.dp),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                if (checked) {
                    Text(
                        text = "✓",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    )
                }
            }
            Spacer(Modifier.width(12.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1D1D1D),
                modifier = Modifier.weight(1f),
            )
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = if (checked) colorSplashOrange else Color(0xFFAAAAAA),
            )
        }
    }
}

@Composable
private fun ExitDetailsBlock(
    deliveredBy: String,
    gateOutTime: String,
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 10.dp),
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_gatepass),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = colorSplashOrange,
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Exit Details",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1D1D1D),
            )
        }
        Text(
            text = "DELIVERED BY",
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFF777777),
        )
        Spacer(Modifier.height(4.dp))
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAFA)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { },
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = deliveredBy,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF1D1D1D),
                    modifier = Modifier.weight(1f),
                )
                Icon(
                    painter = painterResource(R.drawable.back_arrow),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp).rotate(-90f),
                    tint = Color(0xFF888888),
                )
            }
        }
        Spacer(Modifier.height(14.dp))
        Text(
            text = "GATE OUT TIME",
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFF777777),
        )
        Spacer(Modifier.height(4.dp))
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAFA)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_repairprogress),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = Color(0xFF888888),
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    text = gateOutTime,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF1D1D1D),
                )
            }
        }
    }
}

