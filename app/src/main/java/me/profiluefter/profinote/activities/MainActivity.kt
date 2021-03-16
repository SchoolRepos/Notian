package me.profiluefter.profinote.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuBuilder
import androidx.databinding.DataBindingUtil
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import me.profiluefter.profinote.R
import me.profiluefter.profinote.data.Note
import me.profiluefter.profinote.databinding.ActivityMainBinding
import me.profiluefter.profinote.models.MainActivityViewModel

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewModel: MainActivityViewModel by viewModels()

    private val editorActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode != RESULT_OK || it.data == null) return@registerForActivityResult

        val note = it.data!!.getSerializableExtra("note") as Note
        val position = it.data!!.getIntExtra("position", -1)
        viewModel.setNote(position, note)
    }

    private val permissionRequestCode = 187

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMainBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        val adapter = NotesAdapter(viewModel.notes.value!!, this)
        viewModel.notes.observe(this) {
            adapter.notes = it
        }

        val recyclerView = findViewById<RecyclerView>(R.id.notes)
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                recyclerView.context,
                DividerItemDecoration.VERTICAL
            )
        )
    }

    @SuppressLint("RestrictedApi") // https://stackoverflow.com/q/48607853
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)

        if (menu is MenuBuilder) {
            menu.setOptionalIconsVisible(true)
        }

        return super.onCreateOptionsMenu(menu)
    }

    fun onNewNote(item: MenuItem) = onNewNote()

    fun onSaveNotes(item: MenuItem) {
        val usesExternalStorage = PreferenceManager.getDefaultSharedPreferences(this).getString("storageLocation", "")
            .equals("externalStorage")
        if (usesExternalStorage && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), permissionRequestCode)
            return
        }
        if (!checkExternalStorage()) return
        viewModel.saveNotes()
        Snackbar.make(findViewById(R.id.notes), R.string.saved_notes, Snackbar.LENGTH_SHORT).show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode != permissionRequestCode) return
        if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if(!checkExternalStorage()) return
            viewModel.saveNotes()
        } else Snackbar.make(findViewById(R.id.notes), R.string.permission_denied, Snackbar.LENGTH_SHORT)
            .setAction(R.string.grant_permission) {
                requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), permissionRequestCode)
            }
            .show()
    }

    fun onEditNote(index: Int) {
        val intent = Intent(this, NoteEditorActivity::class.java)
        intent.putExtra("position", index)
        intent.putExtra("note", viewModel.notes.value!![index])
        editorActivity.launch(intent)
    }

    fun onShowNoteDetails(index: Int) {
        val intent = Intent(this, NoteDetailsActivity::class.java)
        intent.putExtra("note", viewModel.notes.value!![index])
        startActivity(intent)
    }

    fun onDeleteNote(index: Int, view: View) {
        val note = viewModel.notes.value!![index]
        viewModel.deleteNote(index)
        val snackbar = Snackbar.make(view, R.string.note_deleted, Snackbar.LENGTH_SHORT)
        snackbar.setAction(R.string.undo) {
            viewModel.setNote(-1, note)
        }
        snackbar.show()
    }

    fun onSettings(item: MenuItem) {
        startActivity(Intent(this, PreferenceActivity::class.java))
    }

    fun onNewNote(view: View) = onNewNote()

    private fun onNewNote() {
        val intent = Intent(this, NoteEditorActivity::class.java)
        intent.putExtra("position", -1)
        editorActivity.launch(intent)
    }

    private fun checkExternalStorage(): Boolean =
        (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED).also {
            if (!it) {
                Snackbar.make(findViewById(R.id.notes), R.string.no_sd_card, Snackbar.LENGTH_SHORT).show()
            }
        }
}