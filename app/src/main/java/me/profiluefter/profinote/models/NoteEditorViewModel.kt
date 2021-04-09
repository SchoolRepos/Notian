package me.profiluefter.profinote.models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import me.profiluefter.profinote.data.entities.*

class NoteEditorViewModelFactory(private val note: Note) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (!modelClass.isAssignableFrom(NoteEditorViewModel::class.java))
            throw IllegalArgumentException("Unknown ViewModel")
        return NoteEditorViewModel(note) as T
    }
}

class NoteEditorViewModel(note: Note) : ViewModel() {
    fun setTime(hour: Int, minute: Int) {
        time.value = formatTime(hour, minute)
    }

    fun setDate(day: Int, month: Int, year: Int) {
        date.value = formatDate(day, month, year)
    }

    val localID: Int = note.localID
    val title: MutableLiveData<String> = MutableLiveData(note.title)
    val done: MutableLiveData<Boolean> = MutableLiveData(note.done)
    val date: MutableLiveData<String> = MutableLiveData(note.date)
    val time: MutableLiveData<String> = MutableLiveData(note.time)
    val description: MutableLiveData<String> = MutableLiveData(note.description)

    val note: Note
        get() {
            val (day, month, year) = date.value!!.split(".").map { it.toInt() }
            val (hour, minute) = time.value!!.split(":").map { it.toInt() }
            return Note(localID, title.value!!, done.value!!, minute, hour, day, month, year, description.value!!)
        }
}