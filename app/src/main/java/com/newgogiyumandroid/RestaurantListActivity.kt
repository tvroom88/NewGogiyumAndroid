package com.newgogiyumandroid

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.doOnPreDraw
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.newgogiyumandroid.ConstantsCollector.mySetting
import com.newgogiyumandroid.ConstantsCollector.readSharedPreference
import com.newgogiyumandroid.JsonParsingLists.RestaurantList
import com.newgogiyumandroid.RecyclerView.RestaurantAdapter
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState
import kotlinx.android.synthetic.main.restaurant_list_activity.*
import kotlinx.android.synthetic.main.saved_restaurant_item.*
import org.json.JSONArray
import java.math.RoundingMode
import java.net.URL


class RestaurantListActivity : AppCompatActivity() {

    // 2. 어뎁터 생성
    val adapter = RestaurantAdapter()

    var data: MutableList<RestaurantList> = mutableListOf()

    // If this is bigger than two make able to drag
    var countDragging:Int = 0

    lateinit var foodName:String

    //sliding layout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.restaurant_list_activity)

        // 서버에서 data 받아올때는 한글로 받아와야해서
        foodName = intent.getStringExtra("kFoodName").toString()

        // 영어랑 한글 바꿀수 있게 만들어줌
        val name = intent.getStringExtra("name").toString()
        foodTextView.text = name

        //이미지 사이즈를 핸드폰 사이즈에 맞추기위해 -
        val metrics = resources.displayMetrics
        val width = metrics.widthPixels
        val height = metrics.heightPixels

        // Apply the new height and width for ImageView programmatically
        main_panel.panelHeight = height * 421 / 812
        imageView1.getLayoutParams().width = width * 449 / 375
        imageView1.getLayoutParams().height = height * 447 / 812
        imageView1.setScaleType(ImageView.ScaleType.FIT_XY);

        // 이미지 URL과 이미지를 연결
        val imageURL = intent.getStringExtra("imageURL").toString()
        Glide.with(this)
            .load(imageURL)
            .skipMemoryCache(false)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(imageView1)

        backButton.setOnClickListener {
            super.onBackPressed()
        }


        // read data
        val thread = NetworkThread()
        thread.start()

        // Sliding up Listener.
        main_panel.addPanelSlideListener(object : SlidingUpPanelLayout.PanelSlideListener {
            override fun onPanelSlide(panel: View?, slideOffset: Float) {
            }

            override fun onPanelStateChanged(
                panel: View?,
                previousState: PanelState?,
                newState: PanelState
            ) {
                if (newState == PanelState.COLLAPSED) {
                    recycler.suppressLayout(true)
                    main_panel.setTouchEnabled(true)
                } else if (newState == PanelState.EXPANDED) {
                    recycler.suppressLayout(false)

                    // item이 1개이상은 되게
                    if (adapter.itemCount > 1) {
                        main_panel.setTouchEnabled(false)
                        countDragging = 0
                    }
                }
            }
        })


        recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (adapter.itemCount > 1) {
                    if (!recycler.canScrollVertically(-1)) {
                        countDragging++
                        if (countDragging >= 2) main_panel.setTouchEnabled(true)
                    } else {
                        countDragging = 0
                        main_panel.setTouchEnabled(false)
                    }
                }
            }
        })

        topLayer.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                main_panel.setTouchEnabled(true)
//                if (main_panel.panelState != PanelState.COLLAPSED) {
//                    main_panel.panelState = PanelState.COLLAPSED
//                }
                return false
            }
        })

    }

    inner class NetworkThread: Thread() {

        var averageCost = 0
        var costDivision = 0
        var averageOfRate = 0.0
        var ratingDivision = 0


        override fun run() {
            val jsonArray = readData()
            for (i in 0 until jsonArray.length()) {

                val jsonObject = jsonArray.getJSONObject(i)
//                val foods = jsonObject.getJSONArray("foods")

                val name: String
                val curLang = readSharedPreference(mySetting[1], this@RestaurantListActivity)
                if(curLang == "" || curLang == "English"){
                    name = jsonObject.getString("ename")
                }else{
                    name = jsonObject.getString("name")
                }
                val k_name = jsonObject.getString("name")
                val e_name = jsonObject.getString("ename")
//                val name = jsonObject.getString("name")
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
                val restaurantList = RestaurantList(name, k_name, e_name, address, yelpRating, googleRating, uberURL, grubURL, doorURL,
                    weekday_text, price, menu, phone)

                data.add(restaurantList)

                //calculate average cost and rate
                if(price != ""){
                    averageCost += price.length
                    costDivision++
                }

                if(yelpRating != 0.0){
                    ratingDivision++
                    averageOfRate += yelpRating
                }

                if(googleRating != 0.0){
                    ratingDivision++
                    averageOfRate += googleRating
                }
            }


            runOnUiThread {

//                // 2. 어뎁터 생성
//                 val adapter = RestaurantAdapter()
                // 3. 어뎁터에 데이터 전달
                adapter.listData = data
                // 4. 화면에 있는 리사이클러뷰에 어답터 연결 - recycler 은 recycler_view_example에 있는 recycler view  Id 임
                recycler.adapter = adapter
                // 5. 레이아웃 매니저 연결
                recycler.layoutManager = LinearLayoutManager(applicationContext)

                recycler.suppressLayout(true)

                //Average Cost of all restaurant
                if(costDivision != 0) cost.text = "$".repeat(averageCost/costDivision)
                else cost.text = "No Data"
                // number of restaurant
                numOfRest.text = jsonArray.length().toString()

                if(ratingDivision != 0){
                    val roundedRate = (averageOfRate/ratingDivision).toBigDecimal().setScale(1, RoundingMode.UP).toDouble()
                    if(ratingDivision != 0) averageRate.text = roundedRate.toString()
                }
                else averageRate.text= "0"
            }
        }
    }

    // 식당 정보를 읽어와 JSONArray 로 반환하는 함수
    fun readData(): JSONArray {
        val area = if(readSharedPreference(mySetting[2], this) != "") {
            readSharedPreference(mySetting[2], this).replace(" ","").lowercase()
        } else{
            "seattle"
        }
        val url = URL(ConstantsCollector.RESTAURANTURL(foodName,area))
        val connection = url.openConnection()
        val data = connection.getInputStream().readBytes().toString(charset("UTF-8"))
        return JSONArray(data)
    }
}
