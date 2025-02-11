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


class MainAdapter(val charactersList: List<Character>): RecyclerView.Adapter<MainAdapter.MainViewHolder>() {

    inner class MainViewHolder(private val itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindData(character: Character) {
            val name = itemView.findViewById<TextView>(R.id.name)
            val image = itemView.findViewById<ImageView>(R.id.image)

            name.text = character.characterName
            image.load(character.characterImage) {
                transformations(CircleCropTransformation())
            }
        }}


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.view_item,parent, false))
    }

    override fun getItemCount(): Int {
        return charactersList.size
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        holder.bindData(charactersList[position])
    }
}



