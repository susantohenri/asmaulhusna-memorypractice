package com.test.asmaulhusnamemorypractice.ui.ad

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

/**
 * AdMob Banner Composable to display non-intrusive bottom banner ads.
 */
@Composable
fun AdMobBanner(
    modifier: Modifier = Modifier,
    adUnitId: String = "ca-app-pub-3940256099942544/6300978111" // AdMob Test Banner ID
) {
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
                    this.adUnitId = adUnitId
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
    private val testAdUnitId = "ca-app-pub-3940256099942544/1033173712" // AdMob Test Interstitial ID

    init {
        loadAd()
    }

    fun loadAd() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            context,
            testAdUnitId,
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
        if (interstitialAd != null) {
            interstitialAd?.show(activity)
            interstitialAd = null // Reload for next time
            loadAd()
            onDismiss()
        } else {
            Log.d("AdMobHelper", "Interstitial Ad not ready yet")
            loadAd()
            onDismiss()
        }
    }
}
