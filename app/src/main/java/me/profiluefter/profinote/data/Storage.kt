package me.profiluefter.profinote.data

interface Storage {
    suspend fun store(data: ByteArray)
    suspend fun get(): ByteArray?
}
