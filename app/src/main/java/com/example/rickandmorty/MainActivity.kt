package com.example.rickandmorty

import android.media.Image
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode.Callback
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.rickandmorty.network.ApiClient
import com.example.rickandmorty.network.CharacterResponse
import retrofit2.Call
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.charactersView)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val client = ApiClient.apiService.fetchCharacters("1")

        client.enqueue(object : retrofit2.Callback<CharacterResponse> {

            override fun onResponse(
                call: Call<CharacterResponse>,
                response: Response<CharacterResponse>
            ) {
                if (response.isSuccessful) {
                    Log.d("characters", "" + response.body())

                    val result = response.body()?.result
                    result?.let{
                        val adapter = MainAdapter(result)
                        val recyclerView = findViewById<RecyclerView>(R.id.charactersView)
                        recyclerView?.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                        recyclerView?.adapter=adapter
                    }
                }
            }

            override fun onFailure(call: Call<CharacterResponse>, t: Throwable) {
                Log.e("failed", "" + t.message)
            }
        })
    }
}
