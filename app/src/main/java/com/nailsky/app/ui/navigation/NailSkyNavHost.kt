package com.nailsky.app.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
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
import com.nailsky.app.data.NailSkyRepository
import com.nailsky.app.data.ThemeMode
import com.nailsky.app.data.findDesignById
import com.nailsky.app.data.local.NailSkyDatabase
import com.nailsky.app.ui.screens.DesignDetailScreen
import com.nailsky.app.ui.screens.ExploreScreen
import com.nailsky.app.ui.screens.HomeScreen
import com.nailsky.app.ui.screens.ProfileScreen
import com.nailsky.app.ui.screens.SavedScreen
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
    EXPLORE("explore", "Explore", Icons.Rounded.Explore, Icons.Outlined.Explore),
    SAVED("saved", "Saved", Icons.Rounded.Favorite, Icons.Outlined.FavoriteBorder),
    PROFILE("profile", "Profile", Icons.Rounded.Person, Icons.Outlined.Person),
}

private const val DETAIL_ROUTE = "detail/{designId}"
private fun detailRoute(designId: Int) = "detail/$designId"

/** App shell wired to Room persistence and DataStore user preferences. */
@Composable
fun NailSkyApp(
    themeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit,
    displayName: String,
    onDisplayNameChange: (String) -> Unit,
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

    NailSkyAppContent(
        themeMode = themeMode,
        onThemeModeChange = onThemeModeChange,
        displayName = displayName,
        onDisplayNameChange = onDisplayNameChange,
        savedIds = savedIds,
        savedNotes = savedNotes,
        boards = boards,
        recentViewIds = recentViewIds,
        onToggleSave = { id -> scope.launch { repository.toggleSave(id) } },
        onSaveNote = { id, note -> scope.launch { repository.updateNote(id, note) } },
        onCreateBoard = { name, emoji -> scope.launch { repository.createBoard(name, emoji) } },
        onDeleteBoard = { boardId -> scope.launch { repository.deleteBoard(boardId) } },
        onToggleBoard = { boardId, designId, currentlyInBoard ->
            scope.launch { repository.toggleDesignInBoard(boardId, designId, currentlyInBoard) }
        },
        onRecordView = { id -> scope.launch { repository.recordView(id) } },
        onClearRecentViews = { scope.launch { repository.clearRecentViews() } },
        modifier = modifier,
    )
}

@Composable
internal fun NailSkyAppContent(
    themeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit,
    displayName: String,
    onDisplayNameChange: (String) -> Unit,
    savedIds: List<Int>,
    savedNotes: Map<Int, String>,
    boards: List<BoardWithDesigns>,
    recentViewIds: List<Int>,
    onToggleSave: (Int) -> Unit,
    onSaveNote: (Int, String) -> Unit,
    onCreateBoard: (name: String, emoji: String) -> Unit,
    onDeleteBoard: (Long) -> Unit,
    onToggleBoard: (boardId: Long, designId: Int, currentlyInBoard: Boolean) -> Unit,
    onRecordView: (Int) -> Unit,
    onClearRecentViews: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var pendingExploreQuery by rememberSaveable { mutableStateOf<String?>(null) }

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

    val showMessage: (String) -> Unit = { msg ->
        scope.launch {
            snackbarHostState.currentSnackbarData?.dismiss()
            snackbarHostState.showSnackbar(msg)
        }
    }

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter = slideInVertically { it },
                exit = slideOutVertically { it },
            ) {
                NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                    TopLevelDestination.entries.forEach { destination ->
                        val selected = currentDestination?.hierarchy?.any { it.route == destination.route } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = { navigateToTab(destination) },
                            icon = {
                                Icon(
                                    imageVector = if (selected) destination.selectedIcon else destination.unselectedIcon,
                                    contentDescription = null,
                                )
                            },
                            label = { Text(destination.label) },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                selectedTextColor = MaterialTheme.colorScheme.onSurface,
                            ),
                        )
                    }
                }
            }
        },
    ) { innerPadding ->
        // Each screen draws its own top bar (which handles the status bar inset),
        // so only the bottom padding from the navigation bar is applied here.
        NavHost(
            navController = navController,
            startDestination = TopLevelDestination.HOME.route,
            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding()),
            enterTransition = { fadeIn(animationSpec = tween(220)) },
            exitTransition = { fadeOut(animationSpec = tween(180)) },
        ) {
            composable(TopLevelDestination.HOME.route) {
                HomeScreen(
                    savedIds = savedIds,
                    onToggleSave = onToggleSave,
                    onDesignClick = openDesign,
                    onSearchClick = { navigateToTab(TopLevelDestination.EXPLORE) },
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
            composable(TopLevelDestination.SAVED.route) {
                SavedScreen(
                    savedIds = savedIds,
                    boards = boards,
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
                )
            }
            composable(TopLevelDestination.PROFILE.route) {
                ProfileScreen(
                    displayName = displayName,
                    onDisplayNameChange = onDisplayNameChange,
                    themeMode = themeMode,
                    onThemeModeChange = onThemeModeChange,
                    savedCount = savedIds.size,
                    boardsCount = boards.size,
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
            savedIds = listOf(1, 5),
            savedNotes = emptyMap(),
            boards = emptyList(),
            recentViewIds = listOf(2, 5),
            onToggleSave = {},
            onSaveNote = { _, _ -> },
            onCreateBoard = { _, _ -> },
            onDeleteBoard = {},
            onToggleBoard = { _, _, _ -> },
            onRecordView = {},
            onClearRecentViews = {},
        )
    }
}
