package com.example.weather.data.db

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weather.data.WeatherDisplayable
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class WeatherLocalDataSourceTest{
    @get:Rule
    val rule = InstantTaskExecutorRule()

    lateinit var db : WeatherDataBase
    lateinit var weatherDao:WeatherDAO
    lateinit var local:WeatherLocalDataSource

    @Before
    fun setup(){
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext<Context?>(),
            WeatherDataBase::class.java
        ).allowMainThreadQueries()
            .build()
        weatherDao = db.getWeatherDao()
        local = WeatherLocalDataSource(weatherDao,db.getAlarmDao())

    }

    @After
    fun tearDown(){
        db.close()
    }
    @Test
    fun addLocation_locationIdEquel1() = runTest{
        val loc1 = getDisplayableWeather(1)
        local.addLocation(loc1)

        val result = local.getAllFavLocations().first()

        assertEquals(1,result.first().id)

    }
    @Test
    fun getAllFavLocations_sizeEquelthree() = runTest{
        val loc1 = getDisplayableWeather(1)
        val loc2 = getDisplayableWeather(2)
        val loc3 = getDisplayableWeather(3)
        local.addLocation(loc1)
        local.addLocation(loc3)
        local.addLocation(loc2)

        val result = local.getAllFavLocations().first()

        assertEquals(3,result.size)

    }
    private fun getDisplayableWeather(_id:Int): WeatherDisplayable = WeatherDisplayable(
        id = _id,
        location = "Cairo, EG",
        currentDay = "10-01", // MM-dd format for current day
        currentTime = "14:00", // HH:mm format for current time
        weatherIconUrl = "01d",
        weatherDescription = "Clear sky",
        temperature = "22",
        tempUnit = "°C",
        currentMaxTemp = "25",
        currentMinTemp = "20",
        sunrise = "06:30",
        sunset = "18:45",
        feelLike = "21",
        pressure = "1013",
        windSpeed = "3.5",
        speedUnit = "m/sec",
        humidity = "60",
        cloudCoverage = "10",
        hourlyForecast = listOf(), // Replace with mock data if needed
        dailyForecast = listOf() // Replace with mock data if needed
    )

}