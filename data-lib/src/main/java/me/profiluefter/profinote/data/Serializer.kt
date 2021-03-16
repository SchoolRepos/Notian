package me.profiluefter.profinote.data

interface Serializer {
    suspend fun load(): List<Note>
    suspend fun save(notes: List<Note>)
}
