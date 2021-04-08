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
                    viewModel.selectList(listName.first)
                    findViewById<DrawerLayout>(R.id.nav_layout).close()
                    true
                }
            }
        }
    }

    fun onEditNote(index: Int) {
        navController.navigate(
            NoteListFragmentDirections.openEditor(
                index
            )
        )
    }

    fun onShowNoteDetails(index: Int) {
        navController.navigate(
            NoteListFragmentDirections.showDetails(
                index
            )
        )
    }

    private fun onNewNote() {
        navController.navigate(NoteListFragmentDirections.openEditor(-1))
    }

    fun onSettings(item: MenuItem) {
        navController.navigate(NoteListFragmentDirections.openSettings())
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