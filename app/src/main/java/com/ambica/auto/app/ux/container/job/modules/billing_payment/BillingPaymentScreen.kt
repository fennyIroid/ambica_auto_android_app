package com.ambica.auto.app.ux.container.job.modules.billing_payment

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.ambica.auto.app.R
import com.ambica.auto.app.model.domain.job.PaymentMode
import com.ambica.auto.app.ui.compose.common.PrimaryButton
import com.ambica.auto.app.ui.theme.colorSplashOrange
import com.ambica.auto.app.ux.container.job.modules.JobModuleScaffold

private data class BreakdownLine(
    val title: String,
    val subtitle: String,
    val amount: Double,
    val iconRes: Int,
)

@Composable
fun BillingPaymentScreen(
    navController: NavController,
    jobId: String,
    viewModel: BillingPaymentViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState
    val state by uiState.stateFlow.collectAsStateWithLifecycle()

    LaunchedEffect(jobId) {
        uiState.event(BillingPaymentUiEvent.OnSetJobId(jobId))
    }

    val job = state.job
    val scrollState = rememberScrollState()

    var breakdown by remember(jobId) {
        mutableStateOf(
            listOf(
                BreakdownLine("Labour Charges", "Engine & suspension work", 4500.0, R.drawable.ic_repairprogress),
                BreakdownLine("Parts Cost", "Brake pads, Filters, Oil", 12800.0, R.drawable.ic_sparepart),
                BreakdownLine("Paint Work", "Front Bumper Refinish", 3200.0, R.drawable.ic_repairprogress),
                BreakdownLine("Other Charges", "Washing & Detailing", 850.0, R.drawable.ic_payment),
            ),
        )
    }

    val subtotal = breakdown.sumOf { it.amount }
    val gst = subtotal * 0.18
    val totalAmount = subtotal + gst

    JobModuleScaffold(title = "Generate Bill", navController = navController) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
        ) {
            Text(
                text = "Job Card #${job?.jobCardNumber ?: "—"}",
                style = MaterialTheme.typography.bodySmall,
                color = colorSplashOrange,
            )
            Spacer(Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Repair Breakdown",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF444444),
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { /* Edit breakdown (demo) */ },
                ) {
                    Text(
                        text = "Edit",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = colorSplashOrange,
                    )
                    Spacer(Modifier.size(4.dp))
                    Icon(
                        painter = painterResource(R.drawable.ic_report),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = colorSplashOrange,
                    )
                }
            }
            Spacer(Modifier.height(10.dp))

            breakdown.forEach { line ->
                BreakdownRow(
                    title = line.title,
                    subtitle = line.subtitle,
                    amount = line.amount,
                    iconRes = line.iconRes,
                )
                Spacer(Modifier.height(8.dp))
            }

            Spacer(Modifier.height(16.dp))

            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = colorSplashOrange.copy(alpha = 0.08f)),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "TOTAL AMOUNT PAYABLE",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF666666),
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "₹${formatAmount(totalAmount)}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = colorSplashOrange,
                    )
                    Text(
                        text = "Incl. all taxes (GST 18%)",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF777777),
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            Text(
                text = "Payment Details",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF444444),
            )
            Spacer(Modifier.height(8.dp))

            Text(
                text = "Payment Method",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF777777),
            )
            Spacer(Modifier.height(4.dp))
            Box {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F8F8)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { uiState.event(BillingPaymentUiEvent.OnPaymentMenuExpanded(true)) },
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = paymentModeDisplayLabel(state.paymentMode),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF1D1D1D),
                        )
                        Text(
                            text = "▼",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color(0xFF666666),
                        )
                    }
                }
                DropdownMenu(
                    expanded = state.paymentMenuExpanded,
                    onDismissRequest = { uiState.event(BillingPaymentUiEvent.OnPaymentMenuExpanded(false)) },
                ) {
                    PaymentMode.entries.forEach { mode ->
                        DropdownMenuItem(
                            text = { Text(paymentModeDisplayLabel(mode), color = Color(0xFF1D1D1D)) },
                            onClick = { uiState.event(BillingPaymentUiEvent.OnPaymentModeChange(mode)) },
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Proof of Payment",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF777777),
            )
            Spacer(Modifier.height(6.dp))
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAFA)),
                border = BorderStroke(1.5.dp, colorSplashOrange.copy(alpha = 0.4f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { /* Upload proof (demo) */ },
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_plus),
                        contentDescription = null,
                        tint = colorSplashOrange,
                        modifier = Modifier.size(32.dp),
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Upload Payment Screenshot",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF1D1D1D),
                    )
                    Text(
                        text = "JPG, PNG up to 5MB",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF888888),
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.5.dp, colorSplashOrange),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .clickable(enabled = !state.isSaving) { uiState.event(BillingPaymentUiEvent.OnSubmit(false)) },
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Text(
                            text = if (state.isSaving) "Saving..." else "Save Draft",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = colorSplashOrange,
                        )
                    }
                }
                PrimaryButton(
                    text = if (state.isSaving) "Saving..." else "Generate Bill",
                    enabled = !state.isSaving,
                    onClick = { uiState.event(BillingPaymentUiEvent.OnSubmit(true)) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun BreakdownRow(
    title: String,
    subtitle: String,
    amount: Double,
    iconRes: Int,
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(colorSplashOrange.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(iconRes),
                    contentDescription = null,
                    tint = colorSplashOrange,
                    modifier = Modifier.size(22.dp),
                )
            }
            Spacer(Modifier.size(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1D1D1D),
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF777777),
                )
            }
            Text(
                text = "₹${formatAmount(amount)}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1D1D1D),
            )
        }
    }
}

private fun formatAmount(value: Double): String = "%,.0f".format(value)

private fun paymentModeDisplayLabel(mode: PaymentMode): String {
    return when (mode) {
        PaymentMode.ONLINE -> "UPI (GPay / PhonePe)"
        PaymentMode.CASH -> "Cash"
        PaymentMode.BANK_TRANSFER -> "Bank Transfer"
    }
}

