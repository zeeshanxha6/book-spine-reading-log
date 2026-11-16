package com.bookspine.readinglog.core.di

import android.content.Context
import com.bookspine.readinglog.data.local.ReadingPreferences
import com.bookspine.readinglog.data.repository.BookRepositoryImpl
import com.bookspine.readinglog.data.repository.SettingsRepositoryImpl
import com.bookspine.readinglog.domain.repository.BookRepository
import com.bookspine.readinglog.domain.repository.SettingsRepository
import com.bookspine.readinglog.domain.usecase.CalculateStatsUseCase
import com.bookspine.readinglog.domain.usecase.LogPagesUseCase
import com.bookspine.readinglog.domain.usecase.UpdateStreakUseCase

object ServiceLocator {

    private var books: BookRepository? = null
    private var settings: SettingsRepository? = null
    private var logPages: LogPagesUseCase? = null
    private var calculateStats: CalculateStatsUseCase? = null

    val bookRepository: BookRepository
        get() = requireNotNull(books)

    val settingsRepository: SettingsRepository
        get() = requireNotNull(settings)

    val logPagesUseCase: LogPagesUseCase
        get() = requireNotNull(logPages)

    val calculateStatsUseCase: CalculateStatsUseCase
        get() = requireNotNull(calculateStats)

    fun init(context: Context) {
        if (books != null) return
        val preferences = ReadingPreferences(context)
        val bookRepo = BookRepositoryImpl(preferences)
        val settingsRepo = SettingsRepositoryImpl(preferences)
        books = bookRepo
        settings = settingsRepo
        logPages = LogPagesUseCase(bookRepo, UpdateStreakUseCase())
        calculateStats = CalculateStatsUseCase(bookRepo, settingsRepo)
    }
}
