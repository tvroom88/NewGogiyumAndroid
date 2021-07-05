package com.newgogiyumandroid

import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.newgogiyumandroid.JsonParsingLists.FoodList
import com.newgogiyumandroid.RecyclerView.CustomAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import org.json.JSONArray
import java.io.IOException

var data: MutableList<FoodList> = mutableListOf()

class MainActivity : AppCompatActivity() {

    //current Language
    var curLang: String = "Ko"

    var menu: Menu? = null

    val adapter = CustomAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar) //커스텀한 toolbar를 액션바로 사용
        supportActionBar?.setDisplayShowTitleEnabled(false) //액션바에 표시되는 제목의 표시유무를 설정합니다. false로 해야 custom한 툴바의 이름이 화면에 보이게 됩니다.

        if (checkInternetConnection()) {
            readData()
        } else {
            progressImg.visibility = View.GONE
        }
    }

    fun readData() {
        val client = OkHttpClient.Builder().build()
        val req = Request.Builder().url(ConstantsCollector.MENU_URL).build()
        client.newCall(req).enqueue(object: Callback {
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

    // 인터넷 연결 확인
    fun checkInternetConnection(): Boolean {
        val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo

        if (activeNetwork != null)
            return true

        return false
    }

    //액션버튼 메뉴 액션바에 집어 넣기
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.toolbar_menu, menu)

        return true
    }

    //액션버튼 클릭 했을 때
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.language -> {

                when(curLang) {
                    "Ko" -> {
                        curLang = "En"
                        item.setIcon(R.drawable.en1)
                        item.setTitle("Language : English")
                        Toast.makeText(applicationContext, "Language : English", Toast.LENGTH_SHORT).show()

                    }
                    else -> {
                        curLang = "Ko"
                        item.setIcon(R.drawable.ko1)
                        item.setTitle("Language : Korean")
                        Toast.makeText(applicationContext, "Language : Korean", Toast.LENGTH_SHORT).show()

                    }
                }

                changeName(curLang)
                adapter.listData = data
                adapter.notifyDataSetChanged()
                true
            }

            R.id.bookmark ->{
                Toast.makeText(applicationContext, "click on bookmark", Toast.LENGTH_SHORT).show()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun changeName(language : String){

        when(language) {
            "En" -> {
                for(data1 in data){
                    data1.name = data1.e_name
                }
            }
            "Ko" -> {
                for(data1 in data){
                    data1.name = data1.k_name
                }
            }
        }
    }
}

