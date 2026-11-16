package com.bookspine.readinglog.domain.repository

import com.bookspine.readinglog.domain.model.Book
import com.bookspine.readinglog.domain.model.Note
import com.bookspine.readinglog.domain.model.ReadingEntry
import com.bookspine.readinglog.domain.model.Streak
import kotlinx.coroutines.flow.StateFlow

interface BookRepository {
    val books: StateFlow<List<Book>>
    val entries: StateFlow<List<ReadingEntry>>
    val notes: StateFlow<List<Note>>
    val streak: StateFlow<Streak>

    fun persistBook(book: Book)
    fun persistEntry(entry: ReadingEntry)
    fun persistNote(note: Note)
    fun persistStreak(value: Streak)
    fun clearEverything()
}
