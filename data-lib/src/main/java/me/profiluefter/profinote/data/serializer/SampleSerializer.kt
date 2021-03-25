package me.profiluefter.profinote.data.serializer

import android.util.Log
import me.profiluefter.profinote.data.entities.Note
import me.profiluefter.profinote.data.Serializer
import javax.inject.Inject
import kotlin.math.min
import kotlin.random.Random
import kotlin.random.nextInt

private const val TAG = "SampleDataLoader"

class SampleSerializer @Inject constructor() : Serializer {
    override suspend fun load(): List<Note> = Array(20) {
        Note(
            "Sleep $it ${"is important for your health because it helps your body to regain energy!".substring(0, min(it/2, 72))}",
            false,
            23, 2, 28, Random.nextInt(1..12), 2021,
            "Sleep $it is good for your health!".repeat(it*2)
        )
    }.toList()

    override suspend fun save(notes: List<Note>) {
        Log.w(TAG, "Save is NO-OP")
    }
}