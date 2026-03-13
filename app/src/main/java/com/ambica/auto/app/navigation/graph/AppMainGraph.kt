package com.ambica.auto.app.navigation.graph

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.ambica.auto.app.navigation.SimpleNavComposeRoute
import com.ambica.auto.app.ux.container.job.create.GateEntryRoute
import com.ambica.auto.app.ux.container.job.create.GateEntryScreen
import com.ambica.auto.app.ux.container.job.hub.JobDetailsHubRoute
import com.ambica.auto.app.ux.container.job.hub.JobDetailsHubScreen
import com.ambica.auto.app.ux.container.job.hub.JobDetailsHubViewModel
import com.ambica.auto.app.ux.container.job.modules.estimate_docs.EstimateDocsRoute
import com.ambica.auto.app.ux.container.job.modules.billing_payment.BillingPaymentRoute
import com.ambica.auto.app.ux.container.job.modules.gate_pass.GatePassRoute
import com.ambica.auto.app.ux.container.job.modules.insurance_survey.InsuranceSurveyRoute
import com.ambica.auto.app.ux.container.job.modules.repair_progress.RepairProgressRoute
import com.ambica.auto.app.ux.container.job.modules.spare_parts.SparePartsRoute
import com.ambica.auto.app.ux.container.job.modules.timeline.TimelineRoute
import com.ambica.auto.app.ux.home.HomeScreen
import com.ambica.auto.app.ux.container.branches.BranchesRoute
import com.ambica.auto.app.ux.container.branches.BranchesScreen
import com.ambica.auto.app.ux.container.branches.create.CreateBranchRoute
import com.ambica.auto.app.ux.container.branches.create.CreateBranchScreen
import com.ambica.auto.app.ux.container.branches.detail.BranchDetailRoute
import com.ambica.auto.app.ux.container.branches.detail.BranchDetailScreen
import com.ambica.auto.app.ux.container.branches.edit.EditBranchRoute
import com.ambica.auto.app.ux.container.branches.edit.EditBranchScreen
import com.ambica.auto.app.ux.main.dashboard.DashboardRoute
import com.ambica.auto.app.ux.main.jobs.JobsRoute
import com.ambica.auto.app.ux.main.jobs.JobsScreen
import com.ambica.auto.app.ux.main.profile.ProfileRoute
import com.ambica.auto.app.ux.main.profile.ProfileScreen
import com.ambica.auto.app.ux.main.profile.changepassword.ChangePasswordRoute
import com.ambica.auto.app.ux.main.profile.changepassword.ChangePasswordScreen
import com.ambica.auto.app.ux.main.profile.edit.EditProfileRoute
import com.ambica.auto.app.ux.main.profile.edit.EditProfileScreen

