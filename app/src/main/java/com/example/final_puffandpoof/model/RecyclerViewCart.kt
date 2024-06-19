package com.example.final_puffandpoof.model

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.final_puffandpoof.R

class RecyclerViewCart(
    private val cartItems: MutableList<CartDatabase>, // Change to MutableList
    private val updateQuantityById: (Int, Int) -> Unit // Function to update quantity by ID
) : RecyclerView.Adapter<RecyclerViewCart.CartViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cart_list, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartItem = cartItems[position]
        holder.textViewName.text = cartItem.nama_barang
        holder.textViewPrice.text = "Rp ${cartItem.price}"
        holder.textViewQuantity.setText(cartItem.quantity.toString())

        Glide.with(holder.itemView.context)
            .load(cartItem.imageLink)
            .centerCrop()
            .into(holder.imageView)

        holder.textViewQuantity.removeTextChangedListener(holder.textWatcher)

        holder.textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No-op
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // No-op
            }

            override fun afterTextChanged(s: Editable?) {
                val newQuantity = s.toString().toIntOrNull() ?: return
                if (newQuantity != cartItem.quantity) {
                    updateQuantityById(cartItem.id, newQuantity)
                }
            }
        }
        holder.textViewQuantity.addTextChangedListener(holder.textWatcher)
    }

    override fun getItemCount() = cartItems.size

    fun removeItem(position: Int) {
        cartItems.removeAt(position)
        notifyItemRemoved(position)
    }

    class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.gambar_Barang)
        val textViewName: TextView = view.findViewById(R.id.Nama)
        val textViewQuantity: EditText = view.findViewById(R.id.jumlah)
        val textViewPrice: TextView = view.findViewById(R.id.harga)
        var textWatcher: TextWatcher? = null // Hold reference to TextWatcher
    }
}
