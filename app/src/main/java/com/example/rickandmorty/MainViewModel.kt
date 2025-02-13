package com.example.rickandmorty

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.rickandmorty.network.ApiClient
import com.example.rickandmorty.network.CharacterResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.rickandmorty.network.Character
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView

class MainViewModel (private val repository: Repository
                     =Repository(ApiClient.apiService)): ViewModel(){
    private var _characterLiveData = MutableLiveData<ScreenState<List<Character>?>>()
    val characterLiveData : LiveData<ScreenState<List<Character>?>>
        get() = _characterLiveData

    init{
        fetchCharacter()
    }

    private fun fetchCharacter(){

        val client = repository.getCharacters("1")
        _characterLiveData.postValue(ScreenState.Loading(null))
        client.enqueue(object : Callback<CharacterResponse> {
            override fun onResponse(
                call: Call<CharacterResponse>,
                response: Response<CharacterResponse>
            ) {
                if (response.isSuccessful) {
                    _characterLiveData.postValue(ScreenState.Success(response.body()?.result))
                }
                else{
                    _characterLiveData.postValue(ScreenState.Error(response.code().toString(), null))
                }
            }

            override fun onFailure(call: Call<CharacterResponse>, t: Throwable) {
                _characterLiveData.postValue(ScreenState.Error(t.message.toString(), null))
            }
        })

    }
}
