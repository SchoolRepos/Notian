package me.profiluefter.profinote.models

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import me.profiluefter.profinote.data.Serializer
import javax.inject.Inject

private const val TAG = "MainActivityViewModel"

@HiltViewModel
class MainActivityViewModel @Inject constructor(private val serializer: Serializer) : ViewModel() {
    val notes: MutableLiveData<List<Note>> by lazy {
        MutableLiveData<List<Note>>().also {
            it.value = listOf()
            loadNotes(it)
        }
    }

    private fun loadNotes(liveData: MutableLiveData<List<Note>>) {
        Log.i(TAG, "Loading notes...")
        viewModelScope.launch {
            val existingNotes = serializer.load()
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
            serializer.save(notes.value!!)
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