package com.example.weather.favorite_screen

import androidx.lifecycle.ViewModel
import com.example.weather.data.WeatherDisplayable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SharedHomeViewModel : ViewModel() {
    private val _selectedLocation = MutableStateFlow<WeatherDisplayable?>(null)
    val selectedLocation: StateFlow<WeatherDisplayable?> = _selectedLocation

    fun setSelectedLocation(location: WeatherDisplayable?) {

        _selectedLocation.value = location
    }
}