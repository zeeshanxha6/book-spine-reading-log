package com.bookspine.readinglog.domain.usecase

import com.bookspine.readinglog.core.config.GameConfig
import com.bookspine.readinglog.domain.model.Book
import com.bookspine.readinglog.domain.model.BookStatus
import com.bookspine.readinglog.domain.model.LogPagesResult
import com.bookspine.readinglog.domain.model.ReadingEntry
import com.bookspine.readinglog.domain.repository.BookRepository
import kotlin.math.abs

class LogPagesUseCase(
    private val repository: BookRepository,
    private val updateStreak: UpdateStreakUseCase
) {

    operator fun invoke(
        bookId: String,
        pages: Int,
        epochDay: Long,
        monthIndex: Int
    ): LogPagesResult? {
        val book = repository.books.value.firstOrNull { it.id == bookId } ?: return null
        val applied = pages.coerceIn(0, book.remainingPages)
        if (applied <= 0) return null

        val readTotal = (book.pagesRead + applied).coerceAtMost(book.totalPages)
        val finished = readTotal >= book.totalPages
        val updated = book.copy(
            pagesRead = readTotal,
            status = if (finished) BookStatus.FINISHED else BookStatus.READING,
            spineVariant = if (finished) variantFor(book) else book.spineVariant,
            colorIndex = if (finished) colorFor(book) else book.colorIndex,
            finishDay = if (finished) epochDay else book.finishDay
        )
        repository.persistBook(updated)
        repository.persistEntry(
            ReadingEntry(
                bookId = bookId,
                epochDay = epochDay,
                pages = applied,
                monthIndex = monthIndex
            )
        )
        val streak = updateStreak(repository.streak.value, epochDay)
        repository.persistStreak(streak)

        return LogPagesResult(
            bookId = bookId,
            pagesLogged = applied,
            spineEarned = finished,
            streakDays = streak.days
        )
    }

    private fun variantFor(book: Book): Int =
        abs(book.id.hashCode()) % GameConfig.SPINE_VARIANTS

    private fun colorFor(book: Book): Int =
        abs(book.id.hashCode() / 7) % GameConfig.SPINE_COLOR_COUNT
}
