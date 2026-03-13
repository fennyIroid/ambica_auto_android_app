package com.ambica.auto.app.ux.main.dashboard

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import com.ambica.auto.app.model.domain.job.Job
import com.ambica.auto.app.model.domain.job.JobStatus
import com.ambica.auto.app.navigation.HandleNavigation
import com.ambica.auto.app.ui.compose.common.FilterPillChip
import com.ambica.auto.app.ui.compose.common.PrimaryPillButton
import com.ambica.auto.app.ui.theme.colorSplashOrange

@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: DashboardViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState
    val state by uiState.stateFlow.collectAsStateWithLifecycle()

    DashboardContent(state = state, event = uiState.event)
    HandleNavigation(viewModelNav = viewModel, navController = navController)
}

@Composable
private fun DashboardContent(
    state: DashboardDataState,
    event: (DashboardUiEvent) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.Top,
    ) {
        Text(
            text = "Job Dashboard",
            style = MaterialTheme.typography.titleSmall,
            color = Color(0xFF7A7A7A),
        )
        Spacer(modifier = Modifier.height(8.dp))

        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${state.branchName} Branch",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = state.branchCity,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF8A8A8A),
                    )
                }
                Icon(
                    painter = painterResource(R.drawable.ic_bell),
                    contentDescription = "Notifications",
                    tint = colorSplashOrange,
                    modifier = Modifier
                        .size(22.dp)
                        .padding(end = 14.dp),
                )
                Icon(
                    painter = painterResource(R.drawable.ic_profile),
                    contentDescription = "Profile",
                    tint = colorSplashOrange,
                    modifier = Modifier.size(22.dp),
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextField(
            value = state.search,
            onValueChange = { event(DashboardUiEvent.OnSearchChange(it)) },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            placeholder = { Text("Search vehicle number...", color = Color.White.copy(alpha = 0.85f)) },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = colorSplashOrange,
                unfocusedContainerColor = colorSplashOrange,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = Color.White,
            ),
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.ic_job),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp),
                )
            },
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            FilterPillChip("All Jobs", state.filter == DashboardFilter.ALL) { event(DashboardUiEvent.OnFilterChange(DashboardFilter.ALL)) }
            FilterPillChip("In Repair", state.filter == DashboardFilter.IN_REPAIR) { event(DashboardUiEvent.OnFilterChange(DashboardFilter.IN_REPAIR)) }
            FilterPillChip("Pending", state.filter == DashboardFilter.PENDING) { event(DashboardUiEvent.OnFilterChange(DashboardFilter.PENDING)) }
            FilterPillChip("Completed", state.filter == DashboardFilter.COMPLETED) { event(DashboardUiEvent.OnFilterChange(DashboardFilter.COMPLETED)) }
        }

        Spacer(modifier = Modifier.height(12.dp))

        AnimatedVisibility(
            visible = state.filteredJobs.isNotEmpty(),
            enter = fadeIn(tween(180)),
            exit = fadeOut(tween(180)),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                state.filteredJobs.take(6).forEach { job ->
                    JobDashboardCard(
                        job = job,
                        onViewDetails = { event(DashboardUiEvent.OnJobClick(job.id.value)) },
                    )
                }
                Spacer(modifier = Modifier.height(96.dp))
            }
        }
    }
}

@Composable
private fun JobDashboardCard(job: Job, onViewDetails: () -> Unit) {
    val (pillText, pillBg, pillFg) = when (job.status) {
        JobStatus.APPROVED_AND_WORK_IN_PROGRESS, JobStatus.NEW_ENTRY, JobStatus.VEHICLE_READY ->
            Triple("IN REPAIR", Color(0xFFE8F0FF), Color(0xFF2E5BFF))
        JobStatus.AWAITING_DOCUMENTS, JobStatus.AWAITING_SURVEY,
        JobStatus.AWAITING_INSURANCE_APPROVAL, JobStatus.AWAITING_PAYMENT ->
            Triple("PENDING", Color(0xFFFFF3D6), Color(0xFFB26A00))
        JobStatus.DELIVERED, JobStatus.READY_FOR_DELIVERY ->
            Triple("READY", Color(0xFFE8F7EE), Color(0xFF1B7D3A))
        else -> Triple(job.status.label.uppercase(), Color(0xFFF1F1F1), Color(0xFF555555))
    }

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.Top, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = job.vehicleNumber,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = job.customerName.uppercase(),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF7A7A7A),
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(pillBg)
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                ) {
                    Text(
                        text = pillText,
                        color = pillFg,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = "Claim ID\n${job.claimNumber ?: "-"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF777777),
                )
                Text(
                    text = "Updated\n${formatAgo(job)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF777777),
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                PrimaryPillButton(
                    text = "VIEW DETAILS  →",
                    modifier = Modifier.weight(1f),
                    onClick = onViewDetails,
                )
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFF1F1F1)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_share),
                        contentDescription = "Share",
                        tint = Color(0xFF666666),
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
        }
    }
}

private fun formatAgo(job: Job): String {
    val last = job.timeline.maxOfOrNull { it.atMillis } ?: job.gateEntryAtMillis
    val diff = (System.currentTimeMillis() - last).coerceAtLeast(0L)
    val hours = diff / (1000L * 60L * 60L)
    val mins = (diff / (1000L * 60L)) % 60
    return if (hours >= 1) "${hours}h ago" else "${mins}m ago"
}
