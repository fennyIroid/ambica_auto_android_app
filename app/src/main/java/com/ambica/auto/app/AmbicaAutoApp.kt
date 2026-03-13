package com.ambica.auto.app

import android.app.Application
import android.content.Intent
import com.ambica.auto.app.ux.startup.StartupActivity
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AmbicaAutoApp : Application() {

    companion object {
        var instance: AmbicaAutoApp? = null
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    fun restartApp() {
        val intent = Intent(this, StartupActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            putExtra("reset", true)
        }
        startActivity(intent)
    }
}
