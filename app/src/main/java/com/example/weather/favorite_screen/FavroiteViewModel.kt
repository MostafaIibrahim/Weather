package com.example.weather.favorite_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weather.data.WeatherDisplayable
import com.example.weather.data.repository.IWeatherRepository
import com.example.weather.data.repository.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class FavroiteViewModel(val repo: IWeatherRepository):ViewModel() {
    private var _ListDataFlow= MutableStateFlow<List<WeatherDisplayable>>(emptyList())
    var listDataFlow: StateFlow<List<WeatherDisplayable>> = _ListDataFlow

    init{
        getAllStoredLocations()
    }

    fun getAllStoredLocations() = viewModelScope.launch(Dispatchers.IO){
        repo.getAllFavLocations().catch { println("Error") }
            .collect{
                _ListDataFlow.value = it
            }
    }
    fun insertLocation(location:WeatherDisplayable){ viewModelScope.launch { repo.addToFav(location) } }

    fun deleteLocation(location: WeatherDisplayable){ viewModelScope.launch { repo.deleteFromFav(location) }}

    fun addLocationFav(lat:Double, lon:Double)
            = viewModelScope.launch(Dispatchers.IO){
            repo.getDisplayableData(lat,lon)
            .catch { error(it) }
            .collect{ data ->
                insertLocation(data)
            }
    }
}

class FavroiteViewModelFactory(private val _repo: WeatherRepository): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if(modelClass.isAssignableFrom(FavroiteViewModel::class.java)){
            FavroiteViewModel(_repo) as T
        }else{
            throw IllegalArgumentException("View Model class not found")
        }
    }
}