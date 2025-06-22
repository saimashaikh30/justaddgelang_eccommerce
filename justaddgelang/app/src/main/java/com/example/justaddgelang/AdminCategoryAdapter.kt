package com.example.justaddgelang

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminCategoryAdapter(
    private var categoryList: MutableList<Category>,
    private val context: Context,
    private val onEditClick: (Category) -> Unit  // 👈 Pass edit click listener
) : RecyclerView.Adapter<AdminCategoryAdapter.CategoryViewHolder>() {

    class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val categoryName: TextView = view.findViewById(R.id.tvCategoryName)
        val btnEdit: ImageButton = view.findViewById(R.id.btnEdit)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category_admin, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categoryList[position]
        holder.categoryName.text = category.category_name

        holder.btnEdit.setOnClickListener {
            onEditClick(category) // 👈 Delegate to activity
        }

        holder.btnDelete.setOnClickListener {
            deleteCategory(position)
        }
    }

    override fun getItemCount(): Int = categoryList.size

    private fun deleteCategory(position: Int) {
        val category = categoryList[position]

        val builder = android.app.AlertDialog.Builder(context)
        builder.setTitle("Confirm Deletion")
        builder.setMessage("Are you sure you want to delete the category \"${category.category_name}\"?\n\nAll products under this category will also be deleted.")
        builder.setPositiveButton("Delete") { _, _ ->
            RetrofitClient.apiService.deleteCategory(category.category_id)
                .enqueue(object : Callback<ApiResponse> {
                    override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                        if (response.isSuccessful && response.body()?.success == true) {
                            categoryList.removeAt(position)
                            notifyItemRemoved(position)
                            notifyItemRangeChanged(position, categoryList.size)
                            Toast.makeText(context, "Category deleted", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Failed to delete category", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                        Toast.makeText(context, "Error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
                    }
                })
        }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }

}
