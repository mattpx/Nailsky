package com.nailsky.app

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nailsky.app.data.ThemeMode
import com.nailsky.app.data.ThemePreferences
import com.nailsky.app.ui.navigation.NailSkyApp
import com.nailsky.app.ui.theme.NailSkyTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    @Volatile
    private var themeLoaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        // Keep the splash visible until the saved theme is read, to avoid a light/dark flash.
        splashScreen.setKeepOnScreenCondition { !themeLoaded }
        enableEdgeToEdge()

        val themePreferences = ThemePreferences(applicationContext)

        setContent {
            val storedMode by themePreferences.themeMode
                .collectAsStateWithLifecycle(initialValue = null)
            val scope = rememberCoroutineScope()
            val mode = storedMode
            if (mode != null) {
            SideEffect { themeLoaded = true }

            val darkTheme = when (mode) {
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
            }

            // Keep system bar icon colors in sync with the in-app theme choice.
            DisposableEffect(darkTheme) {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(
                        Color.TRANSPARENT,
                        Color.TRANSPARENT,
                    ) { darkTheme },
                    navigationBarStyle = SystemBarStyle.auto(
                        LightScrim,
                        DarkScrim,
                    ) { darkTheme },
                )
                onDispose {}
            }

            NailSkyTheme(darkTheme = darkTheme) {
                NailSkyApp(
                    themeMode = mode,
                    onThemeModeChange = { newMode ->
                        scope.launch { themePreferences.setThemeMode(newMode) }
                    },
                )
            }
            }
        }
    }

    private companion object {
        val LightScrim = Color.argb(0xE6, 0xFF, 0xFF, 0xFF)
        val DarkScrim = Color.argb(0x80, 0x0F, 0x16, 0x26)
    }
}
