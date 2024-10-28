package com.example.weather.data.repository

import com.example.weather.data.CurrentWeather
import com.example.weather.data.ForcastWeather
import kotlinx.coroutines.flow.Flow

interface IWeatherRepository {
    //Here I want to cache data and be able to update or modify
    suspend fun getFrocastRemoteData(lat: Double, lon: Double): Flow<ForcastWeather>

    suspend fun getWeatherRemoteData(lat: Double, lon: Double): Flow<CurrentWeather>
    fun saveTempUnit(unit: String)
    fun getTempUnit(): String
    fun saveLocType(type: String)
    fun getLocType(): String
    fun saveLang(language: String)
    fun getLang(): String
    fun saveWindUnit(unit:String)
    fun getWindUnit():String

    fun saveLocationCoord(lon:Double,lat:Double)
    fun getLocationCoord_long():Double
    fun getLocationCoord_lat():Double
}