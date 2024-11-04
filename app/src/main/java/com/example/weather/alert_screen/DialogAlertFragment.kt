package com.example.weather.alert_screen

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.weather.R
import com.example.weather.data.Alarm
import com.example.weather.data.db.WeatherDataBase
import com.example.weather.data.db.WeatherLocalDataSource
import com.example.weather.data.remote_network.WeatherRemoteDataSource
import com.example.weather.data.repository.WeatherRepository
import com.example.weather.databinding.FragmentDialogAlertBinding
import com.example.weather.util.SettingsPreferencesHelper
import java.util.Calendar
import java.util.Locale
import android.Manifest
import android.annotation.SuppressLint
import android.widget.Toast

class   DialogAlertFragment : DialogFragment() {
    private lateinit var dialogBinder: FragmentDialogAlertBinding
    val calendar = Calendar.getInstance()
    var selectedYear = calendar.get(Calendar.YEAR)
    var selectedMonth = calendar.get(Calendar.MONTH)
    var selectedDay = calendar.get(Calendar.DAY_OF_MONTH)
    var selectedHour = calendar.get(Calendar.HOUR_OF_DAY)
    var selectedMinute = calendar.get(Calendar.MINUTE)
    var time = false
    private var latitude: Double = 37.7749
    private var longitude: Double = -122.4194
    private var notificationType: String = "Alarm"
    private var locationName: String = ""
    private val REQUEST_CODE = 505

    private lateinit var factory: AlertViewModelFactory
    private lateinit var viewModel: AlertViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialogBinder = FragmentDialogAlertBinding.inflate(inflater, container, false)
        factory = AlertViewModelFactory(
            WeatherRepository.getRepository(
                WeatherRemoteDataSource,
                SettingsPreferencesHelper(requireContext()),
                WeatherLocalDataSource.getInstance(
                    WeatherDataBase.getInstance(requireContext()).getWeatherDao(),
                    WeatherDataBase.getInstance(requireContext()).getAlarmDao()
                )
            )
        )
        viewModel = ViewModelProvider(this, factory).get(AlertViewModel::class.java)
        return dialogBinder.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialogBinder.apply {
            startDuration.setOnClickListener { showTimePicker() }
//            setDate.setOnClickListener { showDatePicker() }

            notifyOptions.setOnCheckedChangeListener { _, checkedId ->
                notificationType = if (checkedId == R.id.notification_config) "Notification" else "Alarm"
            }
            locationRadioButton.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.current_location -> fetchCurrentLocationCoordinates()
                    R.id.choose_location -> navigateToMapForLocation()
                }
            }
            saveButton.setOnClickListener { saveAlertConfig() }
            cancelButton.setOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
        }
    }

    private fun fetchCurrentLocationCoordinates() {
        // Simulating getting coordinates for the current location
        val (lat, lng) = viewModel.getLatAndLong()
        latitude = lat
        longitude = lng
        locationName = getLocationName(latitude, longitude)
    }

    private fun navigateToMapForLocation() {
        // Launch map and retrieve selected coordinates
        // Example - assume you get `latitude` and `longitude` here
    }

    private fun showTimePicker() {
        val datePickerDialog = DatePickerDialog(this.requireContext(), { _, year, month, dayOfMonth ->
            selectedYear = year
            selectedMonth = month
            selectedDay = dayOfMonth

            val timePickerDialog = TimePickerDialog(this.requireContext(), { _, hourOfDay, minute ->
                selectedHour = hourOfDay
                selectedMinute = minute
                time = true
            }, selectedHour, selectedMinute, true)
            timePickerDialog.setOnCancelListener {

            }
            timePickerDialog.show()

        }, selectedYear, selectedMonth, selectedDay)
        datePickerDialog.datePicker.minDate = calendar.timeInMillis
        datePickerDialog.show()
    }

//    private fun showDatePicker() {
//        DatePickerDialog(
//            requireContext(),
//            { _, year, month, dayOfMonth ->
//                selectedYear = year
//                selectedMonth = month
//                selectedDay = dayOfMonth
//            },
//            selectedYear,selectedMonth,selectedDay
//        ).show()
//    }

    private fun saveAlertConfig() {
        val alertDate = calendar.time
        val alertTime = "${calendar.get(Calendar.HOUR_OF_DAY)}:${calendar.get(Calendar.MINUTE)}"

        val alarm = Alarm(
            location = locationName,
            latitude = latitude,
            longitude = longitude,
            alertDate = alertDate,
            alertTime = alertTime,
            notificationType = notificationType
        )
        println("I am testing the location in Dialog alert $locationName")

        // Save alarm in database using ViewModel
        viewModel.addAlerm(alarm)

        // Schedule the alarm
        scheduleAlarm(alarm)
        dismiss()
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun scheduleAlarm(alarm: Alarm) {
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireContext(), AlarmReceiver::class.java).apply {
            putExtra("notificationType", alarm.notificationType)
            putExtra("location", alarm.location)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            alarm.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        calendar.apply {
            set(Calendar.YEAR, selectedYear)
            set(Calendar.MONTH, selectedMonth) // Note: Month is 0-based (0 = January, 11 = December)
            set(Calendar.DAY_OF_MONTH, selectedDay)
            set(Calendar.HOUR_OF_DAY, selectedHour)
            set(Calendar.MINUTE, selectedMinute)
            set(Calendar.SECOND, 0)}
        if (calendar.timeInMillis < System.currentTimeMillis()) {
            Toast.makeText(requireContext(), "can not set alarm for past time ", Toast.LENGTH_SHORT).show()
        }
        else {
            // Set the alarm for a one-time trigger
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )}
    }

    private fun getLocationName(lat: Double, lng: Double): String {
        return try {
            val geocoder = Geocoder(requireContext(), Locale.getDefault())
            val addresses = geocoder.getFromLocation(lat, lng, 1)
            addresses?.firstOrNull()?.locality ?: "Unknown Location"
        } catch (e: Exception) {
            "Unknown Location"
        }
    }

}
