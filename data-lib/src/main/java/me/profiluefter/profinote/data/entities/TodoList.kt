package me.profiluefter.profinote.data.entities

import me.profiluefter.profinote.data.synchronization.APIObjects

data class TodoList(
    val name: String,
    val notes: List<Note>
) {
    fun toAPI(): APIObjects.TodoList = APIObjects.TodoList(
        name,
        ""
    )

    companion object {
        fun default(): TodoList = TodoList("Default", emptyList())

        fun from(list: APIObjects.TodoList, todos: List<APIObjects.Todo>): TodoList = TodoList(
            list.name,
            todos.map { Note.from(it) }
        )
    }
}