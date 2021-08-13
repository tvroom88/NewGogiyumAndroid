package com.newgogiyumandroid

import android.app.Activity
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import com.newgogiyumandroid.JsonParsingLists.RestaurantList
import com.newgogiyumandroid.RoomDataBase.SavedMenuDataBase
import com.newgogiyumandroid.RoomDataBase.SavedRestaurantDataBase

object ConstantsCollector {
    // Menu list url
//    const val MENU_URL = "https://gogiyum.com/api/menu"
    const val TAG_URL = "https://gogiyum.com/api/tag"

//    https://gogiyum.com/api/menu?tag=%EA%B5%AD%EB%B0%A5

    //if tag is all
    fun TAGMENUURL(tag:String) :String{
        return  "https://gogiyum.com/api/menu?tag=$tag"
    }

//    fun RESTAURANTURL(food:String):String {
//        return "https://gogiyum.com/api/restaurant?food=$food&city=seattle"
//    }
    fun RESTAURANTURL(food: String, city:String): String {
        return "https://gogiyum.com/api/restaurant?food=$food&city=$city"
    }

    var preIdx: Int = 0;
    
    //for setting using Preference
    const val SP_NAME = "my_sp_storage"
    val mySetting = arrayOf("location", "language", "region")

    fun readSharedPreference(key:String, activity: Activity): String {
        val sp = activity.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
        val result = sp?.getString(key, "") ?: ""
        return result
    }


    fun getSavedFoodDB(context: Context): SavedMenuDataBase {
        // change restuarant data language
        val db = Room.databaseBuilder(
            context,
            SavedMenuDataBase::class.java,
            "savedMenu-database"
        ).allowMainThreadQueries() // 그냥 강제로 실행
            .fallbackToDestructiveMigration()
            .build()

        return db
    }


    fun getSavedRestaurantDB(context: Context): SavedRestaurantDataBase {
        // change restuarant data language
        val db = Room.databaseBuilder(
            context,
            SavedRestaurantDataBase::class.java,
            "savedRestaurant-database"
        ).allowMainThreadQueries() // 그냥 강제로 실행
            .fallbackToDestructiveMigration()
            .build()

        return db
    }

}

