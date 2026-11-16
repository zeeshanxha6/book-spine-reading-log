package com.bookspine.readinglog.data.sample

import com.bookspine.readinglog.domain.model.Book
import com.bookspine.readinglog.domain.model.BookStatus
import com.bookspine.readinglog.domain.model.Note
import com.bookspine.readinglog.domain.model.ReadingEntry
import com.bookspine.readinglog.domain.model.Streak

object SampleData {

    private val FINISHED = listOf(
        Triple("The Night Garden", "Mira Halloway", 312),
        Triple("Salt and Compass", "Tobias Renn", 268),
        Triple("Letters from Verdant House", "Elena Sarkis", 344),
        Triple("The Quiet Machinist", "Owen Deverell", 226),
        Triple("Orchard of Small Hours", "Nadia Brecht", 290),
        Triple("A Map of Shallow Rivers", "Imogen Clyde", 358),
        Triple("The Lantern Keeper", "Rafael Ortiz", 274),
        Triple("Winter Grammar", "Hester Vale", 198),
        Triple("Paper Birds", "Kenji Morrow", 240),
        Triple("The Glasshouse Year", "Lucia Bannon", 320),
        Triple("Field Notes on Silence", "Aron Teague", 186),
        Triple("The Long Green Mile", "Perrin Ashby", 402)
    )

    private val READING = listOf(
        Quad("The Cartographer's Daughter", "Sylvie Marchand", 180, 156),
        Quad("Rust and Rosemary", "Caleb Fenwick", 310, 42),
        Quad("Nine Lamps", "Yara Osei", 224, 88)
    )

    val MONTHLY_PAGES = listOf(212, 268, 496, 402, 331, 288, 214, 176, 305, 357, 243, 126)

    fun books(today: Long): List<Book> {
        val finished = FINISHED.mapIndexed { index, item ->
            Book(
                id = "finished_$index",
                title = item.first,
                author = item.second,
                totalPages = item.third,
                pagesRead = item.third,
                status = BookStatus.FINISHED,
                spineVariant = index % 3,
                colorIndex = index % 6,
                startDay = today - (330L - index * 24L),
                finishDay = today - (300L - index * 24L)
            )
        }
        val reading = READING.mapIndexed { index, item ->
            Book(
                id = "reading_$index",
                title = item.title,
                author = item.author,
                totalPages = item.total,
                pagesRead = item.read,
                status = BookStatus.READING,
                spineVariant = (index + 1) % 3,
                colorIndex = (index + 2) % 6,
                startDay = today - (28L - index * 9L),
                finishDay = 0L
            )
        }
        return finished + reading
    }

    fun entries(today: Long): List<ReadingEntry> =
        MONTHLY_PAGES.mapIndexed { month, pages ->
            ReadingEntry(
                bookId = "finished_${month % FINISHED.size}",
                epochDay = today - (335L - month * 30L),
                pages = pages,
                monthIndex = month
            )
        }

    fun notes(): List<Note> = listOf(
        Note(
            id = "note_0",
            bookId = "reading_0",
            isQuote = true,
            page = 92,
            text = "She drew the coastline from memory and it was still true."
        ),
        Note(
            id = "note_1",
            bookId = "reading_0",
            isQuote = true,
            page = 118,
            text = "Every map is a promise that somewhere can be found again."
        ),
        Note(
            id = "note_2",
            bookId = "reading_0",
            isQuote = false,
            page = 140,
            text = "The survey chapters finally pay off here; keep the family tree handy."
        ),
        Note(
            id = "note_3",
            bookId = "reading_2",
            isQuote = true,
            page = 54,
            text = "Nine lamps, and not one of them lit for me."
        )
    )

    fun streak(today: Long): Streak = Streak(days = 9, lastEntryDay = today)

    private data class Quad(
        val title: String,
        val author: String,
        val total: Int,
        val read: Int
    )
}
