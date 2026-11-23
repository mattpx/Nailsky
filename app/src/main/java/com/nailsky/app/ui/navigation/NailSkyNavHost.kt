package com.nailsky.app.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.nailsky.app.data.BoardWithDesigns
import com.nailsky.app.data.Category
import com.nailsky.app.data.CustomNailSet
import com.nailsky.app.data.NailDesign
import com.nailsky.app.data.NailFinish
import com.nailsky.app.data.NailPassportState
import com.nailsky.app.data.NailSkyRepository
import com.nailsky.app.data.StudioEffect
import com.nailsky.app.data.ThemeMode
import com.nailsky.app.data.findDesignById
import com.nailsky.app.data.local.NailSkyDatabase
import com.nailsky.app.ui.screens.DesignDetailScreen
import com.nailsky.app.ui.screens.ExploreScreen
import com.nailsky.app.ui.screens.HomeScreen
import com.nailsky.app.ui.screens.ProfileScreen
import com.nailsky.app.ui.screens.SavedScreen
import com.nailsky.app.ui.screens.StudioScreen
import com.nailsky.app.ui.theme.NailSkyTheme
import com.nailsky.app.ui.theme.ThemePreviews
import kotlinx.coroutines.launch

enum class TopLevelDestination(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
) {
    HOME("home", "Home", Icons.Rounded.Home, Icons.Outlined.Home),
    EXPLORE("explore", "Discover", Icons.Rounded.Explore, Icons.Outlined.Explore),
    STUDIO("studio", "Studio", Icons.Rounded.AutoAwesome, Icons.Rounded.AutoAwesome),
    SAVED("saved", "Saved", Icons.Rounded.Favorite, Icons.Outlined.FavoriteBorder),
    PROFILE("profile", "Passport", Icons.Rounded.Person, Icons.Outlined.Person),
}

private const val DETAIL_ROUTE = "detail/{designId}"
private fun detailRoute(designId: Int) = "detail/$designId"

/** Converts a curated [NailDesign] into a live preset for the Interactive Hand Studio. */
private fun NailDesign.toStudioPreset(): CustomNailSet {
    val effect = when {
        category == Category.FRENCH -> StudioEffect.FRENCH
        category == Category.OMBRE -> StudioEffect.AURA
        finish == NailFinish.CHROME -> StudioEffect.CHROME
        finish == NailFinish.GLITTER -> StudioEffect.GLITTER
        finish == NailFinish.MATTE -> StudioEffect.MATTE
        else -> StudioEffect.GLOSSY
    }
    val primary = colors.firstOrNull() ?: Color(0xFFF7A8C4)
    val accent = colors.getOrNull(1) ?: primary
    return CustomNailSet(
        name = title,
        shape = shape,
        lengthFactor = 0.58f,
        effect = effect,
        skinToneIndex = 1,
        nailColors = listOf(primary, primary, accent, primary, primary),
    )
}

/** App shell wired to Room persistence and DataStore user preferences. */
@Composable
fun NailSkyApp(
    themeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit,
    displayName: String,
    onDisplayNameChange: (String) -> Unit,
    passport: NailPassportState,
    onLogNewManicure: (setTitle: String, cycleDays: Int) -> Unit,
    onCheckInCuticle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val repository = remember(context) {
        NailSkyRepository(NailSkyDatabase.getInstance(context.applicationContext).dao())
    }
    val scope = rememberCoroutineScope()

    val savedIds by repository.savedIds.collectAsStateWithLifecycle(initialValue = emptyList())
    val savedNotes by repository.savedNotes.collectAsStateWithLifecycle(initialValue = emptyMap())
    val boards by repository.boards.collectAsStateWithLifecycle(initialValue = emptyList())
    val recentViewIds by repository.recentViewIds.collectAsStateWithLifecycle(initialValue = emptyList())
    val customSets by repository.customSets.collectAsStateWithLifecycle(initialValue = emptyList())

    NailSkyAppContent(
        themeMode = themeMode,
        onThemeModeChange = onThemeModeChange,
        displayName = displayName,
        onDisplayNameChange = onDisplayNameChange,
        passport = passport,
        onLogNewManicure = onLogNewManicure,
        onCheckInCuticle = onCheckInCuticle,
        savedIds = savedIds,
        savedNotes = savedNotes,
        boards = boards,
        recentViewIds = recentViewIds,
        customSets = customSets,
        onToggleSave = { id -> scope.launch { repository.toggleSave(id) } },
        onSaveNote = { id, note -> scope.launch { repository.updateNote(id, note) } },
        onCreateBoard = { name, emoji -> scope.launch { repository.createBoard(name, emoji) } },
        onDeleteBoard = { boardId -> scope.launch { repository.deleteBoard(boardId) } },
        onToggleBoard = { boardId, designId, currentlyInBoard ->
            scope.launch { repository.toggleDesignInBoard(boardId, designId, currentlyInBoard) }
        },
        onRecordView = { id -> scope.launch { repository.recordView(id) } },
        onClearRecentViews = { scope.launch { repository.clearRecentViews() } },
        onSaveCustomSet = { set -> scope.launch { repository.saveCustomSet(set) } },
        onDeleteCustomSet = { id -> scope.launch { repository.deleteCustomSet(id) } },
        modifier = modifier,
    )
}

