package com.ambica.auto.app.navigation.graph

import android.os.Bundle
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.ambica.auto.app.navigation.SimpleNavComposeRoute
import com.ambica.auto.app.ui.theme.colorAppBg
import com.ambica.auto.app.ux.main.MainRoute
import com.ambica.auto.app.ux.main.MainScreen
import com.ambica.auto.app.ux.startup.auth.login.LoginRoute
import com.ambica.auto.app.ux.startup.auth.login.LoginScreen
import com.ambica.auto.app.ux.startup.auth.setpassword.SetPasswordRoute
import com.ambica.auto.app.ux.startup.auth.setpassword.SetPasswordScreen
import com.ambica.auto.app.ux.startup.splash.SplashRoute
import com.ambica.auto.app.ux.startup.splash.SplashScreen

@Composable
fun AppStartupGraph(
    navController: NavHostController,
    startDestination: String,
    bundle: Bundle? = null
) {
    NavHost(
        navController = navController,
        startDestination = SplashRoute.routeDefinition.value,
        modifier = Modifier.fillMaxSize().background(colorAppBg)
    ) {
        (SplashRoute as SimpleNavComposeRoute).addNavigationRoute(
            this,
            enterTransition = { fadeIn(tween(300)) },
            exitTransition = { fadeOut(tween(300)) }
        ) {
            SplashScreen(navController = navController, bundle = bundle)
        }
        (LoginRoute as SimpleNavComposeRoute).addNavigationRoute(
            this,
            enterTransition = { fadeIn(tween(250)) },
            exitTransition = { fadeOut(tween(250)) }
        ) { LoginScreen(navController = navController) }

        (SetPasswordRoute as SimpleNavComposeRoute).addNavigationRoute(
            this,
            enterTransition = { fadeIn(tween(250)) },
            exitTransition = { fadeOut(tween(250)) }
        ) { SetPasswordScreen(navController = navController) }

        (MainRoute as SimpleNavComposeRoute).addNavigationRoute(this) {
            MainScreen(rootNavController = navController)
        }
    }
}
