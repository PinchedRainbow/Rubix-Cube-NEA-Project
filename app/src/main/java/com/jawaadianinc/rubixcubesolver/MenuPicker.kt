package com.jawaadianinc.rubixcubesolver

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.analytics.FirebaseAnalytics


class MenuPicker : AppCompatActivity() {
    private var mFirebaseAnalytics: FirebaseAnalytics? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_picker)

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)

        val mainBt: Button = findViewById(R.id.MainBt)
        val cubeBt: Button = findViewById(R.id.CubeBT)
        val aboutBt: Button = findViewById(R.id.about_bt)
        val darkModeBT: Button = findViewById(R.id.darkmodeBT)
        val logout: Button = findViewById(R.id.log_out)
        val tutorial: Button = findViewById(R.id.Tutorial)
        val twodCube: Button = findViewById(R.id.scrambleSolve)
        val backupBT: Button = findViewById(R.id.backupBT)


        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .build()
        val mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        mainBt.setOnClickListener {
            val intent = Intent(this, MainMenu::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        }

        twodCube.setOnClickListener {
            //Toast.makeText(this, "COMING SOON", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java))
        }


        cubeBt.setOnClickListener {
            startActivity(Intent(this, CubeRenderer::class.java))
            overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        }

        backupBT.setOnClickListener {
            startActivity(Intent(this, BackupActivity::class.java))
        }

        aboutBt.setOnClickListener {
            val intent = Intent(this, About::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        }

        val sharedPreferences = getSharedPreferences(
            "sharedPrefs", MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        val isDarkModeOn = sharedPreferences
            .getBoolean(
                "isDarkModeOn", false
            )

        if (isDarkModeOn) {
            darkModeBT.text = "Dark Mode on"
        } else {
            darkModeBT.text = "Dark Mode off"
        }

        darkModeBT.setOnClickListener {
            if (isDarkModeOn) {
                AppCompatDelegate
                    .setDefaultNightMode(
                        AppCompatDelegate.MODE_NIGHT_NO
                    )
                recreate()
                editor.putBoolean(
                    "isDarkModeOn", false
                )
                editor.apply()
                darkModeBT.text = "Dark Mode off"

            } else {

                AppCompatDelegate
                    .setDefaultNightMode(
                        AppCompatDelegate.MODE_NIGHT_YES
                    )
                recreate()
                editor.putBoolean(
                    "isDarkModeOn", true
                )
                editor.apply()
                darkModeBT.text = "Dark Mode on"
            }
        }

        logout.setOnClickListener {
            mGoogleSignInClient.signOut().addOnCompleteListener {
                val intent = Intent(this, SignInActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        tutorial.setOnClickListener {
            val intent = Intent(this, Tutorial_Activity::class.java)
            startActivity(intent)

        }

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

