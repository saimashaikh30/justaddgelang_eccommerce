package com.example.justaddgelang

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddressActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var btnAddAddress: Button
    private lateinit var emptyPlaceholder: TextView
    private var addressList = mutableListOf<UserAddress>()
    private lateinit var adapter: AddressAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address)

        recyclerView = findViewById(R.id.recyclerViewAddresses)
        btnAddAddress = findViewById(R.id.btnAddAddress)
        emptyPlaceholder = findViewById(R.id.emptyPlaceholder)

        adapter = AddressAdapter(addressList,
            onEdit = { address ->
                // TODO: Pass data to EditAddressActivity (optional)
                Toast.makeText(this, "Edit: ${address.address}", Toast.LENGTH_SHORT).show()
            },
            onDelete = { address ->
                address.address_id?.let { deleteAddress(it) }
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        btnAddAddress.setOnClickListener {
            startActivity(Intent(this, AddAddressActivity::class.java))
        }

        enableSwipeToDelete()
    }

    override fun onResume() {
        super.onResume()
        loadAddresses()
    }

    private fun loadAddresses() {
        val userId = SharedPreferencesHelper.getUserId(this)?.toIntOrNull() ?: return
        RetrofitClient.apiService.getUserAddresses(userId).enqueue(object : Callback<List<UserAddress>> {
            override fun onResponse(call: Call<List<UserAddress>>, response: Response<List<UserAddress>>) {
                if (response.isSuccessful) {
                    addressList.clear()
                    response.body()?.let { addressList.addAll(it) }
                    adapter.notifyDataSetChanged()
                    emptyPlaceholder.visibility = if (addressList.isEmpty()) View.VISIBLE else View.GONE
                } else {
                    Toast.makeText(this@AddressActivity, "Failed to load addresses", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<UserAddress>>, t: Throwable) {
                Toast.makeText(this@AddressActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun deleteAddress(id: Int) {
        RetrofitClient.apiService.deleteAddress(id).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@AddressActivity, "Address deleted", Toast.LENGTH_SHORT).show()
                    loadAddresses()
                } else {
                    Toast.makeText(this@AddressActivity, "Failed to delete", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Toast.makeText(this@AddressActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun enableSwipeToDelete() {
        val swipeHandler = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder) = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                addressList[position].address_id?.let { deleteAddress(it) }
            }
        }
        ItemTouchHelper(swipeHandler).attachToRecyclerView(recyclerView)
    }
}