@Composable
internal fun NailSkyAppContent(
    themeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit,
    displayName: String,
    onDisplayNameChange: (String) -> Unit,
    passport: NailPassportState,
    onLogNewManicure: (setTitle: String, cycleDays: Int) -> Unit,
    onCheckInCuticle: () -> Unit,
    savedIds: List<Int>,
    savedNotes: Map<Int, String>,
    boards: List<BoardWithDesigns>,
    recentViewIds: List<Int>,
    customSets: List<CustomNailSet>,
    onToggleSave: (Int) -> Unit,
    onSaveNote: (Int, String) -> Unit,
    onCreateBoard: (name: String, emoji: String) -> Unit,
    onDeleteBoard: (Long) -> Unit,
    onToggleBoard: (boardId: Long, designId: Int, currentlyInBoard: Boolean) -> Unit,
    onRecordView: (Int) -> Unit,
    onClearRecentViews: () -> Unit,
    onSaveCustomSet: (CustomNailSet) -> Unit,
    onDeleteCustomSet: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var pendingExploreQuery by rememberSaveable { mutableStateOf<String?>(null) }
    var pendingStudioPreset by remember { mutableStateOf<CustomNailSet?>(null) }

    val showBottomBar = TopLevelDestination.entries.any { dest ->
        currentDestination?.hierarchy?.any { it.route == dest.route } == true
    }

    val navigateToTab: (TopLevelDestination) -> Unit = { destination ->
        navController.navigate(destination.route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    val openDesign: (Int) -> Unit = { designId ->
        navController.navigate(detailRoute(designId))
    }

    val openStudioWithPreset: (CustomNailSet) -> Unit = { preset ->
        pendingStudioPreset = preset
        navController.popBackStack(
            route = navController.graph.findStartDestination().route ?: TopLevelDestination.HOME.route,
            inclusive = false,
        )
        navigateToTab(TopLevelDestination.STUDIO)
    }

    val showMessage: (String) -> Unit = { msg ->
        scope.launch {
            snackbarHostState.currentSnackbarData?.dismiss()
            snackbarHostState.showSnackbar(msg)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        NavHost(
            navController = navController,
            startDestination = TopLevelDestination.HOME.route,
            modifier = Modifier.fillMaxSize(),
            enterTransition = { fadeIn(animationSpec = tween(220)) },
            exitTransition = { fadeOut(animationSpec = tween(180)) },
        ) {
            composable(TopLevelDestination.HOME.route) {
                HomeScreen(
                    savedIds = savedIds,
                    onToggleSave = onToggleSave,
                    onDesignClick = openDesign,
                    onSearchClick = { navigateToTab(TopLevelDestination.EXPLORE) },
                    onTryDesignInStudio = { design -> openStudioWithPreset(design.toStudioPreset()) },
                )
            }
            composable(TopLevelDestination.EXPLORE.route) {
                ExploreScreen(
                    savedIds = savedIds,
                    onToggleSave = onToggleSave,
                    onDesignClick = openDesign,
                    initialQuery = pendingExploreQuery,
                    onConsumeInitialQuery = { pendingExploreQuery = null },
                )
            }
            composable(TopLevelDestination.STUDIO.route) {
                StudioScreen(
                    preset = pendingStudioPreset,
                    onConsumePreset = { pendingStudioPreset = null },
                    onSaveCustomSet = { set ->
                        onSaveCustomSet(set)
                        showMessage("Saved \"${set.name}\" to Moodboards")
                    },
                )
            }
            composable(TopLevelDestination.SAVED.route) {
                SavedScreen(
                    savedIds = savedIds,
                    boards = boards,
                    customSets = customSets,
                    onToggleSave = onToggleSave,
                    onDesignClick = openDesign,
                    onCreateBoard = { name, emoji ->
                        onCreateBoard(name, emoji)
                        showMessage("Created board \"$name\"")
                    },
                    onDeleteBoard = { id ->
                        onDeleteBoard(id)
                        showMessage("Board deleted")
                    },
                    onOpenCustomSet = { set -> openStudioWithPreset(set) },
                    onDeleteCustomSet = { id ->
                        onDeleteCustomSet(id)
                        showMessage("Removed custom set")
                    },
                )
            }
            composable(TopLevelDestination.PROFILE.route) {
                ProfileScreen(
                    displayName = displayName,
                    onDisplayNameChange = onDisplayNameChange,
                    themeMode = themeMode,
                    onThemeModeChange = onThemeModeChange,
                    passport = passport,
                    onLogNewManicure = { title, days ->
                        onLogNewManicure(title, days)
                        showMessage("Logged fresh set: $title")
                    },
                    onCheckInCuticle = {
                        onCheckInCuticle()
                        showMessage("Cuticle care logged ✨")
                    },
                    savedCount = savedIds.size,
                    boardsCount = boards.size,
                    customSetsCount = customSets.size,
                    recentViewIds = recentViewIds,
                    onDesignClick = openDesign,
                    onOpenSaved = { navigateToTab(TopLevelDestination.SAVED) },
                    onClearRecentViews = {
                        onClearRecentViews()
                        showMessage("Cleared recently viewed")
                    },
                )
            }
            composable(
                route = DETAIL_ROUTE,
                arguments = listOf(navArgument("designId") { type = NavType.IntType }),
                enterTransition = {
                    slideInHorizontally(animationSpec = tween(260)) { it / 4 } + fadeIn(tween(220))
                },
                exitTransition = { fadeOut(tween(180)) },
                popEnterTransition = { fadeIn(tween(200)) },
                popExitTransition = {
                    slideOutHorizontally(animationSpec = tween(240)) { it / 4 } + fadeOut(tween(180))
                },
            ) { entry ->
                val designId = entry.arguments?.getInt("designId") ?: 1
                val design = findDesignById(designId)
                if (design != null) {
                    LaunchedEffect(design.id) {
                        onRecordView(design.id)
                    }
                    DesignDetailScreen(
                        design = design,
                        isSaved = design.id in savedIds,
                        savedIds = savedIds,
                        savedNote = savedNotes[design.id].orEmpty(),
                        boards = boards,
                        onBack = { navController.popBackStack() },
                        onToggleSave = onToggleSave,
                        onSaveNote = { note -> onSaveNote(design.id, note) },
                        onToggleBoard = { board, currentlyInBoard ->
                            onToggleBoard(board.id, design.id, currentlyInBoard)
                            val action = if (currentlyInBoard) "Removed from" else "Added to"
                            showMessage("$action ${board.emoji} ${board.name}")
                        },
                        onCreateBoard = { name, emoji ->
                            onCreateBoard(name, emoji)
                            showMessage("Created board \"$name\"")
                        },
                        onTryInStudio = { d -> openStudioWithPreset(d.toStudioPreset()) },
                        onTagClick = { tag ->
                            pendingExploreQuery = tag
                            navController.popBackStack(
                                route = navController.graph.findStartDestination().route ?: TopLevelDestination.HOME.route,
                                inclusive = false,
                            )
                            navigateToTab(TopLevelDestination.EXPLORE)
                        },
                        onDesignClick = openDesign,
                        onShowMessage = showMessage,
                    )
                }
            }
        }

        // Snackbar floating cleanly above the dock
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = if (showBottomBar) 92.dp else 16.dp),
        )

        // Floating frosted pill navigation dock with glowing center Studio button
        AnimatedVisibility(
            visible = showBottomBar,
            enter = slideInVertically { it } + fadeIn(),
            exit = slideOutVertically { it } + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp),
        ) {
            FloatingNavDock(
                currentRoute = currentDestination?.route,
                onNavigate = navigateToTab,
            )
        }
    }
}

