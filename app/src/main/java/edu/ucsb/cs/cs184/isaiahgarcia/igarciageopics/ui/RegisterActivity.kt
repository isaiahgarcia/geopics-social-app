package edu.ucsb.cs.cs184.isaiahgarcia.igarciageopics.ui

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import edu.ucsb.cs.cs184.isaiahgarcia.igarciageopics.R
import edu.ucsb.cs.cs184.isaiahgarcia.igarciageopics.models.User

class RegisterActivity : AppCompatActivity() {
    private var etRegEmail: TextInputEditText? = null
    private var etRegPassword: TextInputEditText? = null
    private lateinit var tvLoginHere: TextView
    private lateinit var btnRegister: Button
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDb: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        etRegEmail = findViewById(R.id.etRegEmail)
        etRegPassword = findViewById(R.id.etRegPass)
        tvLoginHere = findViewById(R.id.tvLoginHere)
        btnRegister = findViewById(R.id.btnRegister)
        mAuth = FirebaseAuth.getInstance()
        mDb = FirebaseFirestore.getInstance()
        btnRegister.setOnClickListener { createUser() }
        tvLoginHere.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
        })
    }

    private fun createUser() {
        val email = etRegEmail!!.text.toString()
        val password = etRegPassword!!.text.toString()
        if (TextUtils.isEmpty(email)) {
            etRegEmail!!.error = "Email cannot be empty"
            etRegEmail!!.requestFocus()
        } else if (TextUtils.isEmpty(password)) {
            etRegPassword!!.error = "Password cannot be empty"
            etRegPassword!!.requestFocus()
        } else {
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        this@RegisterActivity,
                        "User registered successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    val userID: String = FirebaseAuth.getInstance().uid.toString()

                    val settings: FirebaseFirestoreSettings = FirebaseFirestoreSettings.Builder()
                        .build()
                    mDb.firestoreSettings = settings

                    val newUserRef = userID.let {
                        mDb
                            .collection(getString(R.string.collection_users))
                            .document(it)
                    }

                    val user = User()
                    user.setUserID(userID)
                    user.setEmail(email)
                    user.setUsername(email.substring(0, email.indexOf("@")))
                    user.setAvatar("1")

                    newUserRef.set(user).addOnSuccessListener(OnSuccessListener<Void?> {
                        Log.d(
                            TAG,
                            "onSuccess: user Profile is created for $userID"
                        )
                    }).addOnFailureListener(
                        OnFailureListener { e -> Log.d(TAG, "onFailure: $e") })

                    startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                } else {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Registration Error: " + task.exception?.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun redirectLoginScreen() {
        Log.d(TAG, "redirectLoginScreen: redirecting to login screen.")
        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

}