package com.bookspine.readinglog.presentation.settings

import com.bookspine.readinglog.core.config.GameConfig
import com.bookspine.readinglog.domain.model.ThemeMode

data class SettingsUiState(
    val themeMode: ThemeMode = ThemeMode.FOREST,
    val goalTarget: Int = GameConfig.DEFAULT_GOAL,
    val hapticsEnabled: Boolean = true,
    val resetDialogShown: Boolean = false
)
