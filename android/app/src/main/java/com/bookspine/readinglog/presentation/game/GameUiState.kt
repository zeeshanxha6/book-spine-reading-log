package com.bookspine.readinglog.presentation.game

import com.bookspine.readinglog.core.config.GameConfig
import com.bookspine.readinglog.domain.model.Book

data class GameUiState(
    val activeBooks: List<Book> = emptyList(),
    val selectedBookId: String = "",
    val pending: Int = GameConfig.DEFAULT_STEP,
    val todayPages: Int = 0,
    val streakDays: Int = 0,
    val saving: Boolean = false,
    val confirmShown: Boolean = false,
    val addBookShown: Boolean = false,
    val addTitle: String = "",
    val addAuthor: String = "",
    val addPages: Int = 200,
    val addInvalid: Boolean = false,
    val hapticsEnabled: Boolean = true
) {
    val selectedBook: Book?
        get() = activeBooks.firstOrNull { it.id == selectedBookId }

    val remaining: Int
        get() = selectedBook?.remainingPages ?: 0

    val maxPending: Int
        get() = remaining.coerceAtMost(GameConfig.MAX_PAGES_PER_ENTRY)

    val canCommit: Boolean
        get() = !saving && selectedBook != null && pending in 1..remaining

    val canFinishBook: Boolean
        get() = !saving && selectedBook != null && remaining > 0 && pending >= remaining

    val libraryEmpty: Boolean
        get() = activeBooks.isEmpty()
}
