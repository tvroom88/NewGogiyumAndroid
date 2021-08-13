package com.newgogiyumandroid.RecyclerView

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.StatsLog.logEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.analytics.FirebaseAnalytics
import com.newgogiyumandroid.JsonParsingLists.FoodList
import com.newgogiyumandroid.R
import com.newgogiyumandroid.RestaurantListActivity
import com.newgogiyumandroid.RoomDataBase.SavedMenuDataBase
import kotlinx.android.synthetic.main.recyler_item.view.*
import kotlin.coroutines.coroutineContext


class CustomAdapter : RecyclerView.Adapter<Holder>() {

    var listData = mutableListOf<FoodList>()

    // provide number for each index
    init {
        setHasStableIds(true)
    }

    //current language
    var curLang: String = null ?: ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recyler_item, parent, false)

        // 싱글톤 패턴을 사용하지 않은 경우
        val db = Room.databaseBuilder(
            parent.context,
            SavedMenuDataBase::class.java,
            "savedMenu-database"
        ).allowMainThreadQueries() // 그냥 강제로 실행
            .fallbackToDestructiveMigration()
            .build()

        return Holder(itemView, db)
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val foodList = listData[position]
        holder.setFoodList(foodList)
        holder.kFoodName = foodList.k_name
        holder.imageURL = foodList.imageURL
        //----------
        holder.adapter = this
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

}

class Holder(itemView: View, val db: SavedMenuDataBase) : RecyclerView.ViewHolder(itemView){

    var kFoodName: String = ""
    var imageURL: String = ""
    var adapter: CustomAdapter = CustomAdapter()

    init {
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
        //imageButton
//        val itemMenuName = itemView.foodImageKName.text as String
        val itemMenuName = foodList.k_name
        var alreadyExist = db.savedMenuDao().exists(itemMenuName)

        if(alreadyExist){
            itemView.imageButton.setImageResource(R.drawable.saved_black)
        }

        itemView.imageButton.setOnClickListener{
            alreadyExist = db.savedMenuDao().exists(itemMenuName)
            if(alreadyExist){
                db.savedMenuDao().deleteMenuByName(itemMenuName)
                itemView.imageButton.setImageResource(R.drawable.saved)
            }else{
                db.savedMenuDao().insert(foodList)
                itemView.imageButton.setImageResource(R.drawable.saved_black)
            }

//            if(adapter.checkWhetherSaveActivity){
//                adapter.listData.removeAt(bindingAdapterPosition)
//                adapter.notifyDataSetChanged()
//            }

            // Google analytic 내용 보내기
//            val mFirebaseAnalytics: FirebaseAnalytics =  FirebaseAnalytics.getInstance(itemView.context);
//            val bundle = Bundle();
//            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "1");
//            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Jaehong");
//            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        }

        Glide.with(itemView)
            .load(foodList.imageURL)
            .skipMemoryCache(false)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(itemView.imageView1)
    }

}

