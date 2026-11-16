package com.bookspine.readinglog.presentation.gameover

import androidx.lifecycle.ViewModel
import com.bookspine.readinglog.domain.repository.BookRepository
import com.bookspine.readinglog.domain.usecase.CalculateStatsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate

class GameOverViewModel(
    private val bookRepository: BookRepository,
    private val calculateStats: CalculateStatsUseCase
) : ViewModel() {

    private val uiState = MutableStateFlow(GameOverUiState())
    val state: StateFlow<GameOverUiState> = uiState.asStateFlow()

    fun bind(bookId: String, pagesLogged: Int, spineEarned: Boolean) {
        val book = bookRepository.books.value.firstOrNull { it.id == bookId }
        val stats = calculateStats(LocalDate.now().monthValue - 1)
        uiState.value = GameOverUiState(
            bookTitle = book?.title.orEmpty(),
            spineEarned = spineEarned,
            pagesLogged = pagesLogged,
            streakDays = stats.streakDays,
            finishedCount = stats.finishedBooks,
            goalTarget = stats.goalTarget,
            spineVariant = book?.spineVariant ?: 0,
            spineColorIndex = book?.colorIndex ?: 0
        )
    }
}
