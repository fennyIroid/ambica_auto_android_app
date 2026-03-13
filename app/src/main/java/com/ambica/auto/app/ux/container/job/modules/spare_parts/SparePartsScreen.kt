package com.ambica.auto.app.ux.container.job.modules.spare_parts

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import com.ambica.auto.app.model.domain.job.PartItem
import com.ambica.auto.app.model.domain.job.PartStatus
import com.ambica.auto.app.ui.theme.colorSplashOrange
import com.ambica.auto.app.ux.container.job.modules.JobModuleScaffold

private val dummySparePartsHeader = SparePartsHeaderData(
    jobCardNumber = "VB-2024-081",
    vehicleNumber = "Toyota Fortuner (KA-01-MJ-2023)",
    totalParts = 12,
    estimatedDelivery = "Oct 24, 2024",
)

private val dummySparePartsList = listOf(
    PartItem(id = "BMR-9920", name = "Front Bumper Grille", status = PartStatus.INSTALLED, notes = "Updated 2h ago"),
    PartItem(id = "SMA-7741", name = "Side Mirror Assembly", status = PartStatus.RECEIVED, notes = "Received Oct 21"),
    PartItem(id = "CBP-3302", name = "Ceramic Brake Pads", status = PartStatus.ORDERED, notes = "ETA: Tomorrow"),
    PartItem(id = "LHU-8815", name = "LED Headlight Unit", status = PartStatus.NOT_AVAILABLE, notes = "Backordered"),
)

private data class SparePartsHeaderData(
    val jobCardNumber: String,
    val vehicleNumber: String,
    val totalParts: Int,
    val estimatedDelivery: String,
)

@Composable
fun SparePartsScreen(
    navController: NavController,
    jobId: String,
    viewModel: SparePartsViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState
    val state by uiState.stateFlow.collectAsStateWithLifecycle()

    LaunchedEffect(jobId) {
        uiState.event(SparePartsUiEvent.OnSetJobId(jobId))
    }

    val job = state.job
    val scrollState = rememberScrollState()

    val displayHeader = if (job != null) SparePartsHeaderData(
        jobCardNumber = job.jobCardNumber,
        vehicleNumber = job.vehicleNumber,
        totalParts = job.parts.size,
        estimatedDelivery = "—",
    ) else dummySparePartsHeader
    val displayParts = job?.parts?.takeIf { it.isNotEmpty() } ?: dummySparePartsList

    JobModuleScaffold(title = "Spare Parts", navController = navController) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
        ) {
            JobHeaderCard(displayHeader)

            Spacer(Modifier.height(16.dp))

            Text(
                text = "REQUIRED INVENTORY",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF5E5E5E),
            )
            Spacer(Modifier.height(10.dp))

            displayParts.forEach { part ->
                SparePartRow(
                    part = part,
                    onUpdateStatus = { uiState.event(SparePartsUiEvent.OnCycleStatus(part)) },
                )
                Spacer(Modifier.height(10.dp))
            }

            Spacer(Modifier.height(80.dp))
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.End,
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(colorSplashOrange)
                    .clickable(enabled = !state.isWorking) { uiState.event(SparePartsUiEvent.OnAddQuickPart) },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_plus),
                    contentDescription = "Add part",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp),
                )
            }
        }
    }
}

@Composable
private fun JobHeaderCard(header: SparePartsHeaderData) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Job Card #${header.jobCardNumber}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1D1D1D),
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(colorSplashOrange.copy(alpha = 0.16f))
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                ) {
                    Text(
                        text = "IN PROGRESS",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = colorSplashOrange,
                    )
                }
            }
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Vehicle: ${header.vehicleNumber}",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF777777),
            )
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column {
                    Text(
                        text = "TOTAL PARTS",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF777777),
                    )
                    Text(
                        text = "${header.totalParts} Items",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1D1D1D),
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "ESTIMATED DELIVERY",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF777777),
                    )
                    Text(
                        text = header.estimatedDelivery,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1D1D1D),
                    )
                }
            }
        }
    }
}

@Composable
private fun SparePartRow(
    part: PartItem,
    onUpdateStatus: () -> Unit,
) {
    Card(
        shape = RoundedCornerShape(18.dp),
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
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF5F5F5)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_sparepart),
                    contentDescription = null,
                    tint = colorSplashOrange,
                    modifier = Modifier.size(28.dp),
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = part.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1D1D1D),
                )
                Text(
                    text = "Part ID: ${part.id} • Qty: 1",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF777777),
                )
                if (part.notes.isNotBlank()) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = part.notes,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF999999),
                    )
                }
            }

            Spacer(Modifier.width(8.dp))

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                val (pillBg, pillFg, label) = when (part.status) {
                    PartStatus.INSTALLED -> Triple(colorSplashOrange.copy(alpha = 0.16f), colorSplashOrange, "INSTALLED")
                    PartStatus.RECEIVED -> Triple(Color(0xFFE8F7EE), Color(0xFF1B7D3A), "RECEIVED")
                    PartStatus.ORDERED -> Triple(Color(0xFFFFF3D6), Color(0xFFB26A00), "ORDERED")
                    PartStatus.NOT_AVAILABLE -> Triple(Color(0xFFFFE4E4), Color(0xFFB00020), "DELAYED")
                    PartStatus.ALTERNATIVE_USED -> Triple(Color(0xFFE8F0FF), Color(0xFF2E5BFF), "ALTERNATIVE")
                    else -> Triple(Color(0xFFF1F1F1), Color(0xFF555555), "REQUIRED")
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(pillBg)
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = pillFg,
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(colorSplashOrange.copy(alpha = 0.12f))
                        .clickable { onUpdateStatus() }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_repairprogress),
                        contentDescription = null,
                        tint = colorSplashOrange,
                        modifier = Modifier.size(14.dp),
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "Update Status",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = colorSplashOrange,
                    )
                }
            }
        }
    }
}

