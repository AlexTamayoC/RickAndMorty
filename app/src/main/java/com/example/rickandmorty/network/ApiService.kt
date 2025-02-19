package com.example.rickandmorty.network

import com.example.rickandmorty.model.CharacterResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

//Create an interface
interface ApiService{
    @GET("character")
    fun fetchCharacters(@Query("page") page:String): Call<CharacterResponse>
}