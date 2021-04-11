package me.profiluefter.profinote.models

import android.util.Log
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.profiluefter.profinote.data.NotesRepository
import me.profiluefter.profinote.data.entities.Note
import me.profiluefter.profinote.data.entities.TodoList
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: NotesRepository) : ViewModel() {
    val listNames: LiveData<List<Pair<Int, String>>> = repository.getListNames()

    private val selectedListID: MutableLiveData<Int> = MutableLiveData()

    private val list: LiveData<TodoList> = selectedListID
        .switchMap { repository.getList(it ?: return@switchMap MutableLiveData()) }

    @Suppress("UNNECESSARY_SAFE_CALL") // LiveData is nullable
    val sortedList: LiveData<TodoList> = list.map { it?.sorted() }

    private val logTag = "MainViewModel"

    fun deleteNote(note: Note) {
        Log.i(logTag, "Deleting note $note")
        GlobalScope.launch {
            repository.deleteNote(note)
        }
    }

    fun addNote(note: Note) {
        Log.i(logTag, "Adding new note $note")
        GlobalScope.launch {
            repository.addNote(note to list.value!!)
        }
    }

    fun setNote(new: Note) {
        Log.i(logTag, "Updating note $new")
        GlobalScope.launch {
            repository.updateNote(new)
        }
    }

    fun setNoteChecked(note: Note, checked: Boolean) {
        Log.i(logTag, "Setting $note to checked=$checked")
        GlobalScope.launch {
            repository.setNoteChecked(note.localID, checked)
        }
    }

    fun selectList(listID: Int) {
        Log.i(logTag, "Selecting list with id=$listID")
        selectedListID.value = listID
    }

    fun addList(listName: String) {
        Log.i(logTag, "Adding new list $list")
        GlobalScope.launch {
            val newListID = repository.addList(listName)
            withContext(Dispatchers.Main) {
                selectList(newListID)
            }
        }
    }

    fun synchronize() {
        Log.i(logTag, "Invoking synchronizer")
        GlobalScope.launch {
            repository.synchronize()
        }
    }

    fun refreshNote(note: Note): Note =
        list.value!!.notes.find {
                it.localID == note.localID
            }!!
}