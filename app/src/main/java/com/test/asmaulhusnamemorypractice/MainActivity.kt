package com.test.asmaulhusnamemorypractice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.ads.MobileAds
import com.test.asmaulhusnamemorypractice.audio.TtsManager
import com.test.asmaulhusnamemorypractice.ui.ad.AdMobBanner
import com.test.asmaulhusnamemorypractice.ui.ad.InterstitialAdHelper
import com.test.asmaulhusnamemorypractice.ui.components.AsmaulHusnaCard
import com.test.asmaulhusnamemorypractice.ui.components.CompletionDialog
import com.test.asmaulhusnamemorypractice.ui.components.ControlButtons
import com.test.asmaulhusnamemorypractice.ui.components.HeaderProgress
import com.test.asmaulhusnamemorypractice.ui.theme.AsmaulHusnaMemoryPracticeTheme
import com.test.asmaulhusnamemorypractice.ui.viewmodel.MemoryPracticeViewModel

class MainActivity : ComponentActivity() {

    private lateinit var ttsManager: TtsManager
    private lateinit var interstitialAdHelper: InterstitialAdHelper
    private val viewModel: MemoryPracticeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize Google Mobile Ads SDK
        MobileAds.initialize(this) {}

        // Initialize TTS Manager & Interstitial Ad Helper
        ttsManager = TtsManager(this)
        interstitialAdHelper = InterstitialAdHelper(this)

        setContent {
            AsmaulHusnaMemoryPracticeTheme {
                val uiState by viewModel.uiState.collectAsState()
                val context = LocalContext.current
                val activity = context as? ComponentActivity

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        // Static non-intrusive bottom banner ad
                        AdMobBanner()
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
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Spacer(modifier = Modifier.height(12.dp))

                                // Header Progress Indicator (12 / 99)
                                HeaderProgress(
                                    currentIndex = uiState.currentIndex,
                                    totalCount = uiState.totalCount,
                                    progress = uiState.progress,
                                    onReset = {
                                        activity?.let { act ->
                                            interstitialAdHelper.showAd(act) {
                                                viewModel.onReset()
                                            }
                                        } ?: viewModel.onReset()
                                    }
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                // Active Recall Interactive Card
                                AsmaulHusnaCard(
                                    item = uiState.currentItem,
                                    isRevealed = uiState.isRevealed,
                                    onCardClick = { viewModel.toggleReveal() },
                                    onPlayAudio = { textToSpeak ->
                                        ttsManager.speakArabic(textToSpeak)
                                    }
                                )
                            }

                            // Thumb-Friendly Navigation Controls
                            ControlButtons(
                                currentIndex = uiState.currentIndex,
                                totalCount = uiState.totalCount,
                                isRevealed = uiState.isRevealed,
                                onPrevious = { viewModel.onPrevious() },
                                onNext = {
                                    viewModel.onNext(
                                        onRoundFinished = {
                                            activity?.let { act ->
                                                interstitialAdHelper.showAd(act)
                                            }
                                        }
                                    )
                                },
                                onToggleReveal = { viewModel.toggleReveal() },
                                onReset = {
                                    activity?.let { act ->
                                        interstitialAdHelper.showAd(act) {
                                            viewModel.onReset()
                                        }
                                    } ?: viewModel.onReset()
                                }
                            )
                        }

                        // Round Completion Dialog
                        if (uiState.isCompleted) {
                            CompletionDialog(
                                onRestart = {
                                    activity?.let { act ->
                                        interstitialAdHelper.showAd(act) {
                                            viewModel.onReset()
                                        }
                                    } ?: viewModel.onReset()
                                },
                                onDismiss = { viewModel.dismissCompletionDialog() }
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ttsManager.shutdown()
    }
}