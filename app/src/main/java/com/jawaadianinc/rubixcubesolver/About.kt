package com.jawaadianinc.rubixcubesolver

import android.app.ActivityOptions
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class About : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        val button: Button = findViewById(R.id.button)

        button.setOnClickListener {
            val intent = Intent(this, MenuPicker::class.java)
            val transitionActivityOptions: ActivityOptions =
                ActivityOptions.makeSceneTransitionAnimation(
                    this
                )
            startActivity(intent, transitionActivityOptions.toBundle())
        }

    }

    fun Insta(view: View) {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://www.instagram.com/faheem.s27/")
            )
        )
    }

}