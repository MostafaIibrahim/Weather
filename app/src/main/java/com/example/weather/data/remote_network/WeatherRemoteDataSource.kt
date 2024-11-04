package com.example.weather.data.remote_network

import android.net.http.HttpException
import android.os.Build
import androidx.annotation.RequiresExtension
import com.example.weather.data.CurrentWeather
import com.example.weather.data.ForcastWeather
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException

object WeatherRemoteDataSource : IWeatherRemoteDataSource {

    override suspend fun getForecastWeather(
        lat: Double, lon: Double, unit: String, language: String
    ): Flow<ForcastWeather> = flow {
            val response = API.retrofitService.getForcastedWeather(lat, lon, unit, language)
//            println("Debug: Forecast response: $response")
            emit(response)
    }

    override suspend fun getCurrentWeather(lat:Double, lon:Double, unit:String, language:String):Flow<CurrentWeather> = flow{
        emit(API.retrofitService.getCurretWeather(lat,lon,unit, language ))
    }
}