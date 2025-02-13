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
import kotlin.reflect.jvm.internal.impl.metadata.ProtoBuf.Visibility


class MainActivity : AppCompatActivity() {

    private lateinit var adapter: MainAdapter
    private lateinit var recyclerView: RecyclerView
    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.charactersView)
        val pb = findViewById<ProgressBar>(R.id.progressBar)

        adapter = MainAdapter(mutableListOf())
        recyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.adapter = adapter

        viewModel.characterLiveData.observe(this, { state ->
            when (state) {
                is ScreenState.Loading -> pb.visibility = View.VISIBLE
                is ScreenState.Success -> {
                    pb.visibility = View.GONE
                    state.data?.let { adapter.updateData(it) }
                }
                is ScreenState.Error -> {
                    pb.visibility = View.GONE
                    Snackbar.make(recyclerView, state.message!!, Snackbar.LENGTH_LONG).show()
                }
            }
        })


        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as StaggeredGridLayoutManager
                val lastVisibleItemPositions = layoutManager.findLastVisibleItemPositions(null)
                val lastVisibleItem = lastVisibleItemPositions.maxOrNull() ?: 0
                val totalItemCount = layoutManager.itemCount

                if (lastVisibleItem >= totalItemCount - 5) {
                    viewModel.fetchCharacters()
                }
            }
        })

        viewModel.characterLiveData.observe(this, { state ->
            when (state) {
                is ScreenState.Loading -> pb.visibility = View.VISIBLE
                is ScreenState.Success -> {
                    pb.visibility = View.GONE
                    state.data?.let { adapter.updateData(it) }

                    if (!viewModel.hasMorePages) {
                        Snackbar.make(recyclerView, "Se han cargado todos los personajes", Snackbar.LENGTH_LONG).show()
                    }
                }
                is ScreenState.Error -> {
                    pb.visibility = View.GONE
                    Snackbar.make(recyclerView, state.message!!, Snackbar.LENGTH_LONG).show()
                }
            }
        })

    }
}
