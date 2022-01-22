package com.jawaadianinc.rubixcubesolver

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.view.View.*
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Task
import com.google.api.services.drive.DriveScopes
import com.tomer.fadingtextview.FadingTextView
import java.sql.Timestamp
import java.util.*


const val RC_SIGN_IN = 123

@SuppressLint("SetTextI18n")
class SignInActivity : AppCompatActivity() {
    private var alreadyPlayedAnimation = 0
    var text = arrayOf(
        "The Cubee Assistant app!",
        "Have fun cubing :)",
        "Material Design Friendly",
        "46 Quintillion possible shuffles!",
        "Welcome back!",
        "We hope you enjoy the app",
        "Coming soon: Multiplayer ;)",
        "The WR for 3x3 is 3.47 seconds!!! :O",
        "Solve a Rubix Cube!",
        "Now with dark mode"
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        val fadingText: FadingTextView = findViewById(R.id.fadingTextView)
        fadingText.setTexts(text)

        //Checks if there is an existing Google account signed in already in the app
        val account = GoogleSignIn.getLastSignedInAccount(this)


        if (account != null) {
            //If an account is present, then skip the sign in section and go to main menu
            Toast.makeText(this, "Welcome ${account.displayName}", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MenuPicker::class.java)
            startActivity(intent)
        }

        val hellotext: TextView = findViewById(R.id.Hello)
        if (account == null) {
            textAnimation()
        } else {
            val name = account.displayName
            hellotext.text = "Welcome back $name!"
            hellotext.gravity = 1
            onToNextPage()
        }

        val btnToggleDark: Button = findViewById(R.id.btnToggleDark)

        val animation = AnimationUtils.loadAnimation(this, R.anim.left_to_right_enter)
        btnToggleDark.startAnimation(animation)
        val sharedPreferences = getSharedPreferences(
            "sharedPrefs", MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
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
            btnToggleDark.text = "Dark Mode on"
        } else {
            AppCompatDelegate
                .setDefaultNightMode(
                    AppCompatDelegate
                        .MODE_NIGHT_NO
                )
            btnToggleDark.text = "Dark Mode off"
        }

        //Handle app theming via button on click listener
        btnToggleDark.setOnClickListener {
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
                btnToggleDark.text = "Dark Mode off"
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
                btnToggleDark.text = "Dark Mode on"
            }
        }

    }

    private fun textAnimation() {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val translationY = displayMetrics.heightPixels * 0.20f
        //Gets the display size and calculates the translation of Y

        val hellotext: TextView = findViewById(R.id.Hello)
        hellotext.text = "Tap here to get started!"

        val animation = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        hellotext.startAnimation(animation)

        hellotext.setOnClickListener {
            //When the user clicks on the text
            if (alreadyPlayedAnimation == 0) {
                hellotext.animate().apply {
                    duration = 750
                    rotationYBy(360f)
                    translationY(-translationY)
                }.start()
                hellotext.text = "Sign in down below"
                showgoogle()
            }
            alreadyPlayedAnimation++
        }
    }

    private fun showgoogle() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(Scope(DriveScopes.DRIVE_FILE))
            .build()
        val mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        //Builds the Google Request

        val button: SignInButton = findViewById(R.id.googleSignIn)
        button.setSize(SignInButton.SIZE_WIDE)
        button.setColorScheme(SignInButton.COLOR_DARK)
        //Properties of button attributes
        button.visibility = VISIBLE
        button.setOnClickListener {
            val signInIntent: Intent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        val button: SignInButton = findViewById(R.id.googleSignIn)
        val hellotext: TextView = findViewById(R.id.Hello)
        val name: TextView = findViewById(R.id.name)
        try {
            val account = completedTask.getResult(ApiException::class.java)
            button.visibility = INVISIBLE
            if (account != null) {
                hellotext.visibility = INVISIBLE
                name.gravity = 1
                name.text = "Hello " + account.displayName + "\nLets get started!"
            }

            val accountId = account?.id
            //Class of user
            val UserModel = UserModel(
                accountId,
                account?.displayName,
                account?.email,
                Timestamp(Date().time).toString()
            )

            val databaseHelper = DataBaseUsers(this)
            databaseHelper.addUser(UserModel)
            //Add class to database
            val intent = Intent(this, MenuPicker::class.java)
            startActivity(intent)
            //onToNextPage()

        } catch (e: ApiException) {
            Toast.makeText(this, "Error Signing in", Toast.LENGTH_SHORT).show()
            button.visibility = VISIBLE

        }
    }

    private fun onToNextPage() {
        val animation = AnimationUtils.loadAnimation(this, R.anim.bounc)
        val nextBttn: Button = findViewById(R.id.nextButton)
        val button: SignInButton = findViewById(R.id.googleSignIn)
        button.visibility = GONE
        nextBttn.visibility = VISIBLE
        val btnToggleDark: Button = findViewById(R.id.btnToggleDark)
        //nextBttn.startAnimation(animation)

        val hellotext: TextView = findViewById(R.id.Hello)

        nextBttn.setOnClickListener {
            val intent = Intent(this, MenuPicker::class.java)
            val p1: Pair<View, String> = Pair.create(nextBttn, "mainMenu")
            val p2: Pair<View, String> = Pair.create(btnToggleDark, "darkButton")
            val p3: Pair<View, String> = Pair.create(hellotext, "appBar")
            //Animation extras

            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, p1, p2, p3)
            startActivity(intent, options.toBundle())
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        //Fade in and out animation
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

