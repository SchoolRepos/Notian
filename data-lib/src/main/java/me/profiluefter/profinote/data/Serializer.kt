package me.profiluefter.profinote.data

import me.profiluefter.profinote.data.entities.TodoList

interface Serializer {
    suspend fun deserialize(data: ByteArray): TodoList
    suspend fun serialize(notes: TodoList): ByteArray
}
