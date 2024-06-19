package com.example.final_puffandpoof.model

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.final_puffandpoof.view.DetailBarang
import com.example.final_puffandpoof.R

class RecyclerViewHome(
    val dataList: List<Doll>,
) : RecyclerView.Adapter<RecyclerViewHome.DollViewHolder>() {


    inner class DollViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.gambarBarang)
        val textName: TextView = itemView.findViewById(R.id.namaBarang)
        val textPrice: TextView = itemView.findViewById(R.id.hargaBarang)
        val cardContainer: androidx.appcompat.widget.Toolbar = itemView.findViewById(R.id.card)

        init {
            cardContainer.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val clickedItem = dataList[position]
                    val intent = Intent(itemView.context, DetailBarang::class.java).apply {
                        putExtra("EXTRA_ID", clickedItem.id) // Assuming Doll class has an 'id' field
                    }
                    itemView.context.startActivity(intent)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DollViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.barang_list, parent, false)
        return DollViewHolder(view)
    }

    override fun onBindViewHolder(holder: DollViewHolder, position: Int) {
        val currentItem = dataList[position]
        Glide.with(holder.itemView.context)
            .load(currentItem.imageLink)
            .centerCrop()
            .into(holder.imageView)

        holder.textName.text = currentItem.name
        holder.textPrice.text = "Rp ${currentItem.price}"

        Log.d("RecyclerViewHome", "ID: ${currentItem.id}")
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}
