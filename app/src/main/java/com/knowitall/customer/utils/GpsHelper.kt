package com.clipitc.ClipitcNewMobile.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.location.LocationProvider
import android.os.Bundle
import androidx.core.app.ActivityCompat


class GpsHelper(private val activity: Activity, private val toInvoke: (() -> Unit)? = null) : LocationListener {

    private var locationManager: LocationManager? = null
    private var locationProvider: String? = null
    var currentLocation: Location? = null

    companion object {
        private const val ONE_MINUTE = 60 * 1000
        private const val RC_LAST_LOCATION_PERMISSION_CHECK = 1079
    }

    init {
        initializeLocationManager()
    }

    private fun startRequestingLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, ONE_MINUTE.toLong(), 1f, this)
    }

    private fun stopRequestingLocationUpdates() {
        locationManager?.removeUpdates(this)
    }

    private fun requestLocationPermission(requestCode: Int): Boolean {
        var isPermit = true
        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION),
                requestCode
            )
            isPermit = true
        }
        return isPermit
    }

    override fun onLocationChanged(location: Location) {
        currentLocation = location
        toInvoke?.invoke()
    }

    override fun onProviderDisabled(provider: String) {}

    override fun onProviderEnabled(provider: String) {}

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle?) {
        if (status == LocationProvider.OUT_OF_SERVICE) {
            stopRequestingLocationUpdates()
        }
    }

    private fun initializeLocationManager() {
        locationManager = activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager?

        val criteriaForLocationService = Criteria().apply {
            accuracy = Criteria.NO_REQUIREMENT
        }
        val acceptableLocationProviders =
            locationManager?.getProviders(criteriaForLocationService, true)
        locationProvider = acceptableLocationProviders?.firstOrNull()
    }

    fun getLocation() :Location {
        try {
            if (requestLocationPermission(RC_LAST_LOCATION_PERMISSION_CHECK)) {
                startRequestingLocationUpdates()
                val criteria = Criteria().apply {
                    powerRequirement = Criteria.POWER_MEDIUM
                }
                val bestProvider = locationManager?.getBestProvider(criteria, true)
                var location = getLastKnownLocation()
                if (location == null) {
                    if (requestLocationPermission(RC_LAST_LOCATION_PERMISSION_CHECK)) {
                        location = getLastKnownLocation()
                        stopRequestingLocationUpdates()
                    }
                } else {
                    stopRequestingLocationUpdates()
                }
                if (location != null){
                    return location
                }
            }
        } catch (ex: Exception) {
        }
        return Location(Context.LOCATION_SERVICE).apply {
            longitude = 0.0
            latitude = 0.0
        }
    }

    private fun getLastKnownLocation(): Location? {
        val providers = locationManager?.getProviders(true)
        var bestLocation: Location? = null
        providers?.forEach { provider ->
            if (ActivityCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return null
            }
            val l = locationManager?.getLastKnownLocation(provider)
            if (l != null && (bestLocation == null || l.accuracy < bestLocation!!.accuracy)) {
                bestLocation = l
            }
        }
        return bestLocation
    }
}