@Composable
private fun FloatingNavDock(
    currentRoute: String?,
    onNavigate: (TopLevelDestination) -> Unit,
) {
    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.75f)),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 14.dp, shape = CircleShape, clip = false),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TopLevelDestination.entries.forEach { destination ->
                val selected = currentRoute == destination.route
                if (destination == TopLevelDestination.STUDIO) {
                    // Glowing gradient center Studio button
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.secondary,
                                    ),
                                ),
                            )
                            .clickable { onNavigate(destination) },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.AutoAwesome,
                            contentDescription = "Nail Studio",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp),
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(
                                if (selected) {
                                    MaterialTheme.colorScheme.primaryContainer
                                } else {
                                    Color.Transparent
                                },
                            )
                            .clickable { onNavigate(destination) },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = if (selected) destination.selectedIcon else destination.unselectedIcon,
                            contentDescription = destination.label,
                            tint = if (selected) {
                                MaterialTheme.colorScheme.onPrimaryContainer
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            modifier = Modifier.size(23.dp),
                        )
                    }
                }
            }
        }
    }
}

@ThemePreviews
@Composable
private fun NailSkyAppPreview() {
    NailSkyTheme {
        NailSkyAppContent(
            themeMode = ThemeMode.SYSTEM,
            onThemeModeChange = {},
            displayName = "Nail Lover",
            onDisplayNameChange = {},
            passport = NailPassportState(),
            onLogNewManicure = { _, _ -> },
            onCheckInCuticle = {},
            savedIds = listOf(1, 5),
            savedNotes = emptyMap(),
            boards = emptyList(),
            recentViewIds = listOf(2, 5),
            customSets = emptyList(),
            onToggleSave = {},
            onSaveNote = { _, _ -> },
            onCreateBoard = { _, _ -> },
            onDeleteBoard = {},
            onToggleBoard = { _, _, _ -> },
            onRecordView = {},
            onClearRecentViews = {},
            onSaveCustomSet = {},
            onDeleteCustomSet = {},
        )
    }
}
