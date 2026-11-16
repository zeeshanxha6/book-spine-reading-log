package com.bookspine.readinglog.domain.model

data class ReadingStats(
    val totalPages: Int,
    val finishedBooks: Int,
    val averagePerMonth: Int,
    val pagesPerMonth: List<Int>,
    val bestMonthIndex: Int,
    val weakestMonthIndex: Int,
    val streakDays: Int,
    val goalTarget: Int,
    val booksAheadOfPace: Int
) {
    val goalProgress: Float
        get() = if (goalTarget <= 0) 0f else (finishedBooks.toFloat() / goalTarget).coerceIn(0f, 1f)
}
