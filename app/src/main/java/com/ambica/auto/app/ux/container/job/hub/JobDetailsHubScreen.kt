package com.ambica.auto.app.ux.container.job.hub

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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.ambica.auto.app.R
import com.ambica.auto.app.data.source.local.demo.DemoRepository
import com.ambica.auto.app.model.domain.job.Job
import com.ambica.auto.app.model.domain.job.JobId
import com.ambica.auto.app.model.domain.job.JobStage
import com.ambica.auto.app.model.domain.job.JobStatus
import com.ambica.auto.app.ui.compose.common.SectionCard
import com.ambica.auto.app.ui.compose.common.StatusChip
import com.ambica.auto.app.ui.compose.common.TimelineItem
import com.ambica.auto.app.ui.compose.common.toStatusChipType
import com.ambica.auto.app.ui.theme.BackgroundScreen
import com.ambica.auto.app.ui.theme.colorSplashOrange
import com.ambica.auto.app.ux.container.job.modules.billing_payment.BillingPaymentRoute
import com.ambica.auto.app.ux.container.job.modules.estimate_docs.EstimateDocsRoute
import com.ambica.auto.app.ux.container.job.modules.gate_pass.GatePassRoute
import com.ambica.auto.app.ux.container.job.modules.insurance_survey.InsuranceSurveyRoute
import com.ambica.auto.app.ux.container.job.modules.repair_progress.RepairProgressRoute
import com.ambica.auto.app.ux.container.job.modules.spare_parts.SparePartsRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@Composable
fun JobDetailsHubScreen(
    navController: NavController,
    jobId: String,
    uiState: JobDetailsHubUiState,
) {
    val state by uiState.stateFlow.collectAsStateWithLifecycle()
    LaunchedEffect(jobId) {
        uiState.event(JobDetailsHubUiEvent.OnSetJobId(jobId))
    }
    val job = state.job
    val visibleSections = state.visibleSections

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundScreen)
            .statusBarsPadding()
    ) {
        JobHubTopBar(
            onBack = { navController.popBackStack() },
            onMenu = { },
            subtitle = job?.let { formatBranchDisplay(it.branchId) } ?: "—"
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .padding(bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (job != null) {
                if (visibleSections.contains(JobDetailSection.OVERVIEW)) {
                    OverviewSectionCard(job = job!!)
                }
                if (visibleSections.contains(JobDetailSection.DOCUMENTS)) {
                    DocumentsSectionCard(
                        job = job!!,
                        onView = { navController.navigate(EstimateDocsRoute.createRoute(jobId).value) }
                    )
                }
                if (visibleSections.contains(JobDetailSection.INSURANCE)) {
                    InsuranceSectionCard(
                        job = job!!,
                        onView = { navController.navigate(InsuranceSurveyRoute.createRoute(jobId).value) }
                    )
                }
                if (visibleSections.contains(JobDetailSection.REPAIR_PROGRESS)) {
                    RepairProgressSectionCard(
                        job = job!!,
                        onView = { navController.navigate(RepairProgressRoute.createRoute(jobId).value) }
                    )
                }
                if (visibleSections.contains(JobDetailSection.PARTS_TRACKING)) {
                    PartsSectionCard(
                        job = job!!,
                        onView = { navController.navigate(SparePartsRoute.createRoute(jobId).value) }
                    )
                }
                if (visibleSections.contains(JobDetailSection.BILLING)) {
                    BillingSectionCard(
                        job = job!!,
                        onView = { navController.navigate(BillingPaymentRoute.createRoute(jobId).value) }
                    )
                }
                if (visibleSections.contains(JobDetailSection.GATE_PASS)) {
                    GatePassSectionCard(
                        job = job!!,
                        onView = { navController.navigate(GatePassRoute.createRoute(jobId).value) }
                    )
                }
                if (visibleSections.contains(JobDetailSection.NOTES)) {
                    NotesSectionCard(job = job!!)
                }
            } else {
                CurrentStatusSkeleton()
                VehicleInformationSkeleton()
            }
        }
    }
}