@Composable
fun AppMainGraph(
    navController: NavHostController,
    rootNavController: NavHostController,
) {
    NavHost(
        navController = navController,
        startDestination = DashboardRoute.routeDefinition.value,
    ) {
        (DashboardRoute as SimpleNavComposeRoute).addNavigationRoute(
            this,
            enterTransition = { fadeIn(tween(200)) },
            exitTransition = { fadeOut(tween(200)) }
        ) { HomeScreen(navController = navController) }

        (JobsRoute as SimpleNavComposeRoute).addNavigationRoute(
            this,
            enterTransition = { fadeIn(tween(200)) },
            exitTransition = { fadeOut(tween(200)) }
        ) { JobsScreen(navController = navController) }

        (ProfileRoute as SimpleNavComposeRoute).addNavigationRoute(
            this,
            enterTransition = { fadeIn(tween(200)) },
            exitTransition = { fadeOut(tween(200)) }
        ) { ProfileScreen(navController = navController, rootNavController = rootNavController) }

        (BranchesRoute as SimpleNavComposeRoute).addNavigationRoute(
            this,
            enterTransition = { fadeIn(tween(200)) },
            exitTransition = { fadeOut(tween(200)) }
        ) { BranchesScreen(navController = navController) }

        (CreateBranchRoute as SimpleNavComposeRoute).addNavigationRoute(
            this,
            enterTransition = { fadeIn(tween(200)) },
            exitTransition = { fadeOut(tween(200)) }
        ) { CreateBranchScreen(navController = navController) }

        BranchDetailRoute.addNavigationRoute(
            this,
            enterTransition = { fadeIn(tween(200)) },
            exitTransition = { fadeOut(tween(200)) }
        ) { BranchDetailScreen(navController = navController) }

        EditBranchRoute.addNavigationRoute(
            this,
            enterTransition = { fadeIn(tween(200)) },
            exitTransition = { fadeOut(tween(200)) }
        ) { EditBranchScreen(navController = navController) }

        (EditProfileRoute as SimpleNavComposeRoute).addNavigationRoute(
            this,
            enterTransition = { fadeIn(tween(200)) },
            exitTransition = { fadeOut(tween(200)) }
        ) { EditProfileScreen(navController = navController) }

        (ChangePasswordRoute as SimpleNavComposeRoute).addNavigationRoute(
            this,
            enterTransition = { fadeIn(tween(200)) },
            exitTransition = { fadeOut(tween(200)) }
        ) { ChangePasswordScreen(navController = navController) }

        // Gate Entry (Create Job)
        (GateEntryRoute as SimpleNavComposeRoute).addNavigationRoute(
            this,
            enterTransition = { fadeIn(tween(220)) },
            exitTransition = { fadeOut(tween(220)) }
        ) { GateEntryScreen(navController = navController) }

        // Job Hub
        JobDetailsHubRoute.addNavigationRoute(this) { backStackEntry ->
            val jobId = backStackEntry.arguments?.getString(JobDetailsHubRoute.ARG_JOB_ID).orEmpty()
            val viewModel: JobDetailsHubViewModel = androidx.hilt.navigation.compose.hiltViewModel()
            JobDetailsHubScreen(navController = navController, jobId = jobId, uiState = viewModel.uiState)
        }

        // Module screens (basic scaffolds; full designs next)
        EstimateDocsRoute.addNavigationRoute(this) { backStackEntry ->
            val jobId = backStackEntry.arguments?.getString(com.ambica.auto.app.ux.container.job.modules.JobIdRoute.ARG_JOB_ID).orEmpty()
            com.ambica.auto.app.ux.container.job.modules.estimate_docs.EstimateDocsScreen(navController, jobId)
        }
        InsuranceSurveyRoute.addNavigationRoute(this) { backStackEntry ->
            val jobId = backStackEntry.arguments?.getString(com.ambica.auto.app.ux.container.job.modules.JobIdRoute.ARG_JOB_ID).orEmpty()
            com.ambica.auto.app.ux.container.job.modules.insurance_survey.InsuranceSurveyScreen(navController, jobId)
        }
        RepairProgressRoute.addNavigationRoute(this) { backStackEntry ->
            val jobId = backStackEntry.arguments?.getString(com.ambica.auto.app.ux.container.job.modules.JobIdRoute.ARG_JOB_ID).orEmpty()
            com.ambica.auto.app.ux.container.job.modules.repair_progress.RepairProgressScreen(navController, jobId)
        }
        SparePartsRoute.addNavigationRoute(this) { backStackEntry ->
            val jobId = backStackEntry.arguments?.getString(com.ambica.auto.app.ux.container.job.modules.JobIdRoute.ARG_JOB_ID).orEmpty()
            com.ambica.auto.app.ux.container.job.modules.spare_parts.SparePartsScreen(navController, jobId)
        }
        BillingPaymentRoute.addNavigationRoute(this) { backStackEntry ->
            val jobId = backStackEntry.arguments?.getString(com.ambica.auto.app.ux.container.job.modules.JobIdRoute.ARG_JOB_ID).orEmpty()
            com.ambica.auto.app.ux.container.job.modules.billing_payment.BillingPaymentScreen(navController, jobId)
        }
        GatePassRoute.addNavigationRoute(this) { backStackEntry ->
            val jobId = backStackEntry.arguments?.getString(com.ambica.auto.app.ux.container.job.modules.JobIdRoute.ARG_JOB_ID).orEmpty()
            com.ambica.auto.app.ux.container.job.modules.gate_pass.GatePassScreen(navController, jobId)
        }
        TimelineRoute.addNavigationRoute(this) { backStackEntry ->
            val jobId = backStackEntry.arguments?.getString(com.ambica.auto.app.ux.container.job.modules.JobIdRoute.ARG_JOB_ID).orEmpty()
            com.ambica.auto.app.ux.container.job.modules.timeline.TimelineScreen(navController, jobId)
        }
    }
}

