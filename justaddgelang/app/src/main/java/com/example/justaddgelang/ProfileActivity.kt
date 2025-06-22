package com.example.justaddgelang
import com.example.justaddgelang.SharedPreferencesHelper
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class ProfileActivity : AppCompatActivity() {

    private lateinit var btnListView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!SharedPreferencesHelper.isLoggedIn(this)) {
            startActivity(Intent(this, Login::class.java))
            finish()
            return
        }

        setContentView(R.layout.fragment_profile)

        val usernameTextView: TextView = findViewById(R.id.textUsername)
        val phoneTextView: TextView = findViewById(R.id.textPhoneNumber)
        btnListView = findViewById(R.id.btnListView)

        // Set username and phone number
        usernameTextView.text = SharedPreferencesHelper.getUsername(this)
        phoneTextView.text = "Phone: ${SharedPreferencesHelper.getPhoneNumber(this)}"

        // Set up ListView adapter
        val actions = arrayOf("Edit Profile", "Address", "Orders", "Logout")
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, actions)
        btnListView.adapter = adapter

     //    Set item click listener to handle button clicks
        btnListView.setOnItemClickListener { _, _, position, _ ->
            when (position) {
                0 -> startActivity(Intent(this, EditProfileActivity::class.java))
                1 -> startActivity(Intent(this, AddressActivity::class.java))
               2 -> startActivity(Intent(this, OrdersActivity::class.java))
                3 -> logout()
            }
        }
    }

    private fun logout() {
        SharedPreferencesHelper.saveLoginStatus(this, false)
        startActivity(Intent(this, Login::class.java))
        finish()
    }
}
