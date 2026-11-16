package com.bookspine.readinglog.presentation.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bookspine.readinglog.core.config.GameConfig
import com.bookspine.readinglog.domain.model.BookStatus
import com.bookspine.readinglog.domain.repository.BookRepository
import com.bookspine.readinglog.domain.repository.SettingsRepository
import com.bookspine.readinglog.domain.usecase.CalculateStatsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.math.roundToInt

class MenuViewModel(
    bookRepository: BookRepository,
    settingsRepository: SettingsRepository,
    private val calculateStats: CalculateStatsUseCase
) : ViewModel() {

    private val uiState = MutableStateFlow(MenuUiState())
    val state: StateFlow<MenuUiState> = uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                bookRepository.books,
                bookRepository.entries,
                bookRepository.streak,
                settingsRepository.goalTarget
            ) { books, _, streak, goal ->
                val finished = books.filter { it.status == BookStatus.FINISHED }
                val capacity = GameConfig.SHELF_COLS * GameConfig.SHELF_ROWS
                val stats = calculateStats(LocalDate.now().monthValue - 1)
                val progress = if (goal <= 0) 0f else {
                    (finished.size.toFloat() / goal).coerceIn(0f, 1f)
                }
                MenuUiState(
                    visibleSpines = finished.takeLast(capacity),
                    extraSpines = (finished.size - capacity).coerceAtLeast(0),
                    finishedCount = finished.size,
                    totalPages = stats.totalPages,
                    streakDays = streak.days,
                    goalTarget = goal,
                    goalProgress = progress,
                    goalPercent = (progress * 100f).roundToInt(),
                    recentFinishedId = finished.lastOrNull()?.id.orEmpty()
                )
            }.collect { next ->
                uiState.value = next
            }
        }
    }
}
