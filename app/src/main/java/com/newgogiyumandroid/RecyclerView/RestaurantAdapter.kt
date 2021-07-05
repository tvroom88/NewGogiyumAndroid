package com.newgogiyumandroid.RecyclerView


import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.newgogiyumandroid.DetailActivity
import com.newgogiyumandroid.JsonParsingLists.FoodList
import com.newgogiyumandroid.JsonParsingLists.RestaurantList
import com.newgogiyumandroid.R
import com.newgogiyumandroid.RestaurantListActivity
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
    // 여기다가 onClickListener
    init {
        itemView.setOnClickListener(){
            val intent = Intent(itemView.context, DetailActivity::class.java)
            intent.putExtra("name", itemView.resName.text)
            itemView.context.startActivity(intent)
        }
    }
    fun setRestaurantList(restuarantList: RestaurantList){
        itemView.resName.text = restuarantList.name
    }

}


