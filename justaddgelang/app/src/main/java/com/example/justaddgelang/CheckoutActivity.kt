package com.example.justaddgelang

import RetrofitClient
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CheckoutActivity : AppCompatActivity() {

    private lateinit var checkoutRecyclerView: RecyclerView
    private lateinit var totalPriceTextView: TextView
    private lateinit var addressSpinner: Spinner
    private lateinit var btnMakePayment: Button
    private lateinit var cartItems: List<CartItem>
    private lateinit var adapter: CheckoutCartAdapter
    private var selectedAddressId: Int = -1
    private var userId: Int = 0
    private var addressList: List<UserAddress> = listOf()

    private val DELIVERY_CHARGE = 70.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        checkoutRecyclerView = findViewById(R.id.checkoutRecyclerView)
        totalPriceTextView = findViewById(R.id.totalPriceTextView)
        addressSpinner = findViewById(R.id.addressSpinner)
        btnMakePayment = findViewById(R.id.btnMakePayment)


        userId = SharedPreferencesHelper.getUserId(this)?.toIntOrNull() ?: -1

        loadCartItems()
        loadUserAddresses()

        btnMakePayment.setOnClickListener {
            val name = findViewById<EditText>(R.id.recipient_name).text.toString().trim()
            val number = findViewById<EditText>(R.id.recipient_number).text.toString().trim()

            if (name.isEmpty()) {
                Toast.makeText(this, "Please enter recipient name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (number.length != 10) {
                Toast.makeText(this, "Please enter a valid 10-digit number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (selectedAddressId != -1) {
                val items = cartItems.map {
                    OrderItemRequest(productId = it.product_id, quantity = it.quantity)
                }

                val totalWithoutDelivery = cartItems.sumOf { it.product_price * it.quantity }
                val totalAmount = totalWithoutDelivery + DELIVERY_CHARGE

                val order = OrderCreateRequest(
                    userId = userId,
                    totalAmount = totalAmount,
                    addressId = selectedAddressId,
                    recipientName = name,
                    recipientNumber = number,
                    orderItems = items
                )


                RetrofitClient.apiService.createOrder(order).enqueue(object : Callback<ApiResponse> {
                    override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                        if (response.isSuccessful && response.body()?.success == true) {
                            Toast.makeText(this@CheckoutActivity, "Order placed!", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@CheckoutActivity, OrderConfirmationActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this@CheckoutActivity, "Order failed!", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                        Toast.makeText(this@CheckoutActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                Toast.makeText(this, "Please select an address", Toast.LENGTH_SHORT).show()
            }
        }

        addressSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedAddressId = addressList[position].address_id ?: -1
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun loadCartItems() {
        RetrofitClient.apiService.getCartItems(userId).enqueue(object : Callback<List<CartItem>> {
            override fun onResponse(call: Call<List<CartItem>>, response: Response<List<CartItem>>) {
                if (response.isSuccessful) {
                    cartItems = response.body() ?: emptyList()
                    adapter = CheckoutCartAdapter(cartItems)
                    checkoutRecyclerView.layoutManager = LinearLayoutManager(this@CheckoutActivity)
                    checkoutRecyclerView.adapter = adapter
                    updateTotalPrice()
                }
            }

            override fun onFailure(call: Call<List<CartItem>>, t: Throwable) {
                Toast.makeText(this@CheckoutActivity, "Failed to load cart", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateTotalPrice() {
        val totalWithoutDelivery = cartItems.sumOf { it.product_price * it.quantity }
        val total = totalWithoutDelivery + DELIVERY_CHARGE
        totalPriceTextView.text = "Total: ₹${"%.2f".format(total)} (incl. ₹70 delivery)"
    }

    private fun loadUserAddresses() {
        RetrofitClient.apiService.getUserAddresses(userId).enqueue(object : Callback<List<UserAddress>> {
            override fun onResponse(call: Call<List<UserAddress>>, response: Response<List<UserAddress>>) {
                if (response.isSuccessful) {
                    addressList = response.body() ?: emptyList()
                    val addressStrings = addressList.map {
                        "${it.address}, ${it.city}, ${it.pincode} (${it.address_type})"
                    }

                    val spinnerAdapter = ArrayAdapter(
                        this@CheckoutActivity,
                        android.R.layout.simple_spinner_item,
                        addressStrings
                    )
                    spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    addressSpinner.adapter = spinnerAdapter
                }
            }

            override fun onFailure(call: Call<List<UserAddress>>, t: Throwable) {
                Toast.makeText(this@CheckoutActivity, "Failed to load addresses", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
