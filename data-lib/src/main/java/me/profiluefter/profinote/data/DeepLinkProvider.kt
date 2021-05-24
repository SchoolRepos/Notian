package me.profiluefter.profinote.data

import android.app.PendingIntent
import me.profiluefter.profinote.data.entities.Note

interface DeepLinkProvider {
    fun deepLinkToNote(note: Note): PendingIntent
}