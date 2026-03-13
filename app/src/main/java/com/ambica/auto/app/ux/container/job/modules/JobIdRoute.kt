package com.ambica.auto.app.ux.container.job.modules

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.ambica.auto.app.navigation.NavComposeRoute
import com.ambica.auto.app.navigation.NavRoute
import com.ambica.auto.app.navigation.NavRouteDefinition

abstract class JobIdRoute(private val base: String) : NavComposeRoute() {
    companion object {
        const val ARG_JOB_ID = "jobId"
    }

    override val routeDefinition: NavRouteDefinition = NavRouteDefinition("$base/{$ARG_JOB_ID}")
    override fun getArguments(): List<NamedNavArgument> =
        listOf(navArgument(ARG_JOB_ID) { type = NavType.StringType })

    fun createRoute(jobId: String): NavRoute = NavRoute("$base/$jobId")
}

