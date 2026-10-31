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

    // Canonical Ad Formats
    const val AD_FORMAT_BANNER = "BANNER"
    const val AD_FORMAT_INTERSTITIAL = "INTERSTITIAL"
    const val AD_FORMAT_REWARDED = "REWARDED"

    @Volatile
    private var firebaseAnalytics: FirebaseAnalytics? = null

    @Volatile
    private var lastScreenName: String? = null

    val currentScreen: String
        get() = synchronized(this) { lastScreenName ?: "main" }

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

    /**
     * Logs ad_request precisely before loading an ad.
     */
    fun logAdRequest(adUnitId: String, adFormat: String, screen: String = currentScreen) {
        val bundle = Bundle().apply {
            putString("ad_unit_id", adUnitId)
            putString("ad_format", adFormat)
            putString("screen", screen)
        }
        logEvent("ad_request", bundle)
    }

    /**
     * Logs ad_impression via OnPaidEventListener as the canonical single source.
     */
    fun logAdImpression(
        adUnitId: String,
        adFormat: String,
        adSource: String?,
        screen: String = currentScreen,
        currency: String?,
        value: Double
    ) {
        val bundle = Bundle().apply {
            putString("ad_unit_id", adUnitId)
            putString("ad_format", adFormat)
            putString("ad_source", adSource ?: "unknown")
            putString("screen", screen)
            putString("currency", currency ?: "USD")
            putDouble("value", value)
        }
        logEvent("ad_impression", bundle)
    }

    /**
     * Logs ad_click when an ad is clicked.
     */
    fun logAdClick(adUnitId: String, adFormat: String, screen: String = currentScreen) {
        val bundle = Bundle().apply {
            putString("ad_unit_id", adUnitId)
            putString("ad_format", adFormat)
            putString("screen", screen)
        }
        logEvent("ad_click", bundle)
    }

    /**
     * Logs ad_failed when ad loading fails.
     */
    fun logAdFailed(adUnitId: String, adFormat: String, errorCode: Int, screen: String = currentScreen) {
        val bundle = Bundle().apply {
            putString("ad_unit_id", adUnitId)
            putString("ad_format", adFormat)
            putInt("error_code", errorCode)
            putString("screen", screen)
        }
        logEvent("ad_failed", bundle)
    }

    /**
     * Logs notification_opened when the user opens the app via a system notification.
     */
    fun logNotificationOpened(notificationType: String = "daily_reminder") {
        val bundle = Bundle().apply {
            putString("notification_type", notificationType)
        }
        logEvent("notification_opened", bundle)
    }

    /**
     * Logs practice_completed when user completes all 99 names.
     */
    fun logPracticeCompleted(totalNames: Int = 99) {
        val bundle = Bundle().apply {
            putInt("total_names", totalNames)
        }
        logEvent("practice_completed", bundle)
    }

    /**
     * Logs quiz_completed when a quiz session is finished.
     */
    fun logQuizCompleted(score: Int, totalQuestions: Int) {
        val bundle = Bundle().apply {
            putInt("score", score)
            putInt("total_questions", totalQuestions)
            putBoolean("is_perfect", score == totalQuestions)
        }
        logEvent("quiz_completed", bundle)
    }

    /**
     * Logs certificate_claimed when the digital certificate is awarded.
     */
    fun logCertificateClaimed(hasCustomName: Boolean) {
        val bundle = Bundle().apply {
            putBoolean("has_custom_name", hasCustomName)
        }
        logEvent("certificate_claimed", bundle)
    }

    /**
     * Logs share_content when user shares progress, quiz score, or certificate.
     */
    fun logShareContent(contentType: String) {
        val bundle = Bundle().apply {
            putString("content_type", contentType)
        }
        logEvent("share_content", bundle)
    }

    /**
     * Logs reminder_toggled when daily reminder is toggled.
     */
    fun logReminderToggled(enabled: Boolean, time: String) {
        val bundle = Bundle().apply {
            putBoolean("enabled", enabled)
            putString("time", time)
        }
        logEvent("reminder_toggled", bundle)
    }
}
