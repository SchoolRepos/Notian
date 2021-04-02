package me.profiluefter.profinote.data.serializer

import me.profiluefter.profinote.data.Serializer
import me.profiluefter.profinote.data.entities.Note
import kotlin.math.min
import kotlin.random.Random
import kotlin.random.nextInt

class SampleSerializer : Serializer {
    override suspend fun load(data: ByteArray): List<Note> = Array(20) {
        Note(
            "Sleep $it ${"is important for your health because it helps your body to regain energy!".substring(0, min(it/2, 72))}",
            false,
            23, 2, 28, Random.nextInt(1..12), 2021,
            "Sleep $it is good for your health!".repeat(it*2)
        )
    }.toList()

    override suspend fun save(notes: List<Note>): ByteArray {
        TODO("Save is NO-OP")
    }
}