package com.example.justaddgelang

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class WishlistAdapter(
    private val wishlist: MutableList<WishlistItem>,
    private val onRemoveClick: (WishlistItem, Int) -> Unit
) : RecyclerView.Adapter<WishlistAdapter.WishlistViewHolder>() {

    class WishlistViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.product_image)
        val name: TextView = view.findViewById(R.id.product_name)
        val price: TextView = view.findViewById(R.id.product_price)
        val removeButton: ImageButton = view.findViewById(R.id.heart_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WishlistViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_wishlist, parent, false)
        return WishlistViewHolder(view)
    }

    override fun onBindViewHolder(holder: WishlistViewHolder, position: Int) {
        val item = wishlist[position]

        holder.name.text = item.product_name
        holder.price.text = "₹%.2f".format(item.product_price)

        Glide.with(holder.itemView.context)
            .load(item.product_picture)
            .placeholder(R.drawable.bridgertonnecklace)
            .error(R.drawable.bridgertonnecklace)
            .into(holder.image)

        holder.removeButton.setImageResource(R.drawable.baseline_favorite_24)

        holder.removeButton.setOnClickListener {
            onRemoveClick(item, position)
        }
    }

    override fun getItemCount(): Int = wishlist.size

    fun removeItem(position: Int) {
        wishlist.removeAt(position)
        notifyItemRemoved(position)
    }
}
