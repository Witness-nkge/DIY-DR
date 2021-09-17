package com.wintech.diydr

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth

class OptionsActivity : AppCompatActivity() {
    private var settings: TextView? = null
    private var logOut: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_options)
        settings = findViewById(R.id.settings)
        logOut = findViewById(R.id.logout)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setTitle("Options")
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }
        logOut.setOnClickListener(View.OnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this@OptionsActivity, SplashActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK))
            finish()
        })
    }
}