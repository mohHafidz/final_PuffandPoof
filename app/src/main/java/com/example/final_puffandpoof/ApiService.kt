package com.example.final_puffandpoof

// ApiService.kt
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("9d7f4f02be5d5631a664")
    fun getDolls(): Call<DollResponse>
}

