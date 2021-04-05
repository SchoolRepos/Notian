package me.profiluefter.profinote.data

@Deprecated("Replaced by Room")
interface Storage {
    suspend fun save(data: ByteArray)
    suspend fun read(listID: Int): ByteArray?
}
