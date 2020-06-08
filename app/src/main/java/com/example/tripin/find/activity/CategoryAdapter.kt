package com.example.tripin.find.activity

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tripin.R
import kotlinx.android.synthetic.main.category_view.view.*

class CategoryAdapter (val list_category: List<String>) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    class CategoryViewHolder(val categoryView : View) : RecyclerView.ViewHolder(categoryView)

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val layoutInflater : LayoutInflater = LayoutInflater.from(parent.context)
        val view: View = layoutInflater.inflate(R.layout.category_view, parent, false)

        context = parent.context

        return CategoryViewHolder(
            view
        )
    }

    override fun getItemCount(): Int = list_category.size



    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {

        holder.categoryView.txt_category.text = list_category[position]


        holder.categoryView.setOnClickListener {

        }



    }

}