package com.test.asmaulhusnamemorypractice

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.ads.MobileAds
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import com.test.asmaulhusnamemorypractice.data.AdsManager
import com.test.asmaulhusnamemorypractice.data.AppLanguage
import com.test.asmaulhusnamemorypractice.data.AppPreferences
import com.test.asmaulhusnamemorypractice.data.AppTheme
import com.test.asmaulhusnamemorypractice.ui.ad.AdMobBanner
import com.test.asmaulhusnamemorypractice.ui.ad.InterstitialAdHelper
import com.test.asmaulhusnamemorypractice.ui.components.AsmaulHusnaCard
import com.test.asmaulhusnamemorypractice.ui.components.CompletionDialog
import com.test.asmaulhusnamemorypractice.ui.components.HeaderProgress
import com.test.asmaulhusnamemorypractice.ui.components.NextButton
import com.test.asmaulhusnamemorypractice.ui.screens.SettingsScreen
import com.test.asmaulhusnamemorypractice.ui.theme.AsmaulHusnaMemoryPracticeTheme
import com.test.asmaulhusnamemorypractice.ui.theme.EnglishStrings
import com.test.asmaulhusnamemorypractice.ui.theme.IndonesianStrings
import com.test.asmaulhusnamemorypractice.ui.theme.LocalAppStrings
import com.test.asmaulhusnamemorypractice.ui.viewmodel.MemoryPracticeViewModel
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean
import java.util.Locale

class MainActivity : ComponentActivity() {

    private lateinit var adsManager: AdsManager
    private lateinit var appPreferences: AppPreferences
    private lateinit var interstitialAdHelper: InterstitialAdHelper
    private val viewModel: MemoryPracticeViewModel by viewModels()
    private val isMobileAdsInitializeCalled = AtomicBoolean(false)

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize Managers
        adsManager = AdsManager(this)
        appPreferences = AppPreferences(this)
        interstitialAdHelper = InterstitialAdHelper(this)

        // Request UMP Consent
        val params = ConsentRequestParameters.Builder().build()
        val consentInformation = UserMessagingPlatform.getConsentInformation(this)
        
        consentInformation.requestConsentInfoUpdate(
            this,
            params,
            {
                UserMessagingPlatform.loadAndShowConsentFormIfRequired(this) { loadAndShowError ->
                    if (loadAndShowError != null) {
                        Log.w("MainActivity", "UMP Error: ${loadAndShowError.errorCode} ${loadAndShowError.message}")
                    }
                    if (consentInformation.canRequestAds()) {
                        initializeMobileAdsSdk()
                    }
                }
            },
            { requestConsentError ->
                Log.w("MainActivity", "UMP Error: ${requestConsentError.errorCode} ${requestConsentError.message}")
            }
        )

        // Check if you can initialize in parallel while checking for new consent information.
        if (consentInformation.canRequestAds()) {
            initializeMobileAdsSdk()
        }

        // Observe the config state to update interstitial helper
        lifecycleScope.launch {
            adsManager.configState.collect { config ->
                interstitialAdHelper.updateConfig(config)
            }
        }

        setContent {
            val languageState by appPreferences.languageState.collectAsState()
            val themeState by appPreferences.themeState.collectAsState()
            
            val systemInDarkTheme = isSystemInDarkTheme()
            val useDarkTheme = when (themeState) {
                AppTheme.AUTO -> systemInDarkTheme
                AppTheme.LIGHT -> false
                AppTheme.DARK -> true
            }

            val currentLanguage = when (languageState) {
                AppLanguage.AUTO -> {
                    val locale = Locale.getDefault().language
                    if (locale == "id" || locale == "in") AppLanguage.ID else AppLanguage.EN
                }
                else -> languageState
            }

            val appStrings = if (currentLanguage == AppLanguage.ID) IndonesianStrings else EnglishStrings

            CompositionLocalProvider(LocalAppStrings provides appStrings) {
                AsmaulHusnaMemoryPracticeTheme(darkTheme = useDarkTheme) {
                    val navController = rememberNavController()
                    
                    NavHost(navController = navController, startDestination = "practice") {
                        composable("practice") {
                            val uiState by viewModel.uiState.collectAsState()
                            val adsConfig by adsManager.configState.collectAsState()
                            val activity = this@MainActivity
                            val strings = LocalAppStrings.current

                            Scaffold(
                                modifier = Modifier.fillMaxSize(),
                                topBar = {
                                    TopAppBar(
                                        title = {
                                            Column {
                                                Text(
                                                    text = strings.appName,
                                                    fontSize = 20.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Text(
                                                    text = strings.memoryPractice,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Normal,
                                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                                )
                                            }
                                        },
                                        actions = {
                                            // Settings Button
                                            IconButton(onClick = { navController.navigate("settings") }) {
                                                Icon(
                                                    imageVector = Icons.Default.Settings,
                                                    contentDescription = strings.settings
                                                )
                                            }
                                            // Reset Button
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
                                                    contentDescription = strings.resetToFirst
                                                )
                                            }
                                        },
                                        colors = TopAppBarDefaults.topAppBarColors(
                                            containerColor = MaterialTheme.colorScheme.surface
                                        )
                                    )
                                },
                                bottomBar = {
                                    Column {
                                        Spacer(modifier = Modifier.height(16.dp))
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
                                        Column(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Spacer(modifier = Modifier.height(8.dp))
                                            HeaderProgress(
                                                currentIndex = uiState.currentIndex,
                                                totalCount = uiState.totalCount,
                                                progress = uiState.progress
                                            )
                                            Spacer(modifier = Modifier.height(12.dp))
                                            AsmaulHusnaCard(
                                                item = uiState.currentItem,
                                                currentLanguage = currentLanguage,
                                                onCardTap = {
                                                    viewModel.onNext(
                                                        onRoundFinished = {
                                                            interstitialAdHelper.showAd(activity)
                                                        }
                                                    )
                                                }
                                            )
                                        }

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
                        
                        composable("settings") {
                            SettingsScreen(
                                currentLanguage = languageState,
                                currentTheme = themeState,
                                onLanguageChange = { appPreferences.setLanguage(it) },
                                onThemeChange = { appPreferences.setTheme(it) },
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }

    private fun initializeMobileAdsSdk() {
        if (isMobileAdsInitializeCalled.getAndSet(true)) {
            return
        }
        MobileAds.initialize(this) {}
        
        lifecycleScope.launch {
            adsManager.fetchAdsConfig()
        }
    }
}