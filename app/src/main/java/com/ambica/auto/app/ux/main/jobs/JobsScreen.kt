package com.ambica.auto.app.ux.main.jobs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.ambica.auto.app.R
import com.ambica.auto.app.model.domain.Branch
import com.ambica.auto.app.model.domain.job.Job
import com.ambica.auto.app.model.domain.job.JobStatus
import com.ambica.auto.app.navigation.HandleNavigation
import com.ambica.auto.app.ui.compose.common.StatusChip
import com.ambica.auto.app.ui.compose.common.toStatusChipType
import com.ambica.auto.app.ui.theme.BackgroundScreen
import com.ambica.auto.app.ui.theme.colorSplashOrange
import com.ambica.auto.app.ui.theme.colorText
import com.ambica.auto.app.ui.theme.colorTextMuted
import com.ambica.auto.app.ui.theme.colorTextSecondary

@Composable
fun JobsScreen(
    navController: NavController,
    viewModel: JobsViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState
    val state by uiState.stateFlow.collectAsStateWithLifecycle()

    // Apply any filter set by the HomeScreen before navigating here
    LaunchedEffect(Unit) {
        uiState.event(JobsUiEvent.OnApplyInitialFilter(InitialJobsFilter.filter))
        InitialJobsFilter.filter = null
    }

    JobsContent(state = state, event = uiState.event)
    HandleNavigation(viewModelNav = viewModel, navController = navController)
}

@Composable
private fun JobsContent(
    state: JobsDataState,
    event: (JobsUiEvent) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundScreen)
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .statusBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.jobs_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black,
                )
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(colorSplashOrange)
                        .clickable { event(JobsUiEvent.OnCreateJobClick) },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_plus),
                        contentDescription = "Create job",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp),
                    )
                }
            }
        }

        item {
            TextField(
                value = state.search,
                onValueChange = { event(JobsUiEvent.OnSearchChange(it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                placeholder = {
                    Text(
                        text = stringResource(R.string.jobs_search_hint),
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    cursorColor = colorSplashOrange,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                ),
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.search_icon),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp),
                    )
                },
            )
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                JobsFilter.entries.forEach { f ->
                    JobsFilterChip(
                        label = when (f) {
                            JobsFilter.TODAY -> stringResource(R.string.jobs_filter_all)
                            JobsFilter.PENDING -> stringResource(R.string.jobs_filter_pending)
                            JobsFilter.IN_PROGRESS -> stringResource(R.string.jobs_filter_in_progress)
                            JobsFilter.COMPLETED -> stringResource(R.string.jobs_filter_completed)
                        },
                        selected = state.filter == f,
                        onClick = { event(JobsUiEvent.OnFilterChange(f)) },
                    )
                }
                if (state.branches.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(8.dp))
                    BranchFilterChip(
                        label = "All Branches",
                        selected = state.selectedBranchId == null,
                        onClick = { event(JobsUiEvent.OnBranchSelect(null)) },
                    )
                    state.branches.forEach { branch ->
                        BranchFilterChip(
                            label = branch.name,
                            selected = state.selectedBranchId == branch.id,
                            onClick = { event(JobsUiEvent.OnBranchSelect(branch.id)) },
                        )
                    }
                }
            }
        }

        item {
            Text(
                text = stringResource(R.string.jobs_active_header, state.jobs.size),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        if (state.jobs.isEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.jobs_no_results),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else {
            items(state.jobs, key = { it.id.value }) { job ->
                JobListCard(
                    job = job,
                    onClick = { event(JobsUiEvent.OnJobClick(job.id.value)) },
                )
            }
        }

        item { Spacer(modifier = Modifier.height(96.dp)) }
    }
}

@Composable
private fun JobsFilterChip(label: String, selected: Boolean, onClick: () -> Unit) {
    val bg = if (selected) colorSplashOrange.copy(alpha = 0.16f) else Color.White
    val fg = if (selected) colorSplashOrange else MaterialTheme.colorScheme.onSurfaceVariant
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(bg)
            .clickable { onClick() }
            .heightIn(min = 48.dp)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, color = fg)
    }
}

@Composable
private fun BranchFilterChip(label: String, selected: Boolean, onClick: () -> Unit) {
    val bg = if (selected) colorSplashOrange.copy(alpha = 0.12f) else Color.White
    val fg = if (selected) colorSplashOrange else MaterialTheme.colorScheme.onSurfaceVariant
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(bg)
            .clickable { onClick() }
            .heightIn(min = 48.dp)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, color = fg)
    }
}

@Composable
private fun JobListCard(job: Job, onClick: () -> Unit) {
    val statusType = job.status.toStatusChipType()
    val statusLabel = when (job.status) {
        JobStatus.APPROVED_AND_WORK_IN_PROGRESS, JobStatus.NEW_ENTRY, JobStatus.VEHICLE_READY ->
            stringResource(R.string.jobs_filter_in_progress)
        JobStatus.AWAITING_DOCUMENTS, JobStatus.AWAITING_SURVEY,
        JobStatus.AWAITING_INSURANCE_APPROVAL, JobStatus.AWAITING_PAYMENT ->
            stringResource(R.string.jobs_filter_pending)
        JobStatus.READY_FOR_DELIVERY, JobStatus.DELIVERED ->
            stringResource(R.string.jobs_filter_completed)
        else -> job.status.label
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "JOB #${job.jobCardNumber}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = colorSplashOrange,
                )
                StatusChip(label = statusLabel, type = statusType)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = job.vehicleNumber, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = colorText)
            Text(text = job.customerName, style = MaterialTheme.typography.bodyMedium, color = colorTextSecondary)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = "Stage: ${job.stage.label}", style = MaterialTheme.typography.bodySmall, color = colorTextMuted)
                Text(text = formatAgo(job), style = MaterialTheme.typography.bodySmall, color = colorTextMuted)
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
