package com.bookspine.readinglog.domain.repository

import com.bookspine.readinglog.domain.model.ThemeMode
import kotlinx.coroutines.flow.StateFlow

interface SettingsRepository {
    val themeMode: StateFlow<ThemeMode>
    val goalTarget: StateFlow<Int>
    val hapticsEnabled: StateFlow<Boolean>

    fun selectThemeMode(mode: ThemeMode)
    fun selectGoalTarget(target: Int)
    fun selectHaptics(enabled: Boolean)
}
