package com.nailsky.app.data

import android.content.Context
import androidx.compose.runtime.Immutable
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

enum class ThemeMode(val label: String) {
    SYSTEM("System"),
    LIGHT("Light"),
    DARK("Dark"),
}

@Immutable
data class NailPassportState(
    val currentSetTitle: String = "Classic French Tip",
    val lastManicureEpochDay: Long = 0L,
    val fillCycleDays: Int = 21,
    val cuticleStreak: Int = 0,
    val lastCuticleEpochDay: Long = 0L,
) {
    fun currentDay(todayEpochDay: Long): Int {
        if (lastManicureEpochDay <= 0L) return 1
        return (todayEpochDay - lastManicureEpochDay).toInt().coerceAtLeast(0) + 1
    }

    fun daysUntilFill(todayEpochDay: Long): Int {
        return (fillCycleDays - currentDay(todayEpochDay)).coerceAtLeast(0)
    }

    fun progress(todayEpochDay: Long): Float {
        return (currentDay(todayEpochDay).toFloat() / fillCycleDays.coerceAtLeast(1).toFloat())
            .coerceIn(0f, 1f)
    }

    fun isCuticleCheckedToday(todayEpochDay: Long): Boolean = lastCuticleEpochDay == todayEpochDay
}

fun currentEpochDay(): Long = System.currentTimeMillis() / 86_400_000L

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

    val nailPassport: Flow<NailPassportState> = dataStore.data.map { prefs ->
        NailPassportState(
            currentSetTitle = prefs[CURRENT_SET_KEY]?.takeIf { it.isNotBlank() } ?: "Classic French Tip",
            lastManicureEpochDay = prefs[LAST_MANICURE_DAY_KEY] ?: (currentEpochDay() - 5L),
            fillCycleDays = (prefs[FILL_CYCLE_DAYS_KEY] ?: 21).coerceIn(7, 42),
            cuticleStreak = (prefs[CUTICLE_STREAK_KEY] ?: 0).coerceAtLeast(0),
            lastCuticleEpochDay = prefs[LAST_CUTICLE_DAY_KEY] ?: 0L,
        )
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        dataStore.edit { prefs -> prefs[THEME_MODE_KEY] = mode.name }
    }

    suspend fun setDisplayName(name: String) {
        val cleaned = name.trim().ifBlank { DEFAULT_DISPLAY_NAME }
        dataStore.edit { prefs -> prefs[DISPLAY_NAME_KEY] = cleaned }
    }

    suspend fun logNewManicure(
        setTitle: String,
        cycleDays: Int,
        todayEpochDay: Long = currentEpochDay(),
    ) {
        dataStore.edit { prefs ->
            prefs[CURRENT_SET_KEY] = setTitle.trim().ifBlank { "Fresh Set" }
            prefs[LAST_MANICURE_DAY_KEY] = todayEpochDay
            prefs[FILL_CYCLE_DAYS_KEY] = cycleDays.coerceIn(7, 42)
        }
    }

    suspend fun checkInCuticleCare(todayEpochDay: Long = currentEpochDay()) {
        dataStore.edit { prefs ->
            val lastDay = prefs[LAST_CUTICLE_DAY_KEY] ?: 0L
            val currentStreak = prefs[CUTICLE_STREAK_KEY] ?: 0
            if (lastDay == todayEpochDay) return@edit
            val nextStreak = if (lastDay == todayEpochDay - 1L) currentStreak + 1 else 1
            prefs[LAST_CUTICLE_DAY_KEY] = todayEpochDay
            prefs[CUTICLE_STREAK_KEY] = nextStreak
        }
    }

    private companion object {
        val THEME_MODE_KEY = stringPreferencesKey("theme_mode")
        val DISPLAY_NAME_KEY = stringPreferencesKey("display_name")
        val CURRENT_SET_KEY = stringPreferencesKey("current_set_title")
        val LAST_MANICURE_DAY_KEY = longPreferencesKey("last_manicure_epoch_day")
        val FILL_CYCLE_DAYS_KEY = intPreferencesKey("fill_cycle_days")
        val CUTICLE_STREAK_KEY = intPreferencesKey("cuticle_streak")
        val LAST_CUTICLE_DAY_KEY = longPreferencesKey("last_cuticle_epoch_day")
        const val DEFAULT_DISPLAY_NAME = "Nail Lover"
    }
}
