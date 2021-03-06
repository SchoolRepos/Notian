package me.profiluefter.profinote.data

import android.util.Log
import me.profiluefter.profinote.models.Note
import java.net.URLDecoder
import java.net.URLEncoder
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "CSVDataLoader"

@Singleton
class CSVSerializer @Inject constructor(private val storage: Storage) : Serializer {
    override suspend fun load(): List<Note> {
        val data = storage.get() ?: return emptyList()
        return data.toString(Charsets.UTF_8).lines()
            .filterNot { it.isBlank() }
            .map { fromCSV(it) }.toList().also {
                Log.i(TAG, "Successfully loaded ${it.size} notes!")
            }
    }

    override suspend fun save(notes: List<Note>) {
        Log.i(TAG, "Serializing ${notes.size} notes.")
        val data = notes.joinToString("\n") { toCSV(it) }.toByteArray(Charsets.UTF_8)
        storage.store(data)
    }

    private fun fromCSV(line: String): Note {
        val (title, minute, hour, day, month, year, description) = line.split(";")
        Log.v(TAG, "Parsed note \"$title\".")
        return Note(
            URLDecoder.decode(title, "UTF-8"),
            minute.toInt(),
            hour.toInt(),
            day.toInt(),
            month.toInt(),
            year.toInt(),
            URLDecoder.decode(description, "UTF-8")
        )
    }

    private fun toCSV(note: Note): String = note.run {
        Log.v(TAG, "Serializing note \"$title\".")
        fun encode(string: String) = URLEncoder.encode(string, "UTF-8")
        "${encode(title)};$minute;$hour;$day;$month;$year;${encode(description)}"
    }
}

// Helper for object destructuring in CSVDataLoader#fromCSV(String)
private operator fun <T> List<T>.component6(): T = get(5)
private operator fun <T> List<T>.component7(): T = get(6)
