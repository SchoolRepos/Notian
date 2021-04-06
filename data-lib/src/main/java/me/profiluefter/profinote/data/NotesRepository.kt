package me.profiluefter.profinote.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities.*
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import dagger.hilt.android.qualifiers.ApplicationContext
import me.profiluefter.profinote.data.entities.Note
import me.profiluefter.profinote.data.entities.TodoList
import me.profiluefter.profinote.data.local.NotesDatabase
import me.profiluefter.profinote.data.remote.Credentials
import me.profiluefter.profinote.data.remote.NotesAPI
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.await
import javax.inject.Inject
import javax.inject.Provider

class NotesRepository @Inject constructor(
    private val remote: NotesAPI,
    private val local: NotesDatabase,
    private val credentials: Provider<Credentials>,
    @ApplicationContext private val context: Context
) {
    private val username: String
        get() = credentials.get().username
    private val password: String
        get() = credentials.get().password

    fun getList(localID: Int): LiveData<TodoList> = local.listDao().getByLocalID(localID).switchMap { list ->
        local.todoDao().getByListID(localID).map { todos ->
            TodoList.from(list, todos)
        }
    }

    suspend fun deleteNote(note: Note) {
        local.todoDao().delete(note.localID)

        if (isNetworkAvailable())
            remote.deleteTodo(note.localID, username, password)
        else
            local.todoDao().scheduleDelete(note.localID)
    }

    suspend fun addNote(pair: Pair<Note, TodoList>) {
        val (note, list) = pair
        val rawList = local.listDao().getByLocalID(list.localID).value!!
        val rawTodo = note.toRaw("NEW", rawList)

        local.todoDao().insert(rawTodo)

        if(isNetworkAvailable()) {
            val newRaw = remote.newTodo(rawTodo, username, password).await()
            local.todoDao().changeID(rawTodo.localID, newRaw.id)
        }
    }

    suspend fun updateNote(note: Note) {
        val previousRaw = local.todoDao().getByLocalID(note.localID)
        val rawList = local.listDao().getByLocalID(previousRaw.localListID).value!!
        val newRaw = note.toRaw(previousRaw.id, rawList)
        local.todoDao().update(newRaw)

        if(isNetworkAvailable()) {
            remote.editTodo(newRaw.id.toInt(), newRaw, username, password).await()
        }
    }

    suspend fun synchronize() {
        TODO("Not yet implemented")
    }

    suspend fun getAvailableNoteID(): Int = local.todoDao().nextAvailableID()

    private fun isNetworkAvailable(): Boolean =
        context.getSystemService(ConnectivityManager::class.java).run {
            getNetworkCapabilities(activeNetwork)?.run {
                hasTransport(TRANSPORT_CELLULAR) ||
                        hasTransport(TRANSPORT_WIFI) ||
                        hasTransport(TRANSPORT_ETHERNET)
            } ?: false
        }
}

private fun <T> retrofitErrorCallback(onError: (Throwable) -> Unit): Callback<T> =
    object : Callback<T> {
        override fun onResponse(call: Call<T>, response: Response<T>) {}

        override fun onFailure(call: Call<T>, t: Throwable) {
            onError(t)
        }
    }