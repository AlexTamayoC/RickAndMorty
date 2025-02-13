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

    private val _charactersLiveData = MutableLiveData<ScreenState<List<Character>?>>()
    val characterLiveData: LiveData<ScreenState<List<Character>?>>
        get() = _charactersLiveData

    private val charactersList = mutableListOf<Character>()
    private var currentPage = 1
    private var isLoading = false
    var hasMorePages = true
    private var totalPages = 1

    init {
        fetchCharacters()
    }

    fun fetchCharacters() {
        if (isLoading || !hasMorePages) return
        isLoading = true

        Log.d("PAGINACIÓN", "Cargando página: $currentPage de $totalPages")

        _charactersLiveData.postValue(ScreenState.Loading(if (currentPage == 1) null else charactersList)) // Indicate loading, possibly with previous data

        val client = repository.getCharacters(currentPage.toString())

        client.enqueue(object : Callback<CharacterResponse> {
            override fun onResponse(call: Call<CharacterResponse>, response: Response<CharacterResponse>) {
                isLoading = false
                if (response.isSuccessful) {
                    response.body()?.let { characterResponse ->

                        if (currentPage == 1) {
                            totalPages = characterResponse.pageInfo.pages
                            charactersList.clear() // Still clear on the first page
                            charactersList.addAll(characterResponse.result)
                            _charactersLiveData.postValue(ScreenState.Success(charactersList.toList())) // Important: Create a new list!
                        } else {
                            val newList = charactersList + characterResponse.result // Create a *new* list
                            charactersList.addAll(characterResponse.result) // Add to the existing list for pagination
                            _charactersLiveData.postValue(ScreenState.Success(newList.toList())) // Post the *new* list
                        }

                        if (characterResponse.pageInfo.next != null) {
                            currentPage++
                        } else {
                            hasMorePages = false
                            Log.d("PAGINACIÓN", "Se cargaron todos los personajes.")
                        }
                    }
                } else {
                    _charactersLiveData.postValue(ScreenState.Error(response.code().toString(), if (currentPage == 1) null else charactersList))
                }
            }

            override fun onFailure(call: Call<CharacterResponse>, t: Throwable) {
                isLoading = false
                _charactersLiveData.postValue(ScreenState.Error(t.message.toString(), if (currentPage == 1) null else charactersList))
            }
        })
    }
}
