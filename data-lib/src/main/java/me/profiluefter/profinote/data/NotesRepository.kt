package me.profiluefter.profinote.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities.*
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.profiluefter.profinote.data.entities.*
import me.profiluefter.profinote.data.local.NotesDatabase
import me.profiluefter.profinote.data.remote.Credentials
import me.profiluefter.profinote.data.remote.NotesAPI
import java.time.LocalDateTime
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

    fun getListLive(localID: Int): LiveData<TodoList> =
        local.listDao().getByLocalIDLive(localID).switchMap { list ->
            local.todoDao().getByListIDLive(localID).map { todos ->
                TodoList.from(list, todos.filterNot { it.additionalData == "DELETE" })
            }
        }

    suspend fun getList(localID: Int): TodoList = TodoList.from(
        local.listDao().getByLocalID(localID),
        local.todoDao().getByListID(localID).filterNot { it.additionalData == "DELETE" }
    )

    suspend fun deleteNote(note: Note) {
        val rawNote = local.todoDao().getByLocalID(note.localID)

        if (rawNote.id == "NEW") {
            local.todoDao().delete(rawNote)
            return
        }

        if (isNetworkAvailable()) {
            local.todoDao().delete(rawNote)
            try {
                remote.deleteTodo(rawNote.id.toInt(), username, password)
            } catch (e: Exception) {
                local.todoDao().insert(rawNote)
                local.todoDao().scheduleDelete(note.localID)
                throw RuntimeException("Error while deleting note $note from server", e)
            }
        } else
            local.todoDao().scheduleDelete(note.localID)
    }

    suspend fun addNote(pair: Pair<Note, TodoList>) {
        val (note, list) = pair
        val rawList = local.listDao().getByLocalID(list.localID)
        val rawTodo = note.toRaw("NEW", rawList)

        val newLocalID = local.todoDao().insert(rawTodo).toInt()

        if (isNetworkAvailable()) {
            val (_, newRemoteID) = remote.newTodo(rawTodo, username, password)
            local.todoDao().changeID(newLocalID, newRemoteID)
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

    suspend fun addList(listName: String): Int {
        val rawList = RawTodoList(0, "NEW", listName, apiPattern.format(LocalDateTime.now()))

        val newLocalID = local.listDao().insert(rawList).toInt()

        GlobalScope.launch {
            if (isNetworkAvailable()) {
                val (_, newRemoteID) = remote.newList(rawList, username, password)
                local.listDao().changeID(newLocalID, newRemoteID)
            }
        }

        return newLocalID
    }

    suspend fun synchronize() {
        if (!isNetworkAvailable()) {
            Log.w(logTag, "Synchronization ignored since the device is offline")
            return
        }

        Log.i(logTag, "Starting synchronization...")
        synchronizeLists()
        Log.i(logTag, "Finished list synchronization! Starting todo synchronization...")
        synchronizeTodos()
        Log.i(logTag, "Finished synchronization!")
    }

    //TODO: merge synchronizeLists() and synchronizeTodos()

    private suspend fun synchronizeLists() {
        val localLists = local.listDao().getAll()

        val (deleteLists, remainingLocal) =
            localLists.partition { it.additionalData == "DELETE" }

        Log.d(logTag, "Deleting ${deleteLists.size} todo lists")
        deleteLists.forEach {
            remote.deleteList(it.id.toInt(), username, password)
            local.listDao().delete(it)
        }

        val uploadLists = remainingLocal.filter { it.id == "NEW" }
        Log.d(logTag, "Uploading ${uploadLists.size} todo lists")
        uploadLists.forEach {
            val (_, newID) = remote.newList(it, username, password)
            local.listDao().changeID(it.localID, newID)
            local.todoDao().changeListID(it.localID, newID)
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
            val (remoteEntity, localEntity) = it
            val localChangeDate = localEntity.changedDate()
            val remoteChangeDate = remoteEntity.changedDate()
            val localNewer = localChangeDate.isAfter(remoteChangeDate)

            RawTodoList(
                localEntity.localID,
                remoteEntity.id,
                remoteEntity.ownerId,
                if (localNewer) localEntity.name else remoteEntity.name,
                apiPattern.format(LocalDateTime.now())
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
            local.todoDao().delete(it)
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
            val (remoteEntity, localEntity) = it
            val localNewer = localEntity.changedDate().isAfter(remoteEntity.changedDate())

            RawTodo(
                localEntity.localID,
                remoteEntity.id,
                remoteEntity.ownerId,
                if (localNewer) localEntity.localListID else local.listDao()
                    .getLocalIDByRemoteID(remoteEntity.todoListId),
                if (localNewer) localEntity.todoListId else remoteEntity.todoListId,
                if (localNewer) localEntity.title else remoteEntity.title,
                if (localNewer) localEntity.description else remoteEntity.description,
                if (localNewer) localEntity.dueDate else remoteEntity.dueDate,
                if (localNewer) localEntity.state else remoteEntity.state,
                apiPattern.format(LocalDateTime.now())
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

    fun getListNamesLive(): LiveData<List<Pair<Int, String>>> =
        local.listDao().getAllLive().map { lists -> lists.map { it.localID to it.name } }

    suspend fun getListNames(): List<Pair<Int, String>> = local.listDao().getAll().map { it.localID to it.name }

    suspend fun setNoteChecked(localID: Int, checked: Boolean) {
        local.todoDao().setChecked(localID, checked)
    }

    suspend fun nuke() {
        local.todoDao().nuke()
        local.listDao().nuke()
    }
}
