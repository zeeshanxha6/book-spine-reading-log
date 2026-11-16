package com.bookspine.readinglog.data.local

import android.content.Context
import android.content.SharedPreferences
import com.bookspine.readinglog.core.config.GameConfig
import com.bookspine.readinglog.domain.model.Book
import com.bookspine.readinglog.domain.model.BookStatus
import com.bookspine.readinglog.domain.model.Note
import com.bookspine.readinglog.domain.model.ReadingEntry
import com.bookspine.readinglog.domain.model.Streak
import com.bookspine.readinglog.domain.model.ThemeMode

class ReadingPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(STORE, Context.MODE_PRIVATE)

    var seeded: Boolean
        get() = prefs.getBoolean(KEY_SEEDED, false)
        set(value) = prefs.edit().putBoolean(KEY_SEEDED, value).apply()

    var goalTarget: Int
        get() = prefs.getInt(KEY_GOAL, GameConfig.DEFAULT_GOAL)
        set(value) = prefs.edit().putInt(KEY_GOAL, value).apply()

    var hapticsEnabled: Boolean
        get() = prefs.getBoolean(KEY_HAPTICS, true)
        set(value) = prefs.edit().putBoolean(KEY_HAPTICS, value).apply()

    var themeMode: ThemeMode
        get() = runCatching { ThemeMode.valueOf(prefs.getString(KEY_THEME, "").orEmpty()) }
            .getOrDefault(ThemeMode.FOREST)
        set(value) = prefs.edit().putString(KEY_THEME, value.name).apply()

    var streak: Streak
        get() = Streak(
            days = prefs.getInt(KEY_STREAK, 0),
            lastEntryDay = prefs.getLong(KEY_LAST_DAY, 0L)
        )
        set(value) = prefs.edit()
            .putInt(KEY_STREAK, value.days)
            .putLong(KEY_LAST_DAY, value.lastEntryDay)
            .apply()

    var books: List<Book>
        get() = decode(KEY_BOOKS) { parts ->
            if (parts.size < 10) null else Book(
                id = parts[0],
                title = parts[1],
                author = parts[2],
                totalPages = parts[3].toIntOrNull() ?: 0,
                pagesRead = parts[4].toIntOrNull() ?: 0,
                status = runCatching { BookStatus.valueOf(parts[5]) }
                    .getOrDefault(BookStatus.READING),
                spineVariant = parts[6].toIntOrNull() ?: 0,
                colorIndex = parts[7].toIntOrNull() ?: 0,
                startDay = parts[8].toLongOrNull() ?: 0L,
                finishDay = parts[9].toLongOrNull() ?: 0L
            )
        }
        set(value) = encode(KEY_BOOKS, value) { book ->
            listOf(
                book.id,
                clean(book.title),
                clean(book.author),
                book.totalPages.toString(),
                book.pagesRead.toString(),
                book.status.name,
                book.spineVariant.toString(),
                book.colorIndex.toString(),
                book.startDay.toString(),
                book.finishDay.toString()
            )
        }

    var entries: List<ReadingEntry>
        get() = decode(KEY_ENTRIES) { parts ->
            if (parts.size < 4) null else ReadingEntry(
                bookId = parts[0],
                epochDay = parts[1].toLongOrNull() ?: 0L,
                pages = parts[2].toIntOrNull() ?: 0,
                monthIndex = parts[3].toIntOrNull() ?: 0
            )
        }
        set(value) = encode(KEY_ENTRIES, value) { entry ->
            listOf(
                entry.bookId,
                entry.epochDay.toString(),
                entry.pages.toString(),
                entry.monthIndex.toString()
            )
        }

    var notes: List<Note>
        get() = decode(KEY_NOTES) { parts ->
            if (parts.size < 5) null else Note(
                id = parts[0],
                bookId = parts[1],
                isQuote = parts[2] == FLAG_ON,
                page = parts[3].toIntOrNull() ?: 0,
                text = parts[4]
            )
        }
        set(value) = encode(KEY_NOTES, value) { note ->
            listOf(
                note.id,
                note.bookId,
                if (note.isQuote) FLAG_ON else FLAG_OFF,
                note.page.toString(),
                clean(note.text)
            )
        }

    fun wipe() {
        prefs.edit().clear().apply()
    }

    private fun <T> decode(key: String, mapper: (List<String>) -> T?): List<T> {
        val raw = prefs.getString(key, "").orEmpty()
        if (raw.isEmpty()) return emptyList()
        return raw.split(RECORD).mapNotNull { record ->
            if (record.isBlank()) null else mapper(record.split(FIELD))
        }
    }

    private fun <T> encode(key: String, items: List<T>, mapper: (T) -> List<String>) {
        val raw = items.joinToString(RECORD) { mapper(it).joinToString(FIELD) }
        prefs.edit().putString(key, raw).apply()
    }

    private fun clean(value: String): String =
        value.replace(FIELD, " ").replace(RECORD, " ").trim()

    private companion object {
        const val STORE = "book_spine_reading_log"
        const val KEY_BOOKS = "books"
        const val KEY_ENTRIES = "entries"
        const val KEY_NOTES = "notes"
        const val KEY_GOAL = "goal_target"
        const val KEY_STREAK = "streak_days"
        const val KEY_LAST_DAY = "last_entry_day"
        const val KEY_THEME = "theme_mode"
        const val KEY_HAPTICS = "haptics_enabled"
        const val KEY_SEEDED = "seeded"
        const val FIELD = "|"
        const val RECORD = "\n"
        const val FLAG_ON = "1"
        const val FLAG_OFF = "0"
    }
}
