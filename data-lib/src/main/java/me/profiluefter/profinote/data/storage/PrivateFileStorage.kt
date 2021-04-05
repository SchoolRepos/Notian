package me.profiluefter.profinote.data.storage

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.profiluefter.profinote.data.Storage
import java.io.FileNotFoundException
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class PrivateFileStorage @Inject constructor(
    @Named("fileName") private val fileName: String,
    @ApplicationContext private val context: Context
) : Storage {
    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun save(data: ByteArray) = withContext(Dispatchers.IO) {
        context.openFileOutput(fileName, Context.MODE_PRIVATE).buffered().use {
            it.write(data)
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun read(listID: Int): ByteArray? = withContext(Dispatchers.IO) {
        try {
            context.openFileInput(fileName).buffered().use {
                it.readBytes()
            }
        } catch (e: FileNotFoundException) {
            null
        }
    }

}
