package edu.ucsb.cs.cs184.isaiahgarcia.igarciageopics.ui

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import edu.ucsb.cs.cs184.isaiahgarcia.igarciageopics.R
import edu.ucsb.cs.cs184.isaiahgarcia.igarciageopics.UserClient
import edu.ucsb.cs.cs184.isaiahgarcia.igarciageopics.models.User


class LoginActivity : AppCompatActivity() {
    private var etLoginEmail: TextInputEditText? = null
    private var etLoginPassword: TextInputEditText? = null
    private lateinit var tvRegisterHere: TextView
    private lateinit var btnLogin: Button
    var mAuth: FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        etLoginEmail = findViewById(R.id.etLoginEmail)
        etLoginPassword = findViewById(R.id.etLoginPass)
        tvRegisterHere = findViewById(R.id.tvRegisterHere)
        btnLogin = findViewById(R.id.btnLogin)
        mAuth = FirebaseAuth.getInstance()
        btnLogin.setOnClickListener { loginUser() }
        tvRegisterHere.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
        })
    }

    private fun loginUser() {
        val email = etLoginEmail!!.text.toString()
        val password = etLoginPassword!!.text.toString()
        if (TextUtils.isEmpty(email)) {
            etLoginEmail!!.error = "Email cannot be empty"
            etLoginEmail!!.requestFocus()
        } else if (TextUtils.isEmpty(password)) {
            etLoginPassword!!.error = "Password cannot be empty"
            etLoginPassword!!.requestFocus()
        } else {
            mAuth!!.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        this@LoginActivity,
                        "User logged in successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    val db = FirebaseFirestore.getInstance()
                    val settings: FirebaseFirestoreSettings = FirebaseFirestoreSettings.Builder()
                        .build()
                    db.firestoreSettings = settings
                    val userRef = db.collection(getString(R.string.collection_users))
                        .document(FirebaseAuth.getInstance().uid!!)
                    userRef.get().addOnCompleteListener { tasker ->
                        if (tasker.isSuccessful) {
                            val user = tasker.result.toObject(User::class.java)
                            (applicationContext as UserClient).user = user
                        }
                    }
                    startActivity(Intent(this@LoginActivity, MapsActivity::class.java))
                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        "Log in Error: " + task.exception?.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}