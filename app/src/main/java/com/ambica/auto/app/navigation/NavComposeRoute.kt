package com.ambica.auto.app.navigation

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

abstract class NavComposeRoute {
    abstract val routeDefinition: NavRouteDefinition
    abstract fun getArguments(): List<NamedNavArgument>
    open fun getDeepLinks(): List<NavDeepLink> = emptyList()

    fun addNavigationRoute(
        navGraphBuilder: NavGraphBuilder,
        enterTransition: (@JvmSuppressWildcards AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?)? = null,
        exitTransition: (@JvmSuppressWildcards AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?)? = null,
        popEnterTransition: (@JvmSuppressWildcards AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?)? = enterTransition,
        popExitTransition: (@JvmSuppressWildcards AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?)? = exitTransition,
        content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit,
    ) {
        navGraphBuilder.composable(
            route = routeDefinition.value,
            arguments = getArguments(),
            deepLinks = getDeepLinks(),
            enterTransition = enterTransition,
            exitTransition = exitTransition,
            popEnterTransition = popEnterTransition,
            popExitTransition = popExitTransition,
            content = content
        )
    }
}

abstract class SimpleNavComposeRoute(routeDefinitionValue: String) : NavComposeRoute() {
    override val routeDefinition: NavRouteDefinition = NavRouteDefinition(routeDefinitionValue)
    fun createRoute(): NavRoute = routeDefinition.asNavRoute()
    override fun getArguments(): List<NamedNavArgument> = emptyList()
    override fun getDeepLinks(): List<NavDeepLink> = emptyList()
}
