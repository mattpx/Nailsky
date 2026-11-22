package com.nailsky.app.ui.navigation

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.nailsky.app.data.ThemeMode
import com.nailsky.app.ui.screens.ExploreScreen
import com.nailsky.app.ui.screens.HomeScreen
import com.nailsky.app.ui.screens.ProfileScreen
import com.nailsky.app.ui.screens.SavedScreen
import com.nailsky.app.ui.theme.NailSkyTheme
import com.nailsky.app.ui.theme.ThemePreviews

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

private val SavedIdsSaver = listSaver<SnapshotStateList<Int>, Int>(
    save = { it.toList() },
    restore = { it.toMutableStateList() },
)

/** App shell: bottom navigation + NavHost. Saved designs are kept in memory for Phase 1. */
@Composable
fun NailSkyApp(
    themeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    val savedIds = rememberSaveable(saver = SavedIdsSaver) { mutableStateListOf<Int>() }
    val onToggleSave: (Int) -> Unit = { id ->
        if (id in savedIds) savedIds.remove(id) else savedIds.add(id)
    }

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                TopLevelDestination.entries.forEach { destination ->
                    val selected = currentDestination?.hierarchy?.any { it.route == destination.route } == true
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(destination.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
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
        },
    ) { innerPadding ->
        // Each screen draws its own top bar (which handles the status bar inset),
        // so only the bottom padding from the navigation bar is applied here.
        NavHost(
            navController = navController,
            startDestination = TopLevelDestination.HOME.route,
            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding()),
        ) {
            composable(TopLevelDestination.HOME.route) {
                HomeScreen(savedIds = savedIds, onToggleSave = onToggleSave)
            }
            composable(TopLevelDestination.EXPLORE.route) {
                ExploreScreen()
            }
            composable(TopLevelDestination.SAVED.route) {
                SavedScreen(savedIds = savedIds, onToggleSave = onToggleSave)
            }
            composable(TopLevelDestination.PROFILE.route) {
                ProfileScreen(
                    themeMode = themeMode,
                    onThemeModeChange = onThemeModeChange,
                    savedCount = savedIds.size,
                )
            }
        }
    }
}

@ThemePreviews
@Composable
private fun NailSkyAppPreview() {
    NailSkyTheme {
        NailSkyApp(themeMode = ThemeMode.SYSTEM, onThemeModeChange = {})
    }
}
