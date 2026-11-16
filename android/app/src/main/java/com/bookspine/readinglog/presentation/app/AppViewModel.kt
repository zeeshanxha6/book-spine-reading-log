package com.bookspine.readinglog.presentation.app

import androidx.lifecycle.ViewModel
import com.bookspine.readinglog.core.navigation.Screen
import com.bookspine.readinglog.domain.model.ThemeMode
import com.bookspine.readinglog.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppViewModel(
    settingsRepository: SettingsRepository
) : ViewModel() {

    private val currentScreen = MutableStateFlow<Screen>(Screen.Splash)
    private val stack = MutableStateFlow<List<Screen>>(emptyList())

    val screen: StateFlow<Screen> = currentScreen.asStateFlow()
    val backStack: StateFlow<List<Screen>> = stack.asStateFlow()
    val themeMode: StateFlow<ThemeMode> = settingsRepository.themeMode

    fun onSplashFinished() {
        stack.value = emptyList()
        currentScreen.value = Screen.Menu
    }

    fun navigate(target: Screen) {
        stack.value = stack.value + currentScreen.value
        currentScreen.value = target
    }

    fun openGame() {
        stack.value = listOf(Screen.Menu)
        currentScreen.value = Screen.Game
    }

    fun openResult(pagesLogged: Int, spineEarned: Boolean, bookId: String) {
        stack.value = listOf(Screen.Menu)
        currentScreen.value = Screen.GameOver(pagesLogged, spineEarned, bookId)
    }

    fun openMenu() {
        stack.value = emptyList()
        currentScreen.value = Screen.Menu
    }

    fun back() {
        val previous = stack.value.lastOrNull() ?: return
        stack.value = stack.value.dropLast(1)
        currentScreen.value = previous
    }
}
