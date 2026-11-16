package com.bookspine.readinglog.presentation.app

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bookspine.readinglog.core.di.AppViewModelFactory
import com.bookspine.readinglog.core.navigation.Screen
import com.bookspine.readinglog.presentation.detail.BookDetailScreen
import com.bookspine.readinglog.presentation.game.GameScreen
import com.bookspine.readinglog.presentation.gameover.GameOverScreen
import com.bookspine.readinglog.presentation.menu.MenuScreen
import com.bookspine.readinglog.presentation.settings.SettingsScreen
import com.bookspine.readinglog.presentation.splash.SplashScreen
import com.bookspine.readinglog.presentation.stats.StatsScreen

@Composable
fun AppRoot(
    appViewModel: AppViewModel = viewModel(factory = AppViewModelFactory.instance)
) {
    val screen by appViewModel.screen.collectAsStateWithLifecycle()
    val stack by appViewModel.backStack.collectAsStateWithLifecycle()

    BackHandler(enabled = stack.isNotEmpty()) {
        appViewModel.back()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        AnimatedContent(
            targetState = screen,
            transitionSpec = {
                fadeIn(tween(260)) togetherWith fadeOut(tween(220))
            }
        ) { target ->
            when (target) {
                is Screen.Splash -> SplashScreen(
                    onFinished = appViewModel::onSplashFinished
                )

                is Screen.Menu -> MenuScreen(
                    onStart = appViewModel::openGame,
                    onOpenShelf = { bookId -> appViewModel.navigate(Screen.BookDetail(bookId)) },
                    onOpenStats = { appViewModel.navigate(Screen.Stats) },
                    onOpenSettings = { appViewModel.navigate(Screen.Settings) }
                )

                is Screen.Game -> GameScreen(
                    onBack = appViewModel::back,
                    onRoundFinished = appViewModel::openResult
                )

                is Screen.GameOver -> GameOverScreen(
                    pagesLogged = target.pagesLogged,
                    spineEarned = target.spineEarned,
                    bookId = target.bookId,
                    onLogAgain = appViewModel::openGame,
                    onMenu = appViewModel::openMenu
                )

                is Screen.BookDetail -> BookDetailScreen(
                    bookId = target.bookId,
                    onBack = appViewModel::back
                )

                is Screen.Stats -> StatsScreen(
                    onBack = appViewModel::back
                )

                is Screen.Settings -> SettingsScreen(
                    onBack = appViewModel::back
                )
            }
        }
    }
}
