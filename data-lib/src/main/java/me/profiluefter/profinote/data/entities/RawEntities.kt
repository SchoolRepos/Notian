package me.profiluefter.profinote.data.entities

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.Gson
import me.profiluefter.profinote.data.LocalOnly
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalQueries

@Entity
data class RawTodoList(
    @LocalOnly @PrimaryKey(autoGenerate = true) val localID: Int,
    /**
     * ID that the server assigns to each Entity. Set to "NEW" if the
     * server-assigned id is not known yet because the entity has not
     * been synchronized yet.
     */
    val id: String,
    @Ignore val ownerId: String,
    val name: String,
    /**
     * Used for conflict resolution when synchronizing.
     *
     * Contains either the datetime from the last modification formatted
     * using the [apiPattern] or "DELETE" if this entity is scheduled for
     * deletion.
     */
    val additionalData: String
) {
    // Room constructor
    constructor(
        localID: Int,
        id: String,
        name: String,
        additionalData: String
    ) : this(
        localID,
        id,
        "0",
        name,
        additionalData
    )
}

@Entity
data class RawTodo(
    @LocalOnly @PrimaryKey(autoGenerate = true) val localID: Int,
    /**
     * ID that the server assigns to each Entity. Set to "NEW" if the
     * server-assigned id is not known yet because the entity has not
     * been synchronized yet.
     */
    val id: String,
    @Ignore val ownerId: String,
    @LocalOnly val localListID: Int,
    val todoListId: String,
    val title: String,
    val description: String,
    val dueDate: String,
    /**
     * The current state of the task.
     *
     * Is set to either "TODO" if the task is outstanding or "DONE" if
     * the task is already marked as completed.
     */
    val state: String,
    /**
     * Used for conflict resolution when synchronizing.
     *
     * Contains either the datetime from the last modification formatted
     * using the [apiPattern] or "DELETE" if this entity is scheduled for
     * deletion.
     */
    val additionalData: String
) {
    // Room constructor
    constructor(
        localID: Int,
        id: String,
        localListID: Int,
        todoListId: String,
        title: String,
        description: String,
        dueDate: String,
        state: String,
        additionalData: String
    ) : this(
        localID,
        id,
        "0",
        localListID,
        todoListId,
        title,
        description,
        dueDate,
        state,
        additionalData
    )

    constructor(serverResponse: RawTodo, localListID: Int) : this(
        serverResponse.localID,
        serverResponse.id,
        serverResponse.ownerId,
        localListID,
        serverResponse.todoListId,
        serverResponse.title,
        serverResponse.description,
        serverResponse.dueDate,
        serverResponse.state,
        serverResponse.additionalData
    )
}

data class AdditionalDataContainer(
    val lastChanged: String,
    val locationInformation: LocationInformation?,
) {
    data class LocationInformation(
        val latitude: Double,
        val longitude: Double,
        val address: String,
    )
}

val apiPattern: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

fun RawTodoList.serverEquals(other: RawTodoList): Boolean =
    this.id == other.id && this.name == other.name && this.additionalData == other.additionalData

fun RawTodoList.localEquals(other: RawTodoList): Boolean =
    this.localID == other.localID && this.name == other.name && this.additionalData == other.additionalData

fun RawTodo.serverEquals(other: RawTodo): Boolean =
    this.id == other.id && this.todoListId == other.todoListId &&
            this.title == other.title && this.description == other.description &&
            this.dueDate == other.dueDate && this.state == other.state && this.additionalData == other.additionalData

fun RawTodo.localEquals(other: RawTodo): Boolean =
    this.localID == other.localID && this.localListID == other.localListID &&
            this.title == other.title && this.description == other.description &&
            this.dueDate == other.dueDate && this.state == other.state && this.additionalData == other.additionalData

fun RawTodoList.changedDate(): LocalDateTime = parseAdditionalData(additionalData)
fun RawTodo.changedDate(): LocalDateTime = parseAdditionalData(additionalData)

fun parseAdditionalData(additionalData: String): LocalDateTime {
    val lastChanged = try {
        Gson().fromJson(additionalData, AdditionalDataContainer::class.java).lastChanged
    } catch (e: Exception) {
        additionalData
    }

    return try {
        val accessor = apiPattern.parse(lastChanged)
        val date = accessor.query(TemporalQueries.localDate())
        val time = accessor.query(TemporalQueries.localTime())
        date.atTime(time)
    } catch (e: Exception) {
        LocalDateTime.MIN
    }
}