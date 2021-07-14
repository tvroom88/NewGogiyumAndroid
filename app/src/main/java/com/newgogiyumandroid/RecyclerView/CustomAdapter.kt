package com.newgogiyumandroid.RecyclerView

import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.newgogiyumandroid.JsonParsingLists.FoodList
import com.newgogiyumandroid.JsonParsingLists.RestaurantList
import com.newgogiyumandroid.R
import com.newgogiyumandroid.RestaurantListActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.recyler_item.view.*
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException


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
        holder.kFoodName = foodList.k_name
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
}


fun saveBitmapToJpeg(bitmap: Bitmap, name:String, itemView: View) {

    //내부저장소 캐시 경로를 받아옵니다.
    val storage:File = itemView.context.getCacheDir();

    //저장할 파일 이름
    val fileName:String = name + ".jpg";

    //storage 에 파일 인스턴스를 생성합니다.
    val tempFile:File = File(storage, fileName);

    try {
        // 자동으로 빈 파일을 생성합니다.
        tempFile.createNewFile();

        // 파일을 쓸 수 있는 스트림을 준비합니다.
        val out:FileOutputStream = FileOutputStream(tempFile);

        // compress 함수를 사용해 스트림에 비트맵을 저장합니다.
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

        // 스트림 사용후 닫아줍니다.
        out.close();

    } catch (e: FileNotFoundException) {
        Log.e("MyTag","FileNotFoundException : " + e.message);
    } catch (e: IOException) {
        Log.e("MyTag","IOException : " + e.message);
    }
}


class Holder(itemView: View) : RecyclerView.ViewHolder(itemView){

    var kFoodName: String = ""
    init {
        itemView.setOnClickListener {
            val intent = Intent(itemView.context, RestaurantListActivity::class.java)
            intent.putExtra("name", itemView.foodImageKName.text)
            intent.putExtra("kFoodName", kFoodName)
            itemView.context.startActivity(intent)
        }
    }

    fun setFoodList(foodList: FoodList) {
        itemView.foodImageKName.text = foodList.name
        itemView.imageButton.setOnClickListener{

        }
        Glide.with(itemView)
            .load(foodList.imageURL)
            .skipMemoryCache(false)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(itemView.imageView1)
    }

}

