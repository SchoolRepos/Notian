package me.profiluefter.profinote.data

import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GsonModule {
    @Provides
    @Singleton
    fun createGson(): Gson = GsonBuilder()
        .setExclusionStrategies(ExposeExclusionStrategy())
        .create()
}

@Target(AnnotationTarget.FIELD)
annotation class LocalOnly

class ExposeExclusionStrategy : ExclusionStrategy {
    override fun shouldSkipClass(clazz: Class<*>): Boolean = false

    override fun shouldSkipField(f: FieldAttributes): Boolean =
        f.getAnnotation(LocalOnly::class.java) != null
}