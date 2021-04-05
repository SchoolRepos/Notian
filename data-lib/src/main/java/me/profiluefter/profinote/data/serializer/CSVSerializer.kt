package me.profiluefter.profinote.data.serializer

import android.util.Log
import me.profiluefter.profinote.data.Serializer
import me.profiluefter.profinote.data.entities.Note
import me.profiluefter.profinote.data.entities.TodoList
import java.net.URLDecoder
import java.net.URLEncoder
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "CSVDataLoader"

@Deprecated("Replaced by Room")
@Singleton
class CSVSerializer @Inject constructor() : Serializer {
    override suspend fun deserialize(data: ByteArray): TodoList {
        return TodoList("CSV", data.toString(Charsets.UTF_8).lines()
            .filterNot { it.isBlank() }
            .map { fromCSV(it) }.toList().also {
                Log.i(TAG, "Successfully loaded ${it.size} notes!")
            })
    }

    override suspend fun serialize(list: TodoList): ByteArray {
        Log.i(TAG, "Serializing ${list.notes.size} notes.")
        return list.notes.joinToString("\n") { toCSV(it) }.toByteArray(Charsets.UTF_8)
    }

    private fun fromCSV(line: String): Note {
        val (title, done, minute, hour, day, month, year, description) = line.split(";")
        Log.v(TAG, "Parsed note \"$title\".")
        return Note(
            null,
            URLDecoder.decode(title, "UTF-8"),
            done.toBoolean(),
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
        "${encode(title)};$done;$minute;$hour;$day;$month;$year;${encode(description)}"
    }
}

// Helper for object destructuring in CSVDataLoader#fromCSV(String)
private operator fun <T> List<T>.component6(): T = get(5)
private operator fun <T> List<T>.component7(): T = get(6)
private operator fun <T> List<T>.component8(): T = get(7)