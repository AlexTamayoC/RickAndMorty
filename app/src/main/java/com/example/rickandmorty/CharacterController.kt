package com.example.rickandmorty

import android.media.Image
import android.os.Parcel
import android.os.Parcelable

class CharacterController (var model: Character, var view: MainActivity) : Parcelable {
    constructor(parcel: Parcel) : this(
        TODO("model"),
        TODO("view")
    ) {
    }

    fun getCharacterName(): String{
        return model.CharacterName
    }

    fun getCharacterSpecies(): String{
        return model.CharacterSpecies
    }

    fun getCharacterImage(): Image{
        return model.CharacterImage
    }

    fun setCharacterName(name:String){
        model.CharacterName = name
    }

    fun setCharacterSpecies(species:String){
        model.CharacterSpecies = species
    }

    fun setCharacterImage(image: Image){
        model.CharacterImage = image
    }

    fun updateView(){
        view.printDetails(model.CharacterName, model.CharacterSpecies, model.CharacterImage)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CharacterController> {
        override fun createFromParcel(parcel: Parcel): CharacterController {
            return CharacterController(parcel)
        }

        override fun newArray(size: Int): Array<CharacterController?> {
            return arrayOfNulls(size)
        }
    }
}