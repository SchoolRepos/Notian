package me.profiluefter.profinote.data.serializer

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import me.profiluefter.profinote.data.Serializer
import me.profiluefter.profinote.data.entities.Note
import me.profiluefter.profinote.data.entities.TodoList
import javax.inject.Inject

private val gson = Gson()

class GSONSerializer @Inject constructor() : Serializer {
    override suspend fun deserialize(data: ByteArray): TodoList {
        val json = data.toString(Charsets.UTF_8)

        val listType = object : TypeToken<List<Note>>() {}.type
        return gson.fromJson(json, listType)
    }

    override suspend fun serialize(notes: TodoList): ByteArray {
        val json = gson.toJson(notes)
        return json.toByteArray(Charsets.UTF_8)
    }
}