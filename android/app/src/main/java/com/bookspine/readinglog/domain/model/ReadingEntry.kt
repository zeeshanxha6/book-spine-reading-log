package com.bookspine.readinglog.domain.model

data class ReadingEntry(
    val bookId: String,
    val epochDay: Long,
    val pages: Int,
    val monthIndex: Int
)
