package com.test.asmaulhusnamemorypractice.data

import android.content.Context
import android.util.Log
import com.test.asmaulhusnamemorypractice.model.AdsConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

class AdsManager(context: Context) {

    companion object {
        private const val TAG = "AdsManager"
        private const val PREFS_NAME = "AdsConfigPrefs"
        private const val KEY_APP_OPEN = "appOpenAdUnitId"
        private const val KEY_BANNER = "bannerAdUnitId"
        private const val KEY_INTERSTITIAL = "interstitialAdUnitId"
        private const val KEY_REWARDED = "rewardedAdUnitId"
        private const val KEY_NATIVE = "nativeAdUnitId"
        private const val KEY_IS_ADS_ENABLED = "isAdsEnabled"
        private const val REMOTE_URL = "https://raw.githubusercontent.com/susantohenri/admob-remote-configs/refs/heads/main/asmaulhusna-memorypractice/ads_config.json"
    }

    private val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    private val _configState = MutableStateFlow(loadCachedConfig())
    val configState: StateFlow<AdsConfig> = _configState.asStateFlow()

    suspend fun fetchAdsConfig() {
        withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Fetching Ads Config from remote...")
                val response = URL(REMOTE_URL).readText()
                val json = JSONObject(response)

                val newConfig = AdsConfig(
                    appOpenAdUnitId = json.optString("appOpenAdUnitId", ""),
                    bannerAdUnitId = json.optString("bannerAdUnitId", "ca-app-pub-3940256099942544/6300978111"),
                    interstitialAdUnitId = json.optString("interstitialAdUnitId", "ca-app-pub-3940256099942544/1033173712"),
                    rewardedAdUnitId = json.optString("rewardedAdUnitId", ""),
                    nativeAdUnitId = json.optString("nativeAdUnitId", ""),
                    isAdsEnabled = json.optBoolean("isAdsEnabled", true)
                )

                saveConfigToCache(newConfig)
                _configState.update { newConfig }
                
                Log.d(TAG, "Ads Config updated successfully: $newConfig")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to fetch Ads Config, using cached or default: ${e.message}")
            }
        }
    }

    private fun saveConfigToCache(config: AdsConfig) {
        sharedPrefs.edit().apply {
            putString(KEY_APP_OPEN, config.appOpenAdUnitId)
            putString(KEY_BANNER, config.bannerAdUnitId)
            putString(KEY_INTERSTITIAL, config.interstitialAdUnitId)
            putString(KEY_REWARDED, config.rewardedAdUnitId)
            putString(KEY_NATIVE, config.nativeAdUnitId)
            putBoolean(KEY_IS_ADS_ENABLED, config.isAdsEnabled)
            apply()
        }
    }

    private fun loadCachedConfig(): AdsConfig {
        return AdsConfig(
            appOpenAdUnitId = sharedPrefs.getString(KEY_APP_OPEN, "") ?: "",
            bannerAdUnitId = sharedPrefs.getString(KEY_BANNER, "ca-app-pub-3940256099942544/6300978111") ?: "ca-app-pub-3940256099942544/6300978111",
            interstitialAdUnitId = sharedPrefs.getString(KEY_INTERSTITIAL, "ca-app-pub-3940256099942544/1033173712") ?: "ca-app-pub-3940256099942544/1033173712",
            rewardedAdUnitId = sharedPrefs.getString(KEY_REWARDED, "") ?: "",
            nativeAdUnitId = sharedPrefs.getString(KEY_NATIVE, "") ?: "",
            isAdsEnabled = sharedPrefs.getBoolean(KEY_IS_ADS_ENABLED, true)
        )
    }
}
