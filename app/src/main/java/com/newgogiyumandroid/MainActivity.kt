package com.newgogiyumandroid

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.gms.common.wrappers.InstantApps
import com.google.firebase.analytics.FirebaseAnalytics
import com.newgogiyumandroid.JsonParsingLists.FoodList
import com.newgogiyumandroid.MainActivityFragments.MenuListFragment
import com.newgogiyumandroid.MainActivityFragments.ProfileFragment
import com.newgogiyumandroid.MainActivityFragments.SavedListFragment
import com.newgogiyumandroid.RecyclerView.CustomAdapter
import com.veinhorn.tagview.TagView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_menu_list.*
import okhttp3.*


class MainActivity : AppCompatActivity() {

    val STATUS_INSTALLED = "installed"
    val STATUS_INSTANT = "instant"
    val ANALYTICS_USER_PROP = "app_type"

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        // Determine the current app context, either installed or instant, then
        // set the corresponding user property for Google Analytics.
        if (InstantApps.isInstantApp(this)) {
            firebaseAnalytics.setUserProperty(ANALYTICS_USER_PROP, STATUS_INSTANT)
            Log.d("A", "A")
        } else {
            firebaseAnalytics.setUserProperty(ANALYTICS_USER_PROP, STATUS_INSTALLED)
            Log.d("B", "B")
        }

        //커스텀한 toolbar를 액션바로 사용
//        setSupportActionBar(toolbar)

        //액션바에 표시되는 제목의 표시유무를 설정합니다. false로 해야 custom한 툴바의 이름이 화면에 보이게 됩니다.
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // main fragment 부분
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentsFrameLayout, MenuListFragment())
            .commit()

        // bottom navigation
        bottomNavigation()
    }

    fun bottomNavigation(){

//        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        //Bottom Navigation onClickListener
        bottom_navigation?.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.page_1 -> {
                    // Respond to navigation item 1 click
                    val fragmentA = MenuListFragment()
                    changeFragment(fragmentA)
                }
                R.id.page_2 -> {
                    // Respond to navigation item 2 click
                    val fragmentB = SavedListFragment()
                    changeFragment(fragmentB)
                }
                R.id.page_3 -> {
                    // Respond to navigation item 3 click
                    val fragmentC = ProfileFragment()
                    changeFragment(fragmentC)
                }
            }

            return@setOnItemSelectedListener true;
        }
    }

    fun changeFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.fragmentsFrameLayout, fragment).commit()
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

