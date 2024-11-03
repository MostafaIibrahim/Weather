package com.example.weather.data.repository

import androidx.lifecycle.LiveData
import com.example.weather.data.Alarm
import com.example.weather.data.CurrentWeather
import com.example.weather.data.ForcastWeather
import com.example.weather.data.WeatherDisplayable
import kotlinx.coroutines.flow.Flow

interface IWeatherRepository {
    suspend fun getDisplayableData(lat: Double, lon: Double):Flow<WeatherDisplayable>
    suspend fun getFrocastRemoteData(lat: Double, lon: Double): Flow<ForcastWeather>
    suspend fun getWeatherRemoteData(lat: Double, lon: Double): Flow<CurrentWeather>

    fun getAllFavLocations():Flow<List<WeatherDisplayable>>
    fun getChachedLocation():Flow<WeatherDisplayable>
    suspend fun addToFav(FavLocation:WeatherDisplayable)
    suspend fun deleteFromFav(FavLocation: WeatherDisplayable)

    fun getAllAlarms(): Flow<List<Alarm>>
    suspend fun insertAlarm(alarm: Alarm)
    suspend fun deleteAlarm(alarm: Alarm)

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