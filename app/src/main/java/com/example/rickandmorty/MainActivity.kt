package com.example.rickandmorty


import android.os.Bundle
import android.view.View
import android.widget.ProgressBar

import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.rickandmorty.network.Character
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

        private val viewModel: MainViewModel by lazy {
            ViewModelProvider(this).get(MainViewModel::class.java)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        viewModel.characterLiveData.observe(this, { state ->
            processCharactersResponse(state)
        })
    }

        private fun processCharactersResponse(state: ScreenState<List<Character>?>){

            val pb = findViewById<ProgressBar>(R.id.progressBar)

            when(state){
                is ScreenState.Loading -> {
                    pb.visibility = View.VISIBLE
                }
                is ScreenState.Success -> {
                    pb.visibility = View.GONE
                    if(state.data != null){
                        val adapter =MainAdapter(state.data)
                        val recyclerView = findViewById<RecyclerView>(R.id.charactersView)
                        recyclerView?.layoutManager =
                                StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
                        recyclerView?.adapter = adapter
                    }
                }

                is ScreenState.Error ->{
                    pb.visibility = View.GONE
                    val view = pb.rootView
                    Snackbar.make(view, state.message!!, Snackbar.LENGTH_LONG).show()
            }
        }
    }
}
