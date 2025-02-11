package com.example.rickandmorty.network

import com.squareup.moshi.Json

data class Character(
    @Json(name="name")
    val CharacterName: String,
    @Json(name="image")
    val CharacterImage: String,
    @Json(name="species")
    val CharacterSpecies: String)


data class CharacterResponse(@Json(name="results")
val result : List<Character>)