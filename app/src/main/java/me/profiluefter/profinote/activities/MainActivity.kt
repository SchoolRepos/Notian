package me.profiluefter.profinote.activities

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import me.profiluefter.profinote.R
import me.profiluefter.profinote.models.MainViewModel

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()

    private val logTag = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.noteListFragment, R.id.loginFragment),
            findViewById<DrawerLayout>(R.id.nav_layout)
        )

        val navController =
            (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        val drawer = findViewById<NavigationView>(R.id.nav_drawer)
        setSupportActionBar(toolbar)
        toolbar.setupWithNavController(navController, appBarConfiguration)
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
}