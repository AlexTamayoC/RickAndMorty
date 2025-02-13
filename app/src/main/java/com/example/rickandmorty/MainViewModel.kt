package com.example.rickandmorty

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.example.rickandmorty.network.ApiClient
import com.example.rickandmorty.network.Character
import com.example.rickandmorty.network.CharacterResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel(private val repository: Repository = Repository(ApiClient.apiService)) : ViewModel() {

    private var _characterLiveData = MutableLiveData<ScreenState<List<Character>?>>()
    val characterLiveData: LiveData<ScreenState<List<Character>?>>
        get() = _characterLiveData

    private var currentPage = 10
    private var isLoading = false
    private var isLastPage = false
    private var allCharacters: MutableList<Character> = mutableListOf()

    init {
        fetchCharacters()
    }

    fun fetchCharacters() {
        if (isLoading || isLastPage) return

        isLoading = true
        _characterLiveData.postValue(ScreenState.Loading(allCharacters))

        val client = repository.getCharacters(currentPage.toString())

        client.enqueue(object : Callback<CharacterResponse> {
            override fun onResponse(call: Call<CharacterResponse>, response: Response<CharacterResponse>) {
                isLoading = false
                if (response.isSuccessful) {
                    val newCharacters = response.body()?.result ?: emptyList()

                    if (newCharacters.isEmpty()) {
                        isLastPage = true
                    } else {
                        allCharacters.addAll(newCharacters)

                        _characterLiveData.postValue(ScreenState.Success(allCharacters))
                        currentPage++
                    }
                } else {
                    _characterLiveData.postValue(ScreenState.Error(response.code().toString(), allCharacters))
                }
            }

            override fun onFailure(call: Call<CharacterResponse>, t: Throwable) {
                isLoading = false
                _characterLiveData.postValue(ScreenState.Error(t.message.toString(), allCharacters))
            }
        })
    }

    fun handlePagination(recyclerView: RecyclerView) {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager
                val visibleItemCount = layoutManager?.childCount ?: 0
                val totalItemCount = layoutManager?.itemCount ?: 0
                val firstVisibleItemPosition = (layoutManager as? androidx.recyclerview.widget.LinearLayoutManager)?.findFirstVisibleItemPosition() ?: 0

                if (!isLoading && !isLastPage && visibleItemCount + firstVisibleItemPosition >= totalItemCount - 5) { // Load more when near the end (adjust 5 as needed)
                    fetchCharacters()
                }
            }
        })
    }
}
