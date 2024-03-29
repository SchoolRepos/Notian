package me.profiluefter.profinote.data.entities

import android.util.Log
import com.google.gson.Gson
import java.io.Serializable
import java.time.LocalDateTime
import java.time.temporal.ChronoField.*
import java.util.*

data class Note(
    val localID: Int,
    val title: String,
    val done: Boolean,
    val minute: Int,
    val hour: Int,
    val day: Int,
    val month: Int,
    val year: Int,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val address: String,
) : Serializable, Comparable<Note> {
    constructor(calendar: Calendar = Calendar.getInstance()) : this(
        0,
        "",
        false,
        calendar.get(Calendar.MINUTE),
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.DAY_OF_MONTH),
        calendar.get(Calendar.MONTH) + 1,
        calendar.get(Calendar.YEAR),
        "",
        0.0,
        0.0,
        "",
    )

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
        Gson().toJson(
            AdditionalDataContainer(
                apiPattern.format(LocalDateTime.now()),
                AdditionalDataContainer.LocationInformation(latitude, longitude, address)
            )
        ),
    )

    companion object {
        fun from(api: RawTodo): Note = api.run {
            val due = apiPattern.parse(dueDate)
            val dataContainer = try {
                Gson().fromJson(additionalData, AdditionalDataContainer::class.java)
            } catch (e: Exception) {
                null
            }

            if (dataContainer?.locationInformation == null)
                Log.w("Note", "Additional data from note id=$localID doesn't contain location information")

            return Note(
                localID,
                title,
                when (state.toUpperCase(Locale.ROOT)) {
                    "TODO" -> false
                    "OPEN" -> false

                    "DONE" -> true
                    "FINISHED" -> true

                    else -> throw IllegalStateException("Unknown Todo state \"$state\"")
                },
                due.get(MINUTE_OF_HOUR),
                due.get(HOUR_OF_DAY),
                due.get(DAY_OF_MONTH),
                due.get(MONTH_OF_YEAR),
                due.get(YEAR),
                description,
                dataContainer?.locationInformation?.latitude ?: 0.0,
                dataContainer?.locationInformation?.longitude ?: 0.0,
                dataContainer?.locationInformation?.address ?: "",
            )
        }
    }
}

val Note.isOverdue: Boolean
    get() = !done && Calendar.getInstance().after(due)

private fun pad(value: Any) = value.toString().padStart(2, '0')

val Note.date: String
    get() = formatDate(day, month, year)

fun formatDate(day: Int, month: Int, year: Int) = "${pad(day)}.${pad(month)}.${year}"

val Note.time: String
    get() = formatTime(hour, minute)

fun formatTime(hour: Int, minute: Int) = "${pad(hour)}:${pad(minute)}"

val Note.due: Calendar
    get() {
        val calendar = Calendar.getInstance()
        calendar.clear()
        calendar.set(year, month - 1, day, hour, minute)
        return calendar
    }