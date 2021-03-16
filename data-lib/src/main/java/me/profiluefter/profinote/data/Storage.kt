package me.profiluefter.profinote.data

/**
 * A binary storage backend
 */
interface Storage {
    suspend fun store(data: ByteArray)
    suspend fun get(): ByteArray?
}
