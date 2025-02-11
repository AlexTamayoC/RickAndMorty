package com.example.rickandmorty.network

import com.squareup.moshi.Json

data class Character(
    @Json(name="name")
    val characterName: String,
    @Json(name="image")
    val characterImage: String)

data class CharacterResponse(@Json(name="results")
val result : List<Character>)