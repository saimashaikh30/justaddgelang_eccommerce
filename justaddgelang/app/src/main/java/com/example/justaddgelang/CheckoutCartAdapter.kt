package com.example.justaddgelang

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CheckoutCartAdapter(private val items: List<CartItem>) :
    RecyclerView.Adapter<CheckoutCartAdapter.CartViewHolder>() {

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productName: TextView = itemView.findViewById(R.id.tvProductName)
        val productPrice: TextView = itemView.findViewById(R.id.tvProductPrice)
        val quantity: TextView = itemView.findViewById(R.id.tvQuantity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_checkout_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = items[position]
        holder.productName.text = item.product_name
        holder.productPrice.text = "₹${"%.2f".format(item.product_price)}"
        holder.quantity.text = "Qty: ${item.quantity}"
    }

    override fun getItemCount(): Int = items.size
}
