package com.example.weather.util

import android.Manifest
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.os.Looper
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import com.example.weather.home_screen.CallBackForGPSCoord
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority


//There is a bug here I doon't why but I thimk it's because I don't understand how does it work
// To solve it first u need to understand how does it work
// Secondly I need to print before I enter to home and check on the location type
class LocationHelper(val context: Context) {
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    val My_LOCATION_PERMISSION_ID = 5005
    fun getActualLocation(activity: Activity, callback: CallBackForGPSCoord) {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                getFreshLocation(callback)
            } else {
                enableLocationServices()
            }
        } else {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(
                    ACCESS_COARSE_LOCATION,
                    ACCESS_FINE_LOCATION
                ),
                My_LOCATION_PERMISSION_ID
            )
        }
    }

    fun checkPermissions(): Boolean {
        var result = false
        if ((ContextCompat.checkSelfPermission(
                context,
                ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED)
            ||
            (ContextCompat.checkSelfPermission(
                context,
                ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED)
        ) {
            result = true;
        }
        return result
    }

    @SuppressLint("MissingPermission")
    fun getFreshLocation(callback: CallBackForGPSCoord) {
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(context)

        fusedLocationProviderClient.requestLocationUpdates(
            LocationRequest.Builder(0).apply {
                setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            }.build(),
            object : LocationCallback() {
                override fun onLocationResult(location: LocationResult) {
                    super.onLocationResult(location)
                    val latitude = location.lastLocation?.latitude
                    val longitude = location.lastLocation?.longitude

                    if (latitude != null && longitude != null) {
                        // Use the callback to return the location
                        callback.onLocationResult(latitude, longitude)
                    }
                    fusedLocationProviderClient.removeLocationUpdates(this)
                }
            },
            Looper.myLooper()
        )
    }


    fun enableLocationServices() {
//        Toast.makeText(this,"Turn On Loction",Toast.LENGTH_SHORT).show()
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        context.startActivity(intent)
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

}