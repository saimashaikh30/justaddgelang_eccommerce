package com.example.justaddgelang

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WishlistFragment : Fragment() {

    private lateinit var wishlistRecyclerView: RecyclerView
    private lateinit var wishlistAdapter: WishlistAdapter
    private var wishlistItems = mutableListOf<WishlistItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_wishlist, container, false)

        wishlistRecyclerView = view.findViewById(R.id.wishlistRecyclerView)
        wishlistRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        wishlistAdapter = WishlistAdapter(wishlistItems) { item, position ->
            removeFromWishlist(item, position)
        }

        wishlistRecyclerView.adapter = wishlistAdapter

        loadWishlistItems()

        return view
    }

    private fun loadWishlistItems() {
        val userIdStr = SharedPreferencesHelper.getUserId(requireContext())
        val userId = userIdStr?.toIntOrNull()

        if (userId != null) {
            RetrofitClient.apiService.getWishlistItems(userId)
                .enqueue(object : Callback<List<WishlistItem>> {
                    override fun onResponse(
                        call: Call<List<WishlistItem>>,
                        response: Response<List<WishlistItem>>
                    ) {
                        if (response.isSuccessful && response.body() != null) {
                            wishlistItems.clear()
                            wishlistItems.addAll(response.body()!!)
                            wishlistAdapter.notifyDataSetChanged()
                        }
                    }

                    override fun onFailure(call: Call<List<WishlistItem>>, t: Throwable) {
                        t.printStackTrace()
                        Toast.makeText(requireContext(), "Failed to load wishlist", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }

    private fun removeFromWishlist(item: WishlistItem, position: Int) {
        val userIdStr = SharedPreferencesHelper.getUserId(requireContext())
        val userId = userIdStr?.toIntOrNull()

        if (userId != null) {
            RetrofitClient.apiService.removeFromWishlist(item.wishlist_id) // assuming item.id is wishlistId
                .enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            wishlistAdapter.removeItem(position)
                            Toast.makeText(requireContext(), "Removed from wishlist", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(requireContext(), "Failed to remove", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        t.printStackTrace()
                        Toast.makeText(requireContext(), "Error removing item", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }
}
