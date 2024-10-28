package com.example.weather.data.remote_network

import com.example.weather.data.CurrentWeather
import com.example.weather.data.ForcastWeather
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET("weather")
    suspend fun getCurretWeather(
        @Query("lat") lat:Double,
        @Query("lon") lon:Double,
        @Query("units") unit:String,
        @Query("lang") language: String ,
        @Query("appid") apiKey:String = "ec4a8f4eccba5da86e09959ca5689d03"
    ):CurrentWeather

    @GET("forecast")
    suspend fun getForcastedWeather(
        @Query("lat") lat:Double,
        @Query("lon") lon:Double,
        @Query("units") unit:String,
        @Query("lang") language: String,
        @Query("appid") apiKey:String = "ec4a8f4eccba5da86e09959ca5689d03"
    ):ForcastWeather
}