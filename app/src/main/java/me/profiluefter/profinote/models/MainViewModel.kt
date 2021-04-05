package me.profiluefter.profinote.models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
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
        Transformations.map(list) { TodoList(it.name, it.notes.sorted()) }

    fun deleteNote(index: Int) {
        Log.i(TAG, "Deleting note at index $index")
        repository.deleteNote(sortedList.value!!, sortedList.value!!.notes[index])
    }

    fun saveNotes() {
        TODO("Will be removed")
    }

    fun setNote(position: Int, note: Note) {
        if (position == -1) {
            repository.addNote(sortedList.value!!, note)
        } else {
            repository.setNote(sortedList.value!!, sortedList.value!!.notes[position], note)
        }
    }

    fun getNote(position: Int): Note = sortedList.value!!.notes[position]
}