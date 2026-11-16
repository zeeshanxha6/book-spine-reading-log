package com.bookspine.readinglog.domain.model

data class LogPagesResult(
    val bookId: String,
    val pagesLogged: Int,
    val spineEarned: Boolean,
    val streakDays: Int
)
