package me.profiluefter.profinote.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities.*
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import dagger.hilt.android.qualifiers.ApplicationContext
import me.profiluefter.profinote.data.entities.*
import me.profiluefter.profinote.data.local.NotesDatabase
import me.profiluefter.profinote.data.remote.Credentials
import me.profiluefter.profinote.data.remote.NotesAPI
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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

    private val logTag = "NotesRepository"

    fun getList(localID: Int): LiveData<TodoList> =
        local.listDao().getByLocalIDLive(localID).switchMap { list ->
            local.todoDao().getByListIDLive(localID).map { todos ->
                TodoList.from(list, todos.filterNot { it.additionalData == "DELETE" })
            }
        }

    suspend fun deleteNote(note: Note) {
        val rawNote = local.todoDao().getByLocalID(note.localID)

        if (isNetworkAvailable()) {
            remote.deleteTodo(rawNote.id.toInt(), username, password)
            local.todoDao().delete(rawNote)
        } else
            local.todoDao().scheduleDelete(note.localID)
    }

    suspend fun addNote(pair: Pair<Note, TodoList>) {
        val (note, list) = pair
        val rawList = local.listDao().getByLocalID(list.localID)
        val rawTodo = note.toRaw("NEW", rawList)

        local.todoDao().insert(rawTodo)

        if (isNetworkAvailable()) {
            val newRaw = remote.newTodo(rawTodo, username, password)
            local.todoDao().changeID(rawTodo.localID, newRaw.id)
        }
    }

    suspend fun updateNote(note: Note) {
        val previousRaw = local.todoDao().getByLocalID(note.localID)
        val rawList = local.listDao().getByLocalID(previousRaw.localListID)
        val newRaw = note.toRaw(previousRaw.id, rawList)
        local.todoDao().update(newRaw)

        if (isNetworkAvailable()) {
            remote.editTodo(newRaw.id.toInt(), newRaw, username, password)
        }
    }

    suspend fun synchronize() {
        Log.i(logTag, "Starting synchronization...")
        synchronizeLists()
        Log.i(logTag, "Finished list synchronization! Starting todo synchronization...")
        synchronizeTodos()
        Log.i(logTag, "Finished synchronization!")
    }

    private suspend fun synchronizeLists() {
        val localLists = local.listDao().getAll()

        val (deleteLists, remainingLocal) =
            localLists.partition { it.additionalData == "DELETE" }

        Log.d(logTag, "Deleting ${deleteLists.size} todo lists")
        deleteLists.forEach {
            remote.deleteList(it.id.toInt(), username, password)
        }

        val uploadLists = remainingLocal.filter { it.id == "NEW" }
        Log.d(logTag, "Uploading ${uploadLists.size} todo lists")
        uploadLists.forEach {
            val (_, newID) = remote.newList(it, username, password)
            local.listDao().changeID(it.localID, newID)
        }

        val remainingLists = local.listDao().getAll()

        val remoteLists = remote.getAllLists(username, password)
        Log.d(logTag, "Downloaded ${remoteLists.size} todo lists")
        val (mergeLists, downloadLists) =
            remoteLists.partition { remote -> remainingLists.any { local -> local.id == remote.id } }

        Log.d(logTag, "Inserting ${downloadLists.size} new todo lists into local database")
        local.listDao().insert(downloadLists)

        val mergedLists = mergeLists.map { remote ->
            remote to remainingLists.find { local -> local.id == remote.id }!!
        }.map {
            val (remote, local) = it
            val localChangeDate = local.changedDate()
            val remoteChangeDate = remote.changedDate()
            val localNewer = localChangeDate.isAfter(remoteChangeDate)

            RawTodoList(
                local.localID,
                remote.id,
                remote.ownerId,
                if (localNewer) local.name else remote.name,
                if (localNewer) local.additionalData else remote.additionalData
            )
        }
        Log.d(logTag, "Merged ${mergedLists.size} todo list")

        val remoteEdits = mergedLists.zip(mergeLists).filterNot { it.first.serverEquals(it.second) }
            .map { it.first }
        Log.d(logTag, "Executing ${remoteEdits.size} remote edits")
        remoteEdits.forEach {
            remote.editList(it.id.toInt(), it, username, password)
        }

        val localEdits = mergedLists.zip(mergeLists).filterNot { it.first.localEquals(it.second) }
            .map { it.first }
        Log.d(logTag, "Executing ${localEdits.size} local edits")
        local.listDao().update(localEdits)
    }

    private suspend fun synchronizeTodos() {
        val localTodos = local.todoDao().getAll()

        val (deleteTodos, remainingLocal) =
            localTodos.partition { it.additionalData == "DELETE" }

        Log.d(logTag, "Deleting ${deleteTodos.size} todos")
        deleteTodos.forEach {
            remote.deleteTodo(it.id.toInt(), username, password)
        }

        val uploadTodos = remainingLocal.filter { it.id == "NEW" }
        Log.d(logTag, "Uploading ${uploadTodos.size} todos")
        uploadTodos.forEach {
            val (_, newID) = remote.newTodo(it, username, password)
            local.todoDao().changeID(it.localID, newID)
        }

        val remainingTodos = local.todoDao().getAll()

        val remoteTodos = remote.getAllTodos(username, password)
        Log.d(logTag, "Downloaded ${remoteTodos.size} todos")
        val (mergeTodos, downloadTodos) =
            remoteTodos.partition { remote -> remainingTodos.any { local -> local.id == remote.id } }

        Log.d(logTag, "Inserting ${downloadTodos.size} new todos into local database")
        local.todoDao().insert(downloadTodos.map {
            RawTodo(
                it,
                local.listDao().getLocalIDByRemoteID(it.todoListId)
            )
        })

        val mergedTodos = mergeTodos.map { remote ->
            RawTodo(remote, local.listDao().getLocalIDByRemoteID(remote.todoListId)) to
                    remainingTodos.find { local -> local.id == remote.id }!!
        }.map {
            val (remote, local) = it
            val localNewer = local.changedDate().isAfter(remote.changedDate())

            RawTodo(
                local.localID,
                remote.id,
                remote.ownerId,
                if (localNewer) local.localListID else remote.localListID,
                if (localNewer) local.todoListId else remote.todoListId,
                if (localNewer) local.title else remote.title,
                if (localNewer) local.description else remote.description,
                if (localNewer) local.dueDate else remote.dueDate,
                if (localNewer) local.state else remote.state,
                if (localNewer) local.additionalData else remote.additionalData
            )
        }
        Log.d(logTag, "Merged ${mergedTodos.size} todos")

        val remoteEdits = mergedTodos.zip(mergeTodos).filterNot { it.first.serverEquals(it.second) }
            .map { it.first }
        Log.d(logTag, "Executing ${remoteEdits.size} remote edits")
        remoteEdits.forEach {
            remote.editTodo(it.id.toInt(), it, username, password)
        }

        val localEdits = mergedTodos.zip(mergeTodos).filterNot { it.first.localEquals(it.second) }
            .map { it.first }
        Log.d(logTag, "Executing ${localEdits.size} local edits")
        local.todoDao().update(localEdits)
    }

    private fun isNetworkAvailable(): Boolean =
        context.getSystemService(ConnectivityManager::class.java).run {
            getNetworkCapabilities(activeNetwork)?.run {
                hasTransport(TRANSPORT_CELLULAR) ||
                        hasTransport(TRANSPORT_WIFI) ||
                        hasTransport(TRANSPORT_ETHERNET)
            } ?: false
        }

    fun getListNames(): LiveData<List<Pair<Int, String>>> =
        local.listDao().getAllLive().map { lists -> lists.map { it.localID to it.name } }

    suspend fun setNoteChecked(localID: Int, checked: Boolean) {
        local.todoDao().setChecked(localID, checked)
    }
}

private fun <T> retrofitErrorCallback(onError: (Throwable) -> Unit): Callback<T> =
    object : Callback<T> {
        override fun onResponse(call: Call<T>, response: Response<T>) {}

        override fun onFailure(call: Call<T>, t: Throwable) {
            onError(t)
        }
    }