package com.henrisusanto.asmaulhusnamemorypractice.ui.ad

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.henrisusanto.asmaulhusnamemorypractice.model.AdsConfig

/**
 * AdMob Banner Composable to display non-intrusive bottom banner ads.
 */
@Composable
fun AdMobBanner(
    modifier: Modifier = Modifier,
    adsConfig: AdsConfig
) {
    if (!adsConfig.isAdsEnabled || adsConfig.bannerAdUnitId.isEmpty()) return

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        AndroidView(
            factory = { context ->
                AdView(context).apply {
                    setAdSize(AdSize.BANNER)
                    this.adUnitId = adsConfig.bannerAdUnitId
                    val adRequest = AdRequest.Builder().build()
                    loadAd(adRequest)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/**
 * AdMob Interstitial Ad manager helper.
 */
class InterstitialAdHelper(private val context: Context) {

    private var interstitialAd: InterstitialAd? = null
    private var currentConfig: AdsConfig? = null

    fun updateConfig(config: AdsConfig) {
        currentConfig = config
        if (config.isAdsEnabled && interstitialAd == null) {
            loadAd()
        } else if (!config.isAdsEnabled) {
            interstitialAd = null
        }
    }

    private fun loadAd() {
        val config = currentConfig ?: return
        if (!config.isAdsEnabled || config.interstitialAdUnitId.isEmpty()) return

        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            context,
            config.interstitialAdUnitId,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    Log.d("AdMobHelper", "Interstitial Ad loaded successfully")
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialAd = null
                    Log.e("AdMobHelper", "Failed to load Interstitial Ad: ${error.message}")
                }
            }
        )
    }

    fun showAd(activity: Activity, onDismiss: () -> Unit = {}) {
        val config = currentConfig
        if (config == null || !config.isAdsEnabled) {
            Log.d("AdMobHelper", "Ads disabled or config missing, skipping interstitial")
            onDismiss()
            return
        }

        if (interstitialAd != null) {
            // Set callback to ensure onDismiss is called when the ad is closed
            interstitialAd?.fullScreenContentCallback = object : com.google.android.gms.ads.FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    interstitialAd = null // Reload for next time
                    loadAd()
                    onDismiss()
                }

                override fun onAdFailedToShowFullScreenContent(p0: com.google.android.gms.ads.AdError) {
                    interstitialAd = null
                    onDismiss()
                }
            }
            interstitialAd?.show(activity)
        } else {
            Log.d("AdMobHelper", "Interstitial Ad not ready yet")
            loadAd()
            onDismiss()
        }
    }
}

/**
 * AdMob Rewarded Ad manager helper.
 */
class RewardedAdHelper(private val context: Context) {

    private var rewardedAd: RewardedAd? = null
    private var currentConfig: AdsConfig? = null
    private var isLoading = false

    fun updateConfig(config: AdsConfig) {
        currentConfig = config
        if (config.isAdsEnabled && rewardedAd == null && !isLoading) {
            loadAd()
        } else if (!config.isAdsEnabled) {
            rewardedAd = null
        }
    }

    fun loadAd() {
        val config = currentConfig ?: return
        if (!config.isAdsEnabled || config.rewardedAdUnitId.isEmpty() || isLoading) return

        isLoading = true
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(
            context,
            config.rewardedAdUnitId,
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    isLoading = false
                    rewardedAd = ad
                    Log.d("RewardedAdHelper", "Rewarded Ad loaded successfully")
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    isLoading = false
                    rewardedAd = null
                    Log.e("RewardedAdHelper", "Failed to load Rewarded Ad: ${error.message}")
                }
            }
        )
    }

    val isAdLoaded: Boolean
        get() = rewardedAd != null

    fun showAd(
        activity: Activity,
        onUserEarnedReward: () -> Unit,
        onAdNotReady: () -> Unit = {},
        onAdClosed: () -> Unit = {}
    ) {
        val config = currentConfig
        if (config == null || !config.isAdsEnabled || config.rewardedAdUnitId.isEmpty()) {
            Log.d("RewardedAdHelper", "Ads disabled or no ad unit, granting reward directly")
            onUserEarnedReward()
            onAdClosed()
            return
        }

        val ad = rewardedAd
        if (ad != null) {
            var earnedReward = false
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    rewardedAd = null
                    loadAd()
                    if (earnedReward) {
                        onUserEarnedReward()
                    }
                    onAdClosed()
                }

                override fun onAdFailedToShowFullScreenContent(error: AdError) {
                    rewardedAd = null
                    loadAd()
                    onAdClosed()
                }
            }
            ad.show(activity) { _ ->
                earnedReward = true
            }
        } else {
            Log.d("RewardedAdHelper", "Rewarded Ad not ready yet")
            loadAd()
            onAdNotReady()
            onAdClosed()
        }
    }
}
