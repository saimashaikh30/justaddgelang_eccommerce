package com.example.justaddgelang

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminProductAdapter(
    private var productList: MutableList<Product>,
    private val context: Context
) : RecyclerView.Adapter<AdminProductAdapter.ProductViewHolder>() {

    private var fullList = mutableListOf<Product>()

    init {
        fullList.addAll(productList)
    }

    fun updateList(newList: List<Product>) {
        fullList.clear()
        fullList.addAll(newList)
        productList.clear()
        productList.addAll(newList)
        notifyDataSetChanged()
    }

    fun filter(query: String) {
        val filtered = if (query.isEmpty()) {
            fullList
        } else {
            fullList.filter {
                it.productName.contains(query, ignoreCase = true)
            }
        }
        productList.clear()
        productList.addAll(filtered)
        notifyDataSetChanged()
    }

    class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val productName: TextView = view.findViewById(R.id.tvProductName)
        val productPrice: TextView = view.findViewById(R.id.tvProductPrice)
        val productStock: TextView = view.findViewById(R.id.tvProductStock)
        val btnEdit: ImageButton = view.findViewById(R.id.btnEdit)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDelete)
        val image: ImageView = view.findViewById(R.id.ivProductImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product_admin, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        holder.productName.text = product.productName
        holder.productPrice.text = "₹${product.productPrice}"
        holder.productStock.text = "Stock: ${product.productStock}"

        Glide.with(context)
            .load(product.imageUrl)
            .placeholder(R.drawable.bridgertonnecklace)
            .error(R.drawable.bridgertonnecklace)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .into(holder.image)

        holder.btnEdit.setOnClickListener {
            val intent = Intent(context, EditProductActivity::class.java)
            intent.putExtra("product", product)
            context.startActivity(intent)
        }

        holder.btnDelete.setOnClickListener {
            showDeleteConfirmation(position, product.productId)
        }
    }

    override fun getItemCount(): Int = productList.size

    private fun showDeleteConfirmation(position: Int, productId: Int) {
        AlertDialog.Builder(context)
            .setTitle("Delete Product")
            .setMessage("Are you sure you want to delete this product?")
            .setPositiveButton("Yes") { _, _ ->
                deleteProductFromApi(position, productId)
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun deleteProductFromApi(position: Int, productId: Int) {
        RetrofitClient.apiService.deleteProduct(productId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    productList.removeAt(position)
                    fullList.removeIf { it.productId == productId }
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, productList.size)
                    Toast.makeText(context, "Product deleted", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Failed to delete product", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
