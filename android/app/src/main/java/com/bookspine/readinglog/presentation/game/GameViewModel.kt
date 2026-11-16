package com.bookspine.readinglog.presentation.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bookspine.readinglog.core.config.GameConfig
import com.bookspine.readinglog.domain.model.Book
import com.bookspine.readinglog.domain.model.BookStatus
import com.bookspine.readinglog.domain.repository.BookRepository
import com.bookspine.readinglog.domain.repository.SettingsRepository
import com.bookspine.readinglog.domain.usecase.LogPagesUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.math.abs

data class RoundResult(
    val pagesLogged: Int,
    val spineEarned: Boolean,
    val bookId: String
)

class GameViewModel(
    private val bookRepository: BookRepository,
    private val settingsRepository: SettingsRepository,
    private val logPages: LogPagesUseCase
) : ViewModel() {

    private val uiState = MutableStateFlow(GameUiState())
    val state: StateFlow<GameUiState> = uiState.asStateFlow()

    private val eventChannel = Channel<RoundResult>(Channel.BUFFERED)
    val events: Flow<RoundResult> = eventChannel.receiveAsFlow()

    private var commitJob: Job? = null

    private data class Snapshot(
        val active: List<Book>,
        val todayPages: Int,
        val streakDays: Int,
        val haptics: Boolean
    )

    init {
        viewModelScope.launch {
            combine(
                bookRepository.books,
                bookRepository.entries,
                bookRepository.streak,
                settingsRepository.hapticsEnabled
            ) { books, entries, streak, haptics ->
                val today = LocalDate.now().toEpochDay()
                Snapshot(
                    active = books.filter { it.status == BookStatus.READING },
                    todayPages = entries.filter { it.epochDay == today }.sumOf { it.pages },
                    streakDays = streak.days,
                    haptics = haptics
                )
            }.collect { snapshot ->
                uiState.update { current ->
                    val active = snapshot.active
                    val selected = if (active.any { it.id == current.selectedBookId }) {
                        current.selectedBookId
                    } else {
                        active.firstOrNull()?.id.orEmpty()
                    }
                    val limit = active.firstOrNull { it.id == selected }?.remainingPages ?: 0
                    current.copy(
                        activeBooks = active,
                        selectedBookId = selected,
                        todayPages = snapshot.todayPages,
                        streakDays = snapshot.streakDays,
                        hapticsEnabled = snapshot.haptics,
                        pending = current.pending.coerceAtMost(
                            limit.coerceAtMost(GameConfig.MAX_PAGES_PER_ENTRY)
                        )
                    )
                }
            }
        }
    }

    fun onBookSelected(bookId: String) {
        uiState.update { current ->
            val limit = current.activeBooks.firstOrNull { it.id == bookId }?.remainingPages ?: 0
            current.copy(
                selectedBookId = bookId,
                pending = GameConfig.DEFAULT_STEP.coerceAtMost(limit)
            )
        }
    }

    fun onPendingChanged(value: Int) {
        uiState.update { current ->
            current.copy(pending = value.coerceIn(0, current.maxPending))
        }
    }

    fun onCommitTapped() {
        val current = uiState.value
        if (!current.canCommit) return
        commitJob?.cancel()
        uiState.update { it.copy(saving = true, confirmShown = true) }
        commitJob = viewModelScope.launch {
            delay(GameConfig.SAVE_DELAY_MS)
            emitResult(current.selectedBookId, current.pending)
        }
    }

    fun onFinishBookTapped() {
        val current = uiState.value
        val book = current.selectedBook ?: return
        if (!current.canFinishBook) return
        commitJob?.cancel()
        uiState.update { it.copy(saving = true, confirmShown = true) }
        commitJob = viewModelScope.launch {
            delay(GameConfig.SAVE_DELAY_MS)
            emitResult(book.id, book.remainingPages)
        }
    }

    fun onAddBookOpened() {
        uiState.update {
            it.copy(
                addBookShown = true,
                addTitle = "",
                addAuthor = "",
                addPages = 200,
                addInvalid = false
            )
        }
    }

    fun onAddBookDismissed() {
        uiState.update { it.copy(addBookShown = false, addInvalid = false) }
    }

    fun onAddTitleChanged(value: String) {
        uiState.update { it.copy(addTitle = value, addInvalid = false) }
    }

    fun onAddAuthorChanged(value: String) {
        uiState.update { it.copy(addAuthor = value) }
    }

    fun onAddPagesChanged(value: Int) {
        uiState.update {
            it.copy(
                addPages = value.coerceIn(GameConfig.MIN_BOOK_PAGES, GameConfig.MAX_BOOK_PAGES)
            )
        }
    }

    fun onAddBookConfirmed() {
        val current = uiState.value
        if (current.addTitle.isBlank()) {
            uiState.update { it.copy(addInvalid = true) }
            return
        }
        val today = LocalDate.now().toEpochDay()
        val id = "book_" + abs((current.addTitle + today).hashCode()).toString()
        bookRepository.persistBook(
            Book(
                id = id,
                title = current.addTitle.trim(),
                author = current.addAuthor.trim(),
                totalPages = current.addPages,
                pagesRead = 0,
                status = BookStatus.READING,
                spineVariant = abs(id.hashCode()) % GameConfig.SPINE_VARIANTS,
                colorIndex = abs(id.hashCode() / 7) % GameConfig.SPINE_COLOR_COUNT,
                startDay = today,
                finishDay = 0L
            )
        )
        uiState.update {
            it.copy(addBookShown = false, selectedBookId = id, addInvalid = false)
        }
    }

    fun pause() {
        commitJob?.cancel()
        commitJob = null
        uiState.update { it.copy(saving = false, confirmShown = false) }
    }

    private suspend fun emitResult(bookId: String, pages: Int) {
        val today = LocalDate.now()
        val outcome = logPages(
            bookId = bookId,
            pages = pages,
            epochDay = today.toEpochDay(),
            monthIndex = today.monthValue - 1
        )
        uiState.update {
            it.copy(
                saving = false,
                confirmShown = false,
                pending = GameConfig.DEFAULT_STEP
            )
        }
        if (outcome != null) {
            eventChannel.send(
                RoundResult(
                    pagesLogged = outcome.pagesLogged,
                    spineEarned = outcome.spineEarned,
                    bookId = outcome.bookId
                )
            )
        }
    }
}
