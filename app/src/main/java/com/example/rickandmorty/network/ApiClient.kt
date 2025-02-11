package com.example.rickandmorty.network

import retrofit2.converter.moshi.MoshiConverterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query

object ApiClient {

    private val  BASE_URL = "https://rickandmortyapi.com/api/"

    //Variable for moshi builder
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}

//Create an interface
interface ApiService{
    @GET("character")
    fun fetchCharacters(@Query("page") page:String): Call<CharacterResponse>
}