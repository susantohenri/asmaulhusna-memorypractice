package com.henrisusanto.asmaulhusnamemorypractice.model

data class AdsConfig(
    val appOpenAdUnitId: String = "",
    val bannerAdUnitId: String = "ca-app-pub-3940256099942544/6300978111", // Default test id
    val interstitialAdUnitId: String = "ca-app-pub-3940256099942544/1033173712", // Default test id
    val rewardedAdUnitId: String = "",
    val nativeAdUnitId: String = "",
    val isAdsEnabled: Boolean = true
)
