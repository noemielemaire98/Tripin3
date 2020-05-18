package com.example.tripin.saved

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.tripin.MainActivity
import com.example.tripin.R
import com.example.tripin.data.AppDatabase
import com.example.tripin.data.HotelDao
import com.example.tripin.find.hotel.HotelsAdapter
import kotlinx.android.synthetic.main.activity_saved_hotel.*
import kotlinx.coroutines.runBlocking

/**
 * A simple [Fragment] subclass.
 */
class SavedHotelFragment : Fragment() {

    private var hotelDaoSaved : HotelDao? = null
    private var list_favoris = arrayListOf<Boolean>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.activity_saved_hotel, container, false)

        val rv = root.findViewById<RecyclerView>(R.id.hotels_saved_recyclerview)
        val iv_nohotel = root.findViewById<ImageView>(R.id.noFlightsImage)
        val layout_nohotel = root.findViewById<RelativeLayout>(R.id.layoutNoSavedHotel)
        val layout_rv = root.findViewById<RelativeLayout>(R.id.layoutRecyclerView_HotelsSaved)

        rv.layoutManager = LinearLayoutManager(requireActivity().baseContext, LinearLayoutManager.VERTICAL, false)

       iv_nohotel.setOnClickListener {
            val intent = Intent(requireActivity().baseContext, MainActivity::class.java)
            intent.putExtra("switchView", 2)
            startActivity(intent)

        }


        val databasesaved =
            Room.databaseBuilder(requireActivity().baseContext, AppDatabase::class.java, "savedDatabase")
                .build()



        hotelDaoSaved = databasesaved.getHotelDao()
        runBlocking {
            val hotels = hotelDaoSaved?.getHotels()
            hotels?.map {
                list_favoris.add(true)
            }

            if(!hotels.isNullOrEmpty()){
                layout_nohotel.visibility = View.GONE
               rv.adapter =
                    HotelsAdapter(hotels!!, list_favoris)

            }else{
                layout_nohotel.visibility = View.VISIBLE
                layout_rv.visibility = View.GONE
            }

        }

        return root
    }

}
