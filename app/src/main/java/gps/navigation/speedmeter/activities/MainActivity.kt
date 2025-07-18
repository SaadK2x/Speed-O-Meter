package gps.navigation.speedmeter.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RemoteViews
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import gps.navigation.speedmeter.R
import gps.navigation.speedmeter.Service.LocationService
import gps.navigation.speedmeter.ads.SpeedMeterLoadAds
import gps.navigation.speedmeter.ads.SpeedMeterShowAds.directAdsSpecificModulesSquareXNavigation
import gps.navigation.speedmeter.databinding.ActivityMainBinding
import gps.navigation.speedmeter.fragments.DigitalFragment
import gps.navigation.speedmeter.fragments.GaugeFragment
import gps.navigation.speedmeter.fragments.MapsFragment
import gps.navigation.speedmeter.sharedprefrences.SharedPreferenceHelperClass
import gps.navigation.speedmeter.utils.Constants
import gps.navigation.speedmeter.utils.Constants.currentAddress
import gps.navigation.speedmeter.utils.Constants.currentLatitude
import gps.navigation.speedmeter.utils.Constants.currentLongitude
import gps.navigation.speedmeter.utils.Constants.getCompleteAddress
import gps.navigation.speedmeter.utils.Constants.mediaPlayer
import gps.navigation.speedmeter.utils.Constants.vibratePhone
import gps.navigation.speedmeter.utils.LocationManager
import gps.navigation.speedmeter.utils.SectionPageAdapter
import gps.navigation.speedmeter.utils.checkForAppUpdates
import gps.navigation.speedmeter.utils.setupAppUpdateListeners
import gps.navigation.speedmeter.utils.unregisterAppUpdateListeners
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    val LOCATION_PERMISSION_REQUEST_CODE = 1
    val NOTIFICATION_ID = 1
    private val PERMISSION_REQUEST_LOCATION = 100
    var sharedPrefrences: SharedPreferenceHelperClass? = null
    var builder: NotificationCompat.Builder? = null
    var maxSpeed: String = "0"
    var tablyout: TabLayout? = null
    private lateinit var appUpdateManager: AppUpdateManager

    companion object {
        var status = false
        var startTime: Long = 0
        var endTime: Long = 0
        var myService: LocationService? = null
        var isPaused = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        squareXGPSBannerAdsSmall()
        sharedPrefrences = SharedPreferenceHelperClass(this)
        SpeedMeterLoadAds.preLoadAdsLiveEarth(this)
        tablyout = binding.tabs
        setupViewPager(binding.container)
        binding.tabs.setupWithViewPager(binding.container)
        binding.tabs.getTabAt(1)!!.select()
        binding.container.offscreenPageLimit = 3
        appUpdateManager = AppUpdateManagerFactory.create(this)
        checkForAppUpdates(appUpdateManager)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                pushNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(
                speedUpdateReceiver,
                IntentFilter("ACTION_SPEED_UPDATE"),
                Context.RECEIVER_EXPORTED
            )
        } else {
            registerReceiver(speedUpdateReceiver, IntentFilter("ACTION_SPEED_UPDATE"))
        }
        if (checkLocationPermission() && checkCourseLocationPermission()) {
            val locationManager = LocationManager(this)
            GlobalScope.launch(Dispatchers.Main) {
                try {
                    val location = locationManager.getCurrentLocation()
                    currentLatitude = location.latitude
                    currentLongitude = location.longitude
                    CoroutineScope(Dispatchers.IO).launch {
                        val addressData = getCompleteAddress(
                            currentLatitude, currentLongitude, this@MainActivity
                        )
                        currentAddress = addressData?.completeAddress ?: "Current Location"

                    }
                } catch (e: SecurityException) {
                    Log.d("LAT_TAG", "Exception: ${e.localizedMessage}")
                }
            }
        } else {
            requestLocationPermission()
        }

        binding.rotateBtn.setOnClickListener {
            if (!isPaused) {
                val screenRotate = sharedPrefrences!!.getBoolean("screenRotate", false)
                sharedPrefrences!!.putBoolean("callForRotate", true)
                if (screenRotate) {
                    sharedPrefrences!!.putBoolean("screenRotate", false)
                    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

                } else {
                    sharedPrefrences!!.putBoolean("screenRotate", true)
                    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                }
            } else {
                Toast.makeText(
                    this,
                    "Resume the Speedometer then press Rotate Button",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        binding.settingsBtn.setOnClickListener {
            val intent=(Intent(this, SettingsActivity::class.java))
            directAdsSpecificModulesSquareXNavigation(this,
                SpeedMeterLoadAds.admobInterstitialNav,intent)
        }
        binding.hudBtn.setOnClickListener {
            binding.layoutHUD.visibility = View.VISIBLE
        }
        binding.hudRevertBtn.setOnClickListener {
            binding.layoutHUD.visibility = View.GONE
        }

        binding.playBtn.setOnClickListener {
            if (!isPaused) {
                if (!Constants.isStart) {
                    if (isLocationEnabled(this)) {
                        if (ContextCompat.checkSelfPermission(
                                this, Manifest.permission.ACCESS_FINE_LOCATION
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            Log.d("TAG__", "bindService: Not Allowed")
                            ActivityCompat.requestPermissions(
                                this,
                                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                                PERMISSION_REQUEST_LOCATION
                            )
                        } else {
                            Log.d("TAG__", "bindService: Starting")
                            Constants.isStart = true
                            bindService()
                        }
                    } else {
                        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    }
                } else {
                    Log.d("TAG__", "bindService: Stoping")
                    Constants.isStart = false
                    sharedPrefrences!!.putBoolean("isStart", false)
                    binding.playBtn.visibility = View.GONE
                    unbindService("Stop", false)
                }
            } else {
                Toast.makeText(
                    this,
                    "Resume the Speedometer then press Stop button",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        binding.pauseBtn.setOnClickListener {
            if (!isPaused) {
                isPaused = true
                sharedPrefrences!!.putBoolean("isPaused", true)
                val speedIntent = Intent("ACTION_PAUSE_UPDATE")
                speedIntent.putExtra("isPause", isPaused)
                sendBroadcast(speedIntent)
            } else {
                isPaused = false
                sharedPrefrences!!.putBoolean("isPaused", false)
                val speedIntent = Intent("ACTION_PAUSE_UPDATE")
                speedIntent.putExtra("isPause", isPaused)
                sendBroadcast(speedIntent)
            }
        }

        binding.resetBtn.setOnClickListener {
            if (!isPaused) {
                if (Constants.isStart) {
                    val speedIntent = Intent("ACTION_RESET_UPDATE")
                    sendBroadcast(speedIntent)
                    unbindService("Reset", false)
                    bindService()
                }
            } else {
                Toast.makeText(
                    this,
                    "Resume the Speedometer then press Stop button",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

    private fun squareXGPSBannerAdsSmall() {
        val adContainer = findViewById<LinearLayout>(R.id.adContainer)
        val smallAd = findViewById<LinearLayout>(R.id.smallAd)
        SpeedMeterLoadAds.loadBanner(
            adContainer, smallAd, this
        )
    }

    val digitalSC: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder: gps.navigation.speedmeter.Service.LocationService.LocalBinder =
                service as gps.navigation.speedmeter.Service.LocationService.LocalBinder
            myService = binder.service
            status = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            status = false
        }
    }
    private val pushNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            if (!checkLocationPermission() && !checkCourseLocationPermission()) {
                requestLocationPermission()
            }
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
        }
    }

    fun bindService() {
        if (status) return
        sendBroadCast("Start", false)
        val i = Intent(this, gps.navigation.speedmeter.Service.LocationService::class.java)
        bindService(i, digitalSC, BIND_AUTO_CREATE)
        status = true
        startTime = System.currentTimeMillis()
        sharedPrefrences!!.putBoolean("isStart", true)
    }

    fun sendBroadCast(value: String, isDestroying: Boolean) {
        val speedIntent = Intent("ACTION_START_UPDATE")
        speedIntent.putExtra("isStart", value)
        speedIntent.putExtra("isDestroying", isDestroying)
        sendBroadcast(speedIntent)
    }

    fun unbindService(value: String, isDestroying: Boolean) {
        if (!status) return
        try {
            sendBroadCast(value, isDestroying)
            unbindService(digitalSC)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace() // Or log it
        }
        status = false
    }

    private fun isLocationEnabled(mContext: Context): Boolean {
        val locationManager =
            mContext.getSystemService(LOCATION_SERVICE) as android.location.LocationManager
        return locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d("LAT_TAG", "onRequestPermissionsResult: ${requestCode}")
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val locationManager = LocationManager(this)
                GlobalScope.launch(Dispatchers.Main) {
                    try {
                        val location = locationManager.getCurrentLocation()

                        Log.d("LAT_TAG", "onRequestPermissionsResult: ${location.latitude}")
                        currentLatitude = location.latitude
                        currentLongitude = location.longitude

                        Constants.observerLAT.value = location.latitude
                        Constants.observerLNG.value = location.longitude
                        CoroutineScope(Dispatchers.IO).launch {
                            val addressData = getCompleteAddress(
                                currentLatitude, currentLongitude, this@MainActivity
                            )
                            currentAddress = addressData?.completeAddress ?: "Current Location"

                        }
                    } catch (e: SecurityException) {
                        Log.d(
                            "LAT_TAG",
                            "Exception onRequestPermissionsResult : ${e.localizedMessage}"
                        )
                    }
                }
            }
        }
    }

    private fun setupViewPager(viewPager: ViewPager) {
        try {
            val adapter = SectionPageAdapter(supportFragmentManager)
            adapter.addFragment(GaugeFragment(), getString(R.string.gauge))
            adapter.addFragment(DigitalFragment(), getString(R.string.digital))
            adapter.addFragment(MapsFragment(), getString(R.string.my_location))
            viewPager.adapter = adapter
        } catch (e: Exception) {
        }
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this, arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ), LOCATION_PERMISSION_REQUEST_CODE
        )
    }


    private fun checkLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this, android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun checkCourseLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun settingColors(color: String) {
        binding.tabs.setTabTextColors(getColor(R.color.grayColor), Color.parseColor(color))
        binding.tabs.setSelectedTabIndicatorColor(Color.parseColor(color))
        binding.shapeBG.backgroundTintList = ColorStateList.valueOf(Color.parseColor(color))
    }

    private val speedUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val speed = intent?.getFloatExtra("speed", 0.0f)
            binding.speedTV.text = (speed!!.toInt().toString())
            val distance = intent?.getStringExtra("distance")
            val avgSpeed = intent?.getStringExtra("avgSpeed")

            val limit = sharedPrefrences?.getInt("speedLimit")
            val vibrate = sharedPrefrences?.getBoolean("Vibration", false)
            val sound = sharedPrefrences?.getBoolean("SoundEffects", false)
            val notification = sharedPrefrences?.getBoolean("Notification", true)

            if (notification!!) {
                showNotification(this@MainActivity, speed ?: 0f, distance ?: "0", avgSpeed ?: "0")
            }

            if (maxSpeed.toFloat().toInt() < speed!!.toInt()) {
                when (sharedPrefrences?.getString("Unit", "KMH")) {
                    "KMH" -> {
                        maxSpeed = speed.toString()
                    }

                    "MPH" -> {

                        maxSpeed =
                            Constants.kmhToMph(maxSpeed.toFloat()).toInt().toString()

                    }

                    "KNOT" -> {
                        maxSpeed =
                            Constants.kmhToKnots(maxSpeed.toFloat()).toInt().toString()

                    }
                }
            }
            when (sharedPrefrences?.getString("Unit", "KMH")) {
                "KMH" -> {
                    if (speed!!.toInt() >= limit!! && vibrate!!) {
                        vibratePhone(this@MainActivity)

                    }
                    if (speed!!.toInt() >= limit!! && sound!!) {
                        mediaPlayer = MediaPlayer.create(this@MainActivity, R.raw.speed_limit)
                        mediaPlayer?.start()
                    } else {
                        if (mediaPlayer != null) {
                            mediaPlayer?.stop()
                        }
                    }
                }

                "MPH" -> {
                    if (Constants.kmhToMph(speed!!).toInt() >= Constants.kmhToMph(limit!!.toFloat())
                            .toInt() && vibrate!!
                    ) {
                        vibratePhone(this@MainActivity)
                    }
                    if (Constants.kmhToMph(speed!!).toInt() >= Constants.kmhToMph(limit!!.toFloat())
                            .toInt() && sound!!
                    ) {
                        mediaPlayer = MediaPlayer.create(this@MainActivity, R.raw.speed_limit)
                        mediaPlayer?.start()
                    } else {
                        if (mediaPlayer != null) {
                            mediaPlayer?.stop()
                        }
                    }
                }

                "KNOT" -> {
                    if (Constants.kmhToKnots(speed!!)
                            .toInt() >= Constants.kmhToKnots(limit!!.toFloat()).toInt() && vibrate!!
                    ) {
                        vibratePhone(this@MainActivity)
                    }
                    if (Constants.kmhToKnots(speed!!)
                            .toInt() >= Constants.kmhToKnots(limit!!.toFloat()).toInt() && sound!!
                    ) {
                        mediaPlayer = MediaPlayer.create(this@MainActivity, R.raw.speed_limit)
                        mediaPlayer?.start()
                    } else {
                        if (mediaPlayer != null) {
                            mediaPlayer?.stop()
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        setupAppUpdateListeners(appUpdateManager)
        binding.playBtn.visibility = View.VISIBLE
        val callForRotate = sharedPrefrences!!.getBoolean("callForRotate", false)
        val isStart = sharedPrefrences!!.getBoolean("isStart", false)
        Log.d("TAG_LL", "onResume callForRotate : $callForRotate.  $isStart")
        val unit = sharedPrefrences?.getString("Unit", "KMH")
        when (unit) {
            "KMH" -> {
                binding.unitTV.text = "Km/h"
            }

            "MPH" -> {
                binding.unitTV.text = "Mph"
            }

            "KNOT" -> {
                binding.unitTV.text = "Knots"
            }
        }
        if (callForRotate && isStart) {
            sharedPrefrences!!.putBoolean("callForRotate", false)
            Constants.isStart = true
            sharedPrefrences!!.putBoolean("getPrevious", true)
            bindService()
        }

        val screenRotate = sharedPrefrences!!.getBoolean("screenRotate", false)

        if (!screenRotate) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                window.setDecorFitsSystemWindows(true)
                window.insetsController?.show(WindowInsets.Type.statusBars())
            } else {
                window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            }
        }
        else {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                window.setDecorFitsSystemWindows(false)
                window.insetsController?.hide(WindowInsets.Type.statusBars())
            } else {
                window.setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
                )
            }
        }
        val sp = SharedPreferenceHelperClass(this)
        settingColors(sp.getString("AppColor", "#0DCF31"))
        Constants.setLocale(this, sp.getString("language", "en"))
        if (tablyout != null) {
            tablyout!!.getTabAt(0)?.text = getString(R.string.gauge)
            tablyout!!.getTabAt(1)?.text = getString(R.string.digital)
            tablyout!!.getTabAt(2)?.text = getString(R.string.my_location)
        }
    }

    @SuppressLint("NotificationPermission")
    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = "My Channel"
            val description = "Channel description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("channel_id", name, importance)
            channel.description = description
            val notificationManager = context.getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    @SuppressLint("MissingPermission")
    private fun showNotification(context: Context, speed: Float, distance: String, avg: String) {
        createNotificationChannel(context)
        val customView = notificationTextView(context, speed, distance, avg)
        // Build the notification
        builder = NotificationCompat.Builder(context, "channel_id")
            .setSmallIcon(R.drawable.speed_meter_notifi)
            .setContentTitle("My Notification")
            .setOnlyAlertOnce(true)
            .setContentText("This is a notification from my app")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCustomContentView(customView)
            .setCustomBigContentView(customView)

        // Show the notification
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(NOTIFICATION_ID, builder!!.build())
    }

    fun notificationTextView(
        context: Context,
        speed: Float,
        distance: String,
        avg: String
    ): RemoteViews {
        val customView = RemoteViews(context.packageName, R.layout.custom_notification_layout)
        val sp = SharedPreferenceHelperClass(context)
        val speedTV = R.id.speedNotifi
        val distanceTV = R.id.distanceNotifi
        val avgSpeedTV = R.id.avgSpeedNotifi
        val maxSpeedTV = R.id.maxSpeedNotifi

        val speedUnit = R.id.speedNotifiUnit
        val distanceUnit = R.id.distanceNotifiUnit
        val avgSpeedUnit = R.id.avgSpeedNotifiUnit
        val maxSpeedUnit = R.id.maxSpeedNotifiUnit
        when (sp.getString("Unit", "KMH")) {
            "KMH" -> {
                customView.setTextViewText(speedTV, speed.toInt().toString())
                customView.setTextViewText(distanceTV, distance)
                customView.setTextViewText(avgSpeedTV, avg)
                customView.setTextViewText(maxSpeedTV, maxSpeed.toFloat().toInt().toString())
                //Units
                customView.setTextViewText(speedUnit, "Km/h")
                customView.setTextViewText(distanceUnit, "km")
                customView.setTextViewText(avgSpeedUnit, "Km/h")
                customView.setTextViewText(maxSpeedUnit, "Km/h")
            }

            "MPH" -> {
                customView.setTextViewText(speedTV, Constants.kmhToMph(speed).toInt().toString())
                customView.setTextViewText(
                    distanceTV,
                    Constants.kmTomi(distance.toFloat()).toString()
                )
                customView.setTextViewText(avgSpeedTV, Constants.kmhToMph(avg.toFloat()).toString())
                customView.setTextViewText(
                    maxSpeedTV,
                    Constants.kmhToMph(maxSpeed.toFloat()).toInt().toString()
                )

                //Units
                customView.setTextViewText(speedUnit, "Mph")
                customView.setTextViewText(distanceUnit, "mi")
                customView.setTextViewText(avgSpeedUnit, "Mph")
                customView.setTextViewText(maxSpeedUnit, "Mph")
            }

            "KNOT" -> {
                customView.setTextViewText(speedTV, Constants.kmhToKnots(speed).toInt().toString())
                customView.setTextViewText(
                    distanceTV,
                    Constants.kmTonm(distance.toFloat()).toString()
                )
                customView.setTextViewText(
                    avgSpeedTV,
                    Constants.kmhToKnots(avg.toFloat()).toString()
                )
                customView.setTextViewText(
                    maxSpeedTV,
                    Constants.kmhToKnots(maxSpeed.toFloat()).toInt().toString()
                )
                //Units
                customView.setTextViewText(speedUnit, "Knots")
                customView.setTextViewText(distanceUnit, "nm")
                customView.setTextViewText(avgSpeedUnit, "Knots")
                customView.setTextViewText(maxSpeedUnit, "Knots")
            }
        }

        return customView
    }

    override fun onBackPressed() {
        exitDialog()
    }

    fun exitDialog() {
        val dialog = Dialog(this)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.requestWindowFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
        dialog.setContentView(R.layout.exit_dialouge)
        dialog.setCancelable(true)
        val titletxt = dialog.findViewById<TextView>(R.id.titletxt)
        val detailTV = dialog.findViewById<TextView>(R.id.detailTV)
        val yesBtn = dialog.findViewById<Button>(R.id.btnYes)
        val noBtn = dialog.findViewById<Button>(R.id.btnNo)
        titletxt.text = getString(R.string.exit_app)
        detailTV.text = getString(R.string.exit_details)
        yesBtn.text = getString(R.string.yes)
        noBtn.text = getString(R.string.no)
        yesBtn.setOnClickListener {
            Constants.isStart = false
            unbindService("Stop", true)
            dialog.dismiss()
            finish()
            finishAffinity()
        }
        noBtn.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    override fun onDestroy() {
        unregisterAppUpdateListeners(appUpdateManager)
        Constants.isStart = false
        unbindService("Stop", true)
        super.onDestroy()
    }
}