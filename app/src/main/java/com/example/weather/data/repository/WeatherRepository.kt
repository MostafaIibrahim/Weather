package com.example.weather.data.repository

import com.example.weather.data.CurrentWeather
import com.example.weather.data.ForcastWeather
import com.example.weather.data.remote_network.WeatherRemoteDataSource
import com.example.weather.util.SettingsPreferencesHelper
import kotlinx.coroutines.flow.Flow

/*
 -Description: This Class is to unify the way that any viewModels access data canter
 -Inputs: It takes remoteDataSource,localDataSource
 */
class WeatherRepository(
    var weatherRemoteDataSource: WeatherRemoteDataSource,
    val settingsPreferences: SettingsPreferencesHelper
    ) : IWeatherRepository {

    companion object{
        private var INSTANCE: WeatherRepository?=null

        fun getRepository(weatherRemoteDataSource: WeatherRemoteDataSource,settingsPreferences: SettingsPreferencesHelper): WeatherRepository {
            return INSTANCE ?: synchronized(this){
                WeatherRepository(weatherRemoteDataSource,settingsPreferences).also {
                    INSTANCE = it
                }
            }
        }
    }

    //Here I want to cache data and be able to update or modify
    override suspend fun getFrocastRemoteData(lat:Double, lon:Double): Flow<ForcastWeather> {
        println("Before fetching directly $lat,$lon,${getTempUnit()},${getLang()}")
        return weatherRemoteDataSource.getForecastWeather(lat, lon, unit = getTempUnit(), language = getLang())
    }

    override suspend fun getWeatherRemoteData(lat:Double, lon:Double): Flow<CurrentWeather> {
        return weatherRemoteDataSource.getCurrentWeather(lat, lon, getTempUnit(), getLang())
    }

    override fun saveTempUnit(unit:String){ settingsPreferences.saveTempUnit(unit) }
    override fun getTempUnit():String {
        return settingsPreferences.getTempUnit() ?: "metric"
    }

    override fun saveWindUnit(unit:String) { settingsPreferences.saveWindSpeedUnit(unit) }
    override fun getWindUnit():String = settingsPreferences.getWindSpeedUnit()
    override fun saveLocationCoord(lon: Double, lat: Double) {
        settingsPreferences.latitude = lat
        settingsPreferences.longitude = lon
    }

    override fun getLocationCoord_long(): Double = settingsPreferences.longitude

    override fun getLocationCoord_lat(): Double  = settingsPreferences.latitude

    override fun saveLocType(type:String){ settingsPreferences.saveLocType(type) }
    override fun getLocType():String = settingsPreferences.getLocType() ?: "gps"


    override fun saveLang(language:String) { settingsPreferences.saveLang(language) }
    override fun getLang():String {
        return settingsPreferences.getLang() ?: "en"
    }


}