package com.bookspine.readinglog.core.config

object GameConfig {
    const val LOADER_DURATION_MS = 8000L
    const val SHELF_COLS = 6
    const val SHELF_ROWS = 2
    const val DEFAULT_GOAL = 24
    const val GOAL_YEAR = 2026
    const val MIN_GOAL = 1
    const val MAX_GOAL = 200
    const val DEFAULT_STEP = 10
    const val MAX_PAGES_PER_ENTRY = 500
    const val MIN_BOOK_PAGES = 1
    const val MAX_BOOK_PAGES = 5000
    const val SAVE_DELAY_MS = 420L
    const val SPINE_VARIANTS = 3
    const val SPINE_COLOR_COUNT = 6
    const val MAX_PICKER_BOOKS = 3
    val QUICK_STEPS = listOf(5, 10, 25, 50)
}
