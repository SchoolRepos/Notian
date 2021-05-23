package me.profiluefter.profinote.models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import me.profiluefter.profinote.data.GeocodingService
import me.profiluefter.profinote.data.entities.*
import javax.inject.Inject

@HiltViewModel
class NoteEditorViewModel @Inject constructor(private val geocodingService: GeocodingService) : ViewModel() {
    fun setTime(hour: Int, minute: Int) {
        time.value = formatTime(hour, minute)
    }

    fun setDate(day: Int, month: Int, year: Int) {
        date.value = formatDate(day, month, year)
    }

    fun receivedGPS(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            val location = geocodingService.reverse(latitude, longitude)
            this@NoteEditorViewModel.location.value = location
            this@NoteEditorViewModel.longitude.value = longitude
            this@NoteEditorViewModel.latitude.value = latitude
        }
    }

    fun setInitialNote(note: Note) {
        localID = note.localID
        title.value = note.title
        done.value = note.done
        date.value = note.date
        time.value = note.time
        description.value = note.description
    }

    var localID: Int? = null
    val title: MutableLiveData<String> = MutableLiveData()
    val done: MutableLiveData<Boolean> = MutableLiveData()
    val date: MutableLiveData<String> = MutableLiveData()
    val time: MutableLiveData<String> = MutableLiveData()
    val description: MutableLiveData<String> = MutableLiveData()
    val latitude: MutableLiveData<Double> = MutableLiveData()
    val longitude: MutableLiveData<Double> = MutableLiveData()
    val location: MutableLiveData<String> = MutableLiveData()

    val note: Note
        get() {
            val (day, month, year) = date.value!!.split(".").map { it.toInt() }
            val (hour, minute) = time.value!!.split(":").map { it.toInt() }
            return Note(
                localID!!,
                title.value!!,
                done.value!!,
                minute,
                hour,
                day,
                month,
                year,
                description.value!!,
                latitude.value!!,
                longitude.value!!,
                location.value!!
            )
        }
}