package me.profiluefter.profinote.data.remote

import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NotesAPIModule {
    @Provides
    @Singleton
    fun notesAPIClient(gson: Gson, client: OkHttpClient): NotesAPI = Retrofit.Builder()
        .baseUrl("https://sickinger-solutions.at/notesserver/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
        .create()
}