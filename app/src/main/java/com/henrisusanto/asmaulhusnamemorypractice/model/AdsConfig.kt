package com.henrisusanto.asmaulhusnamemorypractice.model

data class AdsConfig(
    val appOpenAdUnitId: String = "",
    val bannerAdUnitId: String = "",
    val interstitialAdUnitId: String = "",
    val rewardedAdUnitId: String = "",
    val nativeAdUnitId: String = "",
    val isAdsEnabled: Boolean = true
)
