package com.bookspine.readinglog.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bookspine.readinglog.core.config.GameConfig
import com.bookspine.readinglog.domain.model.ThemeMode
import com.bookspine.readinglog.domain.repository.BookRepository
import com.bookspine.readinglog.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val bookRepository: BookRepository
) : ViewModel() {

    private val uiState = MutableStateFlow(SettingsUiState())
    val state: StateFlow<SettingsUiState> = uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                settingsRepository.themeMode,
                settingsRepository.goalTarget,
                settingsRepository.hapticsEnabled
            ) { mode, goal, haptics ->
                Triple(mode, goal, haptics)
            }.collect { snapshot ->
                uiState.update { current ->
                    current.copy(
                        themeMode = snapshot.first,
                        goalTarget = snapshot.second,
                        hapticsEnabled = snapshot.third
                    )
                }
            }
        }
    }

    fun onThemeSelected(mode: ThemeMode) {
        settingsRepository.selectThemeMode(mode)
    }

    fun onGoalChanged(value: Int) {
        settingsRepository.selectGoalTarget(value)
    }

    fun onHapticsChanged(enabled: Boolean) {
        settingsRepository.selectHaptics(enabled)
    }

    fun onResetRequested() {
        uiState.update { it.copy(resetDialogShown = true) }
    }

    fun onResetDismissed() {
        uiState.update { it.copy(resetDialogShown = false) }
    }

    fun onResetConfirmed() {
        bookRepository.clearEverything()
        settingsRepository.selectThemeMode(ThemeMode.FOREST)
        settingsRepository.selectGoalTarget(GameConfig.DEFAULT_GOAL)
        settingsRepository.selectHaptics(true)
        uiState.update { it.copy(resetDialogShown = false) }
    }
}
