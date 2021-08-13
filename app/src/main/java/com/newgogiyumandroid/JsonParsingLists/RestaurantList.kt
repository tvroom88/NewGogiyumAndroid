package com.newgogiyumandroid.JsonParsingLists

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class RestaurantList(
    var name: String,
    var k_name:String,
    val e_name:String,
    val address: String,
    val yrating: Double,
    val grating: Double,
    val uberURL: String,
    val gruhubURL: String,
    val doordashURL: String,
    val weekday_text:String,
    val price: String,
    val menu: String,
    val phone: String
    ) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}


