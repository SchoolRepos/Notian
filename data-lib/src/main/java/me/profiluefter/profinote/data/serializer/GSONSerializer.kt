package me.profiluefter.profinote.data.serializer

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import me.profiluefter.profinote.data.entities.Note
import me.profiluefter.profinote.data.Serializer
import me.profiluefter.profinote.data.Storage
import javax.inject.Inject

private val gson = Gson()

class GSONSerializer @Inject constructor(private val storage: Storage) : Serializer {
    override suspend fun load(): List<Note> {
        val bytes = storage.get() ?: return emptyList()
        val json = bytes.toString(Charsets.UTF_8)

        val listType = object : TypeToken<List<Note>>() {}.type
        return gson.fromJson(json, listType)
    }

    override suspend fun save(notes: List<Note>) {
        val json = gson.toJson(notes)
        storage.store(json.toByteArray(Charsets.UTF_8))
    }
}