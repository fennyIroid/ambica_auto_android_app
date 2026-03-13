package com.ambica.auto.app.ux.container.branches.edit

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.ambica.auto.app.navigation.NavComposeRoute
import com.ambica.auto.app.navigation.NavRoute
import com.ambica.auto.app.navigation.NavRouteDefinition

object EditBranchRoute : NavComposeRoute() {
    private const val ROUTE_BASE = "edit_branch"
    const val ARG_BRANCH_ID = "branch_id"

    override val routeDefinition: NavRouteDefinition =
        NavRouteDefinition("$ROUTE_BASE/{$ARG_BRANCH_ID}")

    override fun getArguments(): List<NamedNavArgument> = listOf(
        navArgument(ARG_BRANCH_ID) { type = NavType.StringType }
    )

    fun createRoute(branchId: String): NavRoute = NavRoute("$ROUTE_BASE/$branchId")
}
