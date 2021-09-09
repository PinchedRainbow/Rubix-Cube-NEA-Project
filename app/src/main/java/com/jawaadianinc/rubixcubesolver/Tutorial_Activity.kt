package com.jawaadianinc.rubixcubesolver

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener


class Tutorial_Activity : AppCompatActivity() {

    var tabLayout: TabLayout? = null
    var viewPager: ViewPager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tutorial)
        tabLayout = findViewById<View>(R.id.tabs) as? TabLayout
        viewPager = findViewById<View>(R.id.view_pager) as? ViewPager
        tabLayout?.tabGravity = TabLayout.GRAVITY_FILL
        tabLayout?.tabMode = TabLayout.MODE_SCROLLABLE
        tabLayout?.newTab()?.setText("Shuffle Notation")?.let { tabLayout?.addTab(it) }
        tabLayout?.newTab()?.setText("Beginners Method")?.let { tabLayout?.addTab(it) }
        tabLayout?.newTab()?.setText("CFOP Method")?.let { tabLayout?.addTab(it) }
        val adapter = tabLayout?.tabCount?.let { MyAdapter(supportFragmentManager, it) }
        viewPager?.adapter = adapter
        viewPager?.addOnPageChangeListener(TabLayoutOnPageChangeListener(tabLayout))
        viewPager?.setPageTransformer(true, ZoomOutPageTransformer())
        tabLayout?.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager?.currentItem = tab.position
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

    }

}