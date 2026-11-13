package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.Hearing
import androidx.compose.material.icons.outlined.LibraryMusic
import androidx.compose.material.icons.outlined.Piano
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material.icons.rounded.Hearing
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material.icons.rounded.Piano
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ui.screens.EncyclopediaScreen
import com.example.ui.screens.KeyboardStudioScreen
import com.example.ui.screens.MaqamDetailScreen
import com.example.ui.screens.QuizScreen
import com.example.ui.screens.TheoryScreen
import com.example.ui.theme.MaqamatTheme
import com.example.viewmodel.MaqamViewModel

object AppRoutes {
    const val ROUTE_ENCYCLOPEDIA = "encyclopedia"
    const val ROUTE_STUDIO = "studio"
    const val ROUTE_THEORY = "theory"
    const val ROUTE_QUIZ = "quiz"
    const val ROUTE_DETAIL = "maqam_detail/{maqamId}"

    fun detailRoute(maqamId: String): String = "maqam_detail/$maqamId"
}

private data class TopTabItem(
    val route: String,
    val labelRes: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

private val topTabs = listOf(
    TopTabItem(
        route = AppRoutes.ROUTE_ENCYCLOPEDIA,
        labelRes = R.string.nav_encyclopedia,
        selectedIcon = Icons.Rounded.LibraryMusic,
        unselectedIcon = Icons.Outlined.LibraryMusic,
        testTag = "nav_tab_encyclopedia"
    ),
    TopTabItem(
        route = AppRoutes.ROUTE_STUDIO,
        labelRes = R.string.nav_studio,
        selectedIcon = Icons.Rounded.Piano,
        unselectedIcon = Icons.Outlined.Piano,
        testTag = "nav_tab_studio"
    ),
    TopTabItem(
        route = AppRoutes.ROUTE_THEORY,
        labelRes = R.string.nav_theory,
        selectedIcon = Icons.Rounded.AutoStories,
        unselectedIcon = Icons.Outlined.AutoStories,
        testTag = "nav_tab_theory"
    ),
    TopTabItem(
        route = AppRoutes.ROUTE_QUIZ,
        labelRes = R.string.nav_quiz,
        selectedIcon = Icons.Rounded.Hearing,
        unselectedIcon = Icons.Outlined.Hearing,
        testTag = "nav_tab_quiz"
    )
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaqamatTheme {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    MaqamatApp()
                }
            }
        }
    }
}

