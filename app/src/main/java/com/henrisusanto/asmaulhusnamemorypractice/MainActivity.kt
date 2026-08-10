package com.henrisusanto.asmaulhusnamemorypractice

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
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
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.ads.MobileAds
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import com.henrisusanto.asmaulhusnamemorypractice.data.AdsManager
import com.henrisusanto.asmaulhusnamemorypractice.data.AppLanguage
import com.henrisusanto.asmaulhusnamemorypractice.data.AppPreferences
import com.henrisusanto.asmaulhusnamemorypractice.data.AppTheme
import com.henrisusanto.asmaulhusnamemorypractice.ui.ad.AdMobBanner
import com.henrisusanto.asmaulhusnamemorypractice.ui.ad.InterstitialAdHelper
import com.henrisusanto.asmaulhusnamemorypractice.ui.components.AsmaulHusnaCard
import com.henrisusanto.asmaulhusnamemorypractice.ui.components.CompletionDialog
import com.henrisusanto.asmaulhusnamemorypractice.ui.components.HeaderProgress
import com.henrisusanto.asmaulhusnamemorypractice.ui.components.NextButton
import com.henrisusanto.asmaulhusnamemorypractice.ui.screens.ExploreScreen
import com.henrisusanto.asmaulhusnamemorypractice.ui.screens.QuizScreen
import com.henrisusanto.asmaulhusnamemorypractice.ui.screens.SettingsScreen
import com.henrisusanto.asmaulhusnamemorypractice.ui.theme.AsmaulHusnaMemoryPracticeTheme
import com.henrisusanto.asmaulhusnamemorypractice.ui.theme.EnglishStrings
import com.henrisusanto.asmaulhusnamemorypractice.ui.theme.IndonesianStrings
import com.henrisusanto.asmaulhusnamemorypractice.ui.theme.LocalAppStrings
import com.henrisusanto.asmaulhusnamemorypractice.ui.viewmodel.MemoryPracticeViewModel
import com.henrisusanto.asmaulhusnamemorypractice.ui.viewmodel.QuizViewModel
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
            val languageState by appPreferences.languageFlow.collectAsState(initial = AppLanguage.AUTO)
            val themeState by appPreferences.themeFlow.collectAsState(initial = AppTheme.AUTO)
            val reminderEnabled by appPreferences.reminderEnabledFlow.collectAsState(initial = false)
            val reminderHour by appPreferences.reminderHourFlow.collectAsState(initial = 20)
            val reminderMinute by appPreferences.reminderMinuteFlow.collectAsState(initial = 0)
            
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

            // Permission Launcher for Android 13+ Notifications
            val requestPermissionLauncher = rememberLauncherForActivityResult(
                androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    lifecycleScope.launch { appPreferences.setReminderEnabled(true) }
                    com.henrisusanto.asmaulhusnamemorypractice.utils.ReminderManager.scheduleDailyReminder(
                        this@MainActivity, reminderHour, reminderMinute
                    )
                } else {
                    lifecycleScope.launch { appPreferences.setReminderEnabled(false) }
                }
            }

            CompositionLocalProvider(LocalAppStrings provides appStrings) {
                AsmaulHusnaMemoryPracticeTheme(darkTheme = useDarkTheme) {
                    val rootNavController = rememberNavController()
                    
                    NavHost(navController = rootNavController, startDestination = "main") {
                        composable("main") {
                            MainScreen(
                                rootNavController = rootNavController,
                                appPreferences = appPreferences,
                                adsManager = adsManager,
                                interstitialAdHelper = interstitialAdHelper,
                                currentLanguage = currentLanguage,
                                activity = this@MainActivity
                            )
                        }
                        composable("settings") {
                            SettingsScreen(
                                currentLanguage = languageState,
                                currentTheme = themeState,
                                reminderEnabled = reminderEnabled,
                                reminderHour = reminderHour,
                                reminderMinute = reminderMinute,
                                onLanguageChange = { lifecycleScope.launch { appPreferences.setLanguage(it) } },
                                onThemeChange = { lifecycleScope.launch { appPreferences.setTheme(it) } },
                                onReminderToggle = { enabled ->
                                    if (enabled && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                                        if (androidx.core.content.ContextCompat.checkSelfPermission(
                                                this@MainActivity, android.Manifest.permission.POST_NOTIFICATIONS
                                            ) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                                            lifecycleScope.launch { appPreferences.setReminderEnabled(true) }
                                            com.henrisusanto.asmaulhusnamemorypractice.utils.ReminderManager.scheduleDailyReminder(this@MainActivity, reminderHour, reminderMinute)
                                        } else {
                                            requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                                        }
                                    } else {
                                        lifecycleScope.launch { appPreferences.setReminderEnabled(enabled) }
                                        if (enabled) {
                                            com.henrisusanto.asmaulhusnamemorypractice.utils.ReminderManager.scheduleDailyReminder(this@MainActivity, reminderHour, reminderMinute)
                                        } else {
                                            com.henrisusanto.asmaulhusnamemorypractice.utils.ReminderManager.cancelReminder(this@MainActivity)
                                        }
                                    }
                                },
                                onReminderTimeChange = { h, m ->
                                    lifecycleScope.launch { appPreferences.setReminderTime(h, m) }
                                    if (reminderEnabled) {
                                        com.henrisusanto.asmaulhusnamemorypractice.utils.ReminderManager.scheduleDailyReminder(this@MainActivity, h, m)
                                    }
                                },
                                onNavigateBack = { rootNavController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun MainScreen(
        rootNavController: androidx.navigation.NavController,
        appPreferences: AppPreferences,
        adsManager: AdsManager,
        interstitialAdHelper: InterstitialAdHelper,
        currentLanguage: AppLanguage,
        activity: Activity
    ) {
        val bottomNavController = rememberNavController()
        val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        val strings = LocalAppStrings.current
        val adsConfig by adsManager.configState.collectAsState()

        // Inject ViewModel with AppPreferences
        val viewModel: MemoryPracticeViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
            factory = MemoryPracticeViewModel.Factory(appPreferences)
        )

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
                        IconButton(onClick = { rootNavController.navigate("settings") }) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = strings.settings
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
                    NavigationBar {
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.Star, contentDescription = strings.tabPractice) },
                            label = { Text(strings.tabPractice) },
                            selected = currentRoute == "practice",
                            onClick = {
                                bottomNavController.navigate("practice") {
                                    popUpTo(bottomNavController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.List, contentDescription = strings.tabExplore) },
                            label = { Text(strings.tabExplore) },
                            selected = currentRoute == "explore",
                            onClick = {
                                bottomNavController.navigate("explore") {
                                    popUpTo(bottomNavController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.PlayArrow, contentDescription = strings.tabQuiz) },
                            label = { Text(strings.tabQuiz) },
                            selected = currentRoute == "quiz",
                            onClick = {
                                bottomNavController.navigate("quiz") {
                                    popUpTo(bottomNavController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                    AdMobBanner(adsConfig = adsConfig)
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = bottomNavController,
                startDestination = "practice",
                modifier = Modifier.padding(innerPadding)
            ) {
                composable("practice") {
                    val uiState by viewModel.uiState.collectAsState()

                    Surface(
                        modifier = Modifier.fillMaxSize(),
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
                                    .padding(vertical = 16.dp)
                                    .padding(horizontal = 24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // Progres Hafalan Share Card
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(strings.progressCardTitle, fontSize = 14.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha=0.7f))
                                            Text("${uiState.currentIndex + 1} / ${uiState.totalCount} ${strings.nameLabel}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                                        }
                                        IconButton(onClick = {
                                            com.henrisusanto.asmaulhusnamemorypractice.utils.ShareUtils.shareComposableAsImage(
                                                activity = activity,
                                                shareText = strings.shareProgressText,
                                                chooserTitle = strings.shareChooserTitle
                                            ) {
                                                com.henrisusanto.asmaulhusnamemorypractice.ui.components.ShareProgressLayout(
                                                    item = uiState.currentItem,
                                                    currentIndex = uiState.currentIndex,
                                                    totalCount = uiState.totalCount,
                                                    currentLanguage = currentLanguage
                                                )
                                            }
                                        }) {
                                            Icon(Icons.Default.Share, contentDescription = "Share Progress", tint = MaterialTheme.colorScheme.primary)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))
                                
                                NextButton(
                                    currentIndex = uiState.currentIndex,
                                    totalCount = uiState.totalCount,
                                    onPrevious = {
                                        viewModel.onPrevious()
                                    },
                                    onReset = {
                                        interstitialAdHelper.showAd(activity) {
                                            viewModel.onReset()
                                        }
                                    },
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
                composable("explore") {
                    ExploreScreen()
                }
                composable("quiz") {
                    val quizViewModel: QuizViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
                    QuizScreen(
                        viewModel = quizViewModel,
                        currentLanguage = currentLanguage,
                        onQuizFinished = {
                            interstitialAdHelper.showAd(activity)
                        }
                    )
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
