package com.example.final_puffandpoof.model

data class CartDatabase(
    val id: Int,
    val UserID: Int,
    val quantity: Int,
    val imageLink: String,
    val price: Int,
    val nama_barang: String
)
