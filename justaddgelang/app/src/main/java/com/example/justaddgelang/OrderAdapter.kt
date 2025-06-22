package com.example.justaddgelang

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class OrderAdapter(private val context: Context) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    private var orders = listOf<Order>()

    fun setOrders(newOrders: List<Order>) {
        orders = newOrders
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun getItemCount(): Int = orders.size

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        holder.bind(order)

    }

    inner class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvOrderId = view.findViewById<TextView>(R.id.tvOrderId)
        private val tvOrderStatus = view.findViewById<TextView>(R.id.tvOrderStatus)
        private val tvOrderDate = view.findViewById<TextView>(R.id.tvOrderDate)

        fun bind(order: Order) {
            tvOrderId.text = "Order ID: ${order.temp_order_id}"
            tvOrderStatus.text = "Order Status: ${order.status}"
            tvOrderDate.text = "Order Date: ${order.order_date}"

            itemView.setOnClickListener {
                val intent = Intent(context, OrderDetailActivity::class.java)
                intent.putExtra("order_id", order.order_id) // assuming you use this as the identifier
                intent.putExtra("temp_order_id",order.temp_order_id)
                intent.putExtra("source","admin")
                context.startActivity(intent)
            }
        }

    }
}

