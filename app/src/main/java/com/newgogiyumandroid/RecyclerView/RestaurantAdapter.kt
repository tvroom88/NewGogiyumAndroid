package com.newgogiyumandroid.RecyclerView


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.newgogiyumandroid.JsonParsingLists.FoodList
import com.newgogiyumandroid.JsonParsingLists.RestaurantList
import com.newgogiyumandroid.R
import kotlinx.android.synthetic.main.restaurant_item.view.*


class RestaurantAdapter: RecyclerView.Adapter<RestaurantHolder>() {

    var listData = mutableListOf<RestaurantList>()

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
}

class RestaurantHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
    // 여기다가 onClickListener 넣
    init {

    }
    fun setRestaurantList(restuarantList: RestaurantList){

    }
}


