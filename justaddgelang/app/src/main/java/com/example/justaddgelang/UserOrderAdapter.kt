package com.example.justaddgelang

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import java.text.SimpleDateFormat
import java.util.*

class UserOrderAdapter(private val context: Context, private val orders: List<Order>) : BaseAdapter() {
    override fun getCount(): Int = orders.size
    override fun getItem(position: Int): Any = orders[position]
    override fun getItemId(position: Int): Long = orders[position].order_id.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.user_item_order, parent, false)
        val order = orders[position]

        val txtOrderId = view.findViewById<TextView>(R.id.txtOrderId)
        val txtAmount = view.findViewById<TextView>(R.id.txtAmount)
        val txtDate = view.findViewById<TextView>(R.id.txtDate)
        val txtStatus = view.findViewById<TextView>(R.id.txtStatus)

        txtOrderId.text = "Order ID: ${order.temp_order_id}"
        txtAmount.text = "₹${order.total_amount}"
        txtDate.text = "Date: ${formatDate(order.order_date)}"
        txtStatus.text = "Status: ${order.status ?: "Pending"}" // use default if null

        view.setOnClickListener {
            val intent = Intent(context, OrderDetailActivity::class.java)
            intent.putExtra("order_id", order.order_id)
            intent.putExtra("temp_order_id",order.temp_order_id)
            intent.putExtra("source","user")
            context.startActivity(intent)
        }

        return view
    }

    private fun formatDate(dateString: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            val date = inputFormat.parse(dateString)
            if (date != null) outputFormat.format(date) else dateString
        } catch (e: Exception) {
            dateString
        }
    }
}
