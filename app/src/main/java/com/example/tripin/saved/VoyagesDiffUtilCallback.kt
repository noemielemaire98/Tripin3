package com.example.tripin.saved

import androidx.recyclerview.widget.DiffUtil
import com.example.tripin.model.Voyage

// Pour pouvoir filtrer les lignes de la recyclerView de la page d'accueil

class VoyagesDiffUtilCallback(
    private val oldList: MutableList<Voyage>,
    private val newList: MutableList<Voyage>
) : DiffUtil.Callback() {
    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldList[oldItemPosition].id == newList[newItemPosition].id

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) = true
}