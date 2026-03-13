package com.ambica.auto.app.ux.container.branches

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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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

private const val REFRESH_BRANCHES_KEY = "refresh_branches"
private const val SUCCESS_MESSAGE_KEY = "branch_success_message"

@Composable
fun BranchesScreen(
    navController: NavController,
    viewModel: BranchesViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState
    val state by uiState.stateFlow.collectAsStateWithLifecycle()

    val backStackEntry = navController.currentBackStackEntry
    LaunchedEffect(backStackEntry) {
        backStackEntry ?: return@LaunchedEffect
        backStackEntry.savedStateHandle.getStateFlow(REFRESH_BRANCHES_KEY, false)
            .collect { shouldRefresh ->
                if (shouldRefresh) {
                    val successMessage = backStackEntry.savedStateHandle.get<String>(SUCCESS_MESSAGE_KEY)
                    backStackEntry.savedStateHandle.remove<Boolean>(REFRESH_BRANCHES_KEY)
                    backStackEntry.savedStateHandle.remove<String>(SUCCESS_MESSAGE_KEY)
                    uiState.event(BranchesUiEvent.OnRetry)
                    successMessage?.let { uiState.event(BranchesUiEvent.OnShowSuccess(it)) }
                }
            }
    }

    BranchesContent(state = state, event = uiState.event)
    HandleNavigation(viewModelNav = viewModel, navController = navController)
}

