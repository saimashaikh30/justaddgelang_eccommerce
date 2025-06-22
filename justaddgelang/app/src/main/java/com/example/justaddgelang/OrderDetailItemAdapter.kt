package com.example.justaddgelang

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.Glide

class OrderDetailItemAdapter(
    private val context: Context,
    private val items: List<OrderDetailItem>
) : BaseAdapter() {

    override fun getCount(): Int = items.size
    override fun getItem(position: Int): Any = items[position]
    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_order_product, parent, false)
        val item = items[position]

        val imgProduct = view.findViewById<ImageView>(R.id.imgProduct)
        val txtProductName = view.findViewById<TextView>(R.id.txtProductName)
        val txtPriceQty = view.findViewById<TextView>(R.id.txtPriceQty)
        val txtSubtotal = view.findViewById<TextView>(R.id.txtSubtotal)

        txtProductName.text = item.product_name
        txtPriceQty.text = "${item.quantity}"
        txtSubtotal.text = "Subtotal: ₹${item.subtotal}"
        Log.d("picture",item.product_picture)
        Glide.with(context)
            .load(item.product_picture)
            .placeholder(R.drawable.bridgertonnecklace)
            .into(imgProduct)

        return view
    }
}

