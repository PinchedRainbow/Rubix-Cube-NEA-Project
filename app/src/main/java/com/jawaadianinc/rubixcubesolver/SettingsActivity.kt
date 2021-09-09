package com.jawaadianinc.rubixcubesolver

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        PreferenceManager.setDefaultValues(this, R.xml.root_preferences, false)

        //val darkModeSwitch : SwitchPreferenceCompat = findViewById(R.id.dark_mode)
        val sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE)
        //val editor = sharedPreferences.edit()
        val isDarkModeOn = sharedPreferences
            .getBoolean(
                "isDarkModeOn", false
            )

        if (isDarkModeOn) {
            Toast.makeText(this, "Dark Mode is on", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Dark Mode is off", Toast.LENGTH_SHORT).show()
        }

    }


}

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}