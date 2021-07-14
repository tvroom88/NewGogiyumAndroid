package com.newgogiyumandroid

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.newgogiyumandroid.JsonParsingLists.RestaurantList
import com.newgogiyumandroid.RecyclerView.RestaurantAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.recycler
import kotlinx.android.synthetic.main.restaurant_list_activity.*
import org.json.JSONArray
import java.net.URL


class RestaurantListActivity : AppCompatActivity() {

    var data: MutableList<RestaurantList> = mutableListOf()

    lateinit var foodName:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.restaurant_list_activity)

        foodName = intent.getStringExtra("kFoodName").toString()
        val name = intent.getStringExtra("name").toString()
        tv1.text = name

        val thread = NetworkThread()
        thread.start()
    }

    inner class NetworkThread: Thread() {
        override fun run() {

            val jsonArray = readData()

            for (i in 0 until jsonArray.length()) {

                val jsonObject = jsonArray.getJSONObject(i)
//                val foods = jsonObject.getJSONArray("foods")
                val name = jsonObject.getString("name")
                val address = jsonObject.getString("address")
                val yelpRating = jsonObject.getDouble("y_rating")
                val googleRating = jsonObject.getDouble("g_rating")
                val uberURL = jsonObject.getString("uber")
                val grubURL = jsonObject.getString("grubhub")
                val doorURL = jsonObject.getString("doordash")

                val weekday_text = jsonObject.getString("weekday_text")

                val price = jsonObject.getString("price")
                val menu = jsonObject.getString("menu")
                val phone : String
                if (jsonObject.has("phone")) {
                    phone = jsonObject.getString("phone")
                }else{
                    phone = ""
                }

                val restaurantList = RestaurantList(name, address, yelpRating, googleRating, uberURL, grubURL, doorURL,
                    weekday_text, price, menu, phone)

                data.add(restaurantList)
            }

            runOnUiThread {

                // 2. 어뎁터 생성
                val adapter = RestaurantAdapter()
                // 3. 어뎁터에 데이터 전달
                adapter.listData = data
                // 4. 화면에 있는 리사이클러뷰에 어답터 연결 - recycler 은 recycler_view_example에 있는 recycler view  Id 임
                recycler.adapter = adapter
                // 5. 레이아웃 매니저 연결
                recycler.layoutManager = LinearLayoutManager(applicationContext)
            }
        }
    }

    // 식당 정보를 읽어와 JSONArray 로 반환하는 함수
    fun readData(): JSONArray {
//        val url = URL("https://gogiyum.com/api/restaurant?food=짜장면&city=seattle")
        val url = URL(ConstantsCollector.RESTAURANTURL(foodName))
        val connection = url.openConnection()
        val data = connection.getInputStream().readBytes().toString(charset("UTF-8"))
        return JSONArray(data)
    }


}

