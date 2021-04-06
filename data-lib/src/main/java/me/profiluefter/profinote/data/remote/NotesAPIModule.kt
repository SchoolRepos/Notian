package me.profiluefter.profinote.data.remote

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NotesAPIModule {
    @Provides
    @Singleton
    fun notesAPIClient(): NotesAPI = Retrofit.Builder()
        .baseUrl("http://sickinger-solutions.at/notesserver/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create()
}