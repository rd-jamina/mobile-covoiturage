package com.example.app_covoiturage

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import org.osmdroid.config.Configuration

@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Configuration.getInstance().load(
            this,
            getSharedPreferences("osmdroid_prefs", MODE_PRIVATE)
        )
        Configuration.getInstance().userAgentValue = packageName
    }
}