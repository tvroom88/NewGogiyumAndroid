package com.newgogiyumandroid

object ConstantsCollector {
    // Menu list url
    const val MENU_URL = "https://gogiyum.com/api/menu"

    fun RESTAURANTURL(food:String):String {
        return "https://gogiyum.com/api/restaurant?food=$food&city=seattle"
    }

    var preIdx:Int = 0;
}


//https://gogiyum.com/api/restaurant?food=짜장면&city=seattle
