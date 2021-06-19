package com.newgogiyumandroid


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.newgogiyumandroid.JsonParsingLists.FoodList
import com.newgogiyumandroid.RecyclerView.CustomAdapter
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import java.net.URL

class MainActivity : AppCompatActivity() {

    var data: MutableList<FoodList> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val thread = NetworkThread()
        thread.start()

    }

    inner class NetworkThread: Thread() {
        override fun run() {

            val jsonArray = readData()

            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)

                val imageUrl:String = jsonObject.getString("image")
                val name = jsonObject.getString("name")
                val ename = jsonObject.getString("e_name")

                val foodList = FoodList(imageUrl, name, ename)
                data.add(foodList)
            }

            runOnUiThread {

                // 2. 어뎁터 생성
                val adapter = CustomAdapter()
                // 3. 어뎁터에 데이터 전달
                adapter.listData = data
                // 4. 화면에 있는 리사이클러뷰에 어답터 연결 - recycler 은 recycler_view_example에 있는 recycler view  Id 임
                recycler.adapter = adapter
                // 5. 레이아웃 매니저 연결
                recycler.layoutManager = LinearLayoutManager(applicationContext)
            }
        }
    }

    // 음식 정보를 읽어와 JSONArray 로 반환하는 함수
    fun readData(): JSONArray {
        val url = URL("https://gogiyum.com/api/menu")
        val connection = url.openConnection()
        val data = connection.getInputStream().readBytes().toString(charset("UTF-8"))
        return JSONArray(data)
    }

}