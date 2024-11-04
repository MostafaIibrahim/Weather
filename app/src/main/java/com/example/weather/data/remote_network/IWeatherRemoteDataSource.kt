package com.example.weather.data.remote_network

import com.example.weather.data.CurrentWeather
import com.example.weather.data.ForcastWeather
import kotlinx.coroutines.flow.Flow

interface IWeatherRemoteDataSource {
    suspend fun getForecastWeather(
        lat: Double, lon: Double, unit: String, language: String
    ): Flow<ForcastWeather>

    suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        unit: String,
        language: String
    ): Flow<CurrentWeather>
}