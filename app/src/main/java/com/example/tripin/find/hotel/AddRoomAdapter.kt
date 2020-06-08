package com.example.tripin.find.hotel

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tripin.R
import kotlinx.android.synthetic.main.add_room_view.view.*

class AddRoomAdapter(var listAdults: MutableList<String>?): RecyclerView.Adapter<AddRoomAdapter.AddRoomViewHolder>() {

    class AddRoomViewHolder(val addRoomView: View) : RecyclerView.ViewHolder(addRoomView)

    private lateinit var context: Context
    private var Items: String = ""

    override fun getItemCount(): Int {
        if(listAdults.isNullOrEmpty()){
            return 0
        }else{
            return listAdults!!.size
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddRoomViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view: View = layoutInflater.inflate(R.layout.add_room_view, parent, false)
        context = parent.context
        return AddRoomViewHolder(view)
    }

    override fun onBindViewHolder(holder: AddRoomViewHolder, position: Int) {

        val adults = listAdults?.get(position)

        holder.addRoomView.add_room_number_view.text = "Chambre n° ${position +1 }"
        holder.addRoomView.add_room_number_adults_view.text = adults


        holder.addRoomView.delete_room_view.setOnClickListener {
          //  val newList= listAdults?.drop(position)
           // listAdults = newList as MutableList<String>?
            listAdults?.removeAt(position)
            notifyDataSetChanged()
            notifyItemRemoved(position)
            listAdults?.size?.let { it1 -> notifyItemRangeChanged(position, it1) }

        }

    }


}