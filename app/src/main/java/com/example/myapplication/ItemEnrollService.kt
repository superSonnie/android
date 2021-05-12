package com.example.myapplication


import okhttp3.Callback
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ItemEnrollService{
    @Multipart
    @POST("/itemEnroll")
    fun itemEnroll(
        @Part("userID")userID:String,
        @Part ("School")School: String,
        @Part ("title")title:String,
        @Part ("content")content: String,
        @Part ("time")time: String,
        @Part ("photo")photo:String
        ):Call<Register>
}