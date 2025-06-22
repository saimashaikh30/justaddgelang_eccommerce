package com.example.justaddgelang

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class AdminHomePageActivity : AppCompatActivity() {

    private lateinit var btnManageProducts: Button
    private lateinit var btnManageCategories: Button
    private lateinit var btnManageOrders: Button
    private lateinit var btnProfile: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        btnManageProducts = findViewById(R.id.btnManageProducts)
        btnManageCategories = findViewById(R.id.btnManageCategories)
        btnManageOrders = findViewById(R.id.btnManageOrders)
        btnProfile = findViewById(R.id.btnProfile)

        btnManageProducts.setOnClickListener {
            startActivity(Intent(this, ManageProductsActivity::class.java))
        }

        btnManageCategories.setOnClickListener {
            startActivity(Intent(this, ManageCategoriesActivity::class.java))
        }
//
        btnManageOrders.setOnClickListener {
            startActivity(Intent(this, ManageOrdersActivity::class.java))
        }

        btnProfile.setOnClickListener {
            startActivity(Intent(this, AdminProfile::class.java))
        }
    }
}
