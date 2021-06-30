package com.newgogiyumandroid

import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.newgogiyumandroid.JsonParsingLists.FoodList
import com.newgogiyumandroid.RecyclerView.CustomAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import org.json.JSONArray
import java.io.IOException
import java.net.URL

// SplashActivity 추가도 고려해볼만 하다
//

class MainActivity : AppCompatActivity() {

    var data: MutableList<FoodList> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (checkInternetConnection()) {
            readData();
        } else {
            progressImg.visibility = View.GONE
        }
    }

    fun readData() {
        val client = OkHttpClient.Builder().build()
        val req = Request.Builder().url("https://gogiyum.com/api/menu").build()
        client.newCall(req).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                // failure
            }

            override fun onResponse(call: Call, response: Response) {
                val launch = CoroutineScope(Dispatchers.Main).launch {

                    val body = response.body!!.string()
                    val jsonArray = JSONArray(body)

                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)

                        val imageUrl: String = jsonObject.getString("image")
                        val name = jsonObject.getString("name")
                        val ename = jsonObject.getString("e_name")

                        val foodList = FoodList(imageUrl, name, ename)
                        data.add(foodList)
                    }

                    progressImg.visibility = View.GONE
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

        })
    }

    // 인터넷 연결 확인
    fun checkInternetConnection(): Boolean {
        val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo

        if (activeNetwork != null)
            return true

        return false
    }
}