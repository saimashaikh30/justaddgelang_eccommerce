package com.example.justaddgelang

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ManageProductsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var btnAddProduct: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var searchView: SearchView
    private lateinit var adminProductAdapter: AdminProductAdapter
    private var productList = mutableListOf<Product>()

    companion object {
        private const val ADD_PRODUCT_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_products)

        recyclerView = findViewById(R.id.recyclerViewProducts)
        btnAddProduct = findViewById(R.id.btnAddProduct)
        progressBar = findViewById(R.id.progressBar)
        searchView = findViewById(R.id.searchView)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adminProductAdapter = AdminProductAdapter(productList, this)
        recyclerView.adapter = adminProductAdapter

        btnAddProduct.setOnClickListener {
            val intent = Intent(this, AddProductActivity::class.java)
            startActivityForResult(intent, ADD_PRODUCT_REQUEST_CODE)
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                adminProductAdapter.filter(newText.orEmpty())
                return true
            }
        })

        fetchProductsFromApi()
    }

    private fun fetchProductsFromApi() {
        progressBar.visibility = View.VISIBLE

        RetrofitClient.apiService.getProducts().enqueue(object : Callback<List<Product>> {
            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    productList.clear()
                    response.body()?.let {
                        productList.addAll(it)
                        adminProductAdapter.updateList(it)
                    }
                } else {
                    Toast.makeText(this@ManageProductsActivity, "Failed to load products", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                progressBar.visibility = View.GONE
                Toast.makeText(this@ManageProductsActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_PRODUCT_REQUEST_CODE && resultCode == RESULT_OK) {
            fetchProductsFromApi() // Refresh product list
        }
    }
}
