package me.profiluefter.profinote

import android.content.Context
import androidx.preference.PreferenceManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import me.profiluefter.profinote.data.*
import javax.inject.Named
import javax.inject.Provider

@Module
@InstallIn(SingletonComponent::class)
object PreferenceBasedModule {
    @Provides
    @Named("fileName")
    fun storageLocation(@ApplicationContext context: Context): String =
        PreferenceManager.getDefaultSharedPreferences(context).getString("fileName", "notes.bin")!!

    @Provides
    fun serializerBinding(
        @ApplicationContext context: Context,
        binary: Provider<BinarySerializer>,
        csv: Provider<CSVSerializer>
    ): Serializer = when (PreferenceManager.getDefaultSharedPreferences(context)
        .getString("storageFormat", "binary")) {
        "binary" -> binary.get()
        "CSV" -> csv.get()
        else -> null!!
    }

    @Provides
    fun storageBinding(
        @ApplicationContext context: Context,
        private: Provider<PrivateFileStorage>
    ): Storage = when (PreferenceManager.getDefaultSharedPreferences(context)
        .getString("storageLocation", "privateFile")) {
        "privateFile" -> private.get()
        else -> null!!
    }
}