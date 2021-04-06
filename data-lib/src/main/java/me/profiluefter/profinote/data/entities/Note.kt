package me.profiluefter.profinote.data.entities

import java.io.Serializable
import java.time.LocalDateTime
import java.time.temporal.ChronoField.*
import java.util.*
import java.util.Calendar.getInstance

data class Note(
    val localID: Int,
    val title: String,
    val done: Boolean,
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

    fun toRaw(remoteID: String, todoList: RawTodoList): RawTodo = RawTodo(
        localID,
        remoteID,
        "0",
        todoList.localID,
        todoList.id,
        title,
        description,
        apiPattern.format(LocalDateTime.of(year, month, day, hour, minute)),
        if (done) "DONE" else "TODO",
        apiPattern.format(LocalDateTime.now())
    )

    companion object {
        fun from(api: RawTodo): Note = api.run {
            val due = apiPattern.parse(dueDate)

            return Note(
                id.toInt(),
                title,
                when(state) {
                    "DONE" -> true
                    "TODO" -> false
                    else -> throw IllegalStateException("Unknown Todo state \"$state\"")
                },
                due.get(MINUTE_OF_HOUR),
                due.get(HOUR_OF_DAY),
                due.get(DAY_OF_MONTH),
                due.get(MONTH_OF_YEAR),
                due.get(YEAR),
                description
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