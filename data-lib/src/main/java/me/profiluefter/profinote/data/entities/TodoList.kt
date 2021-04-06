package me.profiluefter.profinote.data.entities

import java.time.LocalDateTime

data class TodoList(
    val localID: Int,
    val name: String,
    val notes: List<Note>
) {
    fun toRaw(remoteID: String): RawTodoList = RawTodoList(
        localID,
        remoteID,
        "0",
        name,
        apiPattern.format(LocalDateTime.now())
    )

    fun sorted(): TodoList = TodoList(
        localID,
        name,
        notes.sorted()
    )

    companion object {
        fun default(id: Int): TodoList = TodoList(id, "Default", emptyList())

        fun from(list: RawTodoList, todos: List<RawTodo>): TodoList = TodoList(
            list.id.toInt(),
            list.name,
            todos.map { Note.from(it) }
        )
    }
}