package me.profiluefter.profinote.data

import me.profiluefter.profinote.data.entities.TodoList

@Deprecated("Replaced by Room")
interface Serializer {
    suspend fun deserialize(data: ByteArray): TodoList
    suspend fun serialize(notes: TodoList): ByteArray
}
