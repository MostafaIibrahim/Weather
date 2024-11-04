package com.example.weather.home_screen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.weather.data.ResaultStatus
import com.example.weather.data.WeatherDisplayable
import com.example.weather.data.repository.WeatherRepository
import com.example.weather.data.remote_network.WeatherRemoteDataSource
import com.example.weather.databinding.FragmentHomeBinding
import com.example.weather.util.LocationHelper
import com.example.weather.util.SettingsPreferencesHelper
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.example.weather.data.db.WeatherDataBase
import com.example.weather.data.db.WeatherLocalDataSource
import com.example.weather.favorite_screen.SharedHomeViewModel

//I need to adjust home screening because it's a big mass
//I need to use current weather API too

class HomeFragment : Fragment(),CallBackForGPSCoord{
    lateinit var homeFactory:HomeViewModelFactory
    lateinit var binding:FragmentHomeBinding
    lateinit var viewModel: HomeViewModel
    private lateinit var hourlyAdapter: HourlyAdapter
    private lateinit var dailyAdapter: DailyAdapter
    private lateinit var settingsPreferencesHelper: SettingsPreferencesHelper
    private lateinit var sharedViewModel: SharedHomeViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //fetch data
        settingsPreferencesHelper = SettingsPreferencesHelper(requireContext())
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedHomeViewModel::class.java)
    }

    override fun onStart() {
        super.onStart()
        decideLocationSourceAndFetchData()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeFactory = HomeViewModelFactory(
            WeatherRepository.getRepository(WeatherRemoteDataSource,
                SettingsPreferencesHelper(requireContext()),
                WeatherLocalDataSource.getInstance(WeatherDataBase.getInstance(requireContext()).getWeatherDao(),
                    WeatherDataBase.getInstance(requireContext()).getAlarmDao() )
            ))
        viewModel = ViewModelProvider(this,homeFactory).get(HomeViewModel::class.java)
//        viewModel.getFetchedData(30.0709887,31.0215654)

        observeFavDisplaying()
        lifecycleScope.launch {
            viewModel.stateFlow.collectLatest {
                when(it){
                    is ResaultStatus.Failure -> {
                        println("Failed")
                    }
                    is ResaultStatus.Success -> {
                        var responseObj = it.data
                        updateUI(responseObj)
                    }
                    is ResaultStatus.Loading -> {
                        //Show loading bar
                    }

                    else -> {}
                }
            }
        }
    }
    private fun observeFavDisplaying(){
        lifecycleScope.launchWhenStarted {
            sharedViewModel.selectedLocation.collectLatest { favoriteLocation ->
                favoriteLocation?.let {
                    // Update UI with favorite location data
                    updateUI(it)
                    // Clear the selected location after displaying it
                    sharedViewModel.setSelectedLocation(null)
                }
            }
        }
    }
    private fun decideLocationSourceAndFetchData() {
        // Check if a favorite location is set
        if (sharedViewModel.selectedLocation.value == null) {
            // No favorite location selected, proceed with GPS or saved location
            if (settingsPreferencesHelper.getLocType() == "gps") {
                val locationHelper = LocationHelper(requireContext())
                locationHelper.getActualLocation(requireActivity(), this)
            } else {
                val latitude = settingsPreferencesHelper.latitude
                val longitude = settingsPreferencesHelper.longitude
                if (latitude != null && longitude != null) {
                    viewModel.getFetchedData(latitude, longitude)
                } else {
                    println("Saved location not found, fallback to GPS.")
                    val locationHelper = LocationHelper(requireContext())
                    locationHelper.getActualLocation(requireActivity(), this)
                }
            }
        }
    }
    private fun updateUI(responseObj:WeatherDisplayable){
        binding.apply {
            address.text = responseObj.location
            time.text = responseObj.currentTime
            date.text = responseObj.currentDay
            Glide.with(requireContext())
                .load("https://openweathermap.org/img/wn/${responseObj.weatherIconUrl}@2x.png")
                .override(400, 400)
                .diskCacheStrategy(DiskCacheStrategy.ALL) // Cache all versions of the image
                .into(currentImg)
            status.text = responseObj.weatherDescription
            temp.text = responseObj.temperature
            degreeTxt.text = responseObj.tempUnit
            minTemp.text = responseObj.currentMinTemp+ "°"
            maxTemp.text = responseObj.currentMaxTemp+ "°"
            feelLike.text = responseObj.feelLike + "°"
            sunrise.text = responseObj.sunrise
            sunset.text = responseObj.sunset
            wind.text = responseObj.windSpeed + responseObj.speedUnit
            pressure.text = responseObj.pressure + "hpa"
            humidity.text = responseObj.humidity + "%"
            cloud.text = responseObj.cloudCoverage
            setupHourlyAdapterList(responseObj.hourlyForecast)
            setupDailyAdapterList(responseObj.dailyForecast)
        }
    }

    private fun setupHourlyAdapterList(listOfHours:List<com.example.weather.data.util_pojo.List>){
        hourlyAdapter = HourlyAdapter(requireContext())
        binding.threeHoursRecyclerView.apply {
            adapter = hourlyAdapter
            hourlyAdapter.submitList(listOfHours)
            layoutManager = LinearLayoutManager(context).apply { orientation= RecyclerView.HORIZONTAL }
        }
    }
    private fun setupDailyAdapterList(listOfDays:List<com.example.weather.data.util_pojo.List>){
        dailyAdapter = DailyAdapter(requireContext())
        binding.daysForscastRecycler.apply {
            adapter = dailyAdapter
            dailyAdapter.submitList(listOfDays)
            layoutManager = LinearLayoutManager(context).apply { orientation= RecyclerView.VERTICAL }
        }
    }

    override fun onLocationResult(latitude: Double, longitude: Double) {
        viewModel.getFetchedData(latitude,latitude)
    }

}