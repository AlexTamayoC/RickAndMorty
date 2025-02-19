package com.example.rickandmorty.ui
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.rickandmorty.R
import com.example.rickandmorty.model.Character
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var adapter: MainAdapter
    private lateinit var recyclerView: RecyclerView
    private val viewModel by lazy { ViewModelProvider(this).get(MainViewModel::class.java) }
    private lateinit var searchView: SearchView
    private lateinit var dataList: ArrayList<Character>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        setupRecyclerView()
        setupSearchView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.charactersView)
        searchView = findViewById(R.id.search)
        adapter = MainAdapter(mutableListOf())
        recyclerView.layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.adapter = adapter
        recyclerView.addOnScrollListener(createScrollListener())
        dataList = arrayListOf<Character>()
    }

    private fun setupSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterCharacters(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterCharacters(newText)
                return true
            }
        })
    }

    private fun filterCharacters(query: String?) {
        val filteredList = if (query.isNullOrEmpty()) {
            dataList
        } else {
            dataList.filter { it.characterName.contains(query, ignoreCase = true) }
        }
        adapter.updateData(filteredList)
    }

    private fun observeViewModel() {
        viewModel.pageCharactersLiveData.observe(this) { newCharacters ->
            adapter.hideLoading()
            dataList.addAll(newCharacters)
            adapter.appendData(newCharacters)
        }
    }

    private fun createScrollListener() = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager = recyclerView.layoutManager as StaggeredGridLayoutManager
            val lastVisibleItem = layoutManager.findLastVisibleItemPositions(null).maxOrNull() ?: 0
            if (lastVisibleItem >= layoutManager.itemCount - 4 && viewModel.hasMorePages) {
                adapter.showLoading()
                viewModel.fetchCharacters()
            }
        }
    }
}
