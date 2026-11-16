package com.bookspine.readinglog.core.navigation

sealed interface Screen {
    data object Splash : Screen
    data object Menu : Screen
    data object Game : Screen
    data class GameOver(
        val pagesLogged: Int,
        val spineEarned: Boolean,
        val bookId: String
    ) : Screen
    data class BookDetail(val bookId: String) : Screen
    data object Stats : Screen
    data object Settings : Screen
}
