package com.example.justaddgelang

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ManageCategoriesActivity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
    private lateinit var recyclerView: RecyclerView
    private lateinit var btnAddCategory: Button
    private lateinit var adapter: AdminCategoryAdapter
    private val categoryList = mutableListOf<Category>()

    private val EDIT_CATEGORY_REQUEST_CODE = 101
    private val ADD_CATEGORY_REQUEST_CODE = 102

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_category)

        progressBar = findViewById(R.id.progressBar)
        recyclerView = findViewById(R.id.recyclerView)
        btnAddCategory = findViewById(R.id.btnAddCategory)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = AdminCategoryAdapter(categoryList, this) { category ->
            val intent = Intent(this, EditCategoryActivity::class.java)
            intent.putExtra("CATEGORY_ID", category.category_id)
            startActivityForResult(intent, EDIT_CATEGORY_REQUEST_CODE)
        }
        recyclerView.adapter = adapter

        btnAddCategory.setOnClickListener {
            val intent = Intent(this, AddCategoryActivity::class.java)
            startActivityForResult(intent, ADD_CATEGORY_REQUEST_CODE)
        }

        fetchCategories()
    }

    private fun fetchCategories() {
        progressBar.visibility = View.VISIBLE
        RetrofitClient.apiService.getCategories().enqueue(object : Callback<List<Category>> {
            override fun onResponse(call: Call<List<Category>>, response: Response<List<Category>>) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    categoryList.clear()
                    response.body()?.let { categoryList.addAll(it) }
                    adapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(this@ManageCategoriesActivity, "Failed to load categories", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Category>>, t: Throwable) {
                progressBar.visibility = View.GONE
                Toast.makeText(this@ManageCategoriesActivity, "Error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if ((requestCode == EDIT_CATEGORY_REQUEST_CODE || requestCode == ADD_CATEGORY_REQUEST_CODE)
            && resultCode == Activity.RESULT_OK
        ) {
            Toast.makeText(this, "Refreshing category list...", Toast.LENGTH_SHORT).show() // Debug
            fetchCategories()
        }
    }
}
