package com.example.weather.data.db

import android.content.Context
import com.example.weather.data.WeatherDisplayable
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

class WeatherLocalDataSource internal constructor(
    private val weatherDAO: WeatherDAO,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    companion object{
        private var  INSTANCE : WeatherLocalDataSource? = null
        fun getInstance(weatherDAO: WeatherDAO):WeatherLocalDataSource{
            if(INSTANCE == null){
                INSTANCE = WeatherLocalDataSource(weatherDAO)
            }
            return INSTANCE as WeatherLocalDataSource
        }
    }
    fun getAllFavLocations(): Flow<List<WeatherDisplayable>> = weatherDAO.getAllFavLocations()
    fun getChachedLocation(): Flow<WeatherDisplayable?> = weatherDAO.getCachedLocation()
    suspend fun deleteLocation(location:WeatherDisplayable){ weatherDAO.deleteLocation(location) }
    suspend fun addLocation(location: WeatherDisplayable){ weatherDAO.insertLocation(location) }
}