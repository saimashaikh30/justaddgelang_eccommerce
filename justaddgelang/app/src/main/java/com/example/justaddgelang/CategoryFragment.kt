package com.example.justaddgelang

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CategoryFragment : Fragment() {

    private lateinit var listViewCategories: ListView
    private var categoryList: List<Category> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_category, container, false)

        listViewCategories = view.findViewById(R.id.listViewCategories)

        fetchCategories()

        listViewCategories.setOnItemClickListener { _, _, position, _ ->
            val selectedCategory = categoryList[position]
            val categoryId = selectedCategory.category_id

            val homeFragment = HomeFragment().apply {
                arguments = Bundle().apply {
                    putInt("categoryId", categoryId)
                }
            }

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.middle_container, homeFragment)
                .addToBackStack(null)
                .commit()
        }

        return view
    }

    private fun fetchCategories() {
        val apiService = RetrofitClient.apiService

        apiService.getCategories().enqueue(object : Callback<List<Category>> {
            override fun onResponse(
                call: Call<List<Category>>,
                response: Response<List<Category>>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { categories ->
                        categoryList = categories
                        val categoryNames = categories.map { it.category_name }
                        val adapter = ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_list_item_1,
                            categoryNames
                        )
                        listViewCategories.adapter = adapter
                    }
                } else {
                    Toast.makeText(requireContext(), "Failed to fetch categories", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Category>>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
