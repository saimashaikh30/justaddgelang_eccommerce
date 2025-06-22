package com.example.justaddgelang

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ProductAdapter(
    private val context: Context,
    private val productList: MutableList<Product>,
    private val onItemClick: (Product) -> Unit,
    private val onLoadMore: (() -> Unit)? = null
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.product_image)
        val name: TextView = view.findViewById(R.id.product_name)
        val price: TextView = view.findViewById(R.id.product_price)

        fun bind(product: Product) {
            name.text = product.productName
            price.text = "₹${product.productPrice}"

            Glide.with(context)
                .load(product.imageUrl)
                .into(image)

            itemView.setOnClickListener {
                Log.d("Product_id",product.productId.toString())
                onItemClick(product)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        holder.bind(product)

        // Trigger load more when last item is reached
        if (position == productList.size - 1) {
            onLoadMore?.invoke()
        }
    }

    override fun getItemCount(): Int = productList.size

    fun updateProducts(newProducts: List<Product>) {
        val start = productList.size
        productList.addAll(newProducts)
        notifyItemRangeInserted(start, newProducts.size)
    }

    fun clearProducts() {
        productList.clear()
        notifyDataSetChanged()
    }
}
