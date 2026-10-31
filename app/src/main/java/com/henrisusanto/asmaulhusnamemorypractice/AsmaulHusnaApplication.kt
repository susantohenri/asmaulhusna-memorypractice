package com.henrisusanto.asmaulhusnamemorypractice

import android.app.Application
import android.os.Bundle
import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.henrisusanto.asmaulhusnamemorypractice.analytics.AnalyticsLogger

/**
 * Application class for Asmaul Husna Memory Practice.
 * Handles early initialization of Firebase Analytics and Crashlytics.
 */
class AsmaulHusnaApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initAnalytics()
        initCrashlytics()
    }

    private fun initAnalytics() {
        try {
            AnalyticsLogger.initialize(this)

            // Set default event parameters
            val defaultParams = Bundle().apply {
                putString("app_version", BuildConfig.VERSION_NAME)
            }
            AnalyticsLogger.setDefaultEventParameters(defaultParams)
        } catch (e: Exception) {
            Log.e("AsmaulHusnaApp", "Failed to initialize analytics: ${e.message}", e)
        }
    }

    private fun initCrashlytics() {
        try {
            val crashlytics = FirebaseCrashlytics.getInstance()
            crashlytics.setCrashlyticsCollectionEnabled(true)
            crashlytics.setCustomKey("app_version", BuildConfig.VERSION_NAME)
            Log.d("AsmaulHusnaApp", "Crashlytics initialized successfully")
        } catch (e: Exception) {
            Log.e("AsmaulHusnaApp", "Failed to initialize Crashlytics: ${e.message}", e)
        }
    }
}
