package com.example.weather.alert_screen

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.weather.R
import com.example.weather.data.db.WeatherDataBase
import com.example.weather.data.db.WeatherLocalDataSource
import com.example.weather.data.remote_network.WeatherRemoteDataSource
import com.example.weather.data.repository.WeatherRepository
import com.example.weather.databinding.FragmentAlertBinding
import com.example.weather.util.SettingsPreferencesHelper
import kotlinx.coroutines.launch


class AlertFragment : Fragment() {
    lateinit var factory: AlertViewModelFactory
    lateinit var viemodel:AlertViewModel
    lateinit var alartBinder: FragmentAlertBinding
    lateinit var alertAdapter: AlarmAdapter
    lateinit var animation: LottieAnimationView
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
        setupAdapterList()
        //Displaying Adapter List
        viemodel.getAllAlerms()
        lifecycleScope.launch {
            viemodel.alarmList.collect{
                if(it != null){
                    //lottie gone
                    alartBinder.lottieAnimationView.visibility = View.GONE
                    alertAdapter.submitList(it)
                }else{
                    alartBinder.lottieAnimationView.visibility = View.VISIBLE                    //lottie appears
                }
            }

        }
        fabListener()
    }
    fun setupAdapterList(){
        alertAdapter = AlarmAdapter{ alarm ->
            viemodel.deleteAlerm(alarm)
            cancelAlarm(alarm.id)
        }
         alartBinder.alarmRecycler.apply {
            adapter = alertAdapter
             layoutManager = LinearLayoutManager(context).apply { orientation = RecyclerView.VERTICAL }
         }
    }
    private fun fabListener(){
        alartBinder.floatingActionButton.setOnClickListener{
            if (checkOverApplicationPermissions()){
                showDialog()
            }else{
                enableOverOtherAppService()
                if (checkOverApplicationPermissions()) {
                    showDialog()
                }
            }

        }
    }
    private fun showDialog(){
        alartBinder.floatingActionButton.visibility = View.GONE
        alartBinder.fragmentContainerView2.visibility = View.VISIBLE
        childFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView2, DialogAlertFragment())
            .addToBackStack(null) // Optional: allows the fragment to be closed with the back button
            .commit()
    }
    fun hideDialogFragment() {
        alartBinder.floatingActionButton.visibility = View.VISIBLE
        alartBinder.fragmentContainerView2.visibility = View.GONE
        childFragmentManager.popBackStack() // or use remove transaction
    }
    private fun cancelAlarm(alarmId: Int) {
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireContext(), AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            alarmId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }
    fun checkOverApplicationPermissions(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13 (API 33) and above
            return requireActivity().checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        }
        // For devices running lower than Android 13, permission is not required
        return true
    }

    fun enableOverOtherAppService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Only request on Android 13 and above
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 2)
        }
    }
}

