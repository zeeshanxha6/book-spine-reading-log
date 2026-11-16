package com.bookspine.readinglog.core.di

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.bookspine.readinglog.presentation.app.AppViewModel
import com.bookspine.readinglog.presentation.detail.BookDetailViewModel
import com.bookspine.readinglog.presentation.game.GameViewModel
import com.bookspine.readinglog.presentation.gameover.GameOverViewModel
import com.bookspine.readinglog.presentation.menu.MenuViewModel
import com.bookspine.readinglog.presentation.settings.SettingsViewModel
import com.bookspine.readinglog.presentation.splash.SplashViewModel
import com.bookspine.readinglog.presentation.stats.StatsViewModel

object AppViewModelFactory {

    val instance: ViewModelProvider.Factory = viewModelFactory {
        initializer {
            AppViewModel(ServiceLocator.settingsRepository)
        }
        initializer {
            SplashViewModel()
        }
        initializer {
            MenuViewModel(
                bookRepository = ServiceLocator.bookRepository,
                settingsRepository = ServiceLocator.settingsRepository,
                calculateStats = ServiceLocator.calculateStatsUseCase
            )
        }
        initializer {
            GameViewModel(
                bookRepository = ServiceLocator.bookRepository,
                settingsRepository = ServiceLocator.settingsRepository,
                logPages = ServiceLocator.logPagesUseCase
            )
        }
        initializer {
            GameOverViewModel(
                bookRepository = ServiceLocator.bookRepository,
                calculateStats = ServiceLocator.calculateStatsUseCase
            )
        }
        initializer {
            BookDetailViewModel(ServiceLocator.bookRepository)
        }
        initializer {
            StatsViewModel(
                bookRepository = ServiceLocator.bookRepository,
                settingsRepository = ServiceLocator.settingsRepository,
                calculateStats = ServiceLocator.calculateStatsUseCase
            )
        }
        initializer {
            SettingsViewModel(
                settingsRepository = ServiceLocator.settingsRepository,
                bookRepository = ServiceLocator.bookRepository
            )
        }
    }
}
