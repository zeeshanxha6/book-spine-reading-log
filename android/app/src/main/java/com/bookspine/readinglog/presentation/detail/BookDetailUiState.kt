package com.bookspine.readinglog.presentation.detail

import com.bookspine.readinglog.domain.model.Note

data class BookDetailUiState(
    val title: String = "",
    val author: String = "",
    val totalPages: Int = 0,
    val pagesRead: Int = 0,
    val finished: Boolean = false,
    val progress: Float = 0f,
    val spineVariant: Int = 0,
    val colorIndex: Int = 0,
    val notes: List<Note> = emptyList(),
    val noteDialogShown: Boolean = false,
    val noteText: String = "",
    val noteIsQuote: Boolean = true,
    val notePage: Int = 1,
    val noteInvalid: Boolean = false
)
