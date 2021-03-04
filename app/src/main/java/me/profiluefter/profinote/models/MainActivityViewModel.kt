package me.profiluefter.profinote.models

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import me.profiluefter.profinote.data.ServiceLocator

private const val TAG = "MainActivityViewModel"

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {
    val notes: MutableLiveData<List<Note>> by lazy {
        MutableLiveData<List<Note>>().also {
            it.value = listOf()
            loadNotes(it)
        }
    }

    private fun loadNotes(liveData: MutableLiveData<List<Note>>) {
        Log.i(TAG, "Loading notes...")
        viewModelScope.launch {
            val existingNotes = ServiceLocator.dataLoader.load(getApplication<Application>())
            liveData.postValue((liveData.value!! + existingNotes).sorted())
        }
    }

    fun deleteNote(index: Int) {
        Log.i(TAG, "Deleting note at index $index")
        notes.value = notes.value!!.filterIndexed { i, _ -> index != i }.sorted()
    }

    fun saveNotes() {
        Log.i(TAG, "Saving notes...")
        viewModelScope.launch {
            ServiceLocator.dataLoader.save(notes.value!!, getApplication<Application>())
        }
    }

    fun setNote(position: Int, note: Note) {
        if(position == -1) {
            Log.i(TAG, "Inserting new note with title \"${note.title}\"")
            notes.value = (notes.value!! + note).sorted()
        } else {
            Log.i(TAG, "Setting note at position $position to \"${note.title}\"")
            val list = notes.value!!.toMutableList()
            list[position] = note
            notes.value = list.sorted()
        }
    }
}