package me.profiluefter.profinote.data

import me.profiluefter.profinote.data.entities.Note

interface Serializer {
    suspend fun load(data: ByteArray): List<Note>
    suspend fun save(notes: List<Note>): ByteArray
}
