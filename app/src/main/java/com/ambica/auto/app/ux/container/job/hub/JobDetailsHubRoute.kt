package com.ambica.auto.app.ux.container.job.hub

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.ambica.auto.app.navigation.NavComposeRoute
import com.ambica.auto.app.navigation.NavRoute
import com.ambica.auto.app.navigation.NavRouteDefinition

object JobDetailsHubRoute : NavComposeRoute() {
    private const val ROUTE_BASE = "jobHub"
    const val ARG_JOB_ID = "jobId"

    override val routeDefinition: NavRouteDefinition =
        NavRouteDefinition("$ROUTE_BASE/{$ARG_JOB_ID}")

    override fun getArguments(): List<NamedNavArgument> = listOf(
        navArgument(ARG_JOB_ID) { type = NavType.StringType }
    )

    fun createRoute(jobId: String): NavRoute = NavRoute("$ROUTE_BASE/$jobId")
}

