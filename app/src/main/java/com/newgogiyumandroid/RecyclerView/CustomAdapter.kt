package com.newgogiyumandroid.RecyclerView

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.newgogiyumandroid.JsonParsingLists.FoodList
import com.newgogiyumandroid.R
import com.newgogiyumandroid.RestaurantListActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.recyler_item.view.*


class CustomAdapter : RecyclerView.Adapter<Holder>() {

    var listData = mutableListOf<FoodList>()

    init {
        setHasStableIds(true)
    }


    //current language
    var curLang: String = null ?: ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recyler_item, parent, false)
        return Holder(itemView)
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val foodList = listData.get(position)
        holder.setFoodList(foodList)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
}

class Holder(itemView: View) : RecyclerView.ViewHolder(itemView){

    init {
        itemView.setOnClickListener {
            val intent = Intent(itemView.context, RestaurantListActivity::class.java)
//            intent.putExtra("url", itemView.foodImageKName.text)
            itemView.context.startActivity(intent)
        }
    }

    fun setFoodList(foodList: FoodList) {
        itemView.foodImageKName.text = foodList.name
        Glide.with(itemView)
            .load(foodList.imageURL)
            .into(itemView.imageView1)
    }

}


