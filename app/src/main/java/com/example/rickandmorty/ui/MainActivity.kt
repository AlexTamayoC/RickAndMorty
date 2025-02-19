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
    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }
    private lateinit var searchView: SearchView
    private lateinit var searchList: ArrayList<Character>
    private lateinit var dataList: ArrayList<Character>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.charactersView)
        adapter = MainAdapter(mutableListOf())
        recyclerView.layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.adapter = adapter
        searchView = findViewById(R.id.search)
        dataList = arrayListOf<Character>()
        searchList = arrayListOf<Character>()

        searchView.clearFocus()

        adapter = MainAdapter(mutableListOf()) // Initialize with an empty list
        recyclerView.adapter = adapter

        viewModel.pageCharactersLiveData.observe(this, { characters ->
            adapter.appendData(characters) // Set the data directly from the LiveData
            adapter.notifyDataSetChanged() // Notify the adapter
        })

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val searchText = newText?.toLowerCase(Locale.getDefault()) ?: ""

                val filteredList = if (searchText.isNotEmpty()) {
                    viewModel.pageCharactersLiveData.value?.filter { character ->
                        character.characterName.toLowerCase(Locale.getDefault()).contains(searchText)
                    } ?: emptyList() // Handle the case where charactersLiveData.value is null
                } else {
                    viewModel.pageCharactersLiveData.value ?: emptyList() // Show all if search is empty
                }

                adapter.appendData(filteredList)
                recyclerView.adapter = adapter
                adapter.notifyDataSetChanged()
                return false
            }
        })


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
