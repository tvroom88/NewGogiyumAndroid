package com.newgogiyumandroid.JsonParsingLists

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class FoodList (
    var imageURL:String,
    var name:String,
    var k_name:String,
    val e_name:String
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}

