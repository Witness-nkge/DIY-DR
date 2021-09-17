package com.wintech.diydr

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.wintech.diydr.SplashActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Handler().postDelayed({ startActivity(Intent(this@SplashActivity, LoginActivity::class.java)) }, SPLASH_TIME_OUT.toLong())
    }

    companion object {
        private const val SPLASH_TIME_OUT = 2000
    }
}