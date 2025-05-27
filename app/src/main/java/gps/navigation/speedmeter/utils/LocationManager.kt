package gps.navigation.speedmeter.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class LocationManager(private val context: Context) {

    private var fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    interface LocationListener {
        fun onLocationUpdated(location: Location)
        fun onLocationPermissionDenied()
    }

    suspend fun getCurrentLocation(): Location {
        return if (checkLocationPermission()) {
            try {
                val locationResult = fusedLocationClient.awaitLastLocation()
                locationResult ?: fusedLocationClient.awaitNewLocation()
            } catch (e: SecurityException) {
                throw e
            }
        } else {
            throw SecurityException("Location permission denied")
        }
    }

    @SuppressLint("MissingPermission")
    private suspend fun FusedLocationProviderClient.awaitLastLocation(): Location? {
        return suspendCoroutine { continuation ->
            lastLocation.addOnSuccessListener { location ->
                continuation.resume(location)
            }.addOnFailureListener { exception ->
                continuation.resume(null)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private suspend fun FusedLocationProviderClient.awaitNewLocation(): Location {
        return suspendCancellableCoroutine { continuation ->
            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    val lastLocation: Location = locationResult.lastLocation!!
                    continuation.resume(lastLocation) {
                        // This block will be executed if the coroutine is cancelled
                        removeLocationUpdates(this)
                    }
                }
            }

            val locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setNumUpdates(1)

            requestLocationUpdates(
                locationRequest,
                locationCallback,
                null
            )

            continuation.invokeOnCancellation {
                removeLocationUpdates(locationCallback)
            }
        }
    }

    private fun checkLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
}