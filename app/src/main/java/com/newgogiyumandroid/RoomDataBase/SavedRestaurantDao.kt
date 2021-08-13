package com.newgogiyumandroid.RoomDataBase

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.newgogiyumandroid.JsonParsingLists.RestaurantList

@Dao
interface SavedRestaurantDao {
    @Query("SELECT * FROM RestaurantList")
    fun getAll(): List<RestaurantList>

    @Insert
    fun insert(savedMenu: RestaurantList)

    @Query("DELETE FROM RestaurantList WHERE k_name = :k_name") // 'name'에 해당하는 유저를 삭제해라
    fun deleteMenuByName(k_name: String)

    @Query("DELETE FROM RestaurantList")
    fun deleteAll()

    @Update
    fun update(savedMenu: RestaurantList)

    @Query("SELECT EXISTS (SELECT 1 FROM RestaurantList WHERE k_name = :k_name)")
    fun exists(k_name: String): Boolean
}