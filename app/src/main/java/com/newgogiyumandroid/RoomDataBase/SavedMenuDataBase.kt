package com.newgogiyumandroid.RoomDataBase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.newgogiyumandroid.JsonParsingLists.FoodList

@Database(entities = [FoodList::class], version = 1)
abstract class SavedMenuDataBase: RoomDatabase() {
    abstract fun savedMenuDao(): SavedMenuDao

    companion object {
        private var instance: SavedMenuDataBase? = null

        @Synchronized
        fun getInstance(context: Context): SavedMenuDataBase? {
            if (instance == null) {
                synchronized(SavedMenuDataBase::class) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        SavedMenuDataBase::class.java,
                        "savedMenu-database"
                    ).build()
                }
            }
            return instance
        }
    }
}