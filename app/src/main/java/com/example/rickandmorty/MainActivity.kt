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
        adapter = MainAdapter(mutableListOf())
        recyclerView.layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.adapter = adapter


        viewModel.pageCharactersLiveData.observe(this, { newCharacters ->
            adapter.hideLoading()
            adapter.appendData(newCharacters)
        })

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as StaggeredGridLayoutManager
                val lastVisibleItemPositions = layoutManager.findLastVisibleItemPositions(null)
                val lastVisibleItem = lastVisibleItemPositions.maxOrNull() ?: 0
                val totalItemCount = layoutManager.itemCount

                if (lastVisibleItem >= totalItemCount - 4) {
                    if (viewModel.hasMorePages) {
                        adapter.showLoading()
                        viewModel.fetchCharacters()
                    }
                }
            }
        })
    }
}
