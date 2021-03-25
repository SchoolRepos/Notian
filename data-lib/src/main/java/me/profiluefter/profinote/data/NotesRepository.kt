package me.profiluefter.profinote.data

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import me.profiluefter.profinote.data.synchronization.NotesAPI
import javax.inject.Inject

@Module
@InstallIn(ActivityRetainedComponent::class)
class NotesRepository @Inject constructor(
    private val api: NotesAPI,
    private val local: Serializer
) {

}