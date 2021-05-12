package com.example.myapplication
import retrofit2.Callback
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

//인풋

interface RegisterService{
    @FormUrlEncoded
    @POST("/register")
    fun requestRegister(
        @Field("userID")userID:String,
        @Field("School")School: String
    ):Call<Register> 
}