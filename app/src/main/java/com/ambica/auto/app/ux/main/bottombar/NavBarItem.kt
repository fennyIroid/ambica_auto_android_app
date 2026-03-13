package com.ambica.auto.app.ux.main.bottombar

import androidx.annotation.DrawableRes
import com.ambica.auto.app.R
import com.ambica.auto.app.navigation.NavRoute

enum class NavBarItem(
    val route: NavRoute,
    @DrawableRes val iconRes: Int,
    val label: String,
) {
    DASHBOARD(route = NavRoute("dashboard"), iconRes = R.drawable.ic_dashboard, label = "Dashboard"),
    JOBS(route = NavRoute("jobs"), iconRes = R.drawable.ic_job, label = "Jobs"),
    PROFILE(route = NavRoute("profile"), iconRes = R.drawable.ic_profile, label = "Profile"),
}

