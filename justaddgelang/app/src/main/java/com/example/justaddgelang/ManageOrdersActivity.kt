package com.example.justaddgelang

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Response  // ✅ CORRECT
import retrofit2.Call
import retrofit2.Callback

class ManageOrdersActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var orderAdapter: OrderAdapter
    private var orderList = mutableListOf<Order>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_orders)

        recyclerView = findViewById(R.id.recyclerViewOrders)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize the adapter, but don't pass the list yet
        orderAdapter = OrderAdapter(this)

        recyclerView.adapter = orderAdapter

        // Simulate fetching orders
        fetchOrders()
    }

    private fun fetchOrders() {
        val apiService = RetrofitClient.apiService
        apiService.getAllOrders().enqueue(object : Callback<List<Order>> {
            override fun onResponse(call: Call<List<Order>>, response: Response<List<Order>>) {
                if (response.isSuccessful && response.body() != null) {
                    orderList = response.body()!!.toMutableList()
                    Log.d("ManageOrdersActivity", "Orders Loaded: ${orderList.size}")
                    orderAdapter.setOrders(orderList)
                } else {
                    Log.e("ManageOrdersActivity", "Failed to load orders")
                }
            }

            override fun onFailure(call: Call<List<Order>>, t: Throwable) {
                Log.e("ManageOrdersActivity", "Error: ${t.message}")
            }
        })

    }
}
