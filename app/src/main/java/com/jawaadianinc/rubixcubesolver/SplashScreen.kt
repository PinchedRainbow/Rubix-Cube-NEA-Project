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

        val sharedPreferences = getSharedPreferences(
            "sharedPrefs", MODE_PRIVATE
        )
        val isDarkModeOn = sharedPreferences
            .getBoolean(
                "isDarkModeOn", true
            )

        if (isDarkModeOn) {
            AppCompatDelegate
                .setDefaultNightMode(
                    AppCompatDelegate
                        .MODE_NIGHT_YES
                )
        } else {
            AppCompatDelegate
                .setDefaultNightMode(
                    AppCompatDelegate
                        .MODE_NIGHT_NO
                )
        }

        YoYo.with(Techniques.BounceInUp)
            .duration(1000)
            .playOn(findViewById(R.id.SplashImage))

        YoYo.with(Techniques.FadeIn)
            .duration(1500)
            .playOn(findViewById(R.id.Title))


        val secondsDelayed = 3
        Handler().postDelayed(Runnable {
            startActivity(Intent(this, SignInActivity::class.java))
            overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        }, (secondsDelayed * 1000).toLong())


    }
}