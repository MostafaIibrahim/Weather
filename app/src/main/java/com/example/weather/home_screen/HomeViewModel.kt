package com.example.weather.home_screen

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weather.MainActivity
import com.example.weather.data.ResaultStatus
import com.example.weather.data.repository.WeatherRepository
import com.example.weather.util.LocationHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class HomeViewModel(var weatherRepository: WeatherRepository):ViewModel(),CallBackForGPSCoord{
    private var _StateFlow= MutableStateFlow<ResaultStatus>(ResaultStatus.Loading)
    var stateFlow:StateFlow<ResaultStatus> = _StateFlow
    private var hasLoadedData = false

    fun onHomeScreenStart(activity: Activity, context: Context) {
        if (!hasLoadedData) {
            hasLoadedData = true
            val locationHelper = LocationHelper(context)
            when (weatherRepository.getLocType()) {
                "GPS" -> locationHelper.getActualLocation(activity, this)
                "Map" -> getFetchedData(weatherRepository.getLocationCoord_lat(), weatherRepository.getLocationCoord_long())
            }
        }
    }


    fun getFetchedData(lat:Double,lon:Double)
            = viewModelScope.launch(){
        weatherRepository.getDisplayableData(lat,lon)
            .catch { error ->
                _StateFlow.value =  ResaultStatus.Failure(error)  }
            .collect{ data ->
                _StateFlow.value = ResaultStatus.Success(data)
            }
    }

    override fun onLocationResult(latitude: Double, longitude: Double) {
        getFetchedData(latitude,latitude)
    }

}
class HomeViewModelFactory(private val _repo: WeatherRepository):ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if(modelClass.isAssignableFrom(HomeViewModel::class.java)){
            HomeViewModel(_repo) as T
        }else{
            throw IllegalArgumentException("View Model class not found")
        }
    }
}