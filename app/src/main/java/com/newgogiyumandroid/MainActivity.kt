package com.newgogiyumandroid

import android.content.Intent
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.newgogiyumandroid.JsonParsingLists.FoodList
import com.newgogiyumandroid.RecyclerView.CustomAdapter
import com.veinhorn.tagview.TagView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import org.json.JSONArray
import java.io.IOException


var data: MutableList<FoodList> = mutableListOf()

// List for checking tagView is working well
val tagList = listOf("All Menu", "Meat", "Sea", "food", "Sushi", "Fish",
    "Soup", "Casual", "Noodle" , "Midnight", "Rice", "Soup", "Chinese", "Health",
    "Snack", "Spicy", "Hangover", "Raining")


class MainActivity : AppCompatActivity() {

    val adapter = CustomAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //커스텀한 toolbar를 액션바로 사용
        setSupportActionBar(toolbar)

        //액션바에 표시되는 제목의 표시유무를 설정합니다. false로 해야 custom한 툴바의 이름이 화면에 보이게 됩니다.
        supportActionBar?.setDisplayShowTitleEnabled(false)


        if (checkInternetConnection()) {
            readData()
        } else {
            progressImg.visibility = View.GONE
        }

        // adding tag
        tagView()

        //Bottom Navigation onClickListener
        bottomNavigation()
    }


    //read datas from webpage
    fun readData() {
        val client = OkHttpClient.Builder().build()
        val req = Request.Builder().url(ConstantsCollector.MENU_URL).build()
        client.newCall(req).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // failure
            }

            override fun onResponse(call: Call, response: Response) {
                val launch = CoroutineScope(Dispatchers.Main).launch {

                    val body = response.body?.string()
                    val jsonArray = JSONArray(body)

                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)

                        val imageUrl: String = jsonObject.getString("image")
                        val name = jsonObject.getString("name")
                        val kname = jsonObject.getString("name")
                        val ename = jsonObject.getString("e_name")

                        val foodList = FoodList(imageUrl, name, kname, ename)
                        data.add(foodList)
                    }

                    progressImg.visibility = View.GONE
                    // 2. 어뎁터 생성
                    // 3. 어뎁터에 데이터 전달
                    adapter.listData = data

                    // 4. 화면에 있는 리사이클러뷰에 어답터 연결 - recycler 은 recycler_view_example에 있는 recycler view  Id 임
                    recycler.adapter = adapter
                    // 5. 레이아웃 매니저 연결
                    recycler.layoutManager = LinearLayoutManager(applicationContext)


                }
            }
        })
    }

    // check Internet connection
    fun checkInternetConnection(): Boolean {
        val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo

        if (activeNetwork != null)
            return true

        return false
    }

    //tagview part
    fun tagView(){
        val tagViewList = mutableListOf<TagView>()
        var num:Int = -1
        for(i in 0 until tagList.size){
            val tagView = TagView(this, null)
            tagView.text = tagList.get(i)
            tagView.tagColor = Color.rgb(211, 211, 211)
            tagView.tagTextColor = Color.rgb(105,105,105)
            tagView.setTagBorderRadius(10)

            val tagLayoutParams: LinearLayout.LayoutParams =
                LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            tagLayoutParams.setMargins(20, 0, 0, 0)
            tagView.setLayoutParams(tagLayoutParams);

            tagViewList.add(tagView)
            tagLinearLayout.addView(tagView)

        }

        // set onClickListener in all of View
        for(i in 0 until tagList.size){
            var tagView = tagViewList.get(i)
            tagView.setOnClickListener{
                if(num != -1){
                    tagViewList.get(num).tagColor = Color.rgb(211, 211, 211)
                    tagViewList.get(num).tagTextColor = Color.rgb(105,105,105)
                }
                tagView.tagColor = Color.rgb(255,20,147)
                tagView.tagTextColor = Color.WHITE
                num = i
            }
        }
    }


    fun bottomNavigation(){
        //Bottom Navigation onClickListener
        bottom_navigation?.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.page_1 -> {
                    // Respond to navigation item 1 click
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
                R.id.page_2 -> {
                    // Respond to navigation item 2 click
                }
                R.id.page_3 -> {
                    // Respond to navigation item 3 click
                }
            }
            return@setOnItemSelectedListener true
        }
    }

// 나중에 language 바꿀부분
//  액션버튼 클릭 했을 때
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return when (item.itemId) {
//            R.id.language -> {
//                when(curLang) {
//                    "Ko" -> {
//                        curLang = "En"
//                        item.setIcon(R.drawable.en1)
//                        item.setTitle("Language : English")
//                        Toast.makeText(applicationContext, "Language : English", Toast.LENGTH_SHORT).show()
//                    }
//                    else -> {
//                        curLang = "Ko"
//                        item.setIcon(R.drawable.ko1)
//                        item.setTitle("Language : Korean")
//                        Toast.makeText(applicationContext, "Language : Korean", Toast.LENGTH_SHORT).show()
//
//                    }
//                }
//                changeName(curLang)
//                adapter.listData = data
//                adapter.notifyDataSetChanged()
//                true
//            }
//            R.id.bookmark ->{
//                Toast.makeText(applicationContext, "click on bookmark", Toast.LENGTH_SHORT).show()
//                return true
//            }
//            else -> super.onOptionsItemSelected(item)
//        }
//    }
//    fun changeName(language : String){
//        when(language) {
//            "En" -> {
//                for(data1 in data){
//                    data1.name = data1.e_name
//                }
//            }
//            "Ko" -> {
//                for(data1 in data){
//                    data1.name = data1.k_name
//                }
//            }
//        }
//    }
}

