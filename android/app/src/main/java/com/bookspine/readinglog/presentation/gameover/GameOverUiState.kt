package com.bookspine.readinglog.presentation.gameover

import com.bookspine.readinglog.core.config.GameConfig

data class GameOverUiState(
    val bookTitle: String = "",
    val spineEarned: Boolean = false,
    val pagesLogged: Int = 0,
    val streakDays: Int = 0,
    val finishedCount: Int = 0,
    val goalTarget: Int = GameConfig.DEFAULT_GOAL,
    val spineVariant: Int = 0,
    val spineColorIndex: Int = 0
)
