package me.profiluefter.profinote.data

import android.app.Application
import me.profiluefter.profinote.models.Note

interface DataLoader {
    suspend fun load(app: Application): List<Note>
    suspend fun save(notes: List<Note>, app: Application)
}
