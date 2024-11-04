package com.example.weather.favorite_screen

import com.example.weather.data.Alarm
import com.example.weather.data.CurrentWeather
import com.example.weather.data.ForcastWeather
import com.example.weather.data.WeatherDisplayable
import com.example.weather.data.repository.IWeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeWeatherRepositoryTest: IWeatherRepository{
    private val favoriteLocations = MutableStateFlow<List<WeatherDisplayable>>(emptyList())



    override suspend fun getDisplayableData(lat: Double, lon: Double): Flow<WeatherDisplayable> {
        TODO("Not yet implemented")
    }

    override suspend fun getFrocastRemoteData(lat: Double, lon: Double): Flow<ForcastWeather> {
        TODO("Not yet implemented")
    }

    override suspend fun getWeatherRemoteData(lat: Double, lon: Double): Flow<CurrentWeather> {
        TODO("Not yet implemented")
    }

    override fun getAllFavLocations(): Flow<List<WeatherDisplayable>> = favoriteLocations

    override fun getChachedLocation(): Flow<WeatherDisplayable> {
        TODO("Not yet implemented")
    }

    override suspend fun addToFav(FavLocation: WeatherDisplayable) {
        val currentList = favoriteLocations.value.toMutableList()
        currentList.add(FavLocation)
        favoriteLocations.value = currentList
    }

    override suspend fun deleteFromFav(FavLocation: WeatherDisplayable) {
        val currentList = favoriteLocations.value.toMutableList()
        currentList.remove(FavLocation)
        favoriteLocations.value = currentList
    }

    override fun getAllAlarms(): Flow<List<Alarm>> {
        TODO("Not yet implemented")
    }

    override suspend fun insertAlarm(alarm: Alarm) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAlarm(alarm: Alarm) {
        TODO("Not yet implemented")
    }

    override fun saveTempUnit(unit: String) {
        TODO("Not yet implemented")
    }

    override fun getTempUnit(): String {
        TODO("Not yet implemented")
    }

    override fun saveLocType(type: String) {
        TODO("Not yet implemented")
    }

    override fun getLocType(): String {
        TODO("Not yet implemented")
    }

    override fun saveLang(language: String) {
        TODO("Not yet implemented")
    }

    override fun getLang(): String {
        TODO("Not yet implemented")
    }

    override fun saveWindUnit(unit: String) {
        TODO("Not yet implemented")
    }

    override fun getWindUnit(): String {
        TODO("Not yet implemented")
    }

    override fun saveLocationCoord(lon: Double, lat: Double) {
        TODO("Not yet implemented")
    }

    override fun getLocationCoord_long(): Double {
        TODO("Not yet implemented")
    }

    override fun getLocationCoord_lat(): Double {
        TODO("Not yet implemented")
    }
}