package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import com.example.domain.models.AppLanguage
import com.example.domain.models.GuideArticle
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import com.example.ui.components.AuthModal
import com.example.ui.components.BottomNavBar
import com.example.ui.components.DeleteAccountModal
import com.example.ui.components.LogoutConfirmationModal
import com.example.ui.components.OfflineStatusBanner
import com.example.ui.components.SplashScreen
import com.example.ui.components.TopNavBar
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.BookletScreen
import com.example.ui.screens.FertilizerScreen
import com.example.ui.screens.GuideDetailScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LandingAuthScreen
import com.example.ui.screens.MeasurementScreen
import com.example.ui.screens.SoilAnalysisScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.FarmViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            val osmConfig = org.osmdroid.config.Configuration.getInstance()
            val osmCacheDir = java.io.File(applicationContext.cacheDir, "osmdroid")
            if (!osmCacheDir.exists()) {
                osmCacheDir.mkdirs()
            }
            osmConfig.osmdroidBasePath = osmCacheDir
            osmConfig.osmdroidTileCache = java.io.File(osmCacheDir, "tiles")
            osmConfig.tileFileSystemCacheMaxBytes = 500L * 1024 * 1024 // 500 MB max disk cache for map tiles
            osmConfig.tileFileSystemCacheTrimBytes = 450L * 1024 * 1024 // Trim threshold
            osmConfig.load(
                applicationContext,
                getSharedPreferences("osmdroid", MODE_PRIVATE)
            )
            osmConfig.userAgentValue = packageName
        } catch (e: Exception) {
            e.printStackTrace()
        }
        enableEdgeToEdge()
        setContent {
            val viewModel: FarmViewModel = viewModel()
            val isHighContrastMode by viewModel.isHighContrastMode.collectAsStateWithLifecycle()
            MyApplicationTheme(isHighContrast = isHighContrastMode) {
                RiceFarmAssistantApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun RiceFarmAssistantApp(
    viewModel: FarmViewModel = viewModel()
) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val selectedGuide by viewModel.selectedGuide.collectAsStateWithLifecycle()
    val isHighContrastMode by viewModel.isHighContrastMode.collectAsStateWithLifecycle()
    val displayReadabilityMode by viewModel.displayReadabilityMode.collectAsStateWithLifecycle()
    var isSoilAnalysisOpen by remember { mutableStateOf(false) }

    // Measurement states
    val cropType by viewModel.selectedCrop.collectAsStateWithLifecycle()
    val isTracking by viewModel.isTracking.collectAsStateWithLifecycle()
    val isPaused by viewModel.isPaused.collectAsStateWithLifecycle()
    val boundaryPoints by viewModel.boundaryPoints.collectAsStateWithLifecycle()
    val walkingMeters by viewModel.walkingDistanceMeters.collectAsStateWithLifecycle()
    val estimatedHectares by viewModel.estimatedAreaHectares.collectAsStateWithLifecycle()
    val gpsAccuracy by viewModel.gpsAccuracyText.collectAsStateWithLifecycle()
    val currentLocation by viewModel.currentLocation.collectAsStateWithLifecycle()

    val currentLanguage by viewModel.currentLanguage.collectAsStateWithLifecycle()

    // Fertilizer states
    val fertilizerFarmArea by viewModel.fertilizerFarmArea.collectAsStateWithLifecycle()
    val targetN by viewModel.targetN.collectAsStateWithLifecycle()
    val targetP by viewModel.targetP.collectAsStateWithLifecycle()
    val targetK by viewModel.targetK.collectAsStateWithLifecycle()
    val fertilizerList by viewModel.availableFertilizers.collectAsStateWithLifecycle()
    val calculationResult by viewModel.calculationResult.collectAsStateWithLifecycle()

    // Weather states
    val weatherData by viewModel.weatherData.collectAsStateWithLifecycle()
    val agriculturalRegions = viewModel.agriculturalRegions
    val selectedRegion by viewModel.selectedRegion.collectAsStateWithLifecycle()
    val selectedWeatherScenario by viewModel.selectedWeatherScenario.collectAsStateWithLifecycle()

    // Soil states
    val soilCrop by viewModel.soilCrop.collectAsStateWithLifecycle()
    val soilType by viewModel.soilType.collectAsStateWithLifecycle()
    val nitrogenLevel by viewModel.nitrogenLevel.collectAsStateWithLifecycle()
    val phosphorusLevel by viewModel.phosphorusLevel.collectAsStateWithLifecycle()
    val potassiumLevel by viewModel.potassiumLevel.collectAsStateWithLifecycle()
    val organicMatter by viewModel.organicMatter.collectAsStateWithLifecycle()
    val soilRecommendation by viewModel.soilRecommendation.collectAsStateWithLifecycle()
    val activeSoilReport by viewModel.activeSoilReport.collectAsStateWithLifecycle()
    val savedSoilReports by viewModel.savedSoilReports.collectAsStateWithLifecycle()
    val isGeminiAnalyzing by viewModel.isGeminiAnalyzing.collectAsStateWithLifecycle()
    val geminiError by viewModel.geminiAnalysisError.collectAsStateWithLifecycle()

    // Booklet states
    val bookletSearchQuery by viewModel.bookletSearchQuery.collectAsStateWithLifecycle()
    val bookletArticles by viewModel.bookletArticles.collectAsStateWithLifecycle()

    // History states
    val allFarms by viewModel.allFarms.collectAsStateWithLifecycle()
    val historySearchQuery by viewModel.historySearchQuery.collectAsStateWithLifecycle()
    val filteredFarms by viewModel.filteredFarms.collectAsStateWithLifecycle()
    val totalFarmsCount by viewModel.totalFarmsCount.collectAsStateWithLifecycle()
    val totalAreaHectares by viewModel.totalAreaHectares.collectAsStateWithLifecycle()

    // Delete Account modal state
    val isDeleteAccountModalVisible by viewModel.showDeleteAccountModal.collectAsStateWithLifecycle()
    val accountDeletedMessage by viewModel.accountDeletedMessage.collectAsStateWithLifecycle()

    // Autosave notification state
    val restoredSessionNotice by viewModel.restoredSessionNotice.collectAsStateWithLifecycle()

    // Offline & Connectivity states
    val isOfflineMode by viewModel.isOfflineMode.collectAsStateWithLifecycle()
    val isAccessingCachedContent by viewModel.isAccessingCachedContent.collectAsStateWithLifecycle()
    val isForcedOffline by viewModel.isForcedOffline.collectAsStateWithLifecycle()

    // Auth & Accounts State
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val isAuthModalOpen by viewModel.isAuthModalOpen.collectAsStateWithLifecycle()
    val isAdminDashboardOpen by viewModel.isAdminDashboardOpen.collectAsStateWithLifecycle()
    val isLandingScreenOpen by viewModel.isLandingScreenOpen.collectAsStateWithLifecycle()
    val isLogoutConfirmationOpen by viewModel.isLogoutConfirmationOpen.collectAsStateWithLifecycle()
    val farmerRegistry by viewModel.farmerRegistry.collectAsStateWithLifecycle()
    val auditLogs by viewModel.auditLogs.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(accountDeletedMessage) {
        accountDeletedMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.dismissAccountDeletedMessage()
        }
    }

    // Back handlers for nested states and dialogs
    BackHandler(enabled = isLogoutConfirmationOpen) {
        viewModel.dismissLogoutPrompt()
    }
    BackHandler(enabled = isDeleteAccountModalVisible) {
        viewModel.closeDeleteAccountModal()
    }
    BackHandler(enabled = isAuthModalOpen) {
        viewModel.closeAuthModal()
    }
    BackHandler(enabled = selectedGuide != null) {
        viewModel.closeGuide()
    }
    BackHandler(enabled = isSoilAnalysisOpen) {
        isSoilAnalysisOpen = false
    }
    BackHandler(enabled = !isLandingScreenOpen && !isAdminDashboardOpen && currentTab != 0 && !isSoilAnalysisOpen && selectedGuide == null && !isAuthModalOpen && !isDeleteAccountModalVisible && !isLogoutConfirmationOpen) {
        viewModel.selectTab(0)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedContent(
            targetState = when {
                isLandingScreenOpen -> "landing"
                isAdminDashboardOpen -> "admin"
                else -> "main"
            },
            transitionSpec = {
                (fadeIn(animationSpec = tween(280)) + slideInVertically(animationSpec = tween(280)) { it / 10 })
                    .togetherWith(fadeOut(animationSpec = tween(220)) + slideOutVertically(animationSpec = tween(220)) { -it / 10 })
            },
            label = "root_screen_transition"
        ) { screenType ->
            when (screenType) {
                "landing" -> {
                    LandingAuthScreen(
                        currentLanguage = currentLanguage,
                        onLanguageSelected = { viewModel.setLanguage(it) },
                        onFarmerLogin = { identifier, passcode -> viewModel.loginFarmer(identifier, passcode) },
                        onFarmerRegister = { name, phone, rsbsa, prov, mun, area, crop ->
                            viewModel.registerFarmer(name, phone, rsbsa, prov, mun, area, crop)
                        },
                        onAdminLogin = { identifier, passcode -> viewModel.loginAdminPersonnel(identifier, passcode) },
                        onContinueAsGuest = { viewModel.closeLandingScreen() }
                    )
                }
                "admin" -> {
                    AdminDashboardScreen(
                        currentUser = currentUser,
                        farmerRegistry = farmerRegistry,
                        auditLogs = auditLogs,
                        currentLanguage = currentLanguage,
                        farmRecords = allFarms,
                        onBackToFarmerView = { viewModel.closeAdminDashboard() },
                        onLogout = { viewModel.promptLogout() },
                        onUpdateFarmerStatus = { id, status -> viewModel.updateFarmerStatus(id, status) },
                        onApproveFarmer = { id -> viewModel.approveFarmer(id) },
                        onDeleteFarmer = { id -> viewModel.deleteFarmer(id) },
                        onEditFarmer = { farmer -> viewModel.editFarmer(farmer) },
                        onAddFarmer = { farmer -> viewModel.addFarmer(farmer) },
                        onResetFarmerRegistry = { viewModel.resetFarmerRegistry() },
                        onDeleteFarmRecord = { farm -> viewModel.adminDeleteFarmRecord(farm) },
                        onUpdateFarmRecord = { farm -> viewModel.adminUpdateFarmRecord(farm) },
                        onInsertFarmRecord = { farm -> viewModel.adminInsertFarmRecord(farm) },
                        onDeleteAllFarms = { viewModel.adminDeleteAllFarms() },
                        onSeedSampleFarms = { viewModel.adminSeedSampleFarms() },
                        onDeleteAuditLog = { logId -> viewModel.deleteAuditLog(logId) },
                        onClearAuditLogs = { viewModel.clearAllAuditLogs() }
                    )
                }
                else -> {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        snackbarHost = { SnackbarHost(snackbarHostState) },
                        topBar = {
                            TopNavBar(
                                currentTab = currentTab,
                                isSoilAnalysisOpen = isSoilAnalysisOpen,
                                isGuideDetailOpen = selectedGuide != null,
                                currentLanguage = currentLanguage,
                                onLanguageSelected = { viewModel.setLanguage(it) },
                                currentUser = currentUser,
                                onOpenAuthModal = { viewModel.openAuthModal() },
                                onOpenAdminDashboard = { viewModel.openAdminDashboard() },
                                onLogout = { viewModel.promptLogout() },
                                isHighContrastMode = isHighContrastMode,
                                onToggleHighContrastMode = { viewModel.toggleDisplayReadabilityMode() },
                                isOffline = isOfflineMode,
                                onOfflineClick = { /* Handled via Banner and dialog */ }
                            )
                        },
                        bottomBar = {
                            BottomNavBar(
                                selectedTab = currentTab,
                                currentLanguage = currentLanguage,
                                onTabSelected = { tab ->
                                    isSoilAnalysisOpen = false
                                    viewModel.closeGuide()
                                    viewModel.selectTab(tab)
                                }
                            )
                        }
                    ) { innerPadding ->
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            // Visual Offline & Cache Status Banner
                            OfflineStatusBanner(
                                isOffline = isOfflineMode,
                                isCachedContent = isAccessingCachedContent,
                                currentLanguage = currentLanguage,
                                lastSyncTime = weatherData.lastSyncTime,
                                onRetrySync = { viewModel.retryLiveSync() },
                                isForcedOffline = isForcedOffline,
                                onToggleForcedOffline = { viewModel.toggleForcedOffline() }
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            ) {
                                val activeScreenKey = when {
                                    isSoilAnalysisOpen -> 100
                                    selectedGuide != null -> 101
                                    else -> currentTab
                                }

                                AnimatedContent(
                                    targetState = activeScreenKey,
                                    transitionSpec = {
                                        if (targetState >= 100 || initialState >= 100) {
                                            (slideInVertically(animationSpec = spring(stiffness = Spring.StiffnessMediumLow)) { height -> height / 6 } + fadeIn(tween(240)))
                                                .togetherWith(slideOutVertically(animationSpec = spring(stiffness = Spring.StiffnessMediumLow)) { height -> -height / 6 } + fadeOut(tween(180)))
                                        } else if (targetState > initialState) {
                                            (slideInHorizontally(animationSpec = spring(stiffness = Spring.StiffnessMediumLow)) { width -> width / 4 } + fadeIn(tween(220)))
                                                .togetherWith(slideOutHorizontally(animationSpec = spring(stiffness = Spring.StiffnessMediumLow)) { width -> -width / 4 } + fadeOut(tween(180)))
                                        } else {
                                            (slideInHorizontally(animationSpec = spring(stiffness = Spring.StiffnessMediumLow)) { width -> -width / 4 } + fadeIn(tween(220)))
                                                .togetherWith(slideOutHorizontally(animationSpec = spring(stiffness = Spring.StiffnessMediumLow)) { width -> width / 4 } + fadeOut(tween(180)))
                                        }
                                    },
                                    label = "main_screen_tab_transition"
                                ) { screenKey ->
                                    when (screenKey) {
                                        100 -> {
                                            SoilAnalysisScreen(
                                                crop = soilCrop,
                                                soilType = soilType,
                                                nitrogen = nitrogenLevel,
                                                phosphorus = phosphorusLevel,
                                                potassium = potassiumLevel,
                                                organicMatter = organicMatter,
                                                recommendation = soilRecommendation,
                                                activeReport = activeSoilReport,
                                                savedReports = savedSoilReports,
                                                isGeminiAnalyzing = isGeminiAnalyzing,
                                                geminiError = geminiError,
                                                onCropChange = { viewModel.setSoilCrop(it) },
                                                onSoilTypeChange = { viewModel.setSoilType(it) },
                                                onNitrogenChange = { viewModel.setNitrogenLevel(it) },
                                                onPhosphorusChange = { viewModel.setPhosphorusLevel(it) },
                                                onPotassiumChange = { viewModel.setPotassiumLevel(it) },
                                                onOrganicMatterChange = { viewModel.setOrganicMatter(it) },
                                                onGenerate = { viewModel.generateSoilRecommendation() },
                                                onGenerateCustom = { c, t, n, p, k, om, ph, moisture ->
                                                    viewModel.generateSoilRecommendationWithCustomValues(c, t, n, p, k, om, ph, moisture)
                                                },
                                                onAnalyzeWithGemini = { bitmap, crop, onDone ->
                                                    viewModel.analyzeSoilWithGemini(bitmap, crop, onDone)
                                                },
                                                onDismissGeminiError = { viewModel.dismissGeminiError() },
                                                onSaveReport = { viewModel.saveActiveSoilReport() },
                                                onDeleteReport = { viewModel.deleteSoilReport(it) },
                                                onSelectSavedReport = { viewModel.selectSavedSoilReport(it) },
                                                onBack = { isSoilAnalysisOpen = false },
                                                currentLanguage = currentLanguage,
                                                onLanguageSelected = { viewModel.setLanguage(it) }
                                            )
                                        }
                                        101 -> {
                                            if (selectedGuide != null) {
                                                GuideDetailScreen(
                                                    article = selectedGuide!!,
                                                    onBack = { viewModel.closeGuide() },
                                                    currentLanguage = currentLanguage,
                                                    onLanguageSelected = { viewModel.setLanguage(it) }
                                                )
                                            }
                                        }
                                        0 -> {
                                            HomeScreen(
                                                currentLanguage = currentLanguage,
                                                weatherData = weatherData,
                                                agriculturalRegions = agriculturalRegions,
                                                selectedRegion = selectedRegion,
                                                selectedWeatherScenario = selectedWeatherScenario,
                                                displayReadabilityMode = displayReadabilityMode,
                                                isOffline = isOfflineMode,
                                                isCachedContent = isAccessingCachedContent,
                                                isForcedOffline = isForcedOffline,
                                                currentUser = currentUser,
                                                onOpenAuthModal = { viewModel.openAuthModal() },
                                                onLogout = { viewModel.promptLogout() },
                                                onOpenLanding = { viewModel.openLandingScreen() },
                                                onToggleForcedOffline = { viewModel.toggleForcedOffline() },
                                                onReadabilityModeChanged = { mode -> viewModel.setDisplayReadabilityMode(mode) },
                                                onLanguageSelected = { viewModel.setLanguage(it) },
                                                onRegionSelected = { reg -> viewModel.selectWeatherRegion(reg) },
                                                onScenarioSelected = { scenario -> viewModel.setWeatherScenario(scenario) },
                                                onRefreshWeather = { viewModel.refreshWeatherData() },
                                                onNavigateToTab = { tab -> viewModel.selectTab(tab) },
                                                onOpenSoilAnalysis = { isSoilAnalysisOpen = true },
                                                onOpenDeleteAccount = { viewModel.openDeleteAccountModal() },
                                                onLocationPermissionGranted = { viewModel.onLocationPermissionGranted() }
                                            )
                                        }
                                        1 -> {
                                            MeasurementScreen(
                                                cropType = cropType,
                                                isTracking = isTracking,
                                                isPaused = isPaused,
                                                boundaryPoints = boundaryPoints,
                                                walkingMeters = walkingMeters,
                                                estimatedHectares = estimatedHectares,
                                                gpsAccuracy = gpsAccuracy,
                                                currentLocation = currentLocation,
                                                currentLanguage = currentLanguage,
                                                onLanguageSelected = { viewModel.setLanguage(it) },
                                                restoredNotice = restoredSessionNotice,
                                                onDismissRestoredNotice = { viewModel.dismissRestoredNotice() },
                                                onLocationPermissionGranted = { viewModel.onLocationPermissionGranted() },
                                                onCropChange = { viewModel.setCrop(it) },
                                                onStartTracking = { viewModel.startTracking() },
                                                onPauseTracking = { viewModel.pauseTracking() },
                                                onMarkPoint = { viewModel.markPoint() },
                                                onUndoPoint = { viewModel.undoLastPoint() },
                                                onClearPoints = { viewModel.clearAllPoints() },
                                                onDeletePointAt = { index -> viewModel.deletePointAt(index) },
                                                onAddPointAt = { lat, lng -> viewModel.addManualPointOnMap(lat, lng) },
                                                onSaveFarm = { farmName -> viewModel.saveCompletedFarm(farmName) }
                                            )
                                        }
                                        2 -> {
                                            FertilizerScreen(
                                                farmArea = fertilizerFarmArea,
                                                targetN = targetN,
                                                targetP = targetP,
                                                targetK = targetK,
                                                fertilizerList = fertilizerList,
                                                calculationResult = calculationResult,
                                                selectedCrop = cropType,
                                                currentLanguage = currentLanguage,
                                                weatherData = weatherData,
                                                agriculturalRegions = agriculturalRegions,
                                                selectedRegion = selectedRegion,
                                                selectedWeatherScenario = selectedWeatherScenario,
                                                onLanguageSelected = { viewModel.setLanguage(it) },
                                                onRegionSelected = { reg -> viewModel.selectWeatherRegion(reg) },
                                                onScenarioSelected = { scenario -> viewModel.setWeatherScenario(scenario) },
                                                onRefreshWeather = { viewModel.refreshWeatherData() },
                                                onAreaChange = { viewModel.setFertilizerFarmArea(it) },
                                                onTargetNChange = { viewModel.setTargetN(it) },
                                                onTargetPChange = { viewModel.setTargetP(it) },
                                                onTargetKChange = { viewModel.setTargetK(it) },
                                                onToggleSelected = { id -> viewModel.toggleFertilizerSelected(id) },
                                                onToggleAvailability = { id -> viewModel.toggleFertilizerAvailability(id) },
                                                onUpdatePrice = { id, price -> viewModel.updateFertilizerPrice(id, price) },
                                                onRunCalculation = { viewModel.runCalculation() },
                                                onDismissResult = { viewModel.clearCalculationResult() },
                                                onSaveComputation = { viewModel.saveCalculationToHistory() }
                                            )
                                        }
                                        3 -> {
                                            BookletScreen(
                                                searchQuery = bookletSearchQuery,
                                                articles = bookletArticles,
                                                onSearchChange = { viewModel.setBookletSearchQuery(it) },
                                                onSelectGuide = { article -> viewModel.openGuide(article) },
                                                currentLanguage = currentLanguage,
                                                onLanguageSelected = { viewModel.setLanguage(it) }
                                            )
                                        }
                                        4 -> {
                                            HistoryScreen(
                                                searchQuery = historySearchQuery,
                                                farms = filteredFarms,
                                                totalFarms = totalFarmsCount,
                                                totalArea = totalAreaHectares,
                                                onSearchChange = { viewModel.setHistorySearchQuery(it) },
                                                onDeleteFarm = { farm -> viewModel.deleteFarmRecord(farm) },
                                                onDeleteAllFarms = { viewModel.deleteAllFarms() },
                                                currentUser = currentUser,
                                                onOpenAuthModal = { viewModel.openAuthModal() },
                                                onLogout = { viewModel.promptLogout() },
                                                onOpenAdminDashboard = { viewModel.openAdminDashboard() },
                                                onOpenDeleteAccount = { viewModel.openDeleteAccountModal() },
                                                onUpdateContactInfo = { name, phone, prov, mun, crop, area, rsbsa, agency ->
                                                    viewModel.updateContactInfo(name, phone, prov, mun, crop, area, rsbsa, agency)
                                                },
                                                currentLanguage = currentLanguage,
                                                onLanguageSelected = { viewModel.setLanguage(it) }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Global Auth & Account Login Modal
    AuthModal(
        isVisible = isAuthModalOpen,
        currentUser = currentUser,
        currentLanguage = currentLanguage,
        onFarmerLogin = { identifier, passcode -> viewModel.loginFarmer(identifier, passcode) },
        onFarmerRegister = { name, phone, rsbsa, prov, mun, area, crop ->
            viewModel.registerFarmer(name, phone, rsbsa, prov, mun, area, crop)
        },
        onAdminLogin = { identifier, passcode -> viewModel.loginAdminPersonnel(identifier, passcode) },
        onLogout = { viewModel.promptLogout() },
        onOpenAdminDashboard = { viewModel.openAdminDashboard() },
        onDismiss = { viewModel.closeAuthModal() }
    )

    // Global Delete Account Modal
    DeleteAccountModal(
        isVisible = isDeleteAccountModalVisible,
        currentLanguage = currentLanguage,
        onConfirmDelete = { viewModel.deleteUserAccount() },
        onDismiss = { viewModel.closeDeleteAccountModal() }
    )

    // Global Logout Confirmation Modal
    LogoutConfirmationModal(
        isVisible = isLogoutConfirmationOpen,
        currentLanguage = currentLanguage,
        onConfirmLogout = { viewModel.confirmLogout() },
        onDismiss = { viewModel.dismissLogoutPrompt() }
    )
}

