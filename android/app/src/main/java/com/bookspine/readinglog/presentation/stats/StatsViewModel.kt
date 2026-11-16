package com.bookspine.readinglog.presentation.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bookspine.readinglog.domain.repository.BookRepository
import com.bookspine.readinglog.domain.repository.SettingsRepository
import com.bookspine.readinglog.domain.usecase.CalculateStatsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.LocalDate

class StatsViewModel(
    bookRepository: BookRepository,
    settingsRepository: SettingsRepository,
    private val calculateStats: CalculateStatsUseCase
) : ViewModel() {

    private val uiState = MutableStateFlow(StatsUiState())
    val state: StateFlow<StatsUiState> = uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                bookRepository.books,
                bookRepository.entries,
                settingsRepository.goalTarget
            ) { _, _, _ ->
                val today = LocalDate.now()
                val stats = calculateStats(today.monthValue - 1)
                StatsUiState(
                    pagesPerMonth = stats.pagesPerMonth,
                    bestMonthIndex = stats.bestMonthIndex,
                    totalPages = stats.totalPages,
                    finishedBooks = stats.finishedBooks,
                    averagePerMonth = stats.averagePerMonth,
                    goalTarget = stats.goalTarget,
                    booksAheadOfPace = stats.booksAheadOfPace,
                    goalProgress = stats.goalProgress,
                    year = today.year
                )
            }.collect { next ->
                uiState.value = next
            }
        }
    }
}
