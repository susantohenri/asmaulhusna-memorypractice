package com.test.asmaulhusnamemorypractice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.ads.MobileAds
import com.test.asmaulhusnamemorypractice.data.AdsManager
import com.test.asmaulhusnamemorypractice.ui.ad.AdMobBanner
import com.test.asmaulhusnamemorypractice.ui.ad.InterstitialAdHelper
import com.test.asmaulhusnamemorypractice.ui.components.AsmaulHusnaCard
import com.test.asmaulhusnamemorypractice.ui.components.CompletionDialog
import com.test.asmaulhusnamemorypractice.ui.components.HeaderProgress
import com.test.asmaulhusnamemorypractice.ui.components.NextButton
import com.test.asmaulhusnamemorypractice.ui.theme.AsmaulHusnaMemoryPracticeTheme
import com.test.asmaulhusnamemorypractice.ui.viewmodel.MemoryPracticeViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var adsManager: AdsManager
    private lateinit var interstitialAdHelper: InterstitialAdHelper
    private val viewModel: MemoryPracticeViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize Google Mobile Ads SDK
        MobileAds.initialize(this) {}

        // Initialize AdsManager & Interstitial Ad Helper
        adsManager = AdsManager(this)
        interstitialAdHelper = InterstitialAdHelper(this)

        // Fetch remote config on launch
        lifecycleScope.launch {
            adsManager.fetchAdsConfig()
        }

        // Observe the config state to update interstitial helper
        lifecycleScope.launch {
            adsManager.configState.collect { config ->
                interstitialAdHelper.updateConfig(config)
            }
        }

        setContent {
            AsmaulHusnaMemoryPracticeTheme {
                val uiState by viewModel.uiState.collectAsState()
                val adsConfig by adsManager.configState.collectAsState()
                val activity = this@MainActivity

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = {
                                Column {
                                    Text(
                                        text = "Asmaul Husna",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Memory Practice",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Normal,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                }
                            },
                            actions = {
                                // Reset Button — placed in TopAppBar, far from banner ad
                                FilledTonalIconButton(
                                    onClick = {
                                        interstitialAdHelper.showAd(activity) {
                                            viewModel.onReset()
                                        }
                                    },
                                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                                        containerColor = MaterialTheme.colorScheme.errorContainer,
                                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.RestartAlt,
                                        contentDescription = "Reset to Number 1"
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        )
                    },
                    bottomBar = {
                        // Static non-intrusive bottom banner ad with safe padding
                        Column {
                            Spacer(modifier = Modifier.height(16.dp)) // Safe distance from buttons
                            AdMobBanner(adsConfig = adsConfig)
                        }
                    }
                ) { innerPadding ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState()),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Top section: Progress + Card
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Spacer(modifier = Modifier.height(8.dp))

                                // Progress Indicator (12 / 99)
                                HeaderProgress(
                                    currentIndex = uiState.currentIndex,
                                    totalCount = uiState.totalCount,
                                    progress = uiState.progress
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                // Main Practice Card — tap card = NEXT
                                AsmaulHusnaCard(
                                    item = uiState.currentItem,
                                    onCardTap = {
                                        viewModel.onNext(
                                            onRoundFinished = {
                                                interstitialAdHelper.showAd(activity)
                                            }
                                        )
                                    }
                                )
                            }

                            // Bottom section: Big Next Button (placed well above banner)
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                NextButton(
                                    currentIndex = uiState.currentIndex,
                                    totalCount = uiState.totalCount,
                                    onNext = {
                                        viewModel.onNext(
                                            onRoundFinished = {
                                                interstitialAdHelper.showAd(activity)
                                            }
                                        )
                                    }
                                )

                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }

                        // Completion Dialog
                        if (uiState.isCompleted) {
                            CompletionDialog(
                                onRestart = {
                                    interstitialAdHelper.showAd(activity) {
                                        viewModel.onReset()
                                    }
                                },
                                onDismiss = { viewModel.dismissCompletionDialog() }
                            )
                        }
                    }
                }
            }
        }
    }
}