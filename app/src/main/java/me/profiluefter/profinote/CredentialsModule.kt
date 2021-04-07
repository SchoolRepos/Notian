package me.profiluefter.profinote

import android.content.Context
import androidx.preference.PreferenceManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import me.profiluefter.profinote.data.remote.Credentials

@Module
@InstallIn(SingletonComponent::class)
object CredentialsModule {
    @Provides
    fun getCredentials(@ApplicationContext context: Context): Credentials {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return Credentials(
            preferences.getString("username", "")!!,
            preferences.getString("password", "")!!,
            ""
        )
    }
}