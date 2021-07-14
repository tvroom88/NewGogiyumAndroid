package com.newgogiyumandroid.RecyclerView


import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.newgogiyumandroid.DetailActivity
import com.newgogiyumandroid.JsonParsingLists.RestaurantList
import com.newgogiyumandroid.R
import kotlinx.android.synthetic.main.restaurant_item.view.*
import java.util.*


class RestaurantAdapter: RecyclerView.Adapter<RestaurantHolder>() {

    var listData = mutableListOf<RestaurantList>()

    init {
        setHasStableIds(true)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.restaurant_item, parent, false)
        return RestaurantHolder(itemView)
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onBindViewHolder(holder: RestaurantHolder, position: Int) {
        val restuarantList = listData.get(position)
        holder.setRestaurantList(restuarantList)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
}

class RestaurantHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
    // 여기다가 onClickListener
    init {
        itemView.setOnClickListener(){
            val intent = Intent(itemView.context, DetailActivity::class.java)
            intent.putExtra("name", itemView.resName.text)
            itemView.context.startActivity(intent)
        }
    }

    fun setRestaurantList(restuarantList: RestaurantList) {
        itemView.resName.text = restuarantList.name
        itemView.resAddress.text = restuarantList.address
        itemView.ratingBar1.rating = restuarantList.yrating.toFloat()
        itemView.ratingBar2.rating = restuarantList.grating.toFloat()
        itemView.btn2.text = when (restuarantList.price) {
            "" -> "?"
            else -> restuarantList.price
        }

        itemView.menuBtn.setOnClickListener{
            goToUrl(restuarantList.menu)
        }

        itemView.btn4.setOnClickListener {
            when (restuarantList.phone) {
                "" ->   {
                    val popupMenu = PopupMenu(itemView.context, itemView.btn4)
                    popupMenu.menuInflater.inflate(R.menu.phone_popup_menu,popupMenu.menu)
                    popupMenu.show()
                }
                else -> itemView.btn4.setOnClickListener {
                    phoneCall(restuarantList.phone)
                }
            }
        }

        when(restuarantList.uberURL){
            "0" -> {
                itemView.uberButton.alpha = 0.2F
                itemView.testTV1.text = ""
            }

            else -> {
//                itemView.uberButton.alpha = 1.0F
                itemView.uberButton.setOnClickListener{ goToUrl(restuarantList.uberURL) }
                itemView.testTV1.text = "Url connected"
            }
        }
        when(restuarantList.gruhubURL){
            "0" -> itemView.GrubButton.alpha = 0.2F
            else -> {
//                itemView.GrubButton.alpha = 1F
                itemView.GrubButton.setOnClickListener{ goToUrl(restuarantList.gruhubURL) }
            }
        }
        when(restuarantList.doordashURL){
            "0" -> itemView.DoorButton.alpha = 0.2F
            else -> {
//                itemView.DoorButton.alpha = 1F
                itemView.DoorButton.setOnClickListener{ goToUrl(restuarantList.doordashURL) }
            }
        }


        //just check for time
        time()
    }

    fun time(){
        val calendar: Calendar = Calendar.getInstance()
        val hour24hrs: Int = calendar.get(Calendar.HOUR_OF_DAY)
        val hour12hrs: Int = calendar.get(Calendar.HOUR)
        val minutes: Int = calendar.get(Calendar.MINUTE)
        val seconds: Int = calendar.get(Calendar.SECOND)

    }


    private fun phoneCall(phoneNum: String) {
        val TELEPHONE_SCHEMA = "tel:"

        // Step 1: Define the phone call uri
        val phoneCallUri = Uri.parse(TELEPHONE_SCHEMA + phoneNum)

        // Step 2: Set Intent action to `ACTION_DIAL`
        val phoneCallIntent = Intent(Intent.ACTION_DIAL).also {
            // Step 3. Set phone call uri to Intent data
            it.setData(phoneCallUri)
        }

        // Step 4: Pass the Intent to System to start any <Activity> which can accept `ACTION_DIAL`
        itemView.context.startActivity(phoneCallIntent)
    }

    private fun goToUrl(url: String) {
        val uriUrl: Uri = Uri.parse(url)
        val launchBrowser = Intent(Intent.ACTION_VIEW, uriUrl)
        itemView.context.startActivity(launchBrowser)
    }
}


