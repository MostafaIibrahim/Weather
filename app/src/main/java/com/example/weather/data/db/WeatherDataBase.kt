package com.example.weather.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.weather.data.Alarm
import com.example.weather.data.WeatherDisplayable


@Database(entities = arrayOf(WeatherDisplayable::class , Alarm::class), version = 2)
@TypeConverters(Converters::class)
abstract class WeatherDataBase: RoomDatabase() {
    abstract fun getWeatherDao():WeatherDAO
    abstract fun getAlarmDao(): AlarmDao
    companion object{
        @Volatile
        private var INSTANCE:WeatherDataBase? = null
        fun getInstance(ctx: Context):WeatherDataBase{
            return INSTANCE ?: synchronized(this){
                val instance:WeatherDataBase = Room.databaseBuilder(
                    ctx.applicationContext,WeatherDataBase::class.java,"weatherLoc")
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance }
        }
    }
}