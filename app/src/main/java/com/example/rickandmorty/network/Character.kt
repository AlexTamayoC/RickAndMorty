package com.example.rickandmorty.network

import com.squareup.moshi.Json

data class Character(
    @Json(name="name")
    val characterName: String,
    @Json(name="image")
    val characterImage: String,
    @Json(name="species")
    val characterSpecies: String,
    @Json(name="id")
    val characterId: Int)

data class CharacterResponse(
    @Json(name = "info") val pageInfo: PageInfo,
    @Json(name = "results") val result: List<Character>
)

data class PageInfo(
    @Json(name = "count") val count: Int,
    @Json(name = "pages") val pages: Int,
    @Json(name = "next") val next: String?,
    @Json(name = "prev") val prev: String?
)