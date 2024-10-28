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
import com.example.weather.data.ForcastWeather
import com.example.weather.data.ResaultStatus
import com.example.weather.data.repository.WeatherRepository
import com.example.weather.data.remote_network.WeatherRemoteDataSource
import com.example.weather.data.util_pojo.Weather
import com.example.weather.databinding.FragmentHomeBinding
import com.example.weather.util.SettingsPreferencesHelper
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

//I need to adjust home screening because it's a big mass
//I need to use current weather API too

class HomeFragment : Fragment(){
    lateinit var homeFactory:HomeViewModelFactory
    lateinit var binding:FragmentHomeBinding
    lateinit var viewModel: HomeViewModel
    lateinit var forcastResponseObj:ForcastWeather
    private lateinit var hourlyAdapter: HourlyAdapter
    private lateinit var dailyAdapter: DailyAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeFactory = HomeViewModelFactory(
            WeatherRepository.getRepository(WeatherRemoteDataSource,
            SettingsPreferencesHelper(requireContext())
        ))
        viewModel = ViewModelProvider(this,homeFactory).get(HomeViewModel::class.java)
        //fetch data
        viewModel.onHomeScreenStart(requireContext())

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

        viewModel.getFetchedData(30.0709887,31.0215654)
        lifecycleScope.launch {
            viewModel.forcastStateFlow.collectLatest {
                when(it){
                    is ResaultStatus.Failure -> {
                        println("Failed")
                    }
                    is ResaultStatus.Success -> {
                        forcastResponseObj = it.data
                        binding.apply {
                            address.text = forcastResponseObj.city?.name + "," +forcastResponseObj.city?.country
                            time.text = currentTime(forcastResponseObj.city?.timezone,true)
                            date.text = currentTime(forcastResponseObj.city?.timezone,false)
                            Glide.with(requireContext())
                                .load(getImgUrl(forcastResponseObj.list.get(0).weather.get(0)))
                                .diskCacheStrategy(DiskCacheStrategy.ALL) // Cache all versions of the image
                                .into(currentImg)
                            status.text = forcastResponseObj.list.get(0).weather.get(0).description
                            temp.text = forcastResponseObj.list.get(0).main?.temp?.toInt().toString()
                            minTemp.text = forcastResponseObj.list.get(0).main?.tempMin?.toInt().toString() + "°"
                            maxTemp.text = forcastResponseObj.list.get(0).main?.tempMax?.toInt().toString() + "°"
                            feelLike.text = forcastResponseObj.list.get(0).main?.feelsLike?.toInt().toString() + "°"
                            sunrise.text = currentTime(forcastResponseObj.city?.sunrise,true)
                            sunset.text = currentTime(forcastResponseObj.city?.sunset,true)
                            wind.text = forcastResponseObj.list.get(0).wind?.speed.toString() + " m/s"
                            pressure.text = forcastResponseObj.list.get(0).main?.pressure.toString() +" hpa"
                            humidity.text = forcastResponseObj.list.get(0).main?.humidity.toString() + "%"
                            cloud.text = forcastResponseObj.city?.population.toString()
                            setupHourlyAdapterList(provideHourlyDataForAdapter(forcastResponseObj.list))
                            setupDailyAdapterList(provideDailyListForAfapter(forcastResponseObj.list))
                        }
                    }
                    is ResaultStatus.Loading -> {

                    }
                }
            }


        }
    }
    fun getImgUrl(weatherObj:Weather):String{
        val iconName =weatherObj.icon  // Example: "10d" or "01n"
        val baseUrl = "https://openweathermap.org/img/wn/"
        return "$baseUrl$iconName@2x.png"
    }

    fun currentTime(offsetInSeconds: Int?,isTime:Boolean):String{
        var format:String = ""
        val offset = ZoneOffset.ofTotalSeconds(7250)
        val timeInOffset = Instant.now().atOffset(offset).toZonedDateTime()

        if(isTime){
            format = timeInOffset.format(DateTimeFormatter.ofPattern("HH:mm"))
        }else{
            format = timeInOffset.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        }
        return format
    }

    fun provideHourlyDataForAdapter(listOfHours:List<com.example.weather.data.util_pojo.List>):List<com.example.weather.data.util_pojo.List>
        = listOfHours.take(8)
    fun provideDailyListForAfapter(listOfDays:List<com.example.weather.data.util_pojo.List>):List<com.example.weather.data.util_pojo.List>
       = listOf(listOfDays.get(4),listOfDays.get(12),listOfDays.get(20),listOfDays.get(28),listOfDays.get(36))


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




}