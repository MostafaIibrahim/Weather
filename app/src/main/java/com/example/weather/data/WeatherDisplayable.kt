package com.example.weather.data

import android.provider.Settings.Global.getString
import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.weather.R
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@Entity(tableName = "weatherLoc")
data class WeatherDisplayable (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var location: String? = null,
    var currentDay: String? = null,
    var currentTime: String? = null,
    var weatherIconUrl: String? = null,
    var weatherDescription: String? = null,
    var temperature: String? = null,
    var tempUnit:String? = null,
    var currentMaxTemp: String? = null,
    var currentMinTemp: String? = null,
    var sunrise: String? = null,
    var sunset: String? = null,
    var feelLike: String? = null,
    var pressure: String? = null,
    var windSpeed: String? = null,
    var speedUnit:String? = null,
    var humidity: String? = null,
    var cloudCoverage: String? = null,
    var hourlyForecast: List<com.example.weather.data.util_pojo.List> = arrayListOf(),
    var dailyForecast: List<com.example.weather.data.util_pojo.List> = arrayListOf()
) {
    constructor(forecastWeather: ForcastWeather, currentWeather: CurrentWeather,unit:String) : this() {
        // Initialize fields using data from currentWeather
        location = forecastWeather.city?.name + "," + forecastWeather.city?.country // Adjust based on your actual response
        currentDay = calculateLocalTime(currentWeather.dt!!,currentWeather.timezone!!,true)
        currentTime = calculateLocalTime(currentWeather.dt!!,currentWeather.timezone!!,false)  // Convert to desired time format if needed
        weatherIconUrl = currentWeather.weather.get(0).icon
        weatherDescription = currentWeather.weather.get(0).description
        temperature = currentWeather.main?.temp?.toInt().toString()
        tempUnit = displayTempUnit(unit)
        feelLike = currentWeather.main?.feelsLike?.toInt().toString()
        pressure = currentWeather.main?.pressure.toString()
        windSpeed = currentWeather.wind?.speed.toString()
        speedUnit = displaySpeedUnit(unit)

        humidity = currentWeather.main?.humidity.toString()
        cloudCoverage = currentWeather.clouds?.all.toString()

        // Initialize sunrise and sunset from forecastWeather or currentWeather as needed
        sunrise = calculateLocalTime(forecastWeather.city?.sunrise!!,forecastWeather.city?.timezone!!,false) // Adjust based on actual field in response
        sunset = calculateLocalTime(forecastWeather.city?.sunset!!,forecastWeather.city?.timezone!!,false) // Adjust based on actual field in response

        // Initialize max/min temp for the day
        currentMaxTemp = currentWeather.main?.tempMax?.toInt().toString()
        currentMinTemp = currentWeather.main?.tempMin?.toInt().toString()

        // Populate forecasts with list mappings if applicable
        hourlyForecast = forecastWeather.list.take(8)
        dailyForecast = listOf(forecastWeather.list.get(4),forecastWeather.list.get(12),forecastWeather.list.get(20),forecastWeather.list.get(28),forecastWeather.list.get(36))
    }
    fun calculateLocalTime(dt: Int, timeZoneOffset: Int,isDay:Boolean): String {
        // Convert the Unix timestamp and apply the timezone offset
        lateinit var formatter:DateTimeFormatter
        val instant = Instant.ofEpochSecond(dt.toLong())
        val zoneOffset = ZoneOffset.ofTotalSeconds(timeZoneOffset)

        // Apply the timezone offset to get the local time
        val zonedDateTime = instant.atOffset(zoneOffset).toZonedDateTime()
        if(isDay){
             formatter = DateTimeFormatter.ofPattern("MM-dd")
        }else{
            formatter = DateTimeFormatter.ofPattern("HH:mm")
        }
        // Format the local time in a readable way
        return zonedDateTime.format(formatter)
    }
    private fun displayTempUnit(u :String) = when(u){
            "metric" -> "°C"
            "imperial" -> "°F"
            "standard" -> "°K"
             else -> "°C"
        }
    private fun displaySpeedUnit(u :String) = when(u){
        "metric" -> "m/sec"
        "imperial" -> "mile/hour"
        else -> "m/sec"
    }

}
