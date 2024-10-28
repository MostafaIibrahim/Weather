package com.example.weather.setting_screen

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.weather.R
import com.example.weather.data.repository.WeatherRepository
import com.example.weather.data.remote_network.WeatherRemoteDataSource
import com.example.weather.databinding.FragmentSettingBinding
import com.example.weather.map_ui.MapActivity
import com.example.weather.util.SettingsPreferencesHelper
import java.util.Locale

class SettingFragment : Fragment() {
    private val MAP_PICKER_REQUEST_CODE = 1

    lateinit var settingViewModelFactory: SettingViewModelFactory
    lateinit var binding:FragmentSettingBinding
    lateinit var settingViewModel: SettingViewModel

    private var isWindUnitUpdate = false
    private var isTempUnitUpdate = false

    private var savedLocation:String = ""
    private var savedLanguage:String = ""
    private var savedTempUnit:String = ""
    private var savedWindSpeedUnit:String = ""

    override fun onStart() {
        super.onStart()

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
       binding = FragmentSettingBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        settingViewModelFactory = SettingViewModelFactory(
            WeatherRepository.getRepository(WeatherRemoteDataSource,
            SettingsPreferencesHelper(requireContext())
        ))
        settingViewModel = ViewModelProvider(this,settingViewModelFactory).get(SettingViewModel::class.java)



        loadLatestStatus()
        setButtonListeners()
    }
    private fun setButtonListeners(){
        binding.apply {
            LangRadioGroup.setOnCheckedChangeListener{  group, checkedId ->
                val lang:String = when(checkedId){
                    R.id.radioArabic -> "ar"
                    R.id.radioEnglish -> "en"
                    R.id.radioDefault -> "default"
                    else -> "en"
                }
                setLocale(lang)
            }

            tempRadioGroup.setOnCheckedChangeListener { _, checkedId ->
                if (!isWindUnitUpdate) {
                    val selectedTempUnit: String = when (checkedId) {
                        R.id.radioCelesius -> "metric"
                        R.id.radioKelvin -> "standard" // Kelvin
                        R.id.radiofahr -> "imperial" // Fahrenheit
                        else -> "metric"
                    }
                    settingViewModel.saveTemperatureUnit(selectedTempUnit)

                    isTempUnitUpdate = true
                    if (selectedTempUnit == "imperial") {
                        binding.windRadioGroup.check(R.id.radioImp)
                    } else {
                        binding.windRadioGroup.check(R.id.radiometeric)
                    }
                    isTempUnitUpdate = false
                }
            }

            windRadioGroup.setOnCheckedChangeListener{ group, checkId ->
                if(!isTempUnitUpdate) {
                    val result: String = when (checkId) {
                        R.id.radiometeric -> "metric"
                        R.id.radioImp -> "imperial"
                        else -> "metric"
                    }
                    settingViewModel.saveWindSpeedUnit(result) // m/sec
                    isWindUnitUpdate = true
                    if (tempRadioGroup.checkedRadioButtonId == R.id.radiofahr && result == "metric") {
                        settingViewModel.saveTemperatureUnit("standard")
                        binding.tempRadioGroup.check(R.id.radioKelvin)
                    } else {
                        settingViewModel.saveTemperatureUnit(result)
                        if (result == "imperial") {binding.tempRadioGroup.check(R.id.radiofahr)}
                        else {binding.tempRadioGroup.check(R.id.radioCelesius)}
                    }
                    isWindUnitUpdate = false
                }

            }

            locRadioGroup.setOnCheckedChangeListener{ group,checkId ->
                when(checkId){
                    R.id.radioGps -> settingViewModel.saveLocationStatus("gps")
                    R.id.radioMap -> {
                        settingViewModel.saveLocationStatus("map")
                        openMap()
                    }
                }
            }
        }
    }
    private fun loadLatestStatus(){
        savedLanguage = settingViewModel.getLanguage()
        savedTempUnit = settingViewModel.getTemperatureUnit()
        savedLocation = settingViewModel.getLocationStatus()
        savedWindSpeedUnit = settingViewModel.getWindSpeedUnit()
        updateButtonsStatus()
        //setLocale(savedLanguage)
    }
    private fun setLocale(languageCode: String) {
        val systemLanguage = Resources.getSystem().configuration.locales[0].language //fr
        println(systemLanguage)
        val targetLanguage = when (languageCode) {
            "default" -> systemLanguage //fr
            else -> languageCode //ar,en
        }

        val currentLocale = Locale.getDefault().language
        if (targetLanguage.isNotEmpty() && currentLocale != targetLanguage) {
            val locale = Locale(targetLanguage)
            Locale.setDefault(locale)

            val config = Configuration()
            config.setLocale(locale)

            // Update the configuration for the current resources
            requireActivity().resources.updateConfiguration(config, requireActivity().resources.displayMetrics)

            settingViewModel.saveLanguage(targetLanguage)

            // Restart the activity to apply the language change
            requireActivity().recreate()
        }
    }
    private fun updateButtonsStatus()   {
        binding.LangRadioGroup.check(
            when (savedLanguage) {
                "ar" -> R.id.radioArabic
                "en" -> R.id.radioEnglish
                else -> R.id.radioDefault
            }
        )

        binding.tempRadioGroup.check(
            when (savedTempUnit) {
                "metric" -> R.id.radioCelesius
                "standerd" -> R.id.radioKelvin
                "imperial" -> R.id.radiofahr
                else -> R.id.radioCelesius
            }
        )

        binding.locRadioGroup.check(
            when (savedLocation) {
                "gps" -> R.id.radioGps
                "map" -> R.id.radioMap
                else -> R.id.radioGps
            }
        )
        binding.windRadioGroup.check(
            when (savedWindSpeedUnit) {
                "metric" -> R.id.radiometeric
                "imperial" -> R.id.radioImp
                else -> R.id.radiometeric
            }
        )
    }
    //openMap
    fun openMap() {
        val intent = Intent(requireContext(), MapActivity::class.java)
        startActivityForResult(intent, MAP_PICKER_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == MAP_PICKER_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            val latitude = data?.getDoubleExtra("latitude", 0.0) ?: 0.0
            val longitude = data?.getDoubleExtra("longitude", 0.0) ?: 0.0
            println("$latitude , $longitude")
            settingViewModel.saveLocation(longitude,latitude)
        }
    }
}