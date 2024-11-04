package com.example.weather.map_ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.weather.R
import com.example.weather.databinding.ActivityMapBinding
import com.example.weather.util.SettingsPreferencesHelper
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker

class MapActivity : AppCompatActivity() {
    lateinit var binding:ActivityMapBinding
    lateinit var marker: Marker
    private var selectedGeoPoint: GeoPoint? = null
    lateinit var settingPreference:SettingsPreferencesHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        settingPreference = SettingsPreferencesHelper(this)
        //First load configuration saved in sharedPreference
        Configuration.getInstance().load(applicationContext, applicationContext.getSharedPreferences("osmdroid", MODE_PRIVATE))
        binding.map.apply {
            setMultiTouchControls(true)
            controller.setZoom(12.0)
            controller.setCenter(GeoPoint(settingPreference.latitude,settingPreference.longitude))
        }
        marker = Marker(binding.map)
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        binding.map.overlays.add(marker)

        // Add MapEventsOverlay to detect taps
        val mapEventsOverlay = MapEventsOverlay(object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                // Update marker position on tap
                marker.position = p
                selectedGeoPoint = p
                binding.map.invalidate() // Refresh map display
                return true
            }

            override fun longPressHelper(p: GeoPoint): Boolean {
                return false
            }
        })
        binding.map.overlays.add(mapEventsOverlay)
        binding.selectLocationButton.setOnClickListener {
            selectedGeoPoint?.let {
                val resultIntent = Intent().apply {
                    putExtra("latitude", it.latitude)
                    putExtra("longitude", it.longitude)
                }
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }
        }



    }

    override fun onResume() {
        super.onResume()
        binding.map.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.map.onPause()
    }
}