@Composable
private fun JobHubTopBar(
    onBack: () -> Unit,
    onMenu: () -> Unit,
    subtitle: String = "—",
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
                .clickable { onBack() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.back_arrow),
                contentDescription = "Back",
                tint = Color(0xFF1D1D1D),
                modifier = Modifier.size(24.dp)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Job Details",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1D1D1D)
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = colorSplashOrange
            )
        }

        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
                .clickable { onMenu() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "⋮",
                color = Color(0xFF1D1D1D),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun OverviewSectionCard(job: Job) {
    val statusType = job.status.toStatusChipType()
    val statusLabel = when (job.status) {
        JobStatus.ON_HOLD -> job.holdReason?.label?.let { "On Hold: $it" } ?: "On Hold"
        else -> job.status.label
    }
    SectionCard(
        title = JobDetailSection.OVERVIEW.title,
        iconRes = R.drawable.ic_overview,
    ) {
        InfoRow("Job Card #", job.jobCardNumber)
        InfoRow("Vehicle #", job.vehicleNumber)
        InfoRow("Customer", job.customerName)
        InfoRow("Branch", formatBranchDisplay(job.branchId))
        InfoRow("Gate Entry", formatGateEntryTime(job.gateEntryAtMillis))
        Spacer(modifier = Modifier.height(8.dp))
        StatusChip(label = statusLabel, type = statusType)
    }
}

@Composable
private fun DocumentsSectionCard(job: Job, onView: () -> Unit) {
    val receivedCount = job.documents.count { it.received }
    SectionCard(
        title = JobDetailSection.DOCUMENTS.title,
        iconRes = R.drawable.ic_report,
    ) {
        Text(
            text = "Estimate documents and approvals",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        SectionCardAction(onView = onView)
    }
}

@Composable
private fun InsuranceSectionCard(job: Job, onView: () -> Unit) {
    val ins = job.insuranceInfo
    SectionCard(
        title = JobDetailSection.INSURANCE.title,
        iconRes = R.drawable.ic_insurence,
    ) {
        InfoRow("Approval", ins.approvalStatus.label)
        if (ins.company != null) InfoRow("Company", ins.company)
        if (ins.notes.isNotBlank()) Text(
            text = ins.notes,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp)
        )
        SectionCardAction(onView = onView)
    }
}

@Composable
private fun RepairProgressSectionCard(job: Job, onView: () -> Unit) {
    val stages = JobStage.repairStages
    val currentIndex = stages.indexOf(job.stage)
    SectionCard(
        title = JobDetailSection.REPAIR_PROGRESS.title,
        iconRes = R.drawable.ic_repairprogress,
    ) {
        stages.forEachIndexed { index, stage ->
            TimelineItem(
                title = stage.label,
                subtitle = when {
                    index < currentIndex -> "Done"
                    index == currentIndex -> "In progress"
                    else -> "Pending"
                },
                isCompleted = index < currentIndex,
                isCurrent = index == currentIndex,
                showConnector = index < stages.lastIndex
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "${job.progress.sumOf { it.photos.size }} progress photos",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        SectionCardAction(onView = onView)
    }
}

@Composable
private fun PartsSectionCard(job: Job, onView: () -> Unit) {
    SectionCard(
        title = JobDetailSection.PARTS_TRACKING.title,
        iconRes = R.drawable.ic_sparepart,
    ) {
        if (job.parts.isEmpty()) {
            Text(
                text = "No parts listed",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            job.parts.take(4).forEach { part ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = part.name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    StatusChip(
                        label = part.status.label,
                        type = when (part.status) {
                            com.ambica.auto.app.model.domain.job.PartStatus.INSTALLED,
                            com.ambica.auto.app.model.domain.job.PartStatus.RECEIVED -> com.ambica.auto.app.ui.compose.common.StatusChipType.COMPLETED
                            com.ambica.auto.app.model.domain.job.PartStatus.ORDERED -> com.ambica.auto.app.ui.compose.common.StatusChipType.IN_PROGRESS
                            com.ambica.auto.app.model.domain.job.PartStatus.NOT_AVAILABLE -> com.ambica.auto.app.ui.compose.common.StatusChipType.ON_HOLD
                            else -> com.ambica.auto.app.ui.compose.common.StatusChipType.PENDING
                        }
                    )
                }
            }
            if (job.parts.size > 4) {
                Text(
                    text = "+${job.parts.size - 4} more",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        SectionCardAction(onView = onView)
    }
}

@Composable
private fun BillingSectionCard(job: Job, onView: () -> Unit) {
    val b = job.billing
    SectionCard(
        title = JobDetailSection.BILLING.title,
        iconRes = R.drawable.ic_payment,
    ) {
        InfoRow("Insurance liability", "₹${b.insuranceLiability}")
        InfoRow("Customer payment", "₹${b.customerLiability}")
        InfoRow("Payment verified", if (b.paymentVerified) "Yes" else "No")
        SectionCardAction(onView = onView)
    }
}

@Composable
private fun NotesSectionCard(job: Job) {
    SectionCard(
        title = JobDetailSection.NOTES.title,
        iconRes = R.drawable.ic_report,
    ) {
        if (job.visibleNotes.isNotBlank()) {
            Text(
                text = job.visibleNotes,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        job.notes.forEach { note ->
            Text(
                text = "${note.note} — ${note.createdBy}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        if (job.notes.isEmpty() && job.visibleNotes.isBlank()) {
            Text(
                text = "No notes yet",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun GatePassSectionCard(job: Job, onView: () -> Unit) {
    val gp = job.gatePass
    SectionCard(
        title = JobDetailSection.GATE_PASS.title,
        iconRes = R.drawable.ic_gatepass,
    ) {
        Text(
            text = if (gp.generated) "Gate pass: ${gp.gatePassNumber ?: "—"}" else "Gate pass not generated",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (gp.gateOutAtMillis != null) {
            Text(
                text = "Delivered ${formatGateEntryTime(gp.gateOutAtMillis)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        SectionCardAction(onView = onView)
    }
}

@Composable
private fun SectionCardAction(onView: () -> Unit) {
    Spacer(modifier = Modifier.height(12.dp))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onView() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "View",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = colorSplashOrange
        )
        Icon(
            painter = painterResource(R.drawable.ic_arrow_long_right),
            contentDescription = null,
            tint = colorSplashOrange,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
private fun CurrentStatusSkeleton() {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(Color(0xFFE0E0E0))
            )
            Spacer(Modifier.size(12.dp))
            Box(
                modifier = Modifier
                    .height(14.dp)
                    .fillMaxWidth(0.5f)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFFE8E8E8))
            )
        }
    }
}

@Composable
private fun VehicleInformationSection(job: Job) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "VEHICLE INFORMATION",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF5E5E5E)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                InfoRow("Job Card #", "JC-${job.jobCardNumber}")
                InfoRow("Vehicle #", job.vehicleNumber)
                InfoRow("Customer Name", job.customerName)
                InfoRow("Branch", formatBranchDisplay(job.branchId))
                InfoRow("Gate Entry Time", formatGateEntryTime(job.gateEntryAtMillis))
            }
        }
    }
}

@Composable
private fun VehicleInformationSkeleton() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "VEHICLE INFORMATION",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF5E5E5E)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                repeat(5) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(20.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFFE8E8E8))
                    )
                    if (it < 4) Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF5E5E5E)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF1D1D1D)
        )
    }
}

private fun formatBranchDisplay(branchId: String): String {
    return branchId.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        .replace("_", " ")
}

private fun formatGateEntryTime(millis: Long): String {
    val sdf = SimpleDateFormat("h:mm a, d MMM", Locale.getDefault())
    return sdf.format(Date(millis))
}
