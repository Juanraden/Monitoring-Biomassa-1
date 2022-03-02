package com.kedaireka.monitoring_biomassa

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MonitoringApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        val sharedPreferences = getSharedPreferences(applicationContext.packageName, Context.MODE_PRIVATE)
        val isNightMode: Boolean = sharedPreferences?.getBoolean("NIGHT_MODE", false) ?: false
        if (isNightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}