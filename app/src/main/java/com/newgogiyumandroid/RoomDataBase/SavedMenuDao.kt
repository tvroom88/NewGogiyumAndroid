package com.newgogiyumandroid.RoomDataBase

import androidx.room.*
import com.newgogiyumandroid.JsonParsingLists.FoodList

@Dao
interface SavedMenuDao {
    @Query("SELECT * FROM FoodList")
    fun getAll(): List<FoodList>

    @Insert
    fun insert(savedMenu: FoodList)

    @Query("DELETE FROM FoodList WHERE k_name = :k_name") // 'name'에 해당하는 유저를 삭제해라
    fun deleteMenuByName(k_name: String)

    @Query("DELETE FROM FoodList")
    fun deleteAll()

    @Update
    fun update(savedMenu: FoodList)

    @Query("SELECT EXISTS (SELECT 1 FROM FoodList WHERE k_name = :k_name)")
    fun exists(k_name: String): Boolean

//    @Query("SELECT * FROM user WHERE first_name LIKE :search " +
//            "OR last_name LIKE :search")
//    fun findUserWithName(search: String): List<User>

//    @Query("SELECT EXISTS (SELECT 1 FROM example_table WHERE id = :id)")
//    fun exists(id: Int): Boolean
}