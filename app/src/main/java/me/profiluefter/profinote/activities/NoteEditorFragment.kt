package me.profiluefter.profinote.activities

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import me.profiluefter.profinote.R
import me.profiluefter.profinote.databinding.FragmentNoteEditorBinding
import me.profiluefter.profinote.models.MainViewModel
import me.profiluefter.profinote.models.NoteEditorViewModel
import me.profiluefter.profinote.models.NoteEditorViewModelFactory
import java.util.*

class NoteEditorFragment : Fragment() {
    private val args by navArgs<NoteEditorFragmentArgs>()

    private val viewModel: MainViewModel by activityViewModels()
    private val editor: NoteEditorViewModel by viewModels {
        NoteEditorViewModelFactory(
            args.note
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = DataBindingUtil.inflate<FragmentNoteEditorBinding>(
        inflater,
        R.layout.fragment_note_editor,
        container,
        false
    ).apply {
        lifecycleOwner = this@NoteEditorFragment
        fragment = this@NoteEditorFragment
        layoutViewModel = this@NoteEditorFragment.editor
    }.root

    fun saveNote() {
        if (editor.localID == 0)
            viewModel.addNote(editor.note)
        else
            viewModel.setNote(editor.note)
        findNavController().navigateUp()
    }

    fun openTimePicker(view: View) {
        val calendar = Calendar.getInstance()
        val dialog = TimePickerDialog(
            requireContext(),
            { _, hour, minute -> editor.setTime(hour, minute) },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false
        )
        dialog.show()
    }

    fun openDatePicker(view: View) {
        val calendar = Calendar.getInstance()
        val dialog = DatePickerDialog(
            requireContext(),
            { _, year, month, day -> editor.setDate(day, month + 1, year) },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        dialog.show()
    }
}