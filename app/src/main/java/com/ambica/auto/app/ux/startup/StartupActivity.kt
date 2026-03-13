package com.ambica.auto.app.ux.startup

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.ambica.auto.app.data.source.Constants
import com.ambica.auto.app.model.base.BaseActivity
import com.ambica.auto.app.ui.theme.AmbicaAutoAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StartupActivity : BaseActivity<StartupViewModel>() {

    override val viewModel: StartupViewModel by viewModels()
    private var startDestination = ""

    companion object {
        fun newIntent(context: Context): Intent =
            Intent(context, StartupActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().setKeepOnScreenCondition { false }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        startDestination = intent?.getStringExtra(Constants.IS_COME_FOR).orEmpty()
        val bundle = intent?.extras
        setContent {
            AmbicaAutoAppTheme {
                Box(Modifier.fillMaxSize()) {
                    StartupScreen(startDestination = startDestination, bundle = bundle)
                }
            }
        }
    }

    override fun onStartup() {
        super.onStartup()
        viewModel.startup(this)
    }
}
