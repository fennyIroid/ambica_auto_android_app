package com.ambica.auto.app.ux.main

import com.ambica.auto.app.model.base.BaseViewModel
import com.ambica.auto.app.navigation.DefaultNavBarConfig
import com.ambica.auto.app.navigation.ViewModelNavBar
import com.ambica.auto.app.navigation.ViewModelNavBarImpl
import com.ambica.auto.app.ux.main.bottombar.NavBarItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() :
    BaseViewModel(),
    ViewModelNavBar<NavBarItem> by ViewModelNavBarImpl(
        startNavBarItem = NavBarItem.DASHBOARD,
        navBarConfig = DefaultNavBarConfig(
            navBarItemRouteMap = NavBarItem.entries.associateWith { it.route }
        )
    )

