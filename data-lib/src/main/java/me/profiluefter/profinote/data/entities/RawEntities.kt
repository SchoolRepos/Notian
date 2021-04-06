package me.profiluefter.profinote.data.entities

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import me.profiluefter.profinote.data.LocalOnly
import java.time.format.DateTimeFormatter

@Entity
data class RawTodoList(
    @LocalOnly @PrimaryKey val localID: Int,
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
    @LocalOnly @PrimaryKey val localID: Int,
    val id: String,
    @Ignore val ownerId: String,
    @LocalOnly val localListID: Int,
    val todoListId: String,
    val title: String,
    val description: String,
    val dueDate: String,
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
}

val apiPattern: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")