package com.ambica.auto.app.ux.home

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.ambica.auto.app.R
import com.ambica.auto.app.data.source.local.session.SessionStore
import com.ambica.auto.app.model.domain.job.Job
import com.ambica.auto.app.model.domain.job.JobStatus
import com.ambica.auto.app.ui.compose.common.MetricCard
import com.ambica.auto.app.ui.compose.common.PrimaryPillButton
import com.ambica.auto.app.ui.theme.colorSplashOrange
import com.ambica.auto.app.ux.container.job.create.GateEntryRoute
import com.ambica.auto.app.ux.container.job.hub.JobDetailsHubRoute
import com.ambica.auto.app.ux.container.branches.BranchesRoute
import com.ambica.auto.app.ux.main.jobs.InitialJobsFilter
import com.ambica.auto.app.ux.main.jobs.JobsRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val jobs by viewModel.jobsFlow.collectAsStateWithLifecycle()
    val session by viewModel.sessionFlow.collectAsStateWithLifecycle()

    val bg = Color(0xFFF6F7F9)
    val staffName = session.staffName?.ifBlank { "Staff" } ?: "Staff"
    val branchName = session.branch?.name ?: "Downtown Workshop"

    val vehiclesInWorkshop = jobs.count { it.status == JobStatus.APPROVED_AND_WORK_IN_PROGRESS }
    val pendingInsuranceCount = jobs.count { it.status == JobStatus.AWAITING_INSURANCE_APPROVAL }
    val partsPendingCount = jobs.count { job ->
        job.parts.any { it.status in setOf(com.ambica.auto.app.model.domain.job.PartStatus.REQUIRED, com.ambica.auto.app.model.domain.job.PartStatus.ORDERED) }
    }
    val readyForDeliveryCount = jobs.count { it.status in setOf(JobStatus.READY_FOR_DELIVERY, JobStatus.DELIVERED) }

    val recentJobs = jobs
        .sortedByDescending { lastActivityMillis(it) }
        .take(3)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .statusBarsPadding(),
        verticalArrangement = Arrangement.Top
    ) {
        HomeTopBar(
            staffName = staffName,
            branchName = branchName,
            onNotificationsClick = { /* TODO */ }
        )

        Spacer(Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            MetricCard(
                title = "IN WORKSHOP",
                value = vehiclesInWorkshop.toString().padStart(2, '0'),
                modifier = Modifier.weight(1f),
                onClick = {
                    InitialJobsFilter.filter = "in_progress"
                    navController.navigate(JobsRoute.routeDefinition.value)
                }
            )
            MetricCard(
                title = "PENDING INSURANCE",
                value = pendingInsuranceCount.toString().padStart(2, '0'),
                modifier = Modifier.weight(1f),
                onClick = {
                    InitialJobsFilter.filter = "pending"
                    navController.navigate(JobsRoute.routeDefinition.value)
                }
            )
        }

        Spacer(Modifier.height(10.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            MetricCard(
                title = "PARTS PENDING",
                value = partsPendingCount.toString().padStart(2, '0'),
                modifier = Modifier.weight(1f),
                onClick = { navController.navigate(JobsRoute.routeDefinition.value) }
            )
            MetricCard(
                title = "READY FOR DELIVERY",
                value = readyForDeliveryCount.toString().padStart(2, '0'),
                modifier = Modifier.weight(1f),
                onClick = {
                    InitialJobsFilter.filter = "completed"
                    navController.navigate(JobsRoute.routeDefinition.value)
                }
            )
        }

        Spacer(Modifier.height(14.dp))

        CreateJobQuickActionCard(
            onStartIntake = { navController.navigate(GateEntryRoute.routeDefinition.value) }
        )

        Spacer(Modifier.height(10.dp))

        BranchesQuickAction(
            onClick = { navController.navigate(BranchesRoute.routeDefinition.value) }
        )

        Spacer(Modifier.height(18.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recent Updates",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1D1D1D)
            )
            Text(
                text = "View All",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = colorSplashOrange,
                modifier = Modifier.clickable { navController.navigate(JobsRoute.routeDefinition.value) }
            )
        }

        Spacer(Modifier.height(10.dp))

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            recentJobs.forEach { job ->
                RecentJobRow(
                    job = job,
                    onClick = { navController.navigate(JobDetailsHubRoute.createRoute(job.id.value).value) }
                )
            }
        }

        Spacer(Modifier.height(96.dp))
    }
}

