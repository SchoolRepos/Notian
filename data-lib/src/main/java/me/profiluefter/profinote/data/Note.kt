package me.profiluefter.profinote.data

import java.io.Serializable
import java.util.*
import java.util.Calendar.getInstance

data class Note(
    val title: String,
    var done: Boolean,
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