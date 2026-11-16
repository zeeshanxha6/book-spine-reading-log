package com.bookspine.readinglog.presentation.stats

import com.bookspine.readinglog.core.config.GameConfig

data class StatsUiState(
    val pagesPerMonth: List<Int> = emptyList(),
    val bestMonthIndex: Int = 0,
    val totalPages: Int = 0,
    val finishedBooks: Int = 0,
    val averagePerMonth: Int = 0,
    val goalTarget: Int = GameConfig.DEFAULT_GOAL,
    val booksAheadOfPace: Int = 0,
    val goalProgress: Float = 0f,
    val year: Int = GameConfig.GOAL_YEAR
) {
    val maxMonthPages: Int
        get() = pagesPerMonth.maxOrNull() ?: 0

    val hasData: Boolean
        get() = maxMonthPages > 0
}
