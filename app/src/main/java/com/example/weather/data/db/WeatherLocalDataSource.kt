package com.example.weather.data.db

import android.content.Context
import com.example.weather.data.Alarm
import com.example.weather.data.WeatherDisplayable
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

class WeatherLocalDataSource internal constructor(
    private val weatherDAO: WeatherDAO,
    private val alarmDAO:AlarmDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    companion object{
        private var  INSTANCE : WeatherLocalDataSource? = null
        fun getInstance(weatherDAO: WeatherDAO,alarmDAO:AlarmDao):WeatherLocalDataSource{
            if(INSTANCE == null){
                INSTANCE = WeatherLocalDataSource(weatherDAO,alarmDAO)
            }
            return INSTANCE as WeatherLocalDataSource
        }
    }
    fun getAllFavLocations(): Flow<List<WeatherDisplayable>> = weatherDAO.getAllFavLocations()
    fun getChachedLocation(): Flow<WeatherDisplayable?> = weatherDAO.getCachedLocation()
    suspend fun deleteLocation(location:WeatherDisplayable){ weatherDAO.deleteLocation(location) }
    suspend fun addLocation(location: WeatherDisplayable){ weatherDAO.insertLocation(location) }

    fun getAllAlarms(): Flow<List<Alarm>> = alarmDAO.getAllAlarms()
    suspend fun insertAlarm(alarm: Alarm) { alarmDAO.insertAlarm(alarm) }
    suspend fun updateAlarm(alarm: Alarm) { alarmDAO.updateAlarm(alarm) }
    suspend fun deleteAlarm(alarm: Alarm) { alarmDAO.deleteAlarm(alarm) }
}