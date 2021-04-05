package me.profiluefter.profinote.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import me.profiluefter.profinote.data.entities.Note
import me.profiluefter.profinote.data.entities.TodoList
import me.profiluefter.profinote.data.synchronization.APIObjects
import me.profiluefter.profinote.data.synchronization.NotesAPI
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.await
import javax.inject.Inject
import javax.inject.Provider

class NotesRepository @Inject constructor(
    private val remote: NotesAPI,
    private val local: LocalRepository,
    private val credentials: Provider<APIObjects.Credentials>
) {
    fun getList(id: Int): LiveData<TodoList> = liveData {
        val localList = local.load(id)
        if (localList != null)
            emit(localList)

        val (username, password) = credentials.get()
        val apiList = remote.getList(id, username, password).await()
        val apiTodos = remote.getAllTodos(username, password).await()
            .filter { it.todoListId == apiList.id }
        val remoteList: TodoList = TodoList.from(apiList, apiTodos)

        //TODO: Handle 404

        var result = remoteList
        if(localList != null && remoteList != null)
            result = mergeLists(localList, remoteList)

        emit(result)
        local.save(result)

        if(result == remoteList) return@liveData

        val onSyncError: (Throwable) -> Unit = { TODO("Error handling") }

        remote.editList(id, result.toAPI(), username, password).enqueue(retrofitErrorCallback(onSyncError))
        result.notes.forEach {
            if(it.id == null)
                remote.newTodo(it.toAPI(id), username, password).enqueue(retrofitErrorCallback(onSyncError))
            else
                remote.editTodo(it.id, it.toAPI(id), username, password).enqueue(retrofitErrorCallback(onSyncError))
        }
    }

    suspend fun deleteNote(list: TodoList, note: Note) {
        TODO("Not yet implemented")
    }

    suspend fun addNote(list: TodoList, note: Note) {
        TODO("Not yet implemented")
    }

    suspend fun setNote(list: TodoList, old: Note, new: Note) {
        TODO("Not yet implemented")
    }
}

class LocalRepository @Inject constructor(
    private val serializer: Provider<Serializer>,
    private val storage: Provider<Storage>
) {
    suspend fun save(list: TodoList) {
        val data = serializer.get().serialize(list)
        storage.get().save(data)
    }
    suspend fun load(id: Int): TodoList? {
        val data = storage.get().read(id) ?: return null
        return serializer.get().deserialize(data)
    }

}

private fun mergeLists(localList: TodoList, remoteList: TodoList): TodoList {
    TODO("Not yet implemented")
}

private fun <T> retrofitErrorCallback(onError: (Throwable) -> Unit): Callback<T> = object : Callback<T> {
    override fun onResponse(call: Call<T>, response: Response<T>) {}

    override fun onFailure(call: Call<T>, t: Throwable) {
        onError(t)
    }
}