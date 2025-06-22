package com.example.justaddgelang

import RetrofitClient
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderDetailActivity : AppCompatActivity() {

    private lateinit var txtOrderId: TextView
    private lateinit var txtOrderDate: TextView
    private lateinit var txtStatus: TextView
    private lateinit var txtTotalAmount: TextView
    private lateinit var txtCustomerName: TextView
    private lateinit var txtRecipientName: TextView
    private lateinit var txtRecipientNumber: TextView
    private lateinit var txtShippingAddress: TextView
    private lateinit var orderItemsListView: ListView
    private lateinit var btnChangeStatus: Button
    private lateinit var btnCancelOrder: Button

    private var temp_order_id: Int = -1
    private lateinit var orderItemAdapter: OrderDetailItemAdapter
    private var items: List<OrderDetailItem> = listOf()
    private var source: String = ""
    private var actualOrderId: Int = -1
    private lateinit var order: OrderDetailResponse  // ✅ Declare order globally

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_details)

        // Bind views
        txtOrderId = findViewById(R.id.txtOrderId)
        txtOrderDate = findViewById(R.id.txtOrderDate)
        txtStatus = findViewById(R.id.txtStatus)
        txtTotalAmount = findViewById(R.id.txtTotalAmount)
        txtCustomerName = findViewById(R.id.txtCustomerName)
        txtRecipientName = findViewById(R.id.txtRecipientName)
        txtRecipientNumber = findViewById(R.id.txtRecipientNumber)
        txtShippingAddress = findViewById(R.id.txtShippingAddress)
        orderItemsListView = findViewById(R.id.orderItemsListView)
        btnChangeStatus = findViewById(R.id.btnChangeStatus)
        btnCancelOrder = findViewById(R.id.btnCancelOrder)

        // Hide buttons initially
        btnChangeStatus.visibility = View.GONE
        btnCancelOrder.visibility = View.GONE

        // Get data from intent
        actualOrderId = intent.getIntExtra("order_id", -1)
        temp_order_id = intent.getIntExtra("temp_order_id", -1)
        source = intent.getStringExtra("source") ?: ""

        // Change status button logic
        btnChangeStatus.setOnClickListener {
            val currentStatus = order.status

            if (currentStatus.equals("Cancelled", ignoreCase = true)) {
                Toast.makeText(this, "Cannot update a cancelled order", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Define allowed transitions
            val nextStatus = when (currentStatus) {
                "Pending" -> "Confirmed"
                "Confirmed" -> "Shipped"
                "Shipped" -> "Delivered"
                "Delivered" -> null
                else -> null
            }

            if (nextStatus == null) {
                Toast.makeText(this, "Order is already delivered or in invalid state", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Confirm with user
            val builder = android.app.AlertDialog.Builder(this)
            builder.setTitle("Update Status")
            builder.setMessage("Change status from $currentStatus to $nextStatus?")
            builder.setPositiveButton("Yes") { _, _ ->
                updateOrderStatus(actualOrderId, nextStatus)
            }
            builder.setNegativeButton("Cancel", null)
            builder.show()
        }

        // Load order details
        if (actualOrderId != -1) {
            fetchOrderDetails(actualOrderId)
        } else {
            Toast.makeText(this, "Invalid order ID", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun fetchOrderDetails(orderId: Int) {
        val api = RetrofitClient.apiService
        api.getOrderDetails(orderId).enqueue(object : Callback<OrderDetailResponse> {
            override fun onResponse(
                call: Call<OrderDetailResponse>,
                response: Response<OrderDetailResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    order = response.body()!!  // ✅ Save order for use globally

                    // Set order details
                    txtOrderId.text = "Order ID: $temp_order_id"
                    txtOrderDate.text = "Date: ${order.orderDate}"
                    txtStatus.text = "Status: ${order.status}"
                    txtTotalAmount.text = "Total: ₹${order.totalAmount}"
                    txtCustomerName.text = "Customer: ${order.customerName}"
                    txtRecipientName.text = "Recipient: ${order.recipientName}"
                    txtRecipientNumber.text = "Phone: ${order.recipientNumber}"
                    txtShippingAddress.text = "Address: ${order.shippingAddress.address}, ${order.shippingAddress.city}, ${order.shippingAddress.pincode}"

                    // Set items
                    items = order.items
                    orderItemAdapter = OrderDetailItemAdapter(this@OrderDetailActivity, items)
                    orderItemsListView.adapter = orderItemAdapter

                    // Show buttons based on source and order status
                    if (source == "admin") {
                        btnChangeStatus.visibility = View.VISIBLE
                    } else if (source == "user" && order.status.equals("Pending", ignoreCase = true)) {
                        btnCancelOrder.visibility = View.VISIBLE
                        btnCancelOrder.setOnClickListener {
                            cancelOrder(orderId)
                        }
                    }

                } else {
                    Toast.makeText(this@OrderDetailActivity, "Order not found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<OrderDetailResponse>, t: Throwable) {
                Toast.makeText(this@OrderDetailActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun cancelOrder(orderId: Int) {
        RetrofitClient.apiService.cancelOrder(orderId).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    Toast.makeText(this@OrderDetailActivity, "Order cancelled", Toast.LENGTH_SHORT).show()
                    finish() // Close or refresh
                } else {
                    Toast.makeText(this@OrderDetailActivity, "Failed to cancel order", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Toast.makeText(this@OrderDetailActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateOrderStatus(orderId: Int, newStatus: String) {
        RetrofitClient.apiService.updateOrderStatus(orderId, newStatus).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@OrderDetailActivity, "Status updated to $newStatus", Toast.LENGTH_SHORT).show()
                    fetchOrderDetails(orderId) // Refresh
                } else {
                    Toast.makeText(this@OrderDetailActivity, "Failed to update status", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@OrderDetailActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
