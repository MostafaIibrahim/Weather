package com.example.weather

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.weather.alert_screen.AlertFragment
import com.example.weather.data.remote_network.WeatherRemoteDataSource
import com.example.weather.databinding.ActivityMainBinding
import com.example.weather.favorite_screen.FavoriteFragment
import com.example.weather.home_screen.HomeFragment
import com.example.weather.setting_screen.SettingFragment
import com.example.weather.util.SettingsPreferencesHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale

/*
    *It could be modified to make smoother transation through this link
    *https://developer.android.com/guide/fragments/animate
 */
class MainActivity : AppCompatActivity() {
    lateinit var binding:ActivityMainBinding
    lateinit var homeFragment: HomeFragment
    lateinit var settingFragment: SettingFragment
    lateinit var alertFragment: AlertFragment
    lateinit var favoriteFragment: FavoriteFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.main)
        if(savedInstanceState == null){

            initFrags()
            val pref=SettingsPreferencesHelper(this)
            val savedLanguage = pref.getLang() ?: "en" // Default to English if no language is saved
            setLocale(savedLanguage)
            setCurrentFragment(homeFragment)
        }else {
            // Retrieve fragments from FragmentManager by their tag after configuration change
            homeFragment = supportFragmentManager.findFragmentByTag(HomeFragment::class.java.simpleName) as? HomeFragment ?: HomeFragment()
            favoriteFragment = supportFragmentManager.findFragmentByTag(FavoriteFragment::class.java.simpleName) as? FavoriteFragment ?: FavoriteFragment()
            settingFragment = supportFragmentManager.findFragmentByTag(SettingFragment::class.java.simpleName) as? SettingFragment ?: SettingFragment()
            alertFragment = supportFragmentManager.findFragmentByTag(AlertFragment::class.java.simpleName) as? AlertFragment ?: AlertFragment()
        }

        binding.bottomNavView.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.navHome->setCurrentFragment(homeFragment)
                R.id.navFav ->setCurrentFragment(favoriteFragment)
                R.id.navAlert ->setCurrentFragment(alertFragment)
                R.id.navSetting ->setCurrentFragment(settingFragment)
            }
            true
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }
    private fun initFrags(){
        homeFragment = HomeFragment()
        favoriteFragment= FavoriteFragment()
        settingFragment = SettingFragment()
        alertFragment = AlertFragment()
    }
    private fun setCurrentFragment(fragment: Fragment)=
        supportFragmentManager.beginTransaction().apply {
            setReorderingAllowed(true)
            replace(R.id.fragmentContainerView,fragment, fragment::class.java.simpleName)
            commit()
        }
    private fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = Configuration()
        config.setLocale(locale)

        // Apply the configuration globally
        resources.updateConfiguration(config, resources.displayMetrics)
    }
}

