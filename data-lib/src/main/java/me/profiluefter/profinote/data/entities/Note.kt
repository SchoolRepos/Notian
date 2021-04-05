package me.profiluefter.profinote.data.entities

import me.profiluefter.profinote.data.synchronization.APIObjects
import java.io.Serializable
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoField
import java.util.*
import java.util.Calendar.getInstance

val apiPattern = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

data class Note(
    val id: Int?,
    val title: String,
    var done: Boolean, //TODO: Does not API sync! Make immutable!
    val minute: Int,
    val hour: Int,
    val day: Int,
    val month: Int,
    val year: Int,
    val description: String
) : Serializable, Comparable<Note> {
    override fun compareTo(other: Note): Int {
        return this.due.compareTo(other.due)
    }

    fun toAPI(todoListId: Int): APIObjects.Todo = APIObjects.Todo(
        todoListId.toString(),
        title,
        description,
        apiPattern.format(LocalDateTime.of(year, month, day, hour, minute)),
        if (done) "DONE" else "TODO",
        "",
        null,
        null
    )

    companion object {
        fun from(api: APIObjects.Todo): Note {
            val dueDate = apiPattern.parse(api.dueDate)

            return Note(
                api.id?.toInt(),
                api.title,
                when(api.state) {
                    "DONE" -> true
                    "TODO" -> false
                    else -> false
                },
                dueDate.get(ChronoField.MINUTE_OF_HOUR),
                dueDate.get(ChronoField.HOUR_OF_DAY),
                dueDate.get(ChronoField.DAY_OF_MONTH),
                dueDate.get(ChronoField.MONTH_OF_YEAR),
                dueDate.get(ChronoField.YEAR),
                api.description
            )
        }
    }
}

val Note.isOverdue: Boolean
    get() = !done && getInstance().after(due)

private fun pad(value: Any) = value.toString().padStart(2, '0')

val Note.date: String
    get() = formatDate(day, month, year)

fun formatDate(day: Int, month: Int, year: Int) = "${pad(day)}.${pad(month)}.${year}"

val Note.time: String
    get() = formatTime(hour, minute)

fun formatTime(hour: Int, minute: Int) = "${pad(hour)}:${pad(minute)}"

val Note.due: Calendar
    get() {
        val calendar = getInstance()
        calendar.clear()
        calendar.set(year, month - 1, day, hour, minute)
        return calendar
    }