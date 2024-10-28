package com.example.weather.home_screen

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
    private var _forcastStateFlow= MutableStateFlow<ResaultStatus>(ResaultStatus.Loading)
    var forcastStateFlow:StateFlow<ResaultStatus> = _forcastStateFlow


    fun onHomeScreenStart(context:Context) {
        var activity =MainActivity()
        val locationHelper= LocationHelper(context)
        when (weatherRepository.getLocType()) {
            "GPS" -> {
               locationHelper.getActualLocation(activity,this)
            }
            "Map" -> {
                getFetchedData(weatherRepository.getLocationCoord_lat(),weatherRepository.getLocationCoord_long())
            }
        }
     }


    fun getFetchedData(lat:Double,lon:Double)
    = viewModelScope.launch(Dispatchers.IO){
        weatherRepository.getFrocastRemoteData(lat,lon)
            .catch { error ->
                _forcastStateFlow.value =  ResaultStatus.Failure(error)  }
            .collect{ data ->
                _forcastStateFlow.value = ResaultStatus.Success(data)
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