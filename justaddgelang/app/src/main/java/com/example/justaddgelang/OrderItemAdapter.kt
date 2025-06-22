package com.example.justaddgelang

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class OrderItemAdapter(private val orderItems: List<OrderItem>, private val context: Context) : RecyclerView.Adapter<OrderItemAdapter.OrderItemViewHolder>() {

    inner class OrderItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvProductName: TextView = view.findViewById(R.id.order_product_name)
        val tvQuantity: TextView = view.findViewById(R.id.order_product_quantity)
        val tvPrice: TextView = view.findViewById(R.id.order_product_price)
        val ivProductImage: ImageView = view.findViewById(R.id.order_product_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_order_item, parent, false)
        return OrderItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderItemViewHolder, position: Int) {
        val orderItem = orderItems[position]
        holder.tvProductName.text = orderItem.product_name
        holder.tvQuantity.text = "Quantity: ${orderItem.quantity}"
        holder.tvPrice.text = "₹${orderItem.price}"

        // Set product image if available (you can set an actual image URL if available)
        holder.ivProductImage.setImageResource(R.drawable.bridgertonnecklace) // Use the actual image if available
    }

    override fun getItemCount(): Int = orderItems.size
}
