package me.profiluefter.profinote.activities

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.google.android.material.navigation.NavigationView
import com.google.android.material.transition.MaterialFadeThrough
import dagger.hilt.android.AndroidEntryPoint
import me.profiluefter.profinote.R
import me.profiluefter.profinote.models.MainViewModel

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()

    private val sharedPreferences by lazy { PreferenceManager.getDefaultSharedPreferences(this) }

    private val currentNavigationFragment: Fragment?
        get() = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val drawerLayout = findViewById<DrawerLayout>(R.id.nav_layout)
        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.noteListFragment, R.id.loginFragment),
            drawerLayout
        )

        val navController =
            (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        val drawer = findViewById<NavigationView>(R.id.nav_drawer)
        setSupportActionBar(toolbar)
        toolbar.setupWithNavController(navController, appBarConfiguration)
        drawer.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.loginFragment, R.id.registerFragment -> {
                    toolbar?.visibility = View.GONE
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                }
                R.id.preferenceFragment -> {
                    toolbar?.visibility = View.VISIBLE
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                }
                else -> {
                    toolbar?.visibility = View.VISIBLE
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                }
            }

            //FIXME: Pfusch
            drawer.getHeaderView(0).findViewById<TextView>(R.id.drawer_username).text =
                sharedPreferences.getString("username", "")
        }

        val subMenu = drawer.menu.findItem(R.id.drawer_todolist_subtitle).subMenu
        viewModel.listNames.observe(this) {
            subMenu.clear()
            it.forEach { listName ->
                val item = subMenu.add(listName.second)
                item.setIcon(R.drawable.baseline_label_24)
                item.setOnMenuItemClickListener {
                    currentNavigationFragment?.apply {
                        exitTransition = MaterialFadeThrough().apply {
                            duration = resources.getInteger(R.integer.notian_animation_time).toLong()
                        }
                    }
                    navController.navigate(NoteListFragmentDirections.changeCurrentList(listName.first))
                    drawerLayout.close()
                    true
                }
            }
        }
    }
}