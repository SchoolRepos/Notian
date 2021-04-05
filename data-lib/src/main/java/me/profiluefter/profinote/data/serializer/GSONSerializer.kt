package me.profiluefter.profinote.data.serializer

import com.google.gson.Gson
import me.profiluefter.profinote.data.Serializer
import me.profiluefter.profinote.data.entities.TodoList
import javax.inject.Inject

private val gson = Gson()

@Deprecated("Replaced by Room")
class GSONSerializer @Inject constructor() : Serializer {
    override suspend fun deserialize(data: ByteArray): TodoList {
        val json = data.toString(Charsets.UTF_8)

        return gson.fromJson(json, TodoList::class.java)
    }

    override suspend fun serialize(notes: TodoList): ByteArray {
        val json = gson.toJson(notes)
        return json.toByteArray(Charsets.UTF_8)
    }
}