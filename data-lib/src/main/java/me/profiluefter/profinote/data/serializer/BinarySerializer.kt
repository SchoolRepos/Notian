package me.profiluefter.profinote.data.serializer

import me.profiluefter.profinote.data.entities.Note
import me.profiluefter.profinote.data.Serializer
import me.profiluefter.profinote.data.Storage
import java.nio.BufferUnderflowException
import java.nio.ByteBuffer
import javax.inject.Inject

class BinarySerializer @Inject constructor(private val storage: Storage) : Serializer {
    override suspend fun load(): List<Note> {
        val buffer = ByteBuffer.wrap(storage.get() ?: return emptyList())

        var notes: MutableList<Note>? = null
        try {
            val size = buffer.int
            notes = ArrayList(size)

            fun readString(): String {
                val length = buffer.int
                val data = ByteArray(length)
                buffer.get(data)
                return String(data, Charsets.UTF_8)
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

    override suspend fun save(notes: List<Note>) {
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

        storage.store(buffer.array())
    }

    private fun Note.size(): Int {
        return (4 + title.length) + // title
                1 + // done
                (4 * 1) + // minute, hour, day, month
                2 + // year
                (4 + description.length) // description
    }
}