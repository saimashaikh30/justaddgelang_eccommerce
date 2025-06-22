package com.example.justaddgelang

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splashscreen)

        Handler(Looper.getMainLooper()).postDelayed({
            val isLoggedIn = SharedPreferencesHelper.isLoggedIn(this)
            val userType = SharedPreferencesHelper.getUsertype(this)

            val intent = when {
                isLoggedIn && userType == "admin" -> Intent(this, AdminHomePageActivity::class.java)
                isLoggedIn && userType == "user" -> Intent(this, MainActivity::class.java)
                else -> Intent(this, Login::class.java)
            }

            startActivity(intent)
            finish()

        }, 2000)
    }
}
