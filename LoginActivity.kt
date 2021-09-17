package com.wintech.diydr

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.wintech.diydr.LoginActivity

class LoginActivity : AppCompatActivity() {
    private var email: EditText? = null
    private var password: EditText? = null
    private var login: Button? = null
    private var registerUser: TextView? = null
    private var mAuth: FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        email = findViewById(R.id.email)
        password = findViewById(R.id.password)
        login = findViewById(R.id.login)
        registerUser = findViewById(R.id.register_user)
        mAuth = FirebaseAuth.getInstance()
        registerUser.setOnClickListener(View.OnClickListener { startActivity(Intent(this@LoginActivity, RegisterActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)) })
        login.setOnClickListener(View.OnClickListener {
            val txt_email = email.getText().toString()
            val txt_password = password.getText().toString()
            if (TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password)) {
                Toast.makeText(this@LoginActivity, "Empty Credentials!", Toast.LENGTH_SHORT).show()
            } else {
                loginUser(txt_email, txt_password)
            }
        })
    }

    private fun loginUser(email: String, password: String) {
        mAuth!!.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this@LoginActivity, "Update the profile " +
                        "for better expereince", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                finish()
            }
        }.addOnFailureListener { e -> Toast.makeText(this@LoginActivity, e.message, Toast.LENGTH_SHORT).show() }
    }
}