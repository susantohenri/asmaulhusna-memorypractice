package com.henrisusanto.asmaulhusnamemorypractice.data

import android.util.Log
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.henrisusanto.asmaulhusnamemorypractice.model.AdsConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class AdsManager(
    private val remoteConfig: FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
) {

    companion object {
        private const val TAG = "AdsManager"
        private const val KEY_APP_OPEN = "appOpenAdUnitId"
        private const val KEY_BANNER = "bannerAdUnitId"
        private const val KEY_INTERSTITIAL = "interstitialAdUnitId"
        private const val KEY_REWARDED = "rewardedAdUnitId"
        private const val KEY_NATIVE = "nativeAdUnitId"
        private const val KEY_IS_ADS_ENABLED = "isAdsEnabled"
    }

    private val _configState = MutableStateFlow(AdsConfig(isAdsEnabled = false))
    val configState: StateFlow<AdsConfig> = _configState.asStateFlow()

    init {
        // Set in-app default values
        val defaultMap: Map<String, Any> = mapOf(
            KEY_APP_OPEN to "",
            KEY_BANNER to "",
            KEY_INTERSTITIAL to "",
            KEY_REWARDED to "",
            KEY_NATIVE to "",
            KEY_IS_ADS_ENABLED to true
        )
        remoteConfig.setDefaultsAsync(defaultMap)

        val fetchInterval = if (com.henrisusanto.asmaulhusnamemorypractice.BuildConfig.DEBUG) 0L else 3600L
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(fetchInterval)
            .build()
        remoteConfig.setConfigSettingsAsync(configSettings)

        // Listen for real-time config updates published from Firebase Console
        remoteConfig.addOnConfigUpdateListener(object : ConfigUpdateListener {
            override fun onUpdate(configUpdate: ConfigUpdate) {
                Log.d(TAG, "Remote Config updated keys: ${configUpdate.updatedKeys}")
                remoteConfig.activate().addOnCompleteListener {
                    updateConfigFromRemote()
                }
            }

            override fun onError(error: FirebaseRemoteConfigException) {
                Log.w(TAG, "Config update listener error: ${error.message}")
            }
        })
    }

    suspend fun fetchAdsConfig() {
        withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Fetching Ads Config from Firebase Remote Config...")
                suspendCancellableCoroutine { continuation ->
                    remoteConfig.fetchAndActivate()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                continuation.resume(task.result)
                            } else {
                                continuation.resumeWithException(
                                    task.exception ?: Exception("Failed to fetch Remote Config")
                                )
                            }
                        }
                }
                updateConfigFromRemote()
            } catch (e: Exception) {
                Log.e(TAG, "Failed to fetch Remote Config, using cached/default: ${e.message}")
                updateConfigFromRemote()
            }
        }
    }

    private fun updateConfigFromRemote() {
        val newConfig = AdsConfig(
            appOpenAdUnitId = remoteConfig.getString(KEY_APP_OPEN),
            bannerAdUnitId = remoteConfig.getString(KEY_BANNER),
            interstitialAdUnitId = remoteConfig.getString(KEY_INTERSTITIAL),
            rewardedAdUnitId = remoteConfig.getString(KEY_REWARDED),
            nativeAdUnitId = remoteConfig.getString(KEY_NATIVE),
            isAdsEnabled = remoteConfig.getBoolean(KEY_IS_ADS_ENABLED)
        )
        _configState.update { newConfig }
        Log.d(TAG, "Ads Config applied: $newConfig")
    }
}
