package com.ambica.auto.app.ux.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ambica.auto.app.navigation.HandleNavBarNavigation
import com.ambica.auto.app.navigation.graph.AppMainGraph
import com.ambica.auto.app.ux.main.bottombar.AmbicaBottomBar
import com.ambica.auto.app.ux.main.bottombar.NavBarItem
import com.ambica.auto.app.ui.theme.colorSplashOrange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    rootNavController: NavHostController,
    viewModel: MainViewModel = hiltViewModel(),
) {
    val navController = rememberNavController()
    val currBackStackState by navController.currentBackStackEntryAsState()
    val currDestination = currBackStackState?.destination

    // Routes that should show the bottom navigation bar.
    val mainRoutes = setOf(
        com.ambica.auto.app.ux.main.dashboard.DashboardRoute.routeDefinition.value,
        com.ambica.auto.app.ux.main.jobs.JobsRoute.routeDefinition.value,
        com.ambica.auto.app.ux.main.profile.ProfileRoute.routeDefinition.value,
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        AppMainGraph(navController = navController, rootNavController = rootNavController)

        if (currDestination?.route in mainRoutes) {
            AmbicaBottomBar(
                currDestination = currDestination,
                onNavItemClicked = { item -> viewModel.onNavBarItemSelected(item) },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }

    HandleNavBarNavigation(viewModelNavBar = viewModel, navController = navController)
}

