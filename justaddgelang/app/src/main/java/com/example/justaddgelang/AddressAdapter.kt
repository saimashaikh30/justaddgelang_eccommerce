package com.example.justaddgelang

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AddressAdapter(
    private val addresses: List<UserAddress>,
    private val onEdit: (UserAddress) -> Unit,
    private val onDelete: (UserAddress) -> Unit
) : RecyclerView.Adapter<AddressAdapter.AddressViewHolder>() {

    class AddressViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvType: TextView = view.findViewById(R.id.tvAddressType)
        val tvAddress: TextView = view.findViewById(R.id.tvFullAddress)
        val btnDelete: Button = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_address, parent, false)
        return AddressViewHolder(view)
    }

    override fun getItemCount(): Int = addresses.size

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        val address = addresses[position]
        holder.tvType.text = address.address_type
        holder.tvAddress.text = "${address.address}\n${address.city} - ${address.pincode}"

        holder.btnDelete.setOnClickListener { onDelete(address) }
    }
}
