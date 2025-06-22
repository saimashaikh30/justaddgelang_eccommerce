package com.example.justaddgelang

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProductAdapter
    private lateinit var searchView: SearchView
    private lateinit var layoutManager: GridLayoutManager

    private var productList = mutableListOf<Product>()
    private var currentQuery: String? = null
    private var categoryId: Int? = null
    private var userId: Int = 0

    private var isLoading = false
    private var isLastPage = false
    private var currentPage = 1
    private val pageSize = 10

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        searchView = view.findViewById(R.id.searchView)
        layoutManager = GridLayoutManager(context, 2)
        recyclerView.layoutManager = layoutManager

        adapter = ProductAdapter(
            context = requireContext(),
            productList = productList,
            onItemClick = { product ->
                val intent = Intent(requireContext(), ProductDetailActivity::class.java)
                intent.putExtra("product", product)
                startActivity(intent)
            }
        )

        recyclerView.adapter = adapter

        val sharedPref = requireContext().getSharedPreferences("MyAppPrefs", 0)
        userId = sharedPref.getInt("userId", 0)

        categoryId = arguments?.getInt("categoryId")

        fetchCartItemsAndLoadProducts()

        // SearchView logic
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                currentQuery = query
                resetPagination()
                fetchCartItemsAndLoadProducts()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        // Pagination logic
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(rv, dx, dy)
                if (!rv.canScrollVertically(1) && !isLoading && !isLastPage) {
                    loadProducts()
                }
            }
        })

        return view
    }

    private fun resetPagination() {
        isLoading = false
        isLastPage = false
        currentPage = 1
        adapter.clearProducts()
    }

    private fun fetchCartItemsAndLoadProducts() {
//        RetrofitClient.apiService.getCartItems(userId).enqueue(object : Callback<List<CartItem>> {
//            override fun onResponse(call: Call<List<CartItem>>, response: Response<List<CartItem>>) {
//                if (response.isSuccessful && response.body() != null) {
//                   // val cartProductIds = response.body()!!.map { it.product.productId }
//                }
//                loadProducts()
//            }
//
//            override fun onFailure(call: Call<List<CartItem>>, t: Throwable) {
//                Toast.makeText(context, "Failed to fetch cart items", Toast.LENGTH_SHORT).show()
//                loadProducts()
//            }
//        })
        loadProducts()
    }

    private fun loadProducts() {
        isLoading = true

        val call: Call<List<Product>> = when {
            !currentQuery.isNullOrBlank() -> {
                RetrofitClient.apiService.searchProducts(currentQuery!!, currentPage, pageSize, userId)
            }
            categoryId != null -> {
                RetrofitClient.apiService.getProductsByCategory(categoryId!!,  userId)
            }
            else -> {
                RetrofitClient.apiService.getLatestProducts(userId)
            }
        }

        call.enqueue(object : Callback<List<Product>> {
            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                isLoading = false
                if (response.isSuccessful && response.body() != null) {
                    val products = response.body()!!
                    adapter.updateProducts(products)
                    isLastPage = products.size < pageSize
                    currentPage++
                } else {
                    Toast.makeText(context, "Failed to load products", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                isLoading = false
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
