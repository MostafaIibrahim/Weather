package com.example.weather.data

import com.example.weather.data.util_pojo.City
import com.google.gson.annotations.SerializedName
import com.example.weather.data.util_pojo.List

data class ForcastWeather (
    @SerializedName("cod"     ) var cod     : String?         = null,
    @SerializedName("message" ) var message : Int?            = null,
    @SerializedName("cnt"     ) var cnt     : Int?            = null,
    @SerializedName("list"    ) var list    : ArrayList<List> = arrayListOf(),
    @SerializedName("city"    ) var city    : City?           = City()
)