@Composable
fun MaqamatApp(viewModel: MaqamViewModel = viewModel()) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val filteredMaqamat by viewModel.filteredMaqamat.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedFamily by viewModel.selectedFamily.collectAsStateWithLifecycle()
    val quarterToneFilter by viewModel.quarterToneFilter.collectAsStateWithLifecycle()
    val onlyFavorites by viewModel.onlyFavorites.collectAsStateWithLifecycle()
    val favoriteIds by viewModel.favoriteIds.collectAsStateWithLifecycle()
    val selectedInstrument by viewModel.selectedInstrument.collectAsStateWithLifecycle()
    val playbackState by viewModel.playbackState.collectAsStateWithLifecycle()
    val studioMaqam by viewModel.studioMaqam.collectAsStateWithLifecycle()
    val studioActiveNoteId by viewModel.studioActiveNoteId.collectAsStateWithLifecycle()
    val quizDifficulty by viewModel.quizDifficulty.collectAsStateWithLifecycle()
    val currentQuestion by viewModel.currentQuestion.collectAsStateWithLifecycle()
    val selectedQuizAnswerId by viewModel.selectedQuizAnswerId.collectAsStateWithLifecycle()
    val currentStreak by viewModel.currentStreak.collectAsStateWithLifecycle()
    val bestStreak by viewModel.bestStreak.collectAsStateWithLifecycle()
    val totalCorrect by viewModel.totalCorrect.collectAsStateWithLifecycle()

    val isTopLevelRoute = topTabs.any { it.route == currentRoute }

    fun navigateToTab(route: String) {
        if (currentRoute == AppRoutes.ROUTE_STUDIO && route != AppRoutes.ROUTE_STUDIO) {
            viewModel.stopDrone()
        }
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val useNavigationRail = maxWidth >= 680.dp

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.background,
            contentWindowInsets = if (isTopLevelRoute) WindowInsets.statusBars else WindowInsets(0, 0, 0, 0),
            bottomBar = {
                if (isTopLevelRoute && !useNavigationRail) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        windowInsets = WindowInsets.navigationBars
                    ) {
                        topTabs.forEach { tab ->
                            val selected = currentRoute == tab.route
                            val labelText = stringResource(tab.labelRes)
                            NavigationBarItem(
                                selected = selected,
                                onClick = { navigateToTab(tab.route) },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                        contentDescription = labelText
                                    )
                                },
                                label = {
                                    Text(
                                        text = labelText,
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                                ),
                                modifier = Modifier.testTag(tab.testTag)
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (isTopLevelRoute && useNavigationRail) {
                    NavigationRail(
                        containerColor = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing)
                    ) {
                        topTabs.forEach { tab ->
                            val selected = currentRoute == tab.route
                            val labelText = stringResource(tab.labelRes)
                            NavigationRailItem(
                                selected = selected,
                                onClick = { navigateToTab(tab.route) },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                        contentDescription = labelText
                                    )
                                },
                                label = { Text(labelText) },
                                modifier = Modifier.testTag(tab.testTag)
                            )
                        }
                    }
                }

                Box(modifier = Modifier.weight(1f).fillMaxSize()) {
                    NavHost(
                        navController = navController,
                        startDestination = AppRoutes.ROUTE_ENCYCLOPEDIA
                    ) {
                        composable(AppRoutes.ROUTE_ENCYCLOPEDIA) {
                            EncyclopediaScreen(
                                maqamat = filteredMaqamat,
                                featuredMaqam = viewModel.featuredMaqam,
                                searchQuery = searchQuery,
                                selectedFamily = selectedFamily,
                                quarterToneFilter = quarterToneFilter,
                                onlyFavorites = onlyFavorites,
                                favoriteIds = favoriteIds,
                                playbackState = playbackState,
                                onSearchChange = viewModel::updateSearchQuery,
                                onSelectFamily = viewModel::selectFamily,
                                onSelectQuarterToneFilter = viewModel::setQuarterToneFilter,
                                onToggleOnlyFavorites = viewModel::toggleOnlyFavorites,
                                onClearFilters = viewModel::clearAllFilters,
                                onToggleFavorite = viewModel::toggleFavorite,
                                onPlayPreview = { maqam ->
                                    viewModel.playNoteSequence(
                                        maqam.ascendingScale,
                                        "preview_${maqam.id}"
                                    )
                                },
                                onMaqamClick = { maqamId ->
                                    viewModel.stopPlayback()
                                    navController.navigate(AppRoutes.detailRoute(maqamId))
                                }
                            )
                        }

                        composable(AppRoutes.ROUTE_STUDIO) {
                            KeyboardStudioScreen(
                                allMaqamat = viewModel.allMaqamat,
                                selectedMaqam = studioMaqam,
                                selectedInstrument = selectedInstrument,
                                activeNoteId = studioActiveNoteId,
                                playbackState = playbackState,
                                onSelectMaqam = viewModel::selectStudioMaqam,
                                onSelectInstrument = viewModel::selectInstrument,
                                onNoteTap = viewModel::playSingleNote,
                                onPlayScale = {
                                    viewModel.playNoteSequence(
                                        studioMaqam.ascendingScale,
                                        "studio_scale_${studioMaqam.id}"
                                    )
                                },
                                onPlayMelody = {
                                    viewModel.playNoteSequence(
                                        studioMaqam.melodyPhrase,
                                        "studio_melody_${studioMaqam.id}"
                                    )
                                },
                                onToggleDrone = viewModel::toggleDrone
                            )
                        }

                        composable(AppRoutes.ROUTE_THEORY) {
                            TheoryScreen(
                                topics = viewModel.theoryTopics,
                                playbackState = playbackState,
                                onPlayNote = viewModel::playSingleNote,
                                onPlayDemo = viewModel::playNoteSequence
                            )
                        }

                        composable(AppRoutes.ROUTE_QUIZ) {
                            QuizScreen(
                                question = currentQuestion,
                                difficulty = quizDifficulty,
                                selectedAnswerId = selectedQuizAnswerId,
                                currentStreak = currentStreak,
                                bestStreak = bestStreak,
                                totalCorrect = totalCorrect,
                                selectedInstrument = selectedInstrument,
                                playbackState = playbackState,
                                onSelectDifficulty = viewModel::setQuizDifficulty,
                                onSelectInstrument = viewModel::selectInstrument,
                                onPlayQuestion = viewModel::playQuizQuestionAudio,
                                onSubmitAnswer = viewModel::submitQuizAnswer,
                                onNextQuestion = { viewModel.generateNextQuizQuestion(autoPlay = true) },
                                onOpenMaqamDetail = { maqamId ->
                                    viewModel.stopPlayback()
                                    navController.navigate(AppRoutes.detailRoute(maqamId))
                                },
                                onPlayNote = viewModel::playSingleNote
                            )
                        }

                        composable(
                            route = AppRoutes.ROUTE_DETAIL,
                            arguments = listOf(navArgument("maqamId") { type = NavType.StringType })
                        ) { entry ->
                            val maqamId = entry.arguments?.getString("maqamId") ?: "rast"
                            val maqam = viewModel.getMaqamById(maqamId) ?: viewModel.featuredMaqam
                            MaqamDetailScreen(
                                maqam = maqam,
                                isFavorite = maqam.id in favoriteIds,
                                selectedInstrument = selectedInstrument,
                                playbackState = playbackState,
                                onBack = {
                                    viewModel.stopPlayback()
                                    navController.popBackStack()
                                },
                                onToggleFavorite = { viewModel.toggleFavorite(maqam.id) },
                                onSelectInstrument = viewModel::selectInstrument,
                                onTempoChange = viewModel::setTempoMultiplier,
                                onPlayNote = viewModel::playSingleNote,
                                onPlaySequence = viewModel::playNoteSequence
                            )
                        }
                    }
                }
            }
        }
    }
}
