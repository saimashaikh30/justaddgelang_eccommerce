package com.example.justaddgelang

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class AddProductActivity : AppCompatActivity() {

    private lateinit var editTextProductName: EditText
    private lateinit var editTextPrice: EditText
    private lateinit var editTextQuantity: EditText
    private lateinit var imageViewProduct: ImageView
    private lateinit var spinnerCategory: Spinner
    private lateinit var buttonChooseImage: Button
    private lateinit var buttonSave: Button

    private var selectedImageUri: Uri? = null
    private var categoryList: List<Category> = emptyList()
    private var selectedCategoryId: Int = -1

    companion object {
        private const val IMAGE_PICK_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)

        editTextProductName = findViewById(R.id.etProductName)
        editTextPrice = findViewById(R.id.etProductPrice)
        editTextQuantity = findViewById(R.id.etProductQuantity)
        imageViewProduct = findViewById(R.id.imageViewProduct)
        spinnerCategory = findViewById(R.id.spinnerCategory)
        buttonChooseImage = findViewById(R.id.btnPickImage)
        buttonSave = findViewById(R.id.btnSaveProduct)

        fetchCategories()

        buttonChooseImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, IMAGE_PICK_CODE)
        }

        buttonSave.setOnClickListener {
            saveProduct()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_CODE && resultCode == RESULT_OK) {
            selectedImageUri = data?.data
            imageViewProduct.setImageURI(selectedImageUri)
        }
    }

    private fun fetchCategories() {
        RetrofitClient.apiService.getCategories().enqueue(object : Callback<List<Category>> {
            override fun onResponse(call: Call<List<Category>>, response: Response<List<Category>>) {
                if (response.isSuccessful) {
                    categoryList = response.body() ?: emptyList()
                    val names = categoryList.map { it.category_name }
                    val adapter = ArrayAdapter(this@AddProductActivity, android.R.layout.simple_spinner_item, names)
                    spinnerCategory.adapter = adapter
                    spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                            selectedCategoryId = categoryList[position].category_id
                        }

                        override fun onNothingSelected(parent: AdapterView<*>) {}
                    }
                }
            }

            override fun onFailure(call: Call<List<Category>>, t: Throwable) {
                Toast.makeText(this@AddProductActivity, "Failed: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun saveProduct() {
        val name = editTextProductName.text.toString().trim()
        val priceText = editTextPrice.text.toString().trim()
        val stockText = editTextQuantity.text.toString().trim()

        if (name.isEmpty()) {
            editTextProductName.error = "Product name is required"
            editTextProductName.requestFocus()
            return
        }

        if (priceText.isEmpty()) {
            editTextPrice.error = "Price is required"
            editTextPrice.requestFocus()
            return
        }

        val price = priceText.toDoubleOrNull()
        if (price == null || price <= 0) {
            editTextPrice.error = "Enter a valid price greater than 0"
            editTextPrice.requestFocus()
            return
        }

        if (stockText.isEmpty()) {
            editTextQuantity.error = "Stock quantity is required"
            editTextQuantity.requestFocus()
            return
        }

        val stock = stockText.toIntOrNull()
        if (stock == null || stock <= 0) {
            editTextQuantity.error = "Enter a valid stock quantity greater than 0"
            editTextQuantity.requestFocus()
            return
        }

        if (selectedImageUri == null) {
            Toast.makeText(this, "Please select a product image", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedCategoryId == -1) {
            Toast.makeText(this, "Please select a product category", Toast.LENGTH_SHORT).show()
            return
        }

        // All validations passed, proceed to upload
        val imagePart = prepareFilePart(selectedImageUri!!)
        val namePart = name.toRequestBody("text/plain".toMediaTypeOrNull())
        val pricePart = price.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val stockPart = stock.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val categoryPart = selectedCategoryId.toString().toRequestBody("text/plain".toMediaTypeOrNull())

        RetrofitClient.apiService.addProductWithImage(imagePart, categoryPart, namePart, pricePart, stockPart)
            .enqueue(object : Callback<ApiResponse> {
                override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@AddProductActivity, "Product added!", Toast.LENGTH_SHORT).show()
                        setResult(RESULT_OK)
                        finish()
                    } else {
                        Toast.makeText(this@AddProductActivity, "Failed to add product", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                    Toast.makeText(this@AddProductActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }


    private fun prepareFilePart(uri: Uri): MultipartBody.Part {
        val fileName = getFileName(uri)
        val inputStream = contentResolver.openInputStream(uri)!!
        val file = File(cacheDir, fileName)
        file.outputStream().use { inputStream.copyTo(it) }

        val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("productPicture", file.name, requestFile)
    }

    @SuppressLint("Range")
    private fun getFileName(uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) result = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/') ?: -1
            if (cut != -1) result = result?.substring(cut + 1)
        }
        return result ?: "temp_image.jpg"
    }
}

