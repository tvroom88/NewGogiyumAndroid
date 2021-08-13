package com.newgogiyumandroid.MainActivityFragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.newgogiyumandroid.JsonParsingLists.FoodList
import com.newgogiyumandroid.JsonParsingLists.RestaurantList
import com.newgogiyumandroid.R
import com.newgogiyumandroid.RecyclerView.SavedFoodAdapter
import com.newgogiyumandroid.RecyclerView.SavedRestuarantAdapter
import com.newgogiyumandroid.RoomDataBase.SavedMenuDataBase
import com.newgogiyumandroid.RoomDataBase.SavedRestaurantDataBase
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import kotlinx.android.synthetic.main.restaurant_list_activity.*


class SavedListFragment : Fragment() {

    val savedFoodAdapter = SavedFoodAdapter()
    val savedRestaurantAdapter =  SavedRestuarantAdapter()

    // If this is bigger than two make able to drag
    var countDragging:Int = 0

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        // 싱글톤 패턴을 사용하지 않은 경우
        var db = Room.databaseBuilder(
            requireContext(),
            SavedMenuDataBase::class.java,
            "savedMenu-database"
        ).allowMainThreadQueries() // 그냥 강제로 실행
            .build()

        val root = inflater.inflate(R.layout.fragment_saved_list, container, false)

        val saved_main_panel = root.findViewById<SlidingUpPanelLayout>(R.id.saved_fragment_main_panel)
        val imageView1 = root.findViewById<ImageView>(R.id.imageView1)

        //이미지 사이즈를 핸드폰 사이즈에 맞추기위해 -
        val metrics = resources.displayMetrics
        val width = metrics.widthPixels
        val height = metrics.heightPixels

        // Apply the new height and width for ImageView programmatically
        saved_main_panel.panelHeight = height * 421 / 812
        imageView1.getLayoutParams().width = width * 449 / 375
        imageView1.getLayoutParams().height = height * 447 / 812
        imageView1.setScaleType(ImageView.ScaleType.FIT_XY);

        //  -------------  food RecyclerView 부분 -------------
        val data:MutableList<FoodList> = db.savedMenuDao().getAll() as MutableList<FoodList>
        val savedFoodRecycler = root.findViewById<RecyclerView>(R.id.savedFoodRecycler)
        val numOfItems = root.findViewById<TextView>(R.id.items)
        numOfItems.text = "${data.size} items"

        savedFoodAdapter.listData = data
        // 4. 화면에 있는 리사이클러뷰에 어답터 연결 - recycler 은 recycler_view_example에 있는  recycler view  Id 임
        savedFoodRecycler.adapter = savedFoodAdapter
        // 5. 레이아웃 매니저 연결
        savedFoodRecycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        // set random Image
        if(!data.isEmpty()){
            val randomNum = (0 until data.size).random()
            val imageURL = data[randomNum].imageURL
            Glide.with(this)
                .load(imageURL)
                .skipMemoryCache(false)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView1)
        }

        // ------------- 밑에 saved Restaurant recycler View 부분 ----------------
        val savedRestaurantRecycler = root.findViewById<RecyclerView>(R.id.savedRestaurantRecycler)

        val numOfplaces = root.findViewById<TextView>(R.id.places)

        // saved restaurant를 RoomDataBase로부터 불러옴
        val restaurantData: MutableList<RestaurantList>
        Room.databaseBuilder(
            requireContext(),
            SavedRestaurantDataBase::class.java,
            "savedRestaurant-database"
        ).allowMainThreadQueries() // 그냥 강제로 실행
            .build().let {
                restaurantData = it.savedRestaurantDao().getAll() as MutableList<RestaurantList>
                numOfplaces.text = "${restaurantData.size} items"
            }

        savedRestaurantAdapter.listData = restaurantData
        savedRestaurantRecycler.adapter = savedRestaurantAdapter
        savedRestaurantRecycler.layoutManager = LinearLayoutManager(context)


        // ----- saved_main_panel part -----
        saved_main_panel.addPanelSlideListener(object : SlidingUpPanelLayout.PanelSlideListener {
            override fun onPanelSlide(panel: View?, slideOffset: Float) {  }
            override fun onPanelStateChanged(
                panel: View?,
                previousState: SlidingUpPanelLayout.PanelState?,
                newState: SlidingUpPanelLayout.PanelState?
            ) {
                if (newState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    savedRestaurantRecycler.suppressLayout(true)
                    saved_main_panel.setTouchEnabled(true)
                } else if (newState == SlidingUpPanelLayout.PanelState.EXPANDED) {
                    savedRestaurantRecycler.suppressLayout(false)


                    // item이 1개이상은 되게
                    if (savedRestaurantAdapter.itemCount > 1) {
                        saved_main_panel.setTouchEnabled(false)
                        countDragging = 0
                    }
                }
            }
        })


        savedRestaurantRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (savedRestaurantAdapter.itemCount > 1) {
                    if (!savedRestaurantRecycler.canScrollVertically(-1)) {
                        countDragging++
                        if (countDragging >= 2) saved_main_panel.setTouchEnabled(true)
                    } else {
                        countDragging = 0
                        saved_main_panel.setTouchEnabled(false)
                    }
                }
            }
        })
        val savedTopLayer = root.findViewById<LinearLayout>(R.id.savedTopLayer)
        savedTopLayer.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                saved_main_panel.setTouchEnabled(true)
                return false
            }
        })

        return root
    }

}