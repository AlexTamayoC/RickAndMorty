package com.example.rickandmorty.repository

import com.example.rickandmorty.network.ApiService

class Repository(private val apiService: ApiService) {
    fun getCharacters(page: String) = apiService.fetchCharacters(page)
}