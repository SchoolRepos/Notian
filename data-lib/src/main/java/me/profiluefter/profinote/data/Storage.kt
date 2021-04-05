package me.profiluefter.profinote.data

/**
 * A binary storage backend
 */
interface Storage {
    suspend fun save(data: ByteArray)
    suspend fun read(listID: Int): ByteArray?
}
