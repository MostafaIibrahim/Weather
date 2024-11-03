package com.example.weather.alert_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weather.data.Alarm
import com.example.weather.data.repository.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AlertViewModel(val repo: WeatherRepository):ViewModel() {
    //Use StateFlow to hold the selected date, time, location, and notification type.
    private val _alarmList = MutableStateFlow<List<Alarm>>(emptyList())
    val alarmList:StateFlow<List<Alarm>> = _alarmList

    init {

    }
    //Add functions to handle:
    fun getLatAndLong():Pair<Double,Double> = Pair(repo.getLocationCoord_long(),repo.getLocationCoord_lat() )


    fun addAlerm(alarm:Alarm){
        viewModelScope.launch(Dispatchers.IO) {
            repo.insertAlarm(alarm)
        }
    }
    fun deleteAlerm(alarm:Alarm){
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteAlarm(alarm)
        }
    }

}
class AlertViewModelFactory(private val _repo: WeatherRepository): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if(modelClass.isAssignableFrom(AlertViewModel::class.java)){
            AlertViewModel(_repo) as T
        }else{
            throw IllegalArgumentException("View Model class not found")
        }
    }
}