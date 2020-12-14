package com.example.mytodo.tasks

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.mytodo.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView

class TasksActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tasks_act)
        setupNavigationDrawer()
        setSupportActionBar(findViewById(R.id.toolbar))

        // The on touch opacity only happen when set onclick method
//        val fab = findViewById<FloatingActionButton>(R.id.add_task_fab)
//        fab.setOnClickListener {
//            Toast.makeText(this, "fab", Toast.LENGTH_LONG).show()
//        }
        /**
         * Initialize nav-controller
         */
        val navController: NavController = findNavController(R.id.nav_host_fragment)

        /**
         * Configure appbar-toolbar-actionbar
         */
        appBarConfiguration =
            AppBarConfiguration.Builder(R.id.tasks_fragment_dest, R.id.statistics_fragment_dest)
                .setOpenableLayout(drawerLayout).build()
        /**
         * Setup Nav icon
         * https://xuanthulab.net/toolbar-actionbar-trong-lap-trinh-android.html
         */
        setupActionBarWithNavController(navController, appBarConfiguration)

        /**
         * Setup nav-controller for navigates to difference fragment in the [R.id.nav_view]
         * Ex: navigate to statistics fragment, it will highlighted and checked it
         */
        findViewById<NavigationView>(R.id.nav_view).setupWithNavController(navController)
    }

    /**
     * For detect event click at nav-icon to open drawer
     */
    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav_host_fragment).navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun setupNavigationDrawer() {
        drawerLayout = (findViewById<DrawerLayout>(R.id.drawer_layout)).apply {
            setStatusBarBackground(R.color.colorPrimaryDark)
        }
    }
}

// Keys for navigation
const val ADD_EDIT_RESULT_OK = Activity.RESULT_FIRST_USER + 1
const val DELETE_RESULT_OK = Activity.RESULT_FIRST_USER + 2
const val EDIT_RESULT_OK = Activity.RESULT_FIRST_USER + 3
