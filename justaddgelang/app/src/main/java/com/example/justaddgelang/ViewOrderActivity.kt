package com.example.justaddgelang

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ViewOrderActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var orderItemAdapter: OrderItemAdapter
    private var orderItems = listOf<OrderItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_order)

        // Setup RecyclerView
        recyclerView = findViewById(R.id.recyclerViewOrderItems)
        recyclerView.layoutManager = LinearLayoutManager(this)
        orderItemAdapter = OrderItemAdapter(orderItems, this)
        recyclerView.adapter = orderItemAdapter

        // Fetch and display order items
        val orderId = intent.getIntExtra("ORDER_ID", -1)
       // val order = getOrderById(orderId)
//        if (order != null) {
//            orderItems = order.order_items
//            orderItemAdapter.notifyDataSetChanged()
//        }
    }

//    private fun getOrderById(orderId: Int): Order? {
//        // Mock order data. Replace with actual data fetching logic
//        return Order(
//            order_id = orderId,
//            order_date = "2025-03-01",
//            order_status = "Pending",
//            total_amount = 150.00,
////            order_items = listOf(
////                OrderItem("Bridgerton Necklace", 1, 250.00),
////            )
////        )
//    }
}
