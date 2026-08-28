package com.henrisusanto.asmaulhusnamemorypractice.data

import android.util.Log
import com.henrisusanto.asmaulhusnamemorypractice.model.AdsConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

class AdsManager() {

    companion object {
        private const val TAG = "AdsManager"
        private const val REMOTE_URL = "https://raw.githubusercontent.com/susantohenri/admob-remote-configs/refs/heads/main/asmaulhusna-memorypractice/ads_config.json"
    }

    private val _configState = MutableStateFlow(AdsConfig(isAdsEnabled = false))
    val configState: StateFlow<AdsConfig> = _configState.asStateFlow()

    suspend fun fetchAdsConfig() {
        withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Fetching Ads Config from remote...")
                val response = URL(REMOTE_URL).readText()
                val json = JSONObject(response)

                val newConfig = AdsConfig(
                    appOpenAdUnitId = json.optString("appOpenAdUnitId", ""),
                    bannerAdUnitId = json.optString("bannerAdUnitId", ""),
                    interstitialAdUnitId = json.optString("interstitialAdUnitId", ""),
                    rewardedAdUnitId = json.optString("rewardedAdUnitId", ""),
                    nativeAdUnitId = json.optString("nativeAdUnitId", ""),
                    isAdsEnabled = json.optBoolean("isAdsEnabled", false)
                )

                _configState.update { newConfig }
                
                Log.d(TAG, "Ads Config updated successfully: $newConfig")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to fetch Ads Config, using default (disabled): ${e.message}")
                _configState.update { AdsConfig(isAdsEnabled = false) }
            }
        }
    }
}
