package com.bookspine.readinglog.data.repository

import com.bookspine.readinglog.data.local.ReadingPreferences
import com.bookspine.readinglog.data.sample.SampleData
import com.bookspine.readinglog.domain.model.Book
import com.bookspine.readinglog.domain.model.Note
import com.bookspine.readinglog.domain.model.ReadingEntry
import com.bookspine.readinglog.domain.model.Streak
import com.bookspine.readinglog.domain.repository.BookRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate

class BookRepositoryImpl(
    private val preferences: ReadingPreferences
) : BookRepository {

    private val booksState = MutableStateFlow<List<Book>>(emptyList())
    private val entriesState = MutableStateFlow<List<ReadingEntry>>(emptyList())
    private val notesState = MutableStateFlow<List<Note>>(emptyList())
    private val streakState = MutableStateFlow(Streak(0, 0L))

    override val books: StateFlow<List<Book>> = booksState.asStateFlow()
    override val entries: StateFlow<List<ReadingEntry>> = entriesState.asStateFlow()
    override val notes: StateFlow<List<Note>> = notesState.asStateFlow()
    override val streak: StateFlow<Streak> = streakState.asStateFlow()

    init {
        if (!preferences.seeded) {
            seed()
        }
        reload()
    }

    override fun persistBook(book: Book) {
        val merged = booksState.value.toMutableList()
        val index = merged.indexOfFirst { it.id == book.id }
        if (index >= 0) merged[index] = book else merged.add(book)
        booksState.value = merged
        preferences.books = merged
    }

    override fun persistEntry(entry: ReadingEntry) {
        val merged = entriesState.value + entry
        entriesState.value = merged
        preferences.entries = merged
    }

    override fun persistNote(note: Note) {
        val merged = notesState.value + note
        notesState.value = merged
        preferences.notes = merged
    }

    override fun persistStreak(value: Streak) {
        streakState.value = value
        preferences.streak = value
    }

    override fun clearEverything() {
        preferences.wipe()
        seed()
        reload()
    }

    private fun reload() {
        booksState.value = preferences.books
        entriesState.value = preferences.entries
        notesState.value = preferences.notes
        streakState.value = preferences.streak
    }

    private fun seed() {
        val today = LocalDate.now().toEpochDay()
        preferences.books = SampleData.books(today)
        preferences.entries = SampleData.entries(today)
        preferences.notes = SampleData.notes()
        preferences.streak = SampleData.streak(today)
        preferences.seeded = true
    }
}
