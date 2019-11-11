package com.example.medjournal.registerlogin

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.example.medjournal.home.HomeActivity
import com.example.medjournal.R
import com.example.medjournal.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*

/**
 * A class that performs register activity
 */
class RegisterActivity : AppCompatActivity() {

    companion object {
        const val TAG = "RegisterActivity"
    }

    /**
     * Instantiate register activity view
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        register_button_register.setOnClickListener {
            performRegister()
        }

        already_have_account_text_view.setOnClickListener {
            Log.d(TAG, "Try to show login activity")

            // launch the login activity somehow
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        selectphoto_button_register.setOnClickListener {
            Log.d(TAG, "Try to show photo selector")

            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
    }

    /**
     * Uri object of selected profile photo from the mobile phone
     */
    var selectedPhotoUri: Uri? = null

    /**
     * Get Uri from the photo gallery of mobile phone and control interactions of photo gallery
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            // proceed and check what the selected image was....
            Log.d(TAG, "Photo was selected")

            selectedPhotoUri = data.data

            @Suppress("DEPRECATION") val bitmap =
                MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

            selectphoto_imageview_register.setImageBitmap(bitmap)

            selectphoto_button_register.alpha = 0f

//      val bitmapDrawable = BitmapDrawable(bitmap)
//      selectphoto_button_register.setBackgroundDrawable(bitmapDrawable)
        }
    }

    /**
     * Perform registration with Firebase Auth
     *
     * Perform registration with email and password, validate user input
     * and handle registration error
     */
    private fun performRegister() {
        val username = username_edittext_register.text.toString()
        val email = email_edittext_register.text.toString()
        val password = password_edittext_register.text.toString()

        // else if successful
        when {
            username.isEmpty() -> {
                Toast.makeText(
                    this,
                    resources.getString(R.string.toast_empty_username),
                    Toast.LENGTH_SHORT
                ).show()
                return
            }
            email.isEmpty() -> {
                Toast.makeText(
                    this,
                    resources.getString(R.string.toast_empty_email),
                    Toast.LENGTH_SHORT
                ).show()
                return
            }
            password.isEmpty() -> {
                Toast.makeText(
                    this,
                    resources.getString(R.string.toast_empty_password),
                    Toast.LENGTH_SHORT
                ).show()
                return
            }

            // Firebase Authentication to create a user with email and password
            else -> {
                Log.d(TAG, "Attempting to create user with email: $email")

                // Firebase Authentication to create a user with email and password
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener {
                        if (!it.isSuccessful) return@addOnCompleteListener

                        // else if successful
                        Log.d(TAG, "Successfully created user with uid: ${it.result.user.uid}")

                        uploadImageToFirebaseStorage()
                    }
                    .addOnFailureListener {
                        Log.d(TAG, "Failed to create user: ${it.message}")
                        Toast.makeText(
                            this, resources.getString(R.string.toast_failed_to_create_user)
                                    + "${it.message}", Toast.LENGTH_SHORT
                        )
                            .show()
                    }
            }
        }

    }

    /**
     * Upload image to Firebase storage and handle error
     */
    private fun uploadImageToFirebaseStorage() {
        if (selectedPhotoUri == null) {
            saveUserToFirebaseDatabase("No image")
            return
        }

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d(TAG, "Successfully uploaded image: ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    Log.d(TAG, "File Location: $it")

                    saveUserToFirebaseDatabase(it.toString())
                }
            }
            .addOnFailureListener {
                Log.d(TAG, "Failed to upload image to storage: ${it.message}")
            }
    }

    /**
     * Save user to Firebase database with profile image url in Firebase Storage
     */
    private fun saveUserToFirebaseDatabase(profileImageUrl: String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        val user = User(
            uid,
            username_edittext_register.text.toString(),
            profileImageUrl
        )

        ref.setValue(user)
            .addOnSuccessListener {
                Log.d(TAG, "Finally we saved the user to Firebase Database")

                val intent = Intent(this, HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            .addOnFailureListener {
                Log.d(TAG, "Failed to set value to database: ${it.message}")
            }
    }

}