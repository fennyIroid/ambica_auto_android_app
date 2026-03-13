@file:Suppress("unused")

package com.ambica.auto.app.navigation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.navOptions
import com.ambica.auto.app.utils.ext.requireActivity

sealed interface NavigationActionRoute : NavigationAction {
    fun navigate(navController: NavController, resetNavigate: (NavigationAction) -> Unit): Boolean
}

sealed interface NavigationActionIntent : NavigationAction {
    fun navigate(context: Context, resetNavigate: (NavigationAction) -> Unit): Boolean
}

sealed interface NavigationActionFull : NavigationAction {
    fun navigate(context: Context, navController: NavController, resetNavigate: (NavigationAction) -> Unit): Boolean
}

sealed interface NavigationAction {
    data class Navigate(private val route: NavRoute) : NavigationActionRoute {
        override fun navigate(navController: NavController, resetNavigate: (NavigationAction) -> Unit): Boolean {
            navController.navigate(route.value)
            resetNavigate(this)
            return false
        }
    }

    data class NavigateMultiple(val routes: List<NavRoute>) : NavigationActionRoute {
        override fun navigate(navController: NavController, resetNavigate: (NavigationAction) -> Unit): Boolean {
            routes.forEach { navController.navigate(it.value) }
            resetNavigate(this)
            return false
        }
    }

    data class NavigateWithOptions(val route: NavRoute, val navOptions: NavOptions) : NavigationActionRoute {
        override fun navigate(navController: NavController, resetNavigate: (NavigationAction) -> Unit): Boolean {
            navController.navigate(route.value, navOptions)
            resetNavigate(this)
            return false
        }
    }

    data class NavigateWithPopUpTo(
        private val route: NavRoute,
        private val popUpToRouteDefinition: NavRouteDefinition,
        private val inclusive: Boolean = true
    ) : NavigationActionRoute {
        override fun navigate(navController: NavController, resetNavigate: (NavigationAction) -> Unit): Boolean {
            val options = navOptions { popUpTo(popUpToRouteDefinition.value) { this.inclusive = this@NavigateWithPopUpTo.inclusive } }
            navController.navigate(route.value, options)
            resetNavigate(this)
            return false
        }
    }

    data class NavigateIntent(private val intent: Intent, private val options: Bundle? = null) : NavigationActionIntent {
        override fun navigate(context: Context, resetNavigate: (NavigationAction) -> Unit): Boolean {
            context.startActivity(intent, options)
            resetNavigate(this)
            return false
        }
    }

    data object PopIntent : NavigationActionIntent {
        override fun navigate(context: Context, resetNavigate: (NavigationAction) -> Unit): Boolean {
            context.requireActivity().finish()
            resetNavigate(this)
            return false
        }
    }

    data class PopAndNavigate(private val route: NavRoute) : NavigationActionRoute {
        override fun navigate(navController: NavController, resetNavigate: (NavigationAction) -> Unit): Boolean {
            val stackPopped = navController.popBackStack()
            navController.navigate(route.value)
            resetNavigate(this)
            return stackPopped
        }
    }

    data class PopAndNavigateIntent(private val intent: Intent, private val options: Bundle? = null) : NavigationActionFull {
        override fun navigate(context: Context, navController: NavController, resetNavigate: (NavigationAction) -> Unit): Boolean {
            navController.popBackStack()
            context.startActivity(intent, options)
            resetNavigate(this)
            return true
        }
    }

    data class Pop(private val popToRouteDefinition: NavRouteDefinition? = null, private val inclusive: Boolean = false) : NavigationActionRoute {
        override fun navigate(navController: NavController, resetNavigate: (NavigationAction) -> Unit): Boolean {
            val stackPopped = if (popToRouteDefinition == null) {
                navController.popBackStack()
            } else {
                navController.popBackStack(popToRouteDefinition.value, inclusive)
            }
            resetNavigate(this)
            return stackPopped
        }
    }

    data class PopWithResult(
        private val resultValues: List<PopResultKeyValue>,
        private val popToRouteDefinition: NavRouteDefinition? = null,
        private val inclusive: Boolean = false,
    ) : NavigationActionRoute {
        override fun navigate(navController: NavController, resetNavigate: (NavigationAction) -> Unit): Boolean {
            val entry = if (popToRouteDefinition != null) navController.getBackStackEntry(popToRouteDefinition.value) else navController.previousBackStackEntry
            resultValues.forEach { entry?.savedStateHandle?.set(it.key, it.value) }
            val stackPopped = if (popToRouteDefinition == null) navController.popBackStack() else navController.popBackStack(popToRouteDefinition.value, inclusive)
            resetNavigate(this)
            return stackPopped
        }
    }
}

data class PopResultKeyValue(val key: String, val value: Any)

fun NavigationAction.navigate(context: Context, navController: NavController, resetNavigate: (NavigationAction) -> Unit) {
    when (this) {
        is NavigationActionIntent -> navigate(context, resetNavigate)
        is NavigationActionRoute -> navigate(navController, resetNavigate)
        is NavigationActionFull -> navigate(context, navController, resetNavigate)
    }
}
