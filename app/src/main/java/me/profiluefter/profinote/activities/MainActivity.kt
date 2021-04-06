package me.profiluefter.profinote.activities

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import me.profiluefter.profinote.R
import me.profiluefter.profinote.models.MainViewModel

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navigation = findNavController(R.id.nav_host_fragment)
        setupActionBarWithNavController(this, navigation)
    }

    override fun onSupportNavigateUp() = findNavController(R.id.nav_host_fragment).navigateUp()

    fun onEditNote(index: Int) {
        findNavController(R.id.nav_host_fragment).navigate(NoteListFragmentDirections.openEditor(index))
    }

    fun onShowNoteDetails(index: Int) {
        findNavController(R.id.nav_host_fragment).navigate(NoteListFragmentDirections.showDetails(index))
    }

    private fun onNewNote() {
        findNavController(R.id.nav_host_fragment).navigate(NoteListFragmentDirections.openEditor(-1))
    }

    fun onSettings(item: MenuItem) {
        findNavController(R.id.nav_host_fragment).navigate(NoteListFragmentDirections.openSettings())
    }

    fun onNewNote(item: MenuItem) = onNewNote()

    fun onDeleteNote(index: Int, view: View) {
        val note = viewModel.getNote(index)

        viewModel.deleteNote(index)

        Snackbar.make(view, R.string.note_deleted, Snackbar.LENGTH_SHORT).apply {
            setAction(R.string.undo) {
                viewModel.setNote(-1, note)
            }
            show()
        }
    }

    fun onNewNote(view: View) = onNewNote()

    fun setNoteChecked(index: Int, checked: Boolean) {
        viewModel.setNoteChecked(index, checked)
    }
}