package com.bookspine.readinglog.domain.model

data class Note(
    val id: String,
    val bookId: String,
    val isQuote: Boolean,
    val page: Int,
    val text: String
)
