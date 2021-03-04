package me.profiluefter.profinote

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import me.profiluefter.profinote.databinding.ActivityMainBinding
import me.profiluefter.profinote.models.MainActivityViewModel
import me.profiluefter.profinote.models.Note

class MainActivity : AppCompatActivity() {
    private val viewModel: MainActivityViewModel by viewModels()

    private val editorRequestCode = 187

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

    fun onNewNote(item: MenuItem) {
        val intent = Intent(this, NoteEditorActivity::class.java)
        intent.putExtra("position", -1)
        startActivityForResult(
            intent,
            editorRequestCode
        ) //TODO: Replace with registerForActivityResult(StartActivityForResult())
    }

    fun onSaveNotes(item: MenuItem) {
        viewModel.saveNotes()
        Snackbar.make(findViewById(R.id.notes), R.string.saved_notes, Snackbar.LENGTH_SHORT).show()
    }

    fun onEditNote(index: Int) {
        val intent = Intent(this, NoteEditorActivity::class.java)
        intent.putExtra("position", index)
        intent.putExtra("note", viewModel.notes.value!![index])
        startActivityForResult(
            intent,
            editorRequestCode
        ) //TODO: Replace with registerForActivityResult(StartActivityForResult())
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            editorRequestCode -> {
                if (resultCode != RESULT_OK) return

                val note = data!!.getSerializableExtra("note") as Note
                val position = data.getIntExtra("position", -1)
                viewModel.setNote(position, note)
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }
}