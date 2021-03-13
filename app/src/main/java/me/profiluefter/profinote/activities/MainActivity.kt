package me.profiluefter.profinote.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import me.profiluefter.profinote.R
import me.profiluefter.profinote.databinding.ActivityMainBinding
import me.profiluefter.profinote.models.MainActivityViewModel
import me.profiluefter.profinote.models.Note

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewModel: MainActivityViewModel by viewModels()

    private val editorActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if(it.resultCode != RESULT_OK || it.data == null) return@registerForActivityResult

        val note = it.data!!.getSerializableExtra("note") as Note
        val position = it.data!!.getIntExtra("position", -1)
        viewModel.setNote(position, note)
    }

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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    fun onNewNote(item: MenuItem) = onNewNote()

    fun onSaveNotes(item: MenuItem) {
        viewModel.saveNotes()
        Snackbar.make(findViewById(R.id.notes), R.string.saved_notes, Snackbar.LENGTH_SHORT).show()
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

    fun onNewNote(view: View) = onNewNote()

    private fun onNewNote() {
        val intent = Intent(this, NoteEditorActivity::class.java)
        intent.putExtra("position", -1)
        editorActivity.launch(intent)
    }
}