package com.ambica.auto.app.model.base

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModel

abstract class BaseActivity<VM : ViewModel> : ComponentActivity() {

    protected abstract val viewModel: VM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(com.ambica.auto.app.R.style.Theme_AmbicaAutoApp)
        fitSystemWindow(false)
        onStartup()
    }

    open fun onStartup() {}

    open fun fitSystemWindow(fitToSystem: Boolean) {
        WindowCompat.setDecorFitsSystemWindows(window, fitToSystem)
    }
}
