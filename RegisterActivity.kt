package com.wintech.diydr

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.wintech.diydr.RegisterActivity
import java.util.*

class RegisterActivity : AppCompatActivity() {
    private var username: EditText? = null
    private var name: EditText? = null
    private var email: EditText? = null
    private var password: EditText? = null
    private var register: Button? = null
    private var loginUser: TextView? = null
    private var mRootRef: DatabaseReference? = null
    private var mAuth: FirebaseAuth? = null
    var pd: ProgressDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        username = findViewById(R.id.username)
        name = findViewById(R.id.name)
        email = findViewById(R.id.email)
        password = findViewById(R.id.password)
        register = findViewById(R.id.register)
        loginUser = findViewById(R.id.login_user)
        mRootRef = FirebaseDatabase.getInstance().reference
        mAuth = FirebaseAuth.getInstance()
        pd = ProgressDialog(this)
        loginUser.setOnClickListener(View.OnClickListener { startActivity(Intent(this@RegisterActivity, LoginActivity::class.java)) })
        register.setOnClickListener(View.OnClickListener {
            val txtUsername = username.getText().toString()
            val txtName = name.getText().toString()
            val txtEmail = email.getText().toString()
            val txtPassword = password.getText().toString()
            if (TextUtils.isEmpty(txtUsername) || TextUtils.isEmpty(txtName)
                    || TextUtils.isEmpty(txtEmail) || TextUtils.isEmpty(txtPassword)) {
                Toast.makeText(this@RegisterActivity, "Empty credentials!", Toast.LENGTH_SHORT).show()
            } else if (txtPassword.length < 6) {
                Toast.makeText(this@RegisterActivity, "Password too short!", Toast.LENGTH_SHORT).show()
            } else {
                registerUser(txtUsername, txtName, txtEmail, txtPassword)
            }
        })
    }

    private fun registerUser(username: String, name: String, email: String, password: String) {
        pd!!.setMessage("Please Wail!")
        pd!!.show()
        mAuth!!.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
            val map = HashMap<String, Any>()
            map["name"] = name
            map["email"] = email
            map["username"] = username
            map["id"] = mAuth!!.currentUser!!.uid
            map["bio"] = ""
            map["imageurl"] = "default"
            mRootRef!!.child("Users").child(mAuth!!.currentUser!!.uid).setValue(map).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    pd!!.dismiss()
                    Toast.makeText(this@RegisterActivity, "Update the profile " +
                            "for better expereince", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                    finish()
                }
            }
        }.addOnFailureListener { e ->
            pd!!.dismiss()
            Toast.makeText(this@RegisterActivity, e.message, Toast.LENGTH_SHORT).show()
        }
    }
}