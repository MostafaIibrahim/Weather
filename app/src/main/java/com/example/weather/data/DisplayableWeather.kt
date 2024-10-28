package com.example.weather.data

import com.example.weather.data.util_pojo.List


data class DisplayableWeather (
    var countryName       :String,
    var currentDate       :String, var currentTime       :String,
    var currentImg        :String,
    var currentDescriptin :String,
    var currentTemp       :String, var currentMaxTemp:String , var currentMinTemp:String,
    var feelLike          :String,
    var sunRise :String , var sunSet :String,
    var windSpeed:String , var pressure:String, var humidityPrecent :String, var cloud:String,
    var forcatedList:ArrayList<List> = arrayListOf()
)