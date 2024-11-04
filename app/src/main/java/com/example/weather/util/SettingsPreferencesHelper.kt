package com.example.weather.util

import android.content.Context
import android.content.SharedPreferences

open class SettingsPreferencesHelper(context:Context) : ISettingsPreferencesHelper {
    lateinit var sharedPreferences:SharedPreferences
    companion object {
        const val PREFERENCES_NAME = "weather_preferences"
        const val LANGUAGE_KEY = "language"
        const val TEMP_UNIT_KEY = "temperature_unit"
        const val LOCATION_TYPE = "location"
        const val LONGITUDE = "lon"
        const val LATITUDE = "lat"
        const val WIND_SPEED_KEY = "wind_speed_unit"
    }
    init {
        sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME,Context.MODE_PRIVATE)
    }

     var latitude: Double
        get() = sharedPreferences.getFloat(LATITUDE, 0f).toDouble()
        set(value) = sharedPreferences.edit().putFloat(LATITUDE, value.toFloat()).apply()

     var longitude: Double
        get() = sharedPreferences.getFloat(LONGITUDE, 0f).toDouble()
        set(value) = sharedPreferences.edit().putFloat(LONGITUDE, value.toFloat()).apply()



    override fun saveTempUnit(tempUnit: String) {
        sharedPreferences.edit().putString(TEMP_UNIT_KEY, tempUnit).apply()
    }

    override fun getTempUnit(): String {
        return sharedPreferences.getString(TEMP_UNIT_KEY, "metric") ?: "metric"
    }

    override fun saveWindSpeedUnit(windUnit: String) {
        sharedPreferences.edit().putString(WIND_SPEED_KEY, windUnit).apply()
    }

    override fun getWindSpeedUnit(): String {
        return sharedPreferences.getString(WIND_SPEED_KEY, "metric") ?: "metric"
    }
    override fun saveLocType(type:String) { sharedPreferences.edit().putString(LOCATION_TYPE,type).apply() }
    override fun getLocType():String? = sharedPreferences.getString(LOCATION_TYPE,"gps")

    override fun saveLang(language:String) { sharedPreferences.edit().putString(LANGUAGE_KEY,language).apply() }
    override fun getLang():String? = sharedPreferences.getString(LANGUAGE_KEY,"en")
}