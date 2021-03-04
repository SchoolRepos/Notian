package me.profiluefter.profinote.data

import android.app.Application
import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.profiluefter.profinote.models.Note
import java.io.FileNotFoundException
import java.net.URLDecoder
import java.net.URLEncoder

private const val TAG = "CSVDataLoader"

class CSVDataLoader(private val fileName: String) : DataLoader {
    override suspend fun load(app: Application): List<Note> = withContext(Dispatchers.IO) {
        try {
            @Suppress("BlockingMethodInNonBlockingContext")
            app.openFileInput(fileName).bufferedReader().useLines { lines ->
                lines.map { fromCSV(it) }.toList()
            }
        } catch (e: FileNotFoundException) {
            emptyList()
        }
    }

    override suspend fun save(notes: List<Note>, app: Application) = withContext(Dispatchers.IO) {
        Log.i(TAG,"Saving ${notes.size} notes.")

        @Suppress("BlockingMethodInNonBlockingContext")
        app.openFileOutput(fileName, Context.MODE_PRIVATE).bufferedWriter().use { writer ->
            notes.map { toCSV(it) }.forEach { writer.appendLine(it) }
        }
    }

    private fun fromCSV(line: String): Note {
        var (title, minute, hour, day, month, year, description) = line.split(";")
        title = URLDecoder.decode(title, "UTF-8")
        description = URLDecoder.decode(description, "UTF-8")
        Log.v(TAG, "Parsed note \"$title\".")
        return Note(title, minute.toInt(), hour.toInt(), day.toInt(), month.toInt(), year.toInt(), description)
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
