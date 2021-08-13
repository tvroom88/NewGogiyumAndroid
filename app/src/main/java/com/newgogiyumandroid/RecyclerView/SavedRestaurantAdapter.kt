package com.newgogiyumandroid.RecyclerView

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.newgogiyumandroid.JsonParsingLists.RestaurantList
import com.newgogiyumandroid.R
import com.newgogiyumandroid.RoomDataBase.SavedRestaurantDataBase
import kotlinx.android.synthetic.main.restaurant_item.view.*

class SavedRestuarantAdapter: RecyclerView.Adapter<SavedRestaurantHolder>() {

    var listData = mutableListOf<RestaurantList>()

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedRestaurantHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.saved_restaurant_item, parent, false)

        // This is for getting View from SavedFoodAdapter
        val root = parent.rootView

        val db = Room.databaseBuilder(
            parent.context,
            SavedRestaurantDataBase::class.java,
            "savedRestaurant-database"
        ).allowMainThreadQueries() // 그냥 강제로 실행
            .fallbackToDestructiveMigration()
            .build()


        return SavedRestaurantHolder(itemView, db, root)
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onBindViewHolder(holder: SavedRestaurantHolder, position: Int) {
        val restuarantList = listData.get(position)
        holder.setRestaurantList(restuarantList)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
}

class SavedRestaurantHolder(itemView: View, val db: SavedRestaurantDataBase, root: View) : RecyclerView.ViewHolder(itemView) {
    // 여기다가 onClickListener

    fun setRestaurantList(restuarantList: RestaurantList) {
        itemView.resName.text = restuarantList.name
        itemView.resAddress.text = restuarantList.address
        itemView.yelp_rating_textview.text = restuarantList.yrating.toString()
        itemView.google_rating_textview.text = restuarantList.grating.toString()
        itemView.yelp_rating_bar.rating = restuarantList.yrating.toFloat()
        itemView.google_rating_bar.rating = restuarantList.grating.toFloat()
//        val itemMenuName = itemView.resName.text as String
    }

    fun phoneCall(phoneNum: String) {
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

    fun goToUrl(url: String) {
        val uriUrl: Uri = Uri.parse(url)
        val launchBrowser = Intent(Intent.ACTION_VIEW, uriUrl)
        itemView.context.startActivity(launchBrowser)
    }
}

