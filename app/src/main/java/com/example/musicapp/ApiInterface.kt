package com.example.musicapp


import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import retrofit2.Call

interface ApiInterface {
    @Headers("X-RapidAPI-Key: 140b526307msh8329d66b3796ab6p1abdcfjsncd069ebd4a42",
        "X-RapidAPI-Host: deezerdevs-deezer.p.rapidapi.com")
    @GET("search")
    fun getData(@Query("q") query: String ) : retrofit2.Call<MyData>
}