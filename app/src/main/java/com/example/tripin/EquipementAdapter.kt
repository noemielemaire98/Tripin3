package com.example.tripin

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import kotlinx.android.synthetic.main.equipement_view.view.*
import kotlinx.coroutines.runBlocking



class EquipementAdapter(val equipements: MutableList<String>?, context: Context): RecyclerView.Adapter<EquipementAdapter.EquipementViewHolder>() {

    class EquipementViewHolder(val equipementView : View): RecyclerView.ViewHolder(equipementView)

    override fun getItemCount(): Int = equipements!!.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EquipementViewHolder {
        val layoutInflater : LayoutInflater = LayoutInflater.from(parent.context)
        val view: View = layoutInflater.inflate(R.layout.equipement_view, parent, false)
        return EquipementViewHolder(view)
    }

    override fun onBindViewHolder(holder: EquipementViewHolder, position: Int) {
runBlocking {
        if (equipements == null){

        } else{
        val equipement = equipements!![position]
                    holder.equipementView.equiment_imageview.setImageResource(R.drawable.wifi)
                    holder.equipementView.equiment_textview.text = equipement




        }

            }
        }


    fun  getDrawableByName(context : Context, name : String):Drawable{
    var resources : Resources = context.resources
  var resourceId : Int = resources.getIdentifier(name, "drawable", context.getPackageName());
    return resources.getDrawable(resourceId,null);
}
    }


