package com.example.justaddgelang

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditCategoryActivity : AppCompatActivity() {

    private lateinit var etCategoryName: EditText
    private lateinit var btnSaveCategory: Button
    private var categoryId: Int = -1
    private var currentCategory: Category? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_category)

        etCategoryName = findViewById(R.id.etCategoryName)
        btnSaveCategory = findViewById(R.id.btnSaveCategory)

        categoryId = intent.getIntExtra("CATEGORY_ID", -1)

        if (categoryId != -1) {
            fetchCategoryFromServer(categoryId)
        } else {
            Toast.makeText(this, "Invalid category ID", Toast.LENGTH_SHORT).show()
            finish()
        }

        btnSaveCategory.setOnClickListener {
            updateCategory()
        }
    }

    private fun fetchCategoryFromServer(id: Int) {
        RetrofitClient.apiService.getCategory(id)
            .enqueue(object : Callback<Category> {
                override fun onResponse(call: Call<Category>, response: Response<Category>) {
                    if (response.isSuccessful && response.body() != null) {
                        currentCategory = response.body()
                        etCategoryName.setText(currentCategory?.category_name ?: "")
                    } else {
                        Toast.makeText(this@EditCategoryActivity, "Category not found", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }

                override fun onFailure(call: Call<Category>, t: Throwable) {
                    Toast.makeText(this@EditCategoryActivity, "Failed: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
                    finish()
                }
            })
    }

    private fun updateCategory() {
        val updatedName = etCategoryName.text.toString().trim()

        if (updatedName.isEmpty()) {
            Toast.makeText(this, "Please enter a category name", Toast.LENGTH_SHORT).show()
            return
        }

        currentCategory?.let {
            it.category_name = updatedName

            RetrofitClient.apiService.updateCategory(it.category_id, it)
                .enqueue(object : Callback<ApiResponse> {
                    override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                        if (response.isSuccessful && response.body()?.success == true) {
                            Toast.makeText(this@EditCategoryActivity, "Category updated", Toast.LENGTH_SHORT).show()
                            setResult(Activity.RESULT_OK)
                            finish()
                        } else {
                            Toast.makeText(this@EditCategoryActivity, "Update failed", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                        Toast.makeText(this@EditCategoryActivity, "Error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }
}
