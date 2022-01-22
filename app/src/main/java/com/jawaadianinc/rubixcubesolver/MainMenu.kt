package com.jawaadianinc.rubixcubesolver

import android.app.ActivityOptions
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout


class MainMenu : AppCompatActivity() {
    var tabLayout: TabLayout? = null
    var viewPager: ViewPager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        val toolbar = findViewById<MaterialToolbar>(R.id.topAppBar)
        setSupportActionBar(toolbar)

        //Setting up tabs
        tabLayout = findViewById<View>(R.id.tabs) as? TabLayout
        viewPager = findViewById<View>(R.id.view_pager) as? ViewPager
        tabLayout?.tabGravity = TabLayout.GRAVITY_FILL
        tabLayout?.tabMode = TabLayout.MODE_SCROLLABLE
        tabLayout?.newTab()?.setText("Timer")?.let { tabLayout?.addTab(it) }
        tabLayout?.newTab()?.setText("Solves Analysis")?.let { tabLayout?.addTab(it) }
        tabLayout?.newTab()?.setText("3x3")?.let { tabLayout?.addTab(it) }
        tabLayout?.newTab()?.setText("2x2")?.let { tabLayout?.addTab(it) }
        tabLayout?.newTab()?.setText("4x4")?.let { tabLayout?.addTab(it) }
        val adapter = tabLayout?.tabCount?.let { MyTimerAdaptor(supportFragmentManager, it) }
        viewPager?.adapter = adapter
        viewPager?.offscreenPageLimit = 10
        viewPager?.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        viewPager?.setPageTransformer(true, ZoomOutPageTransformer())
        //Setting up tabs


        //viewPager?.offscreenPageLimit = 3
        val refreshFab: FloatingActionButton = findViewById(R.id.refreshFab)
        //Refreshes the activity for any database changes
        refreshFab.hide()
        tabLayout?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            //Gets called everytime a user is on a tab/new tab
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager?.currentItem = tab.position
                if (tab.position == 1) {
                    refreshFab.show()
                } else {
                    refreshFab.hide()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this, MenuPicker::class.java)
            startActivity(intent)
        }

        refreshFab.setOnClickListener {
            recreate()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            //When a menu item has been clicked
            R.id.settings -> goSettings()
            R.id.about -> goAbout()
            R.id.clear_File -> showAlertDialog()
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun goSettings() {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }

    private fun goAbout() {
        val intent = Intent(this, About::class.java)
        val transitionActivityOptions: ActivityOptions =
            ActivityOptions.makeSceneTransitionAnimation(
                this
            )
        startActivity(intent, transitionActivityOptions.toBundle())
    }

    private fun showAlertDialog() {
        val builder = AlertDialog.Builder(this)

        builder.setTitle("Clear solves?")
        builder.setMessage("Click Yes to delete all solves")
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        builder.setPositiveButton("Yes") { _, _ ->
            deleteDatabase()
        }
        builder.setNegativeButton("Cancel") { _, _ ->
        }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(true)
        alertDialog.show()
    }

    private fun deleteDatabase() {
        this.deleteDatabase("solveTimes.db")
        Toast.makeText(applicationContext, "Solves deleted!", Toast.LENGTH_LONG).show()
        super.finish()
        val intent = Intent(this, MenuPicker::class.java)
        startActivity(intent)

    }

    override fun recreate() {
        finish()
        overridePendingTransition(
            R.anim.fade_in,
            R.anim.fade_out
        )
        startActivity(intent)
        overridePendingTransition(
            R.anim.fade_in,
            R.anim.fade_out
        )

    }
}
