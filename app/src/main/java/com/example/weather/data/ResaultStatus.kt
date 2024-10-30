package com.example.weather.data

sealed class ResaultStatus {
    data class Success(val data: WeatherDisplayable): ResaultStatus()
    data class Failure(val msg:Throwable):ResaultStatus()
    object Loading:ResaultStatus()
}