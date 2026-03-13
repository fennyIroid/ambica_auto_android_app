package com.ambica.auto.app.ux.startup

import android.os.Bundle
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.ambica.auto.app.navigation.HandleNavigation
import com.ambica.auto.app.navigation.graph.AppStartupGraph
import com.ambica.auto.app.ui.theme.AmbicaAutoAppTheme
import com.ambica.auto.app.utils.ext.requireActivity
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun StartupScreen(
    viewModel: StartupViewModel = hiltViewModel(LocalContext.current.requireActivity()),
    startDestination: String = "",
    bundle: Bundle? = null
) {
    val navController = rememberNavController()
    val systemUi = rememberSystemUiController()

    AppStartupGraph(
        navController = navController,
        startDestination = startDestination,
        bundle = bundle
    )
    HandleNavigation(viewModelNav = viewModel, navController = navController)
}
