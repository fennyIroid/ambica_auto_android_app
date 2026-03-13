package com.ambica.auto.app.ux.main.profile

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.ambica.auto.app.BuildConfig
import com.ambica.auto.app.R
import com.ambica.auto.app.navigation.HandleNavigation
import com.ambica.auto.app.ui.theme.colorSplashOrange
import com.ambica.auto.app.ux.main.MainRoute
import com.ambica.auto.app.ux.startup.auth.login.LoginRoute

@Composable
fun ProfileScreen(
    navController: NavController,
    rootNavController: NavHostController,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState
    val state by uiState.stateFlow.collectAsStateWithLifecycle()

    // Logout uses rootNavController to pop the entire main backstack
    LaunchedEffect(state.logoutComplete) {
        if (state.logoutComplete) {
            uiState.event(ProfileUiEvent.OnLogoutNavigated)
            rootNavController.navigate(LoginRoute.routeDefinition.value) {
                popUpTo(MainRoute.routeDefinition.value) { inclusive = true }
            }
        }
    }

    // Edit Profile and other in-stack navigation uses navController
    HandleNavigation(viewModelNav = viewModel, navController = navController)

    ProfileContent(state = state, event = uiState.event)
}

@Composable
private fun ProfileContent(
    state: ProfileDataState,
    event: (ProfileUiEvent) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .statusBarsPadding(),
    ) {
        TopBar()
        Spacer(Modifier.height(24.dp))

        ProfileHeader(
            name = state.name,
            role = state.role.uppercase(),
            branch = "Branch: ${state.branchName}",
            initials = state.initials,
        )
        Spacer(Modifier.height(28.dp))

        Text(
            text = "ACCOUNT SETTINGS",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF5E5E5E),
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        Spacer(Modifier.height(12.dp))

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        ) {
            AccountSettingRow(
                iconRes = R.drawable.ic_report,
                label = "Edit Profile",
                onClick = { event(ProfileUiEvent.OnEditProfileClick) },
            )
            AccountSettingRow(
                iconRes = R.drawable.ic_account_logout,
                label = "Change Password",
                onClick = { event(ProfileUiEvent.OnChangePasswordClick) },
            )
            AccountSettingRow(
                iconRes = R.drawable.ic_insurence,
                label = "Privacy Policy",
                onClick = { },
            )
            AccountSettingRow(
                iconRes = null,
                label = "About Ambica",
                isInfoIcon = true,
                onClick = { },
            )
        }

        Spacer(Modifier.height(24.dp))

        OutlinedButton(
            onClick = { event(ProfileUiEvent.OnLogoutClick) },
            enabled = !state.isLoggingOut,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = colorSplashOrange),
        ) {
            if (state.isLoggingOut) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = colorSplashOrange,
                )
            } else {
                Icon(
                    painter = painterResource(R.drawable.ic_account_logout),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = colorSplashOrange,
                )
                Spacer(Modifier.size(8.dp))
                Text(
                    text = "Logout",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }

        Spacer(Modifier.height(16.dp))
        Text(
            text = "Version ${BuildConfig.VERSION_NAME} (Build ${BuildConfig.VERSION_CODE})",
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFFAAAAAA),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(96.dp))
    }
}

@Composable
private fun TopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFF2F2F2))
                .clickable { },
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
            text = "Profile",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1D1D1D),
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp),
        )
        Spacer(Modifier.size(36.dp))
    }
}

@Composable
private fun ProfileHeader(
    name: String,
    role: String,
    branch: String,
    initials: String,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(colorSplashOrange.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = initials,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = colorSplashOrange,
            )
        }
        Spacer(Modifier.height(12.dp))
        Text(
            text = name,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1D1D1D),
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = role,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = colorSplashOrange,
        )
        Spacer(Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_gatepass),
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = Color(0xFF777777),
            )
            Spacer(Modifier.size(4.dp))
            Text(
                text = branch,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF777777),
            )
        }
    }
}

@Composable
private fun AccountSettingRow(
    label: String,
    onClick: () -> Unit,
    iconRes: Int? = null,
    isInfoIcon: Boolean = false,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (iconRes != null) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFF5F5F5)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(22.dp),
                    tint = colorSplashOrange,
                )
            }
        } else if (isInfoIcon) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF5F5F5)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "i",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = colorSplashOrange,
                )
            }
        }
        Spacer(Modifier.size(14.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF1D1D1D),
            modifier = Modifier.weight(1f),
        )
        Icon(
            painter = painterResource(R.drawable.back_arrow),
            contentDescription = null,
            modifier = Modifier
                .size(18.dp)
                .rotate(180f),
            tint = Color(0xFF888888),
        )
    }
}
