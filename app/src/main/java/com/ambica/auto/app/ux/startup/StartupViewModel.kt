package com.ambica.auto.app.ux.startup

import android.content.Context
import com.ambica.auto.app.model.base.BaseViewModel
import com.ambica.auto.app.navigation.ViewModelNav
import com.ambica.auto.app.navigation.ViewModelNavImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StartupViewModel @Inject constructor() : BaseViewModel(), ViewModelNav by ViewModelNavImpl() {

    var isReady: Boolean = false
        private set

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    fun startup(context: Context) {
        scope.launch {
            isReady = true
        }
    }
}
