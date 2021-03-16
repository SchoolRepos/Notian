package me.profiluefter.profinote.models

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import me.profiluefter.profinote.data.*
import java.util.*

class NoteEditorActivityViewModelFactory(private val intent: Intent) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (!modelClass.isAssignableFrom(NoteEditorActivityViewModel::class.java))
            throw IllegalArgumentException("Unknown ViewModel")
        return NoteEditorActivityViewModel(
            intent.getSerializableExtra("note") as Note?,
            intent.getIntExtra("position", -1)
        ) as T
    }
}

class NoteEditorActivityViewModel(note: Note?, position: Int) : ViewModel() {
    fun setTime(hour: Int, minute: Int) {
        time.value = formatTime(hour, minute)
    }

    fun setDate(day: Int, month: Int, year: Int) {
        date.value = formatDate(day, month, year)
    }

    val title: MutableLiveData<String> = MutableLiveData(note?.title ?: "")
    val date: MutableLiveData<String> = MutableLiveData(note?.date ?: currentDate)
    val time: MutableLiveData<String> = MutableLiveData(note?.time ?: currentTime)
    val description: MutableLiveData<String> = MutableLiveData(note?.description ?: "")

    val note: Note
        get() {
            val (day, month, year) = date.value!!.split(".").map { it.toInt() }
            val (hour, minute) = time.value!!.split(":").map { it.toInt() }
            return Note(title.value!!, minute, hour, day, month, year, description.value!!)
        }
    val notePosition: Int = position

    private val currentDate: String
        get() {
            val calendar = Calendar.getInstance()
            return formatDate(
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.YEAR)
            )
        }

    private val currentTime: String
        get() {
            val calendar = Calendar.getInstance()
            return formatTime(
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE)
            )
        }
}