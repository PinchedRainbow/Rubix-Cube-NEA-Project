package com.jawaadianinc.rubixcubesolver

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo


class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        //Displays splashscreen to users upon loading

        val sharedPreferences = getSharedPreferences(
            "sharedPrefs", MODE_PRIVATE
        )
        //Shared Preferences is the way in which one can store and retrieve small amounts of primitive data as key/value pairs to a file on the device storage

        val isDarkModeOn = sharedPreferences
            .getBoolean(
                "isDarkModeOn", true
            )
        //gets the current boolean value for the key "isDarkModeOn"


        if (isDarkModeOn) {
            //if it is true, set the app theme to dark
            AppCompatDelegate
                .setDefaultNightMode(
                    AppCompatDelegate
                        .MODE_NIGHT_YES
                )
        } else {
            //else set the app theme to light
            AppCompatDelegate
                .setDefaultNightMode(
                    AppCompatDelegate
                        .MODE_NIGHT_NO
                )
        }

        //ANIMATION library
        YoYo.with(Techniques.BounceInUp)
            .duration(1000)
            .playOn(findViewById(R.id.SplashImage))

        YoYo.with(Techniques.FadeIn)
            .duration(1500)
            .playOn(findViewById(R.id.Title))
        //ANIMATION library

        val secondsDelayed = 3
        Handler().postDelayed(Runnable {
            //Start the next activity after 3 seconds
            startActivity(Intent(this, SignInActivity::class.java))
            overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        }, (secondsDelayed * 1000).toLong())
    }
}
