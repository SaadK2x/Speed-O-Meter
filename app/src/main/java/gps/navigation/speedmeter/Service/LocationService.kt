package gps.navigation.speedmeter.Service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.Binder
import android.os.Bundle
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import gps.navigation.speedmeter.activities.MainActivity.Companion.isPaused
import gps.navigation.speedmeter.sharedprefrences.SharedPreferenceHelperClass
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.util.Locale


class LocationService : Service(), LocationListener, GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener {
    var mLocationRequest: LocationRequest? = null
    var mGoogleApiClient: GoogleApiClient? = null
    var mCurrentLocation: Location? = null
    var lStart: Location? = null
    var lEnd: Location? = null
    var speed = 0f
    private val mBinder: IBinder = LocalBinder()
    private var isServiceRunning: Boolean = false
    private var elapsedSeconds = 0L
    var sp: SharedPreferenceHelperClass? = null

    override fun onBind(intent: Intent): IBinder? {
        createLocationRequest()
        Log.d("TAG_REPO", "onBind: ")
        isServiceRunning = true
        mGoogleApiClient = GoogleApiClient.Builder(this)
            .addApi(LocationServices.API)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .build()
        sp = SharedPreferenceHelperClass(this)
        mGoogleApiClient!!.connect()
        startTimer()
        return mBinder
    }


    protected fun createLocationRequest() {
        mLocationRequest = LocationRequest()
        mLocationRequest!!.interval =
            INTERVAL
        mLocationRequest!!.fastestInterval =
            FASTEST_INTERVAL
        mLocationRequest!!.priority =
            LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d("TAG_REPO", "onStartCommand: ")
        return super.onStartCommand(intent, flags, startId)

    }

    @SuppressLint("MissingPermission")
    override fun onConnected(bundle: Bundle?) {
        startLocationUpdates()
    }

