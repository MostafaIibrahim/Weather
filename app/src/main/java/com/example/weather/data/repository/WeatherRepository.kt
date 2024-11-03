package com.example.weather.data.repository

import com.example.weather.data.Alarm
import com.example.weather.data.CurrentWeather
import com.example.weather.data.ForcastWeather
import com.example.weather.data.WeatherDisplayable
import com.example.weather.data.db.WeatherLocalDataSource
import com.example.weather.data.remote_network.WeatherRemoteDataSource
import com.example.weather.util.SettingsPreferencesHelper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/*
 -Description: This Class is to unify the way that any viewModels access data canter
 -Inputs: It takes remoteDataSource,localDataSource
 */
class WeatherRepository(
    var weatherRemoteDataSource: WeatherRemoteDataSource,
    val settingsPreferences: SettingsPreferencesHelper,
    val weatherLocalDataSource: WeatherLocalDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
    ) : IWeatherRepository {

    companion object{
        private var INSTANCE: WeatherRepository?=null

        fun getRepository(weatherRemoteDataSource: WeatherRemoteDataSource,
                          settingsPreferences: SettingsPreferencesHelper,
                          weatherLocalDataSource: WeatherLocalDataSource): WeatherRepository {
            return INSTANCE ?: synchronized(this){
                WeatherRepository(weatherRemoteDataSource,settingsPreferences,weatherLocalDataSource).also {
                    INSTANCE = it
                }
            }
        }
    }

    override suspend fun getDisplayableData(lat: Double, lon: Double): Flow<WeatherDisplayable> =
        flow {
            val displayable = withContext(Dispatchers.IO) {
                val currentWeatherDeferred = async { getWeatherRemoteData(lat, lon).first() }
                val forecastDeferred = async { getFrocastRemoteData(lat, lon).first() }

                val currentWeather = currentWeatherDeferred.await()
                val forecastResponse = forecastDeferred.await()

                WeatherDisplayable(forecastResponse, currentWeather, getTempUnit())
            }
            emit(displayable)
    }
    /*
        override suspend fun getWeather(
            lat: Double,
            lon: Double,
            forceUpdate: Boolean
        ): Result<List<DisplayableWeather>> {
            if(forceUpdate){
                //updateWeatherFromRemoteDataSource
            }
            //return weatherLocalSource.getCountry()
        }*/

    //Here I want to cache data and be able to update or modify
    override suspend fun getFrocastRemoteData(lat:Double, lon:Double): Flow<ForcastWeather> {
        println("Before fetching directly $lat,$lon,${getTempUnit()},${getLang()}")
        return weatherRemoteDataSource.getForecastWeather(lat, lon, unit = getTempUnit(), language = getLang())
    }

    override suspend fun getWeatherRemoteData(lat:Double, lon:Double): Flow<CurrentWeather>{
        return weatherRemoteDataSource.getCurrentWeather(lat, lon, getTempUnit(), getLang())
    }

    override fun getAllFavLocations(): Flow<List<WeatherDisplayable>> = weatherLocalDataSource.getAllFavLocations()


    override fun getChachedLocation(): Flow<WeatherDisplayable> {
        TODO("Not yet implemented")
        //Should I getData from at viewModel or here? I think here but we will leave it for now
    }

    override suspend fun addToFav(favLocation: WeatherDisplayable) {
            weatherLocalDataSource.addLocation(favLocation)
    }

    override suspend fun deleteFromFav(favLocation: WeatherDisplayable){
            weatherLocalDataSource.deleteLocation(favLocation)
    }

    override fun getAllAlarms(): Flow<List<Alarm>> = weatherLocalDataSource.getAllAlarms()

    override suspend fun insertAlarm(alarm: Alarm) { weatherLocalDataSource.insertAlarm(alarm) }

    override suspend fun deleteAlarm(alarm: Alarm) { weatherLocalDataSource.deleteAlarm(alarm) }

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