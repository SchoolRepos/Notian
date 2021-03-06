package me.profiluefter.profinote.data

import me.profiluefter.profinote.models.Note

interface Serializer {
    suspend fun load(): List<Note>
    suspend fun save(notes: List<Note>)
}
