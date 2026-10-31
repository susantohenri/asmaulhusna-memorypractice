package com.henrisusanto.asmaulhusnamemorypractice.analytics

import android.content.Context
import android.os.Bundle
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics

/**
 * Singleton wrapper for Firebase Analytics and Crashlytics.
 * Acts as the single entry point for all manual analytics logging and crashlytics utilities.
 */
object AnalyticsLogger {

    private const val TAG = "AnalyticsLogger"

    @Volatile
    private var firebaseAnalytics: FirebaseAnalytics? = null

    @Volatile
    private var lastScreenName: String? = null

    /**
     * Initializes FirebaseAnalytics with the Application Context.
     */
    fun initialize(context: Context) {
        if (firebaseAnalytics == null) {
            synchronized(this) {
                if (firebaseAnalytics == null) {
                    try {
                        val appContext = context.applicationContext
                        val analytics = FirebaseAnalytics.getInstance(appContext)
                        analytics.setAnalyticsCollectionEnabled(true)
                        firebaseAnalytics = analytics
                        Log.d(TAG, "FirebaseAnalytics initialized successfully")
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to initialize FirebaseAnalytics: ${e.message}", e)
                    }
                }
            }
        }
    }

    /**
     * Sets default event parameters that will be attached to every event logged.
     */
    fun setDefaultEventParameters(parameters: Bundle?) {
        try {
            firebaseAnalytics?.setDefaultEventParameters(parameters)
            Log.d(TAG, "Default event parameters set: $parameters")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to set default event parameters: ${e.message}", e)
        }
    }

    /**
     * Sets a user property value.
     */
    fun setUserProperty(name: String, value: String?) {
        if (name.isBlank()) return
        try {
            firebaseAnalytics?.setUserProperty(name, value)
            Log.d(TAG, "User property set: $name = $value")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to set user property '$name': ${e.message}", e)
        }
    }

    /**
     * Logs a manual event with optional parameters.
     */
    fun logEvent(eventName: String, params: Bundle? = null) {
        if (eventName.isBlank()) return
        try {
            firebaseAnalytics?.logEvent(eventName, params)
            Log.d(TAG, "Event logged: $eventName, params: $params")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to log event '$eventName': ${e.message}", e)
        }
    }

    /**
     * Logs a screen_view event with anti-duplicate guard.
     */
    fun logScreenView(screenName: String, screenClass: String = "MainActivity") {
        if (screenName.isBlank()) return

        synchronized(this) {
            if (lastScreenName == screenName) {
                return
            }
            lastScreenName = screenName
        }

        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            putString(FirebaseAnalytics.Param.SCREEN_CLASS, screenClass)
        }
        logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
    }

    /**
     * Resets screen tracking state if needed.
     */
    fun resetScreenTracking() {
        synchronized(this) {
            lastScreenName = null
        }
    }

    /**
     * Records a non-fatal exception to Firebase Crashlytics safely.
     */
    fun recordException(throwable: Throwable) {
        try {
            FirebaseCrashlytics.getInstance().recordException(throwable)
            Log.d(TAG, "Exception recorded to Crashlytics: ${throwable.message}")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to record exception to Crashlytics: ${e.message}", e)
        }
    }

    /**
     * Writes a custom breadcrumb message to Firebase Crashlytics.
     */
    fun logCrashlytics(message: String) {
        if (message.isBlank()) return
        try {
            FirebaseCrashlytics.getInstance().log(message)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to write Crashlytics log: ${e.message}", e)
        }
    }
}