    protected fun stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
            mGoogleApiClient!!, this
        )
        distance = 0.0
    }

    private fun startLocationUpdates() {
        if (!isPaused) {
            try {
                LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient!!,
                    mLocationRequest!!,
                    this
                )
            } catch (e: SecurityException) {
                // Handle permission-related exception
            }
        }
    }

    override fun onConnectionSuspended(i: Int) {}
    override fun onLocationChanged(location: Location) {
        if (!isPaused) {
            mCurrentLocation = location
            if (lStart == null) {
                lStart = mCurrentLocation
                lEnd = mCurrentLocation
            } else lEnd = mCurrentLocation

            val constIntent = Intent("ACTION_CONSTRAINTS_UPDATE")
            constIntent.putExtra("Lat", lEnd?.latitude)
            constIntent.putExtra("Lng", lEnd?.longitude)
            sendBroadcast(constIntent)
            //Calling the method below updates the  live values of distance and speed to the TextViews.
            updateUI()
            //calculating the speed with getSpeed method it returns speed in m/s so we are converting it into kmph
            speed = location.speed * 18 / 5
        }
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {}
    inner class LocalBinder : Binder() {
        val service: gps.navigation.speedmeter.Service.LocationService
            get() = this@LocationService
    }

    //The live feed of Distance and Speed are being set in the method below .
    private fun updateUI() {
        distance += lStart!!.distanceTo(
            lEnd!!
        ) / 1000.00

        if (speed > maxSpeed) {
            maxSpeed = speed.toDouble()
            //  SpeedometerStreetView.maxSpeed.setText(new DecimalFormat("#.#").format(maxSpeed) + "km/s");
        }
        if (speed <= minspeed) {
            minspeed = speed.toDouble()
            //  SpeedometerStreetView.minSpeed.setText(new DecimalFormat("#.#").format(minspeed) + "km/s");
        }
        avgSpeed =
            (maxSpeed + minspeed) / 2
        // SpeedometerStreetView.avgSpeed.setText(new DecimalFormat("#.#").format(avgSpeed) + "km/s");
        lStart = lEnd
    }

    override fun onUnbind(intent: Intent): Boolean {
        stopLocationUpdates()
        if (mGoogleApiClient!!.isConnected) mGoogleApiClient!!.disconnect()
        lStart = null
        lEnd = null
        distance = 0.0
        isServiceRunning = false
        maxSpeed = 0.0
        minspeed = 0.0
        avgSpeed = 0.0
        elapsedSeconds = 0L
        countdownTimer?.cancel()
        return super.onUnbind(intent)
    }

    companion object {
        private const val INTERVAL = (1000 * 2).toLong()
        private const val FASTEST_INTERVAL = (1000 * 1).toLong()
        var distance = 0.0
        var maxSpeed = 0.0
        var minspeed = 0.0
        var avgSpeed = 0.0
        var countdownTimer: CountDownTimer? = null
    }

    private fun startTimer() {
        countdownTimer =
            object : CountDownTimer(Long.MAX_VALUE, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    if (!isPaused) {
                        val getPrevious = sp!!.getBoolean("getPrevious", false)
                        val newDistance =
                            if (getPrevious) {
                                Log.d("TAG__", "distance: ${sp!!.getFloat("distance").toDouble()}")
                                sp!!.getFloat("distance").toDouble()
                            } else 0.0

                        distance += newDistance
                        val newTime = if (getPrevious) {
                            sp!!.putBoolean("getPrevious", false)
                            sp!!.getLong("time")
                        } else {
                            0
                        }
                        elapsedSeconds = newTime + (elapsedSeconds + 1)

                        sp!!.putLong("time", elapsedSeconds)
                        val hours = elapsedSeconds / 3600
                        val minutes = (elapsedSeconds % 3600) / 60
                        val seconds = elapsedSeconds % 60
                        val timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds)
                        if (speed > 0.0) {

                            Log.d("TAG__", "VAr distance: ${newDistance}")

                            Log.d("TAG__", "new distance: $distance")
                            val speedIntent = Intent("ACTION_SPEED_UPDATE")
                            val finalDistance = parseLocaleNumber(
                                distance.toString(),
                                Locale("en")
                            )
                            sp!!.putFloat("distance", finalDistance)
                            speedIntent.putExtra(
                                "speed",
                                DecimalFormat(
                                    "#.#",
                                    DecimalFormatSymbols(Locale.US)
                                ).format(speed.toDouble()).toFloat()
                            )
                            speedIntent.putExtra("time", timeString)

                            speedIntent.putExtra(
                                "distance",
                                DecimalFormat("#.#", DecimalFormatSymbols(Locale.US)).format(
                                    finalDistance
                                )
                            )
                            val finalHour = (elapsedSeconds / 3600.000000000000000)


                            if (finalDistance > 0.0) {
                                val calAvg = finalDistance / finalHour.toFloat()
                                val finalAVG = parseLocaleNumber(
                                    calAvg.toString(),
                                    Locale("en")
                                )
                                speedIntent.putExtra(
                                    "avgSpeed",
                                    DecimalFormat("#.#", DecimalFormatSymbols(Locale.US)).format(
                                        finalAVG
                                    )
                                )
                            } else {
                                speedIntent.putExtra("avgSpeed", "0")
                            }
                            sendBroadcast(speedIntent)
                        } else {
                            val speedIntent = Intent("ACTION_SPEED_UPDATE")
                            val finalDistance = parseLocaleNumber(
                                distance.toString(),
                                Locale("en")
                            )
                            speedIntent.putExtra("speed", 0.0f)
                            speedIntent.putExtra("time", timeString)
                            speedIntent.putExtra(
                                "distance",
                                DecimalFormat("#.#", DecimalFormatSymbols(Locale.US)).format(
                                    finalDistance
                                )
                            )
                            val finalHour = (elapsedSeconds / 3600.000000000000000)

                            if (finalDistance > 0.0) {
                                val calAvg = finalDistance / finalHour.toFloat()
                                val finalAVG = parseLocaleNumber(
                                    calAvg.toString(),
                                    Locale("en")
                                )
                                speedIntent.putExtra(
                                    "avgSpeed",
                                    DecimalFormat("#.#", DecimalFormatSymbols(Locale.US)).format(
                                        finalAVG
                                    )
                                )
                            } else {
                                speedIntent.putExtra("avgSpeed", "0")
                            }
                            sendBroadcast(speedIntent)
                        }
                    }

                }

                override fun onFinish() {

                }
            }
        countdownTimer?.start()
    }

    fun parseLocaleNumber(input: String, locale: Locale = Locale.getDefault()): Float {
        val format = NumberFormat.getInstance(locale)
        return format.parse(input)?.toFloat() ?: 0f
    }
}