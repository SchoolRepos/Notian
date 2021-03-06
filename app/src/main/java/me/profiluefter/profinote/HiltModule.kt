package me.profiluefter.profinote

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import me.profiluefter.profinote.data.CSVSerializer
import me.profiluefter.profinote.data.PrivateFileStorage
import me.profiluefter.profinote.data.Serializer
import me.profiluefter.profinote.data.Storage
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
abstract class BindingModule {
    @Binds
    abstract fun bindStorage(impl: PrivateFileStorage): Storage

    @Binds
    abstract fun bindSerializer(impl: CSVSerializer): Serializer
}

@Module
@InstallIn(SingletonComponent::class)
object ConstantsModule {
    @Provides
    @Named("fileName")
    fun storageLocation(): String = "notes.csv"
}