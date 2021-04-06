package me.profiluefter.profinote.models

import android.util.Log
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.profiluefter.profinote.data.NotesRepository
import me.profiluefter.profinote.data.entities.Note
import me.profiluefter.profinote.data.entities.TodoList
import javax.inject.Inject

private const val TAG = "MainActivityViewModel"

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: NotesRepository) : ViewModel() {
    private val selectedListID: MutableLiveData<Int> = MutableLiveData()

    private val list: LiveData<TodoList> =
        Transformations.switchMap(selectedListID) { repository.getList(it) }
    val sortedList: LiveData<TodoList> =
        Transformations.map(list) { it.sorted() }

    fun deleteNote(index: Int) {
        Log.i(TAG, "Deleting note at index $index")
        GlobalScope.launch {
            repository.deleteNote(getNote(index))
        }
    }

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated("Feature will be removed")
    fun saveNotes() {
        TODO("Will be removed")
    }

    fun setNote(position: Int, note: Note) = GlobalScope.launch {
        if (position == -1) {
            repository.addNote(note to list.value!!)
        } else {
            repository.updateNote(note)
        }
    }

    fun setNoteChecked(index: Int, checked: Boolean) {
        TODO("Not yet implemented")
    }

    fun getNote(position: Int): Note = sortedList.value!!.notes[position]

    fun getLiveNote(position: Int): LiveData<Note> = sortedList.map { it.notes[position] }
}