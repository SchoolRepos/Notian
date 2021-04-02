package me.profiluefter.profinote.data.serializer

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import me.profiluefter.profinote.data.Serializer
import me.profiluefter.profinote.data.entities.Note

private val gson = Gson()

class GSONSerializer : Serializer {
    override suspend fun load(data: ByteArray): List<Note> {
        val json = data.toString(Charsets.UTF_8)

        val listType = object : TypeToken<List<Note>>() {}.type
        return gson.fromJson(json, listType)
    }

    override suspend fun save(notes: List<Note>): ByteArray {
        val json = gson.toJson(notes)
        return json.toByteArray(Charsets.UTF_8)
    }
}