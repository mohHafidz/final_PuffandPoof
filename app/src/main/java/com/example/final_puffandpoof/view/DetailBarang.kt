package com.example.final_puffandpoof.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.final_puffandpoof.DatabaseHelper
import com.example.final_puffandpoof.R
import com.example.final_puffandpoof.databinding.ActivityDetailBarangBinding
import com.example.final_puffandpoof.model.Doll

class DetailBarang : AppCompatActivity() {
    lateinit var namaBarangNav:TextView
    lateinit var backBTN: ImageView
    lateinit var gambar: ImageView
    lateinit var namaBarang: TextView
    lateinit var size: TextView
    lateinit var rating: TextView
    lateinit var desc: TextView
    lateinit var price: TextView
    private lateinit var dbHelper: DatabaseHelper
    lateinit var buy: Button
    private lateinit var binding: ActivityDetailBarangBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBarangBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_detail_barang)

        namaBarangNav = findViewById(R.id.nama_barang)
        backBTN = findViewById(R.id.backBTN)
        gambar = findViewById(R.id.gambar_barang)
        namaBarang = findViewById(R.id.tv_item_name)
        size = findViewById(R.id.tv_item_size)
        rating = findViewById(R.id.tv_item_rating)
        desc = findViewById(R.id.tv_item_description)
        price = findViewById(R.id.tv_item_price)
        buy = findViewById(R.id.buyBTN)

        val dollId = intent.getIntExtra("EXTRA_ID", -1)

        dbHelper = DatabaseHelper(this)


        backBTN.setOnClickListener {
            finish()
        }

//        val doll: Doll? = intent.getParcelableExtra("DOLL_KEY")
//
        if (dollId != -1) {
            val doll = dbHelper.getDollById(dollId)
            if (doll != null) {
                // Display the details of the doll
                displayDollDetails(doll)
            }
        }

        buy.setOnClickListener {
            val doll = dbHelper.getDollById(dollId)
            if (doll != null) {
                val bottomSheet = BottomSheetBuy.newInstance(doll.imageLink, doll.name,doll.id)
                bottomSheet.show(supportFragmentManager, "BottomSheetBuy")
            }
        }

    }

    private fun displayDollDetails(doll: Doll) {
        namaBarangNav.text = doll.name
        namaBarang.text = doll.name
        size.text = doll.size
        rating.text = doll.rating.toString()
        desc.text = doll.desc
        price.text = "Rp ${doll.price}"

        Glide.with(this)
            .load(doll.imageLink)
            .centerCrop()
            .into(gambar)
    }
}