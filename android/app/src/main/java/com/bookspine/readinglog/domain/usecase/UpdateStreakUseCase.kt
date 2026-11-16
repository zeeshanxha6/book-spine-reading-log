package com.bookspine.readinglog.domain.usecase

import com.bookspine.readinglog.domain.model.Streak

class UpdateStreakUseCase {

    operator fun invoke(current: Streak, epochDay: Long): Streak = when {
        current.days <= 0 -> Streak(days = 1, lastEntryDay = epochDay)
        epochDay == current.lastEntryDay -> current
        epochDay == current.lastEntryDay + 1 -> Streak(current.days + 1, epochDay)
        else -> Streak(days = 1, lastEntryDay = epochDay)
    }
}
