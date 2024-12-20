package com.example.weather.data.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Date

class Converters {
    @TypeConverter
    fun fromHourlyForecast(value: List<com.example.weather.data.util_pojo.List>): String {
        val gson = Gson()
        return gson.toJson(value)
    }

    @TypeConverter
    fun toHourlyForecast(value: String): List<com.example.weather.data.util_pojo.List> {
        val listType = object : TypeToken<List<com.example.weather.data.util_pojo.List>>() {}.type
        return Gson().fromJson(value, listType)
    }
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

}