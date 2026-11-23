package com.nailsky.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

enum class ThemeMode(val label: String) {
    SYSTEM("System"),
    LIGHT("Light"),
    DARK("Dark"),
}

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class ThemePreferences(context: Context) {

    private val dataStore = context.applicationContext.settingsDataStore

    val themeMode: Flow<ThemeMode> = dataStore.data.map { prefs ->
        prefs[THEME_MODE_KEY]
            ?.let { stored -> ThemeMode.entries.firstOrNull { it.name == stored } }
            ?: ThemeMode.SYSTEM
    }

    val displayName: Flow<String> = dataStore.data.map { prefs ->
        prefs[DISPLAY_NAME_KEY]?.takeIf { it.isNotBlank() } ?: DEFAULT_DISPLAY_NAME
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        dataStore.edit { prefs -> prefs[THEME_MODE_KEY] = mode.name }
    }

    suspend fun setDisplayName(name: String) {
        val cleaned = name.trim().ifBlank { DEFAULT_DISPLAY_NAME }
        dataStore.edit { prefs -> prefs[DISPLAY_NAME_KEY] = cleaned }
    }

    private companion object {
        val THEME_MODE_KEY = stringPreferencesKey("theme_mode")
        val DISPLAY_NAME_KEY = stringPreferencesKey("display_name")
        const val DEFAULT_DISPLAY_NAME = "Nail Lover"
    }
}
