package com.example.weather.alert_screen

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.weather.R
import com.example.weather.data.Alarm
import com.example.weather.data.db.WeatherDataBase
import com.example.weather.data.db.WeatherLocalDataSource
import com.example.weather.data.remote_network.WeatherRemoteDataSource
import com.example.weather.data.repository.WeatherRepository
import com.example.weather.databinding.FragmentAlertBinding
import com.example.weather.util.SettingsPreferencesHelper
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.util.Calendar


class AlertFragment : Fragment() {
    lateinit var factory: AlertViewModelFactory
    lateinit var viemodel:AlertViewModel
    lateinit var alartBinder: FragmentAlertBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        alartBinder = FragmentAlertBinding.inflate(inflater,container,false)
        factory = AlertViewModelFactory(
            WeatherRepository.getRepository(
                WeatherRemoteDataSource,
                SettingsPreferencesHelper(requireContext()),
                WeatherLocalDataSource.getInstance(WeatherDataBase.getInstance(requireContext()).getWeatherDao(),
                    WeatherDataBase.getInstance(requireContext()).getAlarmDao())
            ))
        viemodel = ViewModelProvider(this,factory).get(AlertViewModel::class.java)
        return alartBinder.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //Handle deleting an alert which leads to canceling it
        //Displaying Adapter List
        fabListener()
    }
    private fun fabListener(){
        alartBinder.floatingActionButton.setOnClickListener{
            DialogAlertFragment().show(parentFragmentManager, "DialogAlertFragment")
        }
    }

}

