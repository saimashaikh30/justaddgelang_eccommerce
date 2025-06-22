package com.example.justaddgelang

import android.os.Bundle
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrdersActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var orderAdapter: UserOrderAdapter
    private var orderList: ArrayList<Order> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders)

        listView = findViewById(R.id.orderListView)
        orderAdapter = UserOrderAdapter(this, orderList)
        listView.adapter = orderAdapter

        val user_Id = SharedPreferencesHelper.getUserId(this)
        val userId = user_Id?.toIntOrNull() ?: -1
        if (userId != null) {
            fetchUserOrders(userId)
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun fetchUserOrders(userId: Int) {
        val call = RetrofitClient.apiService.getOrdersByUser(userId)
        call.enqueue(object : Callback<List<Order>> {
            override fun onResponse(call: Call<List<Order>>, response: Response<List<Order>>) {
                if (response.isSuccessful && response.body() != null) {
                    orderList.clear()
                    orderList.addAll(response.body()!!)
                    orderAdapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(this@OrdersActivity, "No orders found.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Order>>, t: Throwable) {
                Toast.makeText(this@OrdersActivity, "Failed to fetch orders", Toast.LENGTH_SHORT).show()
            }
        })
    }


}
