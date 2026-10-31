package com.henrisusanto.asmaulhusnamemorypractice

import android.app.Application
import android.os.Build
import android.os.Bundle
import android.util.Log
import com.henrisusanto.asmaulhusnamemorypractice.analytics.AnalyticsLogger

/**
 * Application class for Asmaul Husna Memory Practice.
 * Handles early initialization of Firebase Analytics and default parameters.
 */
class AsmaulHusnaApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initAnalytics()
    }

    private fun initAnalytics() {
        try {
            AnalyticsLogger.initialize(this)

            // Resolve app_version from package info
            val appVersion = try {
                val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    packageManager.getPackageInfo(packageName, android.content.pm.PackageManager.PackageInfoFlags.of(0))
                } else {
                    @Suppress("DEPRECATION")
                    packageManager.getPackageInfo(packageName, 0)
                }
                packageInfo.versionName ?: "unknown"
            } catch (e: Exception) {
                "unknown"
            }

            // Set default event parameters
            val defaultParams = Bundle().apply {
                putString("app_version", appVersion)
            }
            AnalyticsLogger.setDefaultEventParameters(defaultParams)
        } catch (e: Exception) {
            Log.e("AsmaulHusnaApp", "Failed to initialize analytics: ${e.message}", e)
        }
    }
}
