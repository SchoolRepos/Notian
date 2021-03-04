package me.profiluefter.profinote

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import me.profiluefter.profinote.databinding.ActivityNoteEditorBinding
import me.profiluefter.profinote.models.Note
import me.profiluefter.profinote.models.NoteEditorActivityViewModel
import java.util.*

class NoteEditorActivity : AppCompatActivity() {
    private val viewModel: NoteEditorActivityViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return modelClass.getConstructor(Note::class.java, Int::class.java)
                    .newInstance(
                        intent.getSerializableExtra("note") as Note?,
                        intent.getIntExtra("position", -1)
                    )
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityNoteEditorBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                setResult(RESULT_CANCELED)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun saveNote(view: View) {
        val intent = Intent()
        intent.putExtra("note", viewModel.note)
        intent.putExtra("position", viewModel.notePosition)
        setResult(RESULT_OK, intent)
        finish()
    }

    fun openTimePicker(view: View) {
        val calendar = Calendar.getInstance()
        val dialog = TimePickerDialog(
            this,
            { _, hour, minute -> viewModel.setTime(hour, minute) },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false
        )
        dialog.show()
    }

    fun openDatePicker(view: View) {
        val calendar = Calendar.getInstance()
        val dialog = DatePickerDialog(
            this,
            { _, year, month, day -> viewModel.setDate(day, month, year) },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        dialog.show()
    }
}