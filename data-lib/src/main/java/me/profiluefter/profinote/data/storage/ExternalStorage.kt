package me.profiluefter.profinote.data.storage

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.profiluefter.profinote.data.Storage
import java.io.File
import java.io.FileNotFoundException
import javax.inject.Inject
import javax.inject.Named

private const val TAG = "ExternalStorage"

class ExternalStorage @Inject constructor(
    @ApplicationContext private val context: Context,
    @Named("fileName") private val fileName: String
) : Storage {
    override suspend fun store(data: ByteArray) = withContext(Dispatchers.IO) {
        val externalStorage = context.getExternalFilesDir(null)
        val file = File(externalStorage!!.absolutePath + File.separator + fileName)
        Log.i(TAG, "Saving data to ${file.absolutePath}!")
        file.writeBytes(data)
    }

    override suspend fun get(): ByteArray? = withContext(Dispatchers.IO) {
        val externalStorage = context.getExternalFilesDir(null)
        try {
            val file = File(externalStorage!!.absolutePath + File.separator + fileName)
            Log.i(TAG, "Trying to read data from ${file.absolutePath}!")
            file.readBytes()
        } catch (e: FileNotFoundException) {
            Log.w(TAG, "File not found.")
            null
        }
    }
}