package com.example.tripin.find.hotel

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tripin.R
import com.example.tripin.model.Equipement
import kotlinx.android.synthetic.main.equipement_view.view.*


class EquipementAdapter(val equipements: List<Equipement>, val drawableNameList: MutableList<String>, context: Context): RecyclerView.Adapter<EquipementAdapter.EquipementViewHolder>() {


    class EquipementViewHolder(val equipementView : View): RecyclerView.ViewHolder(equipementView)

    private lateinit var context : Context
    private var Items : String = ""

    override fun getItemCount(): Int = equipements.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EquipementViewHolder {
        val layoutInflater : LayoutInflater = LayoutInflater.from(parent.context)
        val view: View = layoutInflater.inflate(R.layout.equipement_view, parent, false)
        context = parent.context
          return EquipementViewHolder(view)
    }

    override fun onBindViewHolder(holder: EquipementViewHolder, position: Int) {

        val equipement = equipements[position]
        Items = ""
        val drawableName = drawableNameList[position]
            val id: Int = context.resources.getIdentifier(drawableName, "drawable", context.packageName)
                    holder.equipementView.equiment_imageview.setImageResource(id)
                    holder.equipementView.header_textview.text = equipement.heading


        equipement.listItem.forEach {
            Items += "${it} \n"
        }
        Items = Items.replace("&nbsp;"," ",false)
        holder.equipementView.list_equipements.text = Items



        }





    fun  getDrawableByName(context : Context, name : String):Drawable{
    var resources : Resources = context.resources
  var resourceId : Int = resources.getIdentifier(name, "drawable", context.getPackageName());
    return resources.getDrawable(resourceId,null);
}
}


