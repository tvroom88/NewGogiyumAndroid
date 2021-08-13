package com.newgogiyumandroid.RoomDataBase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.newgogiyumandroid.JsonParsingLists.FoodList
import com.newgogiyumandroid.JsonParsingLists.RestaurantList

@Database(entities = [RestaurantList::class], version = 2)
abstract class SavedRestaurantDataBase : RoomDatabase() {
    abstract fun savedRestaurantDao(): SavedRestaurantDao

    companion object {
        private var instance: SavedRestaurantDataBase? = null

        @Synchronized
        fun getInstance(context: Context): SavedRestaurantDataBase? {
            if (instance == null) {
                synchronized(SavedRestaurantDataBase::class) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        SavedRestaurantDataBase::class.java,
                        "savedRestaurant-database"
                    ).build()
                }
            }
            return instance
        }
    }
}