@Composable
private fun HomeTopBar(
    staffName: String,
    branchName: String,
    onNotificationsClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(colorSplashOrange.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "A", color = colorSplashOrange, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.size(10.dp))
            Column {
                Text(
                    text = "Hello, $staffName!",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1D1D1D)
                )
                Text(
                    text = branchName,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF8A8A8A)
                )
            }
        }

        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(Color.White)
                .clickable { onNotificationsClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_bell),
                contentDescription = "Notifications",
                tint = Color(0xFF1D1D1D),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun CreateJobQuickActionCard(
    onStartIntake: () -> Unit,
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = colorSplashOrange),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White.copy(alpha = 0.16f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_plus),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(Color.White.copy(alpha = 0.18f))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "QUICK ACTION",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }

            Spacer(Modifier.height(12.dp))
            Text(
                text = "Create New Job",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Start a new vehicle intake process\nimmediately for the current shift",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.9f)
            )

            Spacer(Modifier.height(14.dp))
            PrimaryPillButton(
                text = "Start Intake",
                modifier = Modifier.fillMaxWidth(),
                onClick = onStartIntake,
            )
        }
    }
}

@Composable
private fun SecondaryHomeButton(
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .height(44.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFFF7518))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
        )
    }
}

@Composable
private fun RecentJobRow(
    job: Job,
    onClick: () -> Unit,
) {
    val (pillText, pillBg, pillFg) = when (job.status) {
        JobStatus.APPROVED_AND_WORK_IN_PROGRESS, JobStatus.NEW_ENTRY, JobStatus.VEHICLE_READY -> Triple("IN PROGRESS", Color(0xFFE8F0FF), Color(0xFF2E5BFF))
        JobStatus.AWAITING_DOCUMENTS, JobStatus.AWAITING_SURVEY, JobStatus.AWAITING_INSURANCE_APPROVAL, JobStatus.AWAITING_PAYMENT -> Triple("AWAITING", Color(0xFFFFF3D6), Color(0xFFB26A00))
        JobStatus.DELIVERED, JobStatus.READY_FOR_DELIVERY -> Triple("READY", Color(0xFFE8F7EE), Color(0xFF1B7D3A))
        else -> Triple(job.status.label.uppercase(), Color(0xFFF1F1F1), Color(0xFF555555))
    }
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF2F2F2)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ambica_auto_bg_rem_logo),
                    contentDescription = null,
                    tint = Color(0xFF1D1D1D),
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(Modifier.size(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${job.vehicleNumber} - ${job.jobCardNumber}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1D1D1D),
                    maxLines = 1
                )
                Text(
                    text = job.customerName,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF8A8A8A),
                    maxLines = 1
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(pillBg)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = pillText,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = pillFg
                    )
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    text = formatAgo(lastActivityMillis(job)),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF8A8A8A)
                )
            }
        }
    }
}

@Composable
private fun BranchesQuickAction(onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(colorSplashOrange.copy(alpha = 0.10f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_branch),
                    contentDescription = null,
                    tint = colorSplashOrange,
                    modifier = Modifier.size(22.dp),
                )
            }
            Spacer(Modifier.size(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Manage Branches",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1D1D1D),
                )
                Text(
                    text = "View and search all branches",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF8A8A8A),
                )
            }
            Icon(
                painter = painterResource(R.drawable.ic_arrow_long_right),
                contentDescription = null,
                tint = colorSplashOrange,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

private fun lastActivityMillis(job: Job): Long =
    job.timeline.maxOfOrNull { it.atMillis } ?: job.gateEntryAtMillis

private fun formatAgo(lastMillis: Long): String {
    val diff = (System.currentTimeMillis() - lastMillis).coerceAtLeast(0L)
    val hours = diff / (1000L * 60L * 60L)
    val mins = (diff / (1000L * 60L)) % 60
    return if (hours >= 1) "${hours}h ago" else "${mins}m ago"
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    jobService: com.ambica.auto.app.domain.repository.JobService,
    sessionStore: SessionStore,
) : androidx.lifecycle.ViewModel() {
    val jobsFlow = jobService.allJobsFlow
    val sessionFlow = sessionStore.sessionFlow
}
