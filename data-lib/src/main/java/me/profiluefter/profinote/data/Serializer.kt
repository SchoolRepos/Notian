package me.profiluefter.profinote.data

import me.profiluefter.profinote.data.entities.Note

interface Serializer {
    suspend fun load(): List<Note>
    suspend fun save(notes: List<Note>)
}
