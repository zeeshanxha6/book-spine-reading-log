package com.bookspine.readinglog.domain.usecase

import com.bookspine.readinglog.domain.model.BookStatus
import com.bookspine.readinglog.domain.model.ReadingStats
import com.bookspine.readinglog.domain.repository.BookRepository
import com.bookspine.readinglog.domain.repository.SettingsRepository

class CalculateStatsUseCase(
    private val bookRepository: BookRepository,
    private val settingsRepository: SettingsRepository
) {

    operator fun invoke(currentMonthIndex: Int): ReadingStats {
        val entries = bookRepository.entries.value
        val perMonth = MutableList(MONTHS) { 0 }
        entries.forEach { entry ->
            val index = entry.monthIndex.coerceIn(0, MONTHS - 1)
            perMonth[index] = perMonth[index] + entry.pages
        }
        val totalPages = perMonth.sum()
        val active = perMonth.count { it > 0 }.coerceAtLeast(1)
        val finished = bookRepository.books.value.count { it.status == BookStatus.FINISHED }
        val target = settingsRepository.goalTarget.value
        val monthsGone = (currentMonthIndex + 1).coerceIn(1, MONTHS)
        val expected = target * monthsGone / MONTHS

        return ReadingStats(
            totalPages = totalPages,
            finishedBooks = finished,
            averagePerMonth = totalPages / active,
            pagesPerMonth = perMonth,
            bestMonthIndex = perMonth.indexOf(perMonth.maxOrNull() ?: 0),
            weakestMonthIndex = perMonth.indexOf(perMonth.filter { it > 0 }.minOrNull() ?: 0),
            streakDays = bookRepository.streak.value.days,
            goalTarget = target,
            booksAheadOfPace = finished - expected
        )
    }

    private companion object {
        const val MONTHS = 12
    }
}
