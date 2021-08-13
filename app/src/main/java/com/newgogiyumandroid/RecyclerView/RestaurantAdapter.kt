package com.newgogiyumandroid.RecyclerView


import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.core.view.doOnPreDraw
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.newgogiyumandroid.JsonParsingLists.RestaurantList
import com.newgogiyumandroid.R
import com.newgogiyumandroid.RoomDataBase.SavedRestaurantDataBase
import kotlinx.android.synthetic.main.recyler_item.view.*
import kotlinx.android.synthetic.main.restaurant_item.view.*
import kotlinx.android.synthetic.main.saved_food_item.view.*
import kotlinx.android.synthetic.main.saved_restaurant_item.*


class RestaurantAdapter: RecyclerView.Adapter<RestaurantHolder>() {

    var listData = mutableListOf<RestaurantList>()

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.restaurant_item, parent, false)

        // 싱글톤 패턴을 사용하지 않은 경우
        val db = Room.databaseBuilder(
            parent.context,
            SavedRestaurantDataBase::class.java,
            "savedRestaurant-database"
        ).allowMainThreadQueries() // 그냥 강제로 실행
            .fallbackToDestructiveMigration()
            .build()


        return RestaurantHolder(itemView, db)
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

class RestaurantHolder(itemView: View, val db: SavedRestaurantDataBase) : RecyclerView.ViewHolder(itemView){
    // 여기다가 onClickListener
//    init {
//        itemView.setOnClickListener(){
//            val intent = Intent(itemView.context, DetailActivity::class.java)
//            intent.putExtra("name", itemView.resName.text)
//            itemView.context.startActivity(intent)
//        }
//    }
//
    fun setRestaurantList(restuarantList: RestaurantList) {
        itemView.resName.text = restuarantList.name
        itemView.resAddress.text = restuarantList.address
        itemView.yelp_rating_textview.text = restuarantList.yrating.toString()
        itemView.google_rating_textview.text = restuarantList.grating.toString()
        itemView.yelp_rating_bar.rating = restuarantList.yrating.toFloat()
        itemView.google_rating_bar.rating = restuarantList.grating.toFloat()
//        itemView.btn2.text = when (restuarantList.price) {
//            "" -> "?"
//            else -> restuarantList.price
//        }

//      line 처리를 위해
         itemView.resName.setMaxLinesToEllipsize()
//        put DataBase
        val itemMenuName = itemView.resName.text as String
        var alreadyExist = db.savedRestaurantDao().exists(itemMenuName)

        if(alreadyExist){
            itemView.saved.setImageResource(R.drawable.saved_black)
        }

        itemView.saved.setOnClickListener {
            alreadyExist = db.savedRestaurantDao().exists(itemMenuName)
            if(alreadyExist){
                db.savedRestaurantDao().deleteMenuByName(itemMenuName)
                itemView.saved.setImageResource(R.drawable.saved)
            }else{
                db.savedRestaurantDao().insert(restuarantList)
                itemView.saved.setImageResource(R.drawable.saved_black)
            }
//            itemView.showNumOfSaved.text = db.savedRestaurantDao().getAll().size.toString()
        }



        itemView.menuButton.setOnClickListener{
            goToUrl(restuarantList.menu)
        }
//
        itemView.callButton.setOnClickListener {
            when (restuarantList.phone) {
                "" ->   {
                    val popupMenu = PopupMenu(itemView.context, itemView.callButton)
                    popupMenu.menuInflater.inflate(R.menu.phone_popup_menu,popupMenu.menu)
                    popupMenu.show()
                }
                else -> itemView.callButton.setOnClickListener {
                    phoneCall(restuarantList.phone)
                }
            }
        }
        when (restuarantList.uberURL) {
            "0" -> {
                itemView.uberButton.setTextColor(Color.rgb(176, 176, 176))
                itemView.uberButton.setBackgroundResource(R.drawable.restaurant_button_empty)
                itemView.uberButton.isClickable = false
            }
            else -> {
                itemView.uberButton.setTextColor(Color.rgb(247, 247, 248))
                itemView.uberButton.setBackgroundResource(R.drawable.restaurant_button)
                itemView.uberButton.setOnClickListener { goToUrl(restuarantList.uberURL) }
            }
        }
        when (restuarantList.gruhubURL) {
            "0" -> {
                itemView.GrubButton.setTextColor(Color.rgb(176, 176, 176))
                itemView.GrubButton.setBackgroundResource(R.drawable.restaurant_button_empty)
                itemView.GrubButton.isClickable = false
            }
            else -> {
                itemView.GrubButton.setTextColor(Color.rgb(247, 247, 248))
                itemView.GrubButton.setBackgroundResource(R.drawable.restaurant_button)
                itemView.GrubButton.setOnClickListener { goToUrl(restuarantList.gruhubURL) }
            }
        }
        when (restuarantList.doordashURL) {
            "0" -> {
                itemView.DoorButton.setTextColor(Color.rgb(176, 176, 176))
                itemView.DoorButton.setBackgroundResource(R.drawable.restaurant_button_empty)
                itemView.DoorButton.isClickable = false
            }
            else -> {
                itemView.DoorButton.setTextColor(Color.rgb(247, 247, 248))
                itemView.DoorButton.setBackgroundResource(R.drawable.restaurant_button)
                itemView.DoorButton.setOnClickListener { goToUrl(restuarantList.doordashURL) }
            }
        }
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

    fun TextView.setMaxLinesToEllipsize() = doOnPreDraw {
        val numberOfCompletelyVisibleLines = (measuredHeight - paddingTop - paddingBottom) / lineHeight
        maxLines = numberOfCompletelyVisibleLines
    }
}


