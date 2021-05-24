package me.profiluefter.profinote

import android.app.PendingIntent
import android.content.Context
import androidx.navigation.NavDeepLinkBuilder
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import me.profiluefter.profinote.activities.NoteDetailsFragmentArgs
import me.profiluefter.profinote.data.DeepLinkProvider
import me.profiluefter.profinote.data.entities.Note
import javax.inject.Inject

class NavigationDeepLinkProvider @Inject constructor(@ApplicationContext private val context: Context) : DeepLinkProvider {
    override fun deepLinkToNote(note: Note): PendingIntent = NavDeepLinkBuilder(context)
        .setGraph(R.navigation.nav_graph)
        .setDestination(R.id.noteDetailsFragment)
        .setArguments(NoteDetailsFragmentArgs(note).toBundle())
        .createPendingIntent()
}

@Module
@InstallIn(SingletonComponent::class)
interface NavigationDeepLinkProviderBinding {
    @Binds
    fun navigationDeepLinkProvider(provider: NavigationDeepLinkProvider): DeepLinkProvider
}