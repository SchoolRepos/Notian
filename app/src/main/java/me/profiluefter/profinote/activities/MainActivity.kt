package me.profiluefter.profinote.activities

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.getSystemService
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.google.android.material.navigation.NavigationView
import com.google.android.material.transition.MaterialFadeThrough
import dagger.hilt.android.AndroidEntryPoint
import me.profiluefter.profinote.R
import me.profiluefter.profinote.data.entities.Note
import me.profiluefter.profinote.models.MainViewModel
import kotlin.random.Random
import kotlin.random.nextInt

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

        lifecycleScope.launchWhenCreated { showStartupNotification() }
    }

    private suspend fun showStartupNotification() {
        createNotificationChannel()

        if (!sharedPreferences.getBoolean("startup_notification", true)) return

        val dueToday: List<Note> = viewModel.getNotesDueToday()
        showParentNotification()
        showNoteNotifications(dueToday)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getSystemService<NotificationManager>()!!.createNotificationChannel(
                NotificationChannel(
                    getString(R.string.notification_startup_channel_id),
                    getString(R.string.notification_startup_channel_name),
                    NotificationManager.IMPORTANCE_LOW
                )
            )
        }
    }

    private fun showNoteNotifications(dueToday: List<Note>) {
        val notificationManager = NotificationManagerCompat.from(this)

        dueToday.map {
            NotificationCompat.Builder(this, getString(R.string.notification_startup_channel_id))
                .setSmallIcon(R.drawable.baseline_task_24)
                .setContentTitle(it.title)
                .setContentText(it.description)
                .setStyle(NotificationCompat.BigTextStyle().bigText(it.description))
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setGroup(getString(R.string.notification_startup_group_id))
                .build()
        }.forEach {
            notificationManager.notify(
                Random.nextInt(Integer.MIN_VALUE..Integer.MAX_VALUE),
                it
            )
        }
    }

    private fun showParentNotification() {
        val notification = NotificationCompat.Builder(this, getString(R.string.notification_startup_channel_id))
            .setSmallIcon(R.drawable.baseline_task_24)
            .setContentTitle(getString(R.string.notification_startup_title))
            .setContentText(getString(R.string.notification_startup_description))
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setGroup(getString(R.string.notification_startup_group_id))
            .setGroupSummary(true)
            .setStyle(NotificationCompat.InboxStyle()
                .setSummaryText(getString(R.string.notification_startup_description)))
            .build()
        NotificationManagerCompat.from(this).notify(
            Random.nextInt(Integer.MIN_VALUE..Integer.MAX_VALUE),
            notification
        )
    }
}