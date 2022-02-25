package com.example.coba_app

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiEndPoint {

    @GET("menus")
    fun getMenu(): Call<MenuModel>

    @GET("menus/search/{search}")
    fun searchMenu(@Path("search") search: String): Call<MenuModel>

    @Multipart
    @POST("menus")
    fun createMenu(@Part("name") name: RequestBody, @Part("price") price: RequestBody, @Part("carbo") carbo: RequestBody, @Part("protein") protein: RequestBody, @Part image: MultipartBody.Part): Call<ResponseModel>

    @Multipart
    @POST("menus/{id}?_method=PUT")
    fun updateMenu(@Path("id") id: Int, @Part("name") name: RequestBody, @Part("price") price: RequestBody, @Part("carbo") carbo: RequestBody, @Part("protein") protein: RequestBody, @Part image: MultipartBody.Part): Call<ResponseModel>

    @DELETE("menus/{id}")
    fun deleteMenu(@Path("id") id: Int): Call<ResponseModel>

    @FormUrlEncoded
    @POST("login")
    fun login(@Field("email") email: String, @Field("password") password: String): Call<UserModel>

    @FormUrlEncoded
    @POST("orderheaders")
    fun orderHeader(@Field("user_id") user_id: Int): Call<ResponseModel>

    @FormUrlEncoded
    @POST("orderdetails")
    fun orderDetail(@Field("header_id") header_id: Int, @Field("menu_id") menu_id: Int, @Field("qty") qty: Int): Call<ResponseModel>

}