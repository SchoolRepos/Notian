package me.profiluefter.profinote.data.serializer

import me.profiluefter.profinote.data.Serializer
import me.profiluefter.profinote.data.entities.Note
import me.profiluefter.profinote.data.entities.TodoList
import javax.inject.Inject
import kotlin.math.min
import kotlin.random.Random
import kotlin.random.nextInt

@Deprecated("Replaced by Room")
class SampleSerializer @Inject constructor() : Serializer {
    override suspend fun deserialize(data: ByteArray): TodoList = TodoList("Sample", Array(20) {
        Note(
            null,
            "Sleep $it ${"is important for your health because it helps your body to regain energy!".substring(0, min(it/2, 72))}",
            false,
            23, 2, 28, Random.nextInt(1..12), 2021,
            "Sleep $it is good for your health!".repeat(it*2)
        )
    }.toList())

    override suspend fun serialize(notes: TodoList): ByteArray {
        TODO("Save is NO-OP")
    }
}