@Composable
private fun BranchesContent(
    state: BranchesDataState,
    event: (BranchesUiEvent) -> Unit,
) {
    val errorSnackbar = remember { SnackbarHostState() }
    val successSnackbar = remember { SnackbarHostState() }
    var branchToDelete by remember { mutableStateOf<BranchItemResponse?>(null) }

    LaunchedEffect(state.error) {
        state.error?.let {
            errorSnackbar.showSnackbar(it)
            event(BranchesUiEvent.OnDismissError)
        }
    }

    LaunchedEffect(state.successMessage) {
        state.successMessage?.let {
            successSnackbar.showSnackbar(it, duration = SnackbarDuration.Short)
            event(BranchesUiEvent.OnDismissSuccess)
        }
    }

    branchToDelete?.let { branch ->
        DeleteConfirmDialog(
            branchName = branch.name.orEmpty(),
            onDismiss = { branchToDelete = null },
            onConfirm = {
                branch.id?.let { id -> event(BranchesUiEvent.OnDeleteBranch(id)) }
                branchToDelete = null
            },
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundScreen)
                .padding(horizontal = 16.dp)
                .statusBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item { Spacer(modifier = Modifier.height(4.dp)) }

            item {
                TopBar(
                    onBack = { event(BranchesUiEvent.OnBack) },
                    canCreate = state.canCreateBranch,
                    onCreate = { event(BranchesUiEvent.OnCreateBranch) },
                )
            }

            item {
                TextField(
                    value = state.search,
                    onValueChange = { event(BranchesUiEvent.OnSearchChange(it)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    placeholder = {
                        Text(
                            text = "Search branch name...",
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
                    BranchStatusFilter.entries.forEach { filter ->
                        FilterChip(
                            label = when (filter) {
                                BranchStatusFilter.ALL -> "All"
                                BranchStatusFilter.ACTIVE -> "Active"
                                BranchStatusFilter.INACTIVE -> "Inactive"
                            },
                            selected = state.statusFilter == filter,
                            onClick = { event(BranchesUiEvent.OnStatusFilterChange(filter)) },
                        )
                    }
                }
            }

            item {
                Text(
                    text = "${state.branches.size} branch${if (state.branches.size != 1) "es" else ""}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            if (state.isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 48.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(color = colorSplashOrange)
                    }
                }
            } else if (state.branches.isEmpty() && state.error == null) {
                item {
                    EmptyState(
                        search = state.search,
                        filter = state.statusFilter,
                        onRetry = { event(BranchesUiEvent.OnRetry) },
                    )
                }
            } else {
                items(state.branches, key = { it.id ?: "" }) { branch ->
                    BranchCard(
                        branch = branch,
                        canDelete = state.canCreateBranch,
                        isDeleting = state.deletingBranchId == branch.id,
                        onCardClick = { branch.id?.let { id -> event(BranchesUiEvent.OnBranchClick(id)) } },
                        onDeleteClick = { branchToDelete = branch },
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(96.dp)) }
        }

        SnackbarHost(
            hostState = successSnackbar,
            modifier = Modifier.align(Alignment.BottomCenter),
        ) { data ->
            Snackbar(
                snackbarData = data,
                containerColor = Color(0xFF2E7D32),
                contentColor = Color.White,
                shape = RoundedCornerShape(12.dp),
            )
        }

        SnackbarHost(
            hostState = errorSnackbar,
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
    canCreate: Boolean,
    onCreate: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
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
            text = "Branches",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = colorText,
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp),
        )
        if (canCreate) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(colorSplashOrange)
                    .clickable { onCreate() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_plus),
                    contentDescription = "Create branch",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}

@Composable
private fun FilterChip(label: String, selected: Boolean, onClick: () -> Unit) {
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
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = fg,
        )
    }
}

@Composable
private fun BranchCard(
    branch: BranchItemResponse,
    canDelete: Boolean,
    isDeleting: Boolean,
    onCardClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    val isActive = branch.status == 1
    val statusText = branch.statusDisplay ?: if (isActive) "Active" else "Inactive"
    val statusBg = if (isActive) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
    val statusFg = if (isActive) Color(0xFF2E7D32) else Color(0xFFC62828)

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick() },
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
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(colorSplashOrange.copy(alpha = 0.10f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_branch),
                            contentDescription = null,
                            tint = colorSplashOrange,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = branch.name.orEmpty(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = colorText,
                        )
                        val locationOrCity = branch.location ?: branch.city
                        if (!locationOrCity.isNullOrBlank()) {
                            Text(
                                text = locationOrCity,
                                style = MaterialTheme.typography.bodySmall,
                                color = colorTextSecondary,
                            )
                        }
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(statusBg)
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = statusText.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = statusFg,
                        )
                    }
                    if (canDelete) {
                        if (isDeleting) {
                            CircularProgressIndicator(
                                color = colorSplashOrange,
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp,
                            )
                        } else {
                            IconButton(
                                onClick = onDeleteClick,
                                modifier = Modifier.size(36.dp),
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_delete),
                                    contentDescription = "Delete branch",
                                    tint = Color(0xFFC62828),
                                    modifier = Modifier.size(20.dp),
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                val managerDisplay = branch.managerNames ?: branch.managerName
                if (!managerDisplay.isNullOrBlank()) {
                    InfoPill(label = "Manager", value = managerDisplay)
                }
                val staffCount = branch.staffCount ?: branch.totalStaff
                if (staffCount != null) {
                    InfoPill(label = "Staff", value = staffCount.toString())
                }
            }

            if (!branch.address.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = branch.address,
                    style = MaterialTheme.typography.bodySmall,
                    color = colorTextMuted,
                    lineHeight = 18.sp,
                )
            }

            if (!branch.phone.isNullOrBlank() || !branch.email.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    if (!branch.phone.isNullOrBlank()) {
                        Text(
                            text = branch.phone,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = colorTextSecondary,
                        )
                    }
                    if (!branch.email.isNullOrBlank()) {
                        Text(
                            text = branch.email,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = colorTextSecondary,
                            maxLines = 1,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoPill(label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "$label: ",
            style = MaterialTheme.typography.bodySmall,
            color = colorTextMuted,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = colorText,
        )
    }
}

@Composable
private fun EmptyState(
    search: String,
    filter: BranchStatusFilter,
    onRetry: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_branch),
            contentDescription = null,
            tint = colorTextMuted.copy(alpha = 0.5f),
            modifier = Modifier.size(48.dp),
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = if (search.isNotBlank()) "No branches found for \"$search\""
            else if (filter != BranchStatusFilter.ALL) "No ${filter.name.lowercase()} branches"
            else "No branches available",
            style = MaterialTheme.typography.bodyMedium,
            color = colorTextMuted,
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Tap to retry",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = colorSplashOrange,
            modifier = Modifier.clickable { onRetry() },
        )
    }
}

@Composable
private fun DeleteConfirmDialog(
    branchName: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Branch") },
        text = { Text("Are you sure you want to delete \"$branchName\"? This action cannot be undone.") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Delete", color = Color(0xFFC62828), fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = colorSplashOrange, fontWeight = FontWeight.SemiBold)
            }
        },
    )
}
