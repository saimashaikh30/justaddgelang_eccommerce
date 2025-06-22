package com.example.justaddgelang.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.justaddgelang.CartItem
import com.example.justaddgelang.R
import com.example.justaddgelang.ApiService
import com.example.justaddgelang.CartItemQuantityUpdateRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CartAdapter(
    private val cartItems: MutableList<CartItem>,
    private val updateTotalPrice: () -> Unit,
    private val userId: Int // Assuming the userId is passed to handle cart actions
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cartProductImage: ImageView = itemView.findViewById(R.id.cart_product_image)
        val cartProductName: TextView = itemView.findViewById(R.id.cart_product_name)
        val cartProductPrice: TextView = itemView.findViewById(R.id.cart_product_price)
        val cartQuantity: TextView = itemView.findViewById(R.id.cart_quantity)
        val btnIncrease: ImageButton = itemView.findViewById(R.id.btn_increase)
        val btnDecrease: ImageButton = itemView.findViewById(R.id.btn_decrease)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btn_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = cartItems[position]

        holder.cartProductName.text = item.product_name
        holder.cartProductPrice.text = "₹%.2f".format(item.product_price * item.quantity)
        holder.cartQuantity.text = item.quantity.toString()

        Glide.with(holder.itemView.context)
            .load(item.product_picture)
            .placeholder(R.drawable.bridgertonnecklace)
            .into(holder.cartProductImage)

        holder.btnIncrease.setOnClickListener {
            // Ensure that the quantity doesn't exceed the stock
            Log.d("stock",item.product_stock.toString())
            if (item.quantity < item.product_stock) { // Replace `item.stock` with the actual stock property
                item.quantity += 1
                updateCartItemQuantity(item, position)
            } else {
                Toast.makeText(holder.itemView.context, "Cannot exceed stock", Toast.LENGTH_SHORT).show()
            }
        }


        holder.btnDecrease.setOnClickListener {
            if (item.quantity > 1) {
                item.quantity -= 1
                updateCartItemQuantity(item, position)
            } else {
                // Remove item from cart if quantity reaches zero
                removeCartItem(item, position)
            }
        }

        holder.btnDelete.setOnClickListener {
            removeCartItem(item, position)
        }
    }

    private fun updateCartItemQuantity(item: CartItem, position: Int) {
        // Create the request object
        val updateRequest = CartItemQuantityUpdateRequest(item.cart_item_id, item.quantity)

        // Call to API to update the cart item quantity
        // Use the cartItemId in the URL
        val apiService = RetrofitClient.apiService
        apiService.updateCartItemQuantity(item.cart_item_id, updateRequest).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    // Update UI
                    notifyItemChanged(position)
                    updateTotalPrice()
                } else {
                    Log.e("CartAdapter", "Failed to update quantity: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("CartAdapter", "Error updating quantity: ${t.message}")
            }
        })

    }


    private fun removeCartItem(item: CartItem, position: Int) {
        // Call to API to remove item from the cart
        val apiService = RetrofitClient.apiService
        apiService.deleteCartItem(item.cart_item_id).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    // Remove the item from the list if successful
                    cartItems.removeAt(position)
                    notifyItemRemoved(position)
                    updateTotalPrice()
                } else {
                    Log.e("CartAdapter", "Failed to remove item: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("CartAdapter", "Error removing item: ${t.message}")
            }
        })
    }

    override fun getItemCount(): Int = cartItems.size
}
