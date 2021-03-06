package me.profiluefter.profinote.data

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
    override suspend fun store(data: ByteArray) = withContext(Dispatchers.IO) {
        context.openFileOutput(fileName, Context.MODE_PRIVATE).buffered().use {
            it.write(data)
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun get(): ByteArray? = withContext(Dispatchers.IO) {
        try {
            context.openFileInput(fileName).buffered().use {
                it.readBytes()
            }
        } catch (e: FileNotFoundException) {
            null
        }
    }

}
