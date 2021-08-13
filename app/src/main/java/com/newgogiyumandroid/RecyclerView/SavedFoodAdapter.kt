package com.newgogiyumandroid.RecyclerView

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.View.inflate
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.newgogiyumandroid.JsonParsingLists.FoodList
import com.newgogiyumandroid.MainActivityFragments.SavedListFragment
import com.newgogiyumandroid.R
import com.newgogiyumandroid.RestaurantListActivity
import com.newgogiyumandroid.RoomDataBase.SavedMenuDataBase
import kotlinx.android.synthetic.main.fragment_saved_list.view.*
import kotlinx.android.synthetic.main.restaurant_list_activity.view.*
import kotlinx.android.synthetic.main.saved_food_item.view.*
import kotlinx.android.synthetic.main.saved_food_item.view.foodImage
import java.util.zip.Inflater

class SavedFoodAdapter : RecyclerView.Adapter<SavedFoodHolder>() {

    var listData = mutableListOf<FoodList>()

    //just for Test

    // provide number for each index
    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedFoodHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.saved_food_item, parent, false)

        // This is for getting View from SavedFoodAdapter
        val root = parent.rootView

        // 싱글톤 패턴을 사용하지 않은 경우
        val db = Room.databaseBuilder(
            parent.context,
            SavedMenuDataBase::class.java,
            "savedMenu-database"
        ).allowMainThreadQueries() // 그냥 강제로 실행
            .fallbackToDestructiveMigration()
            .build()

        return SavedFoodHolder(itemView, db, root)
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onBindViewHolder(holder: SavedFoodHolder, position: Int) {
        val foodList = listData[position]
        holder.setFoodList(foodList)
//        holder.kFoodName = foodList.k_name
        holder.kFoodName = foodList.k_name
        holder.imageURL = foodList.imageURL
        //----------
        holder.adapter = this
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
}

class SavedFoodHolder(itemView: View, val db: SavedMenuDataBase, root: View) : RecyclerView.ViewHolder(itemView){

    var kFoodName: String = ""
    var imageURL: String = ""
    var adapter: SavedFoodAdapter = SavedFoodAdapter()

    var numOfItems: TextView

    init {

        numOfItems = root.findViewById(R.id.items)!!

        itemView.setOnClickListener {
            val intent = Intent(itemView.context, RestaurantListActivity::class.java)
            intent.putExtra("name", itemView.foodImageKName.text)
            intent.putExtra("kFoodName", kFoodName)
            intent.putExtra("imageURL", imageURL)
            itemView.context.startActivity(intent)
        }
    }

    fun setFoodList(foodList: FoodList) {

        itemView.foodImageKName.text = foodList.name
        val itemMenuName = foodList.k_name
//        val itemMenuName = itemView.foodImageKName.text as String

        itemView.savedFoodButton.setOnClickListener {
            db.savedMenuDao().deleteMenuByName(itemMenuName)
            adapter.listData.removeAt(bindingAdapterPosition)
            adapter.notifyDataSetChanged()

            numOfItems.text = "${adapter.listData.size} items"

        }

        Glide.with(itemView)
            .load(foodList.imageURL)
            .skipMemoryCache(false)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(itemView.foodImage)
    }

}
