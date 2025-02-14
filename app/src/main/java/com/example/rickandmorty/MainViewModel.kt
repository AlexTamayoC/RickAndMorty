package com.example.rickandmorty

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.rickandmorty.network.ApiClient
import com.example.rickandmorty.network.Character
import com.example.rickandmorty.network.CharacterResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel(private val repository: Repository = Repository(ApiClient.apiService)) : ViewModel() {

    private val _pageCharactersLiveData = MutableLiveData<List<Character>>()
    val pageCharactersLiveData: LiveData<List<Character>> get() = _pageCharactersLiveData


    private val accumulatedCharacters = mutableListOf<Character>()
    var currentPage = 1
    private var isLoading = false
    var totalPages = 1
    var hasMorePages = true

    init {
        fetchCharacters()
    }

    fun fetchCharacters() {
        if (isLoading || !hasMorePages) return
        isLoading = true

        Log.d("PAGINACION", "Cargando página: $currentPage de $totalPages")
        val client = repository.getCharacters(currentPage.toString())

        client.enqueue(object : Callback<CharacterResponse> {
            override fun onResponse(call: Call<CharacterResponse>, response: Response<CharacterResponse>) {
                isLoading = false
                if (response.isSuccessful) {
                    response.body()?.let { characterResponse ->
                        val newCharacters = characterResponse.result

                        accumulatedCharacters.addAll(newCharacters)

                        _pageCharactersLiveData.postValue(newCharacters)

                        if (currentPage == 1) {
                            totalPages = characterResponse.pageInfo.pages
                        }

                        if (characterResponse.pageInfo.next != null) {
                            currentPage++
                        } else {
                            hasMorePages = false
                            Log.d("PAGINACION", "Se han cargado todos los personajes.")
                        }
                    }
                }
            }

            override fun onFailure(call: Call<CharacterResponse>, t: Throwable) {
                isLoading = false
            }
        })
    }
}