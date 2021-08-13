package com.newgogiyumandroid.MainActivityFragments

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.newgogiyumandroid.ConstantsCollector
import com.newgogiyumandroid.ConstantsCollector.readSharedPreference
import com.newgogiyumandroid.JsonParsingLists.FoodList
import com.newgogiyumandroid.JsonParsingLists.TagList
import com.newgogiyumandroid.R
import com.newgogiyumandroid.RecyclerView.CustomAdapter
import com.veinhorn.tagview.TagView
import kotlinx.android.synthetic.main.fragment_menu_list.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import org.json.JSONArray
import java.io.IOException

class MenuListFragment : Fragment() {

    private var mContext: Context? = null

    val data: MutableList<FoodList> = mutableListOf()
    val adapter = CustomAdapter()
    var tagList:MutableList<TagList> = mutableListOf()
    var tagViewList = mutableListOf<TagView>()

    //check whether first click or not
    var num: Int = -1
    //current tag(default = "All")
    var curTag = "모든 메뉴"

    lateinit var curLang:String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //add Tag Linear Layout part
        val root = inflater.inflate(R.layout.fragment_menu_list, container, false)
        val tagLinearLayout = root.findViewById<LinearLayout>(R.id.tagLinearLayout)

        // check current language
        curLang = readSharedPreference(ConstantsCollector.mySetting[1], requireActivity())


        // Inflate the layout for this fragment
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter.listData = data
        recycler.adapter = adapter
        recycler.layoutManager = LinearLayoutManager(context)

        //call Tag Views
        if(context!=null){
            readTag()
        }
        readTag()

        //Read Datas
        if (checkInternetConnection()) {
            readData(curTag)
            progressImg.visibility = View.GONE
        } else {
            progressImg.visibility = View.GONE
        }
    }

    // Load Tag List Function
    fun readTag(){
        val client = OkHttpClient.Builder().build()
        val req = Request.Builder().url(ConstantsCollector.TAG_URL).build()
        // failure
        client.newCall(req).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
            }
            override fun onResponse(call: Call, response: Response) {
                CoroutineScope(Dispatchers.Main).launch {

                    val body = response.body?.string()
                    val jsonArray = JSONArray(body)

                    //name, k_name, e_name
                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)

                        // -- It will be changed depends on current language --
                        val name: String
                        if(curLang == "" || curLang == "English"){
                            name = jsonObject.getString("e_name")
                        }else{
                            name = jsonObject.getString("name")
                        }

                        val kname = jsonObject.getString("name")
                        val ename = jsonObject.getString("e_name")
                        val tag = TagList(name, kname, ename)
                        tagList.add(tag)
                    }

                    activity?.runOnUiThread({
                        tagView(tagLinearLayout)
                    })
                }
            }
        })
    }

    // tagView helper function
    // 글꼴 : Nunito Sans
    fun tagView(tagLinearLayout : LinearLayout) {

        for (i in 0 until tagList.size) {
            val tagView = TagView(context, null)
            tagView.text = tagList.get(i).name
            tagView.setTypeface(ResourcesCompat.getFont(requireContext(), R.font.nunito_sans))

            if (i == 0) {
                tagView.tagColor = Color.rgb(57, 57, 57)
                tagView.tagTextColor = Color.rgb(255, 255, 255)
            } else {
                tagView.tagColor = Color.rgb(255, 255, 255)
                tagView.tagTextColor = Color.rgb(57, 57, 57)
            }

            tagView.setTextSize(TypedValue.COMPLEX_UNIT_PX, convertPxToDp(16f))
            tagView.setTagBorderRadius(10)

            val tagLayoutParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            convertPxToDp(8f).toInt().let {
                tagView.tagLeftPadding = it * 2
                tagView.tagRightPadding = it * 2
                tagView.tagTopPadding = it
                tagView.tagBottomPadding = it
                tagLayoutParams.setMargins(0, 0, it, 0)
                tagView.setLayoutParams(tagLayoutParams)
            }
            tagViewList.add(tagView)
            tagLinearLayout.addView(tagView)
        }

        addTagListener()
    }


    fun addTagListener(){
        // set onClickListener in all of View
        for (i in 0 until tagList.size) {
            val tagView = tagViewList.get(i)
            tagView.setOnClickListener {
                if (num == -1) {
                    tagViewList.get(0).tagColor = Color.rgb(255, 255, 255)
                    tagViewList.get(0).tagTextColor = Color.rgb(57, 57, 57)
                } else{
                    tagViewList.get(num).tagColor = Color.rgb(255, 255, 255)
                    tagViewList.get(num).tagTextColor = Color.rgb(57, 57, 57)
                }
                tagView.tagColor = Color.rgb(57, 57, 57)
                tagView.tagTextColor = Color.rgb(255, 255, 255)
                num = i

                curTag = tagList.get(i).k_name
                readData(curTag)
            }

        }
    }

    // Convert Px To Dp function
    fun convertPxToDp(dp : Float): Float{
        val dip = dp
        val resources: Resources = resources
        val px = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dip,
            resources.displayMetrics
        )
        return px
    }

    //read datas from webpage
    fun readData(tag: String) {
        val client = OkHttpClient.Builder().build()
        val req = Request.Builder().url(ConstantsCollector.TAGMENUURL(tag)).build()

        val data: MutableList<FoodList> = mutableListOf()

        client.newCall(req).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // failure
            }
            override fun onResponse(call: Call, response: Response) {
                CoroutineScope(Dispatchers.Main).launch {

                    val body = response.body?.string()
                    val jsonArray = JSONArray(body)

                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val imageUrl: String = jsonObject.getString("image")

                        // -- It will be changed depends on current language --
                        val name: String
                        if(curLang == "" || curLang == "English"){
                            name = jsonObject.getString("e_name")

                        }else{
                            name = jsonObject.getString("name")
                        }

                        val kname = jsonObject.getString("name")
                        val ename = jsonObject.getString("e_name")

                        val foodList = FoodList(imageUrl, name, kname, ename)
                        data.add(foodList)
                    }

                    // 3. 어뎁터에 데이터 전달
                    adapter.listData = data
                    adapter.notifyDataSetChanged()
                }
            }
        })
    }

    // check Internet connection
    fun checkInternetConnection(): Boolean {
        val cm = getActivity()?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        if (activeNetwork != null)
            return true
        return false
    }

}
