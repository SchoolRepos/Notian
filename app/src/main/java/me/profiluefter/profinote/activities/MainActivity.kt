package me.profiluefter.profinote.activities

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import me.profiluefter.profinote.R
import me.profiluefter.profinote.data.entities.Note
import me.profiluefter.profinote.models.MainViewModel

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()

    private lateinit var appBarConfiguration: AppBarConfiguration

    private val navController: NavController
        get() = (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController

    private val logTag = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.noteListFragment, R.id.loginFragment),
            findViewById<DrawerLayout>(R.id.nav_layout)
        )

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.setupWithNavController(navController, appBarConfiguration)
        val drawer = findViewById<NavigationView>(R.id.nav_drawer)
        drawer.setupWithNavController(navController)

        viewModel.listNames.observe(this) {
            val subtitleMenu = drawer.menu.findItem(R.id.drawer_todolist_subtitle)
            val subMenu = subtitleMenu?.subMenu

            if (subMenu == null) {
                Log.w(logTag, "MenuItem subMenu was null")
                return@observe
            }

            subMenu.clear()
            it.forEach { listName ->
                val item = subMenu.add(listName.second)
                item.setOnMenuItemClickListener {
                    navController.navigate(NoteListFragmentDirections.changeCurrentList(listName.first))
                    findViewById<DrawerLayout>(R.id.nav_layout).close()
                    true
                }
            }
        }
    }

    fun onEditNote(note: Note) {
        navController.navigate(
            NoteListFragmentDirections.openEditor(
                viewModel.refreshNote(note)
            )
        )
    }

    fun onShowNoteDetails(note: Note) {
        navController.navigate(
            NoteListFragmentDirections.showDetails(
                viewModel.refreshNote(note)
            )
        )
    }

    private fun onNewNote() {
        navController.navigate(NoteListFragmentDirections.openEditor(Note()))
    }

    fun onSettings(item: MenuItem) {
        navController.navigate(NoteListFragmentDirections.openSettings())
    }

    fun onNewNote(item: MenuItem) = onNewNote()

    fun onDeleteNote(note: Note) {
        viewModel.deleteNote(note)

        Snackbar.make(
            findViewById(R.id.nav_host_fragment),
            R.string.note_deleted,
            Snackbar.LENGTH_SHORT
        ).apply {
            setAction(R.string.undo) {
                viewModel.addNote(note)
            }
            show()
        }
    }

    fun onNewNote(view: View) = onNewNote()

    fun setNoteChecked(note: Note, checked: Boolean) {
        if (note.done == checked) return
        viewModel.setNoteChecked(note, checked)
    }

    fun onSynchronize(item: MenuItem) {
        viewModel.synchronize()
    }
}