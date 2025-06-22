package com.example.justaddgelang

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class EditProductActivity : AppCompatActivity() {

    private lateinit var editProductName: EditText
    private lateinit var editProductPrice: EditText
    private lateinit var editProductStock: EditText
    private lateinit var spinnerCategory: Spinner
    private lateinit var imageViewProduct: ImageView
    private lateinit var buttonSelectImage: Button
    private lateinit var buttonUpdateProduct: Button

    private var selectedCategoryId: Int = 0
    private var imageUri: Uri? = null
    private val PICK_IMAGE_REQUEST = 1

    private var product: Product? = null
    private var categories: List<Category> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_product)

        editProductName = findViewById(R.id.editProductName)
        editProductPrice = findViewById(R.id.editProductPrice)
        editProductStock = findViewById(R.id.editProductStock)
        spinnerCategory = findViewById(R.id.editProductCategorySpinner)
        imageViewProduct = findViewById(R.id.editProductImageView)
        buttonSelectImage = findViewById(R.id.btnSelectImage)
        buttonUpdateProduct = findViewById(R.id.btnUpdateProduct)

        product = intent.getSerializableExtra("product") as? Product

        product?.let {
            editProductName.setText(it.productName)
            editProductPrice.setText(it.productPrice.toString())
            editProductStock.setText(it.productStock.toString())
            Glide.with(this).load(it.imageUrl).into(imageViewProduct)
        }

        loadCategories()

        buttonSelectImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        buttonUpdateProduct.setOnClickListener {
            updateProduct()
        }
    }

    private fun loadCategories() {
        RetrofitClient.apiService.getCategories().enqueue(object : Callback<List<Category>> {
            override fun onResponse(call: Call<List<Category>>, response: Response<List<Category>>) {
                if (response.isSuccessful) {
                    categories = response.body() ?: emptyList()
                    val categoryNames = categories.map { it.category_name }

                    val adapter = ArrayAdapter(this@EditProductActivity, android.R.layout.simple_spinner_item, categoryNames)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerCategory.adapter = adapter

                    // Move UI setup inside this block to ensure it's only done after spinner is loaded
                    product?.let { prod ->
                        editProductName.setText(prod.productName)
                        editProductPrice.setText(prod.productPrice.toString())
                        editProductStock.setText(prod.productStock.toString())

                        // Set category spinner selection
                        val index = categories.indexOfFirst { it.category_id == prod.categoryId }
                        if (index != -1) {
                            spinnerCategory.setSelection(index)
                            selectedCategoryId = prod.categoryId
                        }

                        Glide.with(this@EditProductActivity).load(prod.imageUrl).into(imageViewProduct)
                    }

                    spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                            selectedCategoryId = categories[position].category_id
                        }

                        override fun onNothingSelected(parent: AdapterView<*>) {}
                    }
                }
            }

            override fun onFailure(call: Call<List<Category>>, t: Throwable) {
                Toast.makeText(this@EditProductActivity, "Failed to load categories", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateProduct() {
        val name = editProductName.text.toString().trim()
        val price = editProductPrice.text.toString().toDoubleOrNull() ?: 0.0
        val stock = editProductStock.text.toString().toIntOrNull() ?: 0
        val id = product?.productId ?: return

        val productId = id.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val catId = selectedCategoryId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val prodName = name.toRequestBody("text/plain".toMediaTypeOrNull())
        val prodPrice = price.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val prodStock = stock.toString().toRequestBody("text/plain".toMediaTypeOrNull())

        val imagePart: MultipartBody.Part? = imageUri?.let {
            val inputStream = contentResolver.openInputStream(it)
            val tempFile = File(cacheDir, "temp_image.jpg")
            tempFile.outputStream().use { fileOut -> inputStream?.copyTo(fileOut) }

            val reqFile = tempFile.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("productPicture", tempFile.name, reqFile)
        }

        RetrofitClient.apiService.editProductWithImage(
            productId, catId, prodName, prodPrice, prodStock, imagePart
        ).enqueue(object : Callback<EditProductResponse> {
            override fun onResponse(call: Call<EditProductResponse>, response: Response<EditProductResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@EditProductActivity, "Product updated", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@EditProductActivity, "Update failed: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<EditProductResponse>, t: Throwable) {
                Toast.makeText(this@EditProductActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }




    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.data
            imageViewProduct.setImageURI(imageUri)
        }
    }
}
