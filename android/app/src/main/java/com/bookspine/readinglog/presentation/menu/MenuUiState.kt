package com.bookspine.readinglog.presentation.menu

import com.bookspine.readinglog.core.config.GameConfig
import com.bookspine.readinglog.domain.model.Book

data class MenuUiState(
    val visibleSpines: List<Book> = emptyList(),
    val extraSpines: Int = 0,
    val finishedCount: Int = 0,
    val totalPages: Int = 0,
    val streakDays: Int = 0,
    val goalTarget: Int = GameConfig.DEFAULT_GOAL,
    val goalProgress: Float = 0f,
    val goalPercent: Int = 0,
    val recentFinishedId: String = ""
) {
    val shelfEmpty: Boolean
        get() = visibleSpines.isEmpty()
}
