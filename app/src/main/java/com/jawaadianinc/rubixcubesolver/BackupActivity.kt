package com.jawaadianinc.rubixcubesolver

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.squareup.picasso.Picasso
import java.io.File
import java.util.*

class BackupActivity : AppCompatActivity() {

    private var mDriveServiceHelper: DriveServiceHelper? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_backup)
        val backup: Button = findViewById(R.id.backup)
        val restore: Button = findViewById(R.id.restore)
        val userPFP: ImageView = findViewById(R.id.userPFP)

        if (isLoggedIn()) {
            val account = GoogleSignIn.getLastSignedInAccount(this)
            val credential = GoogleAccountCredential.usingOAuth2(
                this, Collections.singleton(DriveScopes.DRIVE_FILE)
            )
            credential.selectedAccount = account?.account
            val googleDriveService = Drive.Builder(
                AndroidHttp.newCompatibleTransport(),
                GsonFactory(),
                credential
            )
                .setApplicationName("Cube Assistant Drive API")
                .build()
            mDriveServiceHelper = DriveServiceHelper(googleDriveService)
            val picture = account?.photoUrl
            Picasso.get().load(picture).into(userPFP)

        }

        backup.setOnClickListener {
            if (isLoggedIn()) {
                val progressDialog = ProgressDialog(this)
                progressDialog.setTitle("Uploading to Google Drive")
                progressDialog.setMessage("Backing up the saved times database")

                if (doesDatabaseExist(this, "times.db")) {
                    progressDialog.show()
                    mDriveServiceHelper?.createFile("TestFile")?.addOnSuccessListener {
                        progressDialog.dismiss()
                        Toast.makeText(this, "Success! $it", Toast.LENGTH_SHORT).show()
                    }?.addOnFailureListener {
                        progressDialog.dismiss()
                        Toast.makeText(this, "Error $it", Toast.LENGTH_SHORT).show()
                    }

                } else {
                    Toast.makeText(this, "Not found file...", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Not signed in...", Toast.LENGTH_SHORT).show()
            }
        }

        restore.setOnClickListener {
            if (isLoggedIn()) {
                val account = GoogleSignIn.getLastSignedInAccount(this)
                if (doesDatabaseExist(this, "times.db")) {
                    Toast.makeText(this, "Already have file!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Restoring...", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    private fun isLoggedIn(): Boolean {
        return GoogleSignIn.getLastSignedInAccount(this) != null
    }

    private fun doesDatabaseExist(context: Context, dbName: String): Boolean {
        val dbFile: File = context.getDatabasePath(dbName)
        return dbFile.exists()
    }
}