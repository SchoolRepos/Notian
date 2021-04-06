package me.profiluefter.profinote.activities

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.preference.PreferenceManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import me.profiluefter.profinote.R
import me.profiluefter.profinote.models.MainViewModel

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()

    private val permissionRequestCode = 187

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

    fun onSaveNotes(item: MenuItem) {
        val usesExternalStorage =
            PreferenceManager.getDefaultSharedPreferences(this).getString("storageLocation", "")
                .equals("externalStorage")
        if (usesExternalStorage && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                permissionRequestCode
            )
            return
        }
        if (!checkExternalStorage()) return
        viewModel.saveNotes()
        Snackbar.make(findViewById(R.id.notes), R.string.saved_notes, Snackbar.LENGTH_SHORT).show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode != permissionRequestCode) return
        if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (!checkExternalStorage()) return
            viewModel.saveNotes()
        } else Snackbar.make(
            findViewById(R.id.notes),
            R.string.permission_denied,
            Snackbar.LENGTH_SHORT
        )
            .setAction(R.string.grant_permission) {
                requestPermissions(
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    permissionRequestCode
                )
            }
            .show()
    }

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

    private fun checkExternalStorage(): Boolean =
        (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED).also {
            if (!it) {
                Snackbar.make(findViewById(R.id.notes), R.string.no_sd_card, Snackbar.LENGTH_SHORT)
                    .show()
            }
        }

    fun setNoteChecked(index: Int, checked: Boolean) {
        viewModel.setNoteChecked(index, checked)
    }
}