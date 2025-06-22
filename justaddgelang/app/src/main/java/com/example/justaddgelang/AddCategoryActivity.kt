package com.example.justaddgelang

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
//import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddCategoryActivity : AppCompatActivity() {

    private lateinit var etCategoryName: EditText
    private lateinit var btnSaveCategory: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_category)

        etCategoryName = findViewById(R.id.etCategoryName)
        btnSaveCategory = findViewById(R.id.btnSaveCategory)

        btnSaveCategory.setOnClickListener {
            val categoryName = etCategoryName.text.toString().trim()

            if (categoryName.isEmpty()) {
                Toast.makeText(this, "Category name cannot be empty", Toast.LENGTH_SHORT).show()
            } else {
                val newCategory = Category(0, categoryName) // '0' is placeholder, server assigns ID

                RetrofitClient.apiService.addCategory(newCategory).enqueue(object : Callback<ApiResponse> {
                    override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                        if (response.isSuccessful && response.body()?.success == true) {
                            Toast.makeText(this@AddCategoryActivity, "Category added successfully", Toast.LENGTH_SHORT).show()
                            setResult(Activity.RESULT_OK) // ✅ Important to trigger refresh in ManageCategoriesActivity
                            finish()
                        } else {
                            Toast.makeText(this@AddCategoryActivity, "Failed to add category", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                        Toast.makeText(this@AddCategoryActivity, "Error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }
    }
}
