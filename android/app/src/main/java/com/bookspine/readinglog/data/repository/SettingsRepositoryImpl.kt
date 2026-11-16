package com.bookspine.readinglog.data.repository

import com.bookspine.readinglog.core.config.GameConfig
import com.bookspine.readinglog.data.local.ReadingPreferences
import com.bookspine.readinglog.domain.model.ThemeMode
import com.bookspine.readinglog.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsRepositoryImpl(
    private val preferences: ReadingPreferences
) : SettingsRepository {

    private val themeState = MutableStateFlow(preferences.themeMode)
    private val goalState = MutableStateFlow(preferences.goalTarget)
    private val hapticsState = MutableStateFlow(preferences.hapticsEnabled)

    override val themeMode: StateFlow<ThemeMode> = themeState.asStateFlow()
    override val goalTarget: StateFlow<Int> = goalState.asStateFlow()
    override val hapticsEnabled: StateFlow<Boolean> = hapticsState.asStateFlow()

    override fun selectThemeMode(mode: ThemeMode) {
        themeState.value = mode
        preferences.themeMode = mode
    }

    override fun selectGoalTarget(target: Int) {
        val clamped = target.coerceIn(GameConfig.MIN_GOAL, GameConfig.MAX_GOAL)
        goalState.value = clamped
        preferences.goalTarget = clamped
    }

    override fun selectHaptics(enabled: Boolean) {
        hapticsState.value = enabled
        preferences.hapticsEnabled = enabled
    }
}
