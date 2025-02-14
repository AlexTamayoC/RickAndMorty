package com.example.rickandmorty

import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import com.example.rickandmorty.network.Character
import android.widget.TextView
import android.view.View
import android.view.ViewGroup
import coil.load
import coil.transform.CircleCropTransformation
import android.widget.ImageView


class MainAdapter(private val charactersList: MutableList<Character>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_ITEM = 0
    private val VIEW_TYPE_LOADING = 1
    private var isLoading = false

    inner class MainViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindData(character: Character) {
            val name = itemView.findViewById<TextView>(R.id.name)
            val image = itemView.findViewById<ImageView>(R.id.image)
            val species = itemView.findViewById<TextView>(R.id.namespecies)

            name.text = character.characterName
            species.text = character.characterSpecies
            image.load(character.characterImage) {
                transformations(CircleCropTransformation())
            }
        }
    }

    inner class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_ITEM) {
            MainViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.view_item, parent, false))
        } else {
            LoadingViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.loading_item, parent, false))
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < charactersList.size) VIEW_TYPE_ITEM else VIEW_TYPE_LOADING
    }

    override fun getItemCount(): Int {
        return charactersList.size + if (isLoading) 1 else 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MainViewHolder) {
            holder.bindData(charactersList[position])
        }
    }

    fun appendData(newCharacters: List<Character>) {
        val startPos = charactersList.size
        charactersList.addAll(newCharacters)
        notifyItemRangeInserted(startPos, newCharacters.size)
    }

    fun showLoading() {
        if (!isLoading) {
            isLoading = true
            notifyItemInserted(charactersList.size)
        }
    }

    fun hideLoading() {
        if (isLoading) {
            isLoading = false
            notifyItemRemoved(charactersList.size)
        }
    }
}

