package com.ambica.auto.app.ux.container.branches.detail

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.ambica.auto.app.R
import com.ambica.auto.app.data.source.remote.model.branch.BranchItemResponse
import com.ambica.auto.app.navigation.HandleNavigation
import com.ambica.auto.app.ui.theme.BackgroundScreen
import com.ambica.auto.app.ui.theme.colorSplashOrange
import com.ambica.auto.app.ui.theme.colorText
import com.ambica.auto.app.ui.theme.colorTextMuted
import com.ambica.auto.app.ui.theme.colorTextSecondary

@Composable
fun BranchDetailScreen(
    navController: NavController,
    viewModel: BranchDetailViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState
    val state by uiState.stateFlow.collectAsStateWithLifecycle()

    BranchDetailContent(state = state, event = uiState.event)
    HandleNavigation(viewModelNav = viewModel, navController = navController)
}

@Composable
private fun BranchDetailContent(
    state: BranchDetailDataState,
    event: (BranchDetailUiEvent) -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.error) {
        state.error?.let {
            snackbarHostState.showSnackbar(it)
            event(BranchDetailUiEvent.OnDismissError)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundScreen)
                .statusBarsPadding(),
        ) {
            TopBar(
                onBack = { event(BranchDetailUiEvent.OnBack) },
                canEdit = state.canEditBranch,
                onEdit = { event(BranchDetailUiEvent.OnEditBranch) },
            )

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
                state.branch == null && state.error == null -> {
                    EmptyRetry(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(48.dp),
                        onRetry = { event(BranchDetailUiEvent.OnRetry) },
                    )
                }
                state.branch != null -> {
                    BranchDetailBody(branch = state.branch)
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
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
private fun TopBar(
    onBack: () -> Unit,
    canEdit: Boolean = false,
    onEdit: () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
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
            text = "Branch Details",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = colorText,
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp),
        )
        if (canEdit) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(colorSplashOrange)
                    .clickable { onEdit() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_edit),
                    contentDescription = "Edit branch",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}

@Composable
private fun BranchDetailBody(branch: BranchItemResponse) {
    val isActive = branch.status == 1
    val statusText = branch.statusDisplay ?: if (isActive) "Active" else "Inactive"
    val statusBg = if (isActive) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
    val statusFg = if (isActive) Color(0xFF2E7D32) else Color(0xFFC62828)

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
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(colorSplashOrange.copy(alpha = 0.10f)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_branch),
                                contentDescription = null,
                                tint = colorSplashOrange,
                                modifier = Modifier.size(24.dp),
                            )
                        }
                        Spacer(modifier = Modifier.size(14.dp))
                        Column {
                            Text(
                                text = branch.name.orEmpty(),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = colorText,
                            )
                            if (!branch.code.isNullOrBlank()) {
                                Text(
                                    text = "Code: ${branch.code}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = colorTextSecondary,
                                )
                            }
                        }
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(statusBg)
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = statusText.uppercase(),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = statusFg,
                        )
                    }
                }
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
                DetailSectionTitle("Location & Contact")

                val locationOrCity = branch.location ?: branch.city
                if (!locationOrCity.isNullOrBlank()) {
                    DetailRow(label = "Location", value = locationOrCity)
                }
                if (!branch.tagline.isNullOrBlank()) {
                    DetailRow(label = "Tagline", value = branch.tagline)
                }
                if (!branch.address.isNullOrBlank()) {
                    DetailRow(label = "Address", value = branch.address)
                }
                if (!branch.phone.isNullOrBlank()) {
                    DetailRow(label = "Phone", value = branch.phone)
                }
                if (!branch.email.isNullOrBlank()) {
                    DetailRow(label = "Email", value = branch.email)
                }
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
                DetailSectionTitle("Staff")

                val managerDisplay = branch.managerNames ?: branch.managerName
                if (!managerDisplay.isNullOrBlank()) {
                    DetailRow(label = "Manager(s)", value = managerDisplay)
                }
                val staffCount = branch.staffCount ?: branch.totalStaff
                if (staffCount != null) {
                    DetailRow(label = "Total Staff", value = staffCount.toString())
                }
                if (managerDisplay.isNullOrBlank() && staffCount == null) {
                    Text(
                        text = "No staff assigned yet",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorTextMuted,
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailSectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold,
        color = colorText,
    )
}

@Composable
private fun DetailRow(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = colorTextMuted,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = colorText,
            lineHeight = 20.sp,
        )
    }
}

@Composable
private fun EmptyRetry(
    modifier: Modifier = Modifier,
    onRetry: () -> Unit,
) {
    Column(
        modifier = modifier.clickable { onRetry() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_branch),
            contentDescription = null,
            tint = colorTextMuted.copy(alpha = 0.5f),
            modifier = Modifier.size(48.dp),
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Unable to load branch details",
            style = MaterialTheme.typography.bodyMedium,
            color = colorTextMuted,
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Tap to retry",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = colorSplashOrange,
            modifier = Modifier.padding(16.dp),
        )
    }
}
