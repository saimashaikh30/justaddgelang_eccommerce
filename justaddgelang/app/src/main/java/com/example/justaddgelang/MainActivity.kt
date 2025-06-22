package com.example.justaddgelang

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.justaddgelang.fragments.CartFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadFragment(HomeFragment())

        findViewById<View>(R.id.nav_home).setOnClickListener {
            loadFragment(HomeFragment())
        }

        findViewById<View>(R.id.nav_profile).setOnClickListener {
            if (SharedPreferencesHelper.isLoggedIn(this)) {
                startActivity(Intent(this, ProfileActivity::class.java))
            } else {
                startActivity(Intent(this, Login::class.java))
            }
        }

        findViewById<View>(R.id.nav_category).setOnClickListener {
            loadFragment(CategoryFragment())
        }

        findViewById<View>(R.id.nav_cart).setOnClickListener {
            loadFragment(CartFragment())
        }

        findViewById<View>(R.id.nav_wishlist).setOnClickListener {
            loadFragment(WishlistFragment())
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.middle_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}
