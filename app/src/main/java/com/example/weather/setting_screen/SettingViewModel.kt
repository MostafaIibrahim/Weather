package com.example.weather.setting_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weather.data.repository.IWeatherRepository

class SettingViewModel(private val weatherRepository: IWeatherRepository):ViewModel() {

    fun saveLanguage(lang: String) { weatherRepository.saveLang(lang) }
    fun getLanguage(): String = weatherRepository.getLang()

    fun saveTemperatureUnit(unit: String) { weatherRepository.saveTempUnit(unit) }
    fun getTemperatureUnit(): String = weatherRepository.getTempUnit()

    fun saveWindSpeedUnit(unit: String) { weatherRepository.saveWindUnit(unit) }
    fun getWindSpeedUnit(): String = weatherRepository.getWindUnit()

    fun saveLocationStatus(location: String) { weatherRepository.saveLocType(location) }
    fun getLocationStatus(): String = weatherRepository.getLocType()

    fun saveLocation(lon:Double,lat:Double){ weatherRepository.saveLocationCoord(lon,lat) }

}

class SettingViewModelFactory(private val _repo: IWeatherRepository): ViewModelProvider.Factory{

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if(modelClass.isAssignableFrom(SettingViewModel::class.java)){
            SettingViewModel(_repo) as T
        }else{
            throw IllegalArgumentException("View Model class not found")
        }
    }
}