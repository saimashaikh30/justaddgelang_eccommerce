package com.example.justaddgelang.fragments

import RetrofitClient
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.justaddgelang.*
import com.example.justaddgelang.adapters.CartAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CartFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var totalPriceTextView: TextView
    private lateinit var checkoutButton: Button
    private lateinit var cartAdapter: CartAdapter
    private val cartList = mutableListOf<CartItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cart, container, false)

        recyclerView = view.findViewById(R.id.cartRecyclerView)
        totalPriceTextView = view.findViewById(R.id.total_price)
        checkoutButton = view.findViewById(R.id.btn_checkout)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Assuming user ID is stored in SharedPreferences
        val userId = SharedPreferencesHelper.getUserId(requireContext())?.toIntOrNull() ?: 0

        if (userId == 0) {
            Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
            return view
        }

        // Pass the userId and updateTotalPrice function here
        cartAdapter = CartAdapter(cartList, ::updateTotalPrice, userId)
        recyclerView.adapter = cartAdapter

        checkoutButton.setOnClickListener {
            val total = cartList.sumOf { it.product_price * it.quantity }
            val intent = Intent(requireContext(), CheckoutActivity::class.java)
            intent.putExtra("TOTAL_PRICE", total)
            startActivity(intent)
        }

        loadCartItems(userId)

        return view
    }

    private fun loadCartItems(userId: Int) {
        RetrofitClient.apiService.getCartItems(userId).enqueue(object : Callback<List<CartItem>> {
            override fun onResponse(
                call: Call<List<CartItem>>,
                response: Response<List<CartItem>>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    cartList.clear()
                    cartList.addAll(response.body()!!)
                    cartAdapter.notifyDataSetChanged()
                    updateTotalPrice()  // Update total price when cart items are loaded
                } else {
                    Toast.makeText(context, "Failed to load cart", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<CartItem>>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // This function is used to update the total price
    private fun updateTotalPrice() {
        val total = cartList.sumOf { it.product_price * it.quantity }
        val deliveryCharge = 70.0
        val finalTotal = total + deliveryCharge
        totalPriceTextView.text = "Total: ₹%.2f".format(finalTotal)
    }

}

