package me.profiluefter.profinote.data.serializer

import me.profiluefter.profinote.data.Serializer
import me.profiluefter.profinote.data.entities.Note
import me.profiluefter.profinote.data.entities.TodoList
import java.nio.BufferUnderflowException
import java.nio.ByteBuffer
import javax.inject.Inject

class BinarySerializer @Inject constructor() : Serializer {
    override suspend fun deserialize(data: ByteArray): TodoList {
        val buffer = ByteBuffer.wrap(data)

        var notes: MutableList<Note>? = null
        try {
            val size = buffer.int
            notes = ArrayList(size)

            fun readString(): String {
                val length = buffer.int
                val readBuffer = ByteArray(length)
                buffer.get(readBuffer)
                return String(readBuffer, Charsets.UTF_8)
            }

            repeat(size) {
                notes.add(
                    Note(
                        readString(),
                        buffer.get().toInt() == 1,
                        buffer.get().toInt(),
                        buffer.get().toInt(),
                        buffer.get().toInt(),
                        buffer.get().toInt(),
                        buffer.short.toInt(),
                        readString()
                    )
                )
            }

            return notes
        } catch (e: BufferUnderflowException) {
            return notes ?: emptyList()
        }
    }

    override suspend fun serialize(notes: TodoList): ByteArray {
        val buffer = ByteBuffer.allocate(4 + notes.sumBy { it.size() })

        buffer.putInt(notes.size)
        for (note in notes) {
            buffer
                .putInt(note.title.length)
                .put(note.title.toByteArray(Charsets.UTF_8))
                .put((if(note.done) 1 else 0).toByte())
                .put(note.minute.toByte())
                .put(note.hour.toByte())
                .put(note.day.toByte())
                .put(note.month.toByte())
                .putShort(note.year.toShort())
                .putInt(note.description.length)
                .put(note.description.toByteArray(Charsets.UTF_8))
        }

        return buffer.array()
    }

    private fun Note.size(): Int {
        return (4 + title.length) + // title
                1 + // done
                (4 * 1) + // minute, hour, day, month
                2 + // year
                (4 + description.length) // description
    }
}