package com.bookspine.readinglog.domain.model

data class Book(
    val id: String,
    val title: String,
    val author: String,
    val totalPages: Int,
    val pagesRead: Int,
    val status: BookStatus,
    val spineVariant: Int,
    val colorIndex: Int,
    val startDay: Long,
    val finishDay: Long
) {
    val progress: Float
        get() = if (totalPages <= 0) 0f else (pagesRead.toFloat() / totalPages).coerceIn(0f, 1f)

    val remainingPages: Int
        get() = (totalPages - pagesRead).coerceAtLeast(0)
}
