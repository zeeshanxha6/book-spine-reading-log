package com.bookspine.readinglog.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bookspine.readinglog.domain.model.BookStatus
import com.bookspine.readinglog.domain.model.Note
import com.bookspine.readinglog.domain.repository.BookRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.abs

class BookDetailViewModel(
    private val bookRepository: BookRepository
) : ViewModel() {

    private val selectedId = MutableStateFlow("")
    private val uiState = MutableStateFlow(BookDetailUiState())
    val state: StateFlow<BookDetailUiState> = uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                bookRepository.books,
                bookRepository.notes,
                selectedId
            ) { books, notes, id ->
                val book = books.firstOrNull { it.id == id }
                Triple(book, notes.filter { it.bookId == id }, id)
            }.collect { snapshot ->
                val book = snapshot.first
                uiState.update { current ->
                    current.copy(
                        title = book?.title.orEmpty(),
                        author = book?.author.orEmpty(),
                        totalPages = book?.totalPages ?: 0,
                        pagesRead = book?.pagesRead ?: 0,
                        finished = book?.status == BookStatus.FINISHED,
                        progress = book?.progress ?: 0f,
                        spineVariant = book?.spineVariant ?: 0,
                        colorIndex = book?.colorIndex ?: 0,
                        notes = snapshot.second,
                        notePage = current.notePage.coerceAtLeast(1)
                    )
                }
            }
        }
    }

    fun bind(bookId: String) {
        selectedId.value = bookId
    }

    fun onNoteDialogOpened() {
        uiState.update {
            it.copy(
                noteDialogShown = true,
                noteText = "",
                noteIsQuote = true,
                notePage = (it.pagesRead).coerceAtLeast(1),
                noteInvalid = false
            )
        }
    }

    fun onNoteDialogDismissed() {
        uiState.update { it.copy(noteDialogShown = false, noteInvalid = false) }
    }

    fun onNoteTextChanged(value: String) {
        uiState.update { it.copy(noteText = value, noteInvalid = false) }
    }

    fun onNoteKindChanged(isQuote: Boolean) {
        uiState.update { it.copy(noteIsQuote = isQuote) }
    }

    fun onNotePageChanged(value: Int) {
        uiState.update { current ->
            val limit = current.totalPages.coerceAtLeast(1)
            current.copy(notePage = value.coerceIn(1, limit))
        }
    }

    fun onNoteConfirmed() {
        val current = uiState.value
        if (current.noteText.isBlank()) {
            uiState.update { it.copy(noteInvalid = true) }
            return
        }
        val id = "note_" + abs((current.noteText + current.notePage).hashCode()).toString()
        bookRepository.persistNote(
            Note(
                id = id,
                bookId = selectedId.value,
                isQuote = current.noteIsQuote,
                page = current.notePage,
                text = current.noteText.trim()
            )
        )
        uiState.update { it.copy(noteDialogShown = false, noteText = "", noteInvalid = false) }
    }
}
