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

    val listNames: LiveData<List<Pair<Int, String>>> = repository.getListNames()

    private val list: LiveData<TodoList> =
        selectedListID.switchMap { repository.getList(it ?: return@switchMap MutableLiveData())  }
    val sortedList: LiveData<TodoList> =
        Transformations.map(list) { it?.sorted() }

    fun deleteNote(index: Int) {
        Log.i(TAG, "Deleting note at index $index")
        GlobalScope.launch {
            repository.deleteNote(getNote(index))
        }
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

    fun selectList(listID: Int) {
        selectedListID.value = listID
    }

    fun getNote(position: Int): Note = getLiveNote(position).value!!
    fun getLiveNote(position: Int): LiveData<Note> = sortedList.map { it.notes[position] }
}