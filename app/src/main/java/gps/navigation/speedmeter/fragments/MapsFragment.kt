package gps.navigation.speedmeter.fragments

import android.Manifest
import android.animation.ObjectAnimator
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Color.rgb
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.mapbox.common.MapboxOptions
import com.mapbox.geojson.Point
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.extension.observable.eventdata.MapLoadedEventData
import com.mapbox.maps.extension.style.atmosphere.generated.atmosphere
import com.mapbox.maps.extension.style.layers.properties.generated.ProjectionName
import com.mapbox.maps.extension.style.projection.generated.projection
import com.mapbox.maps.extension.style.sources.generated.rasterDemSource
import com.mapbox.maps.extension.style.style
import com.mapbox.maps.extension.style.terrain.generated.terrain
import com.mapbox.maps.plugin.annotation.AnnotationConfig
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotation
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPolylineAnnotationManager
import com.mapbox.maps.plugin.compass.compass
import com.mapbox.maps.plugin.delegates.listeners.OnMapLoadedListener
import gps.navigation.speedmeter.R
import gps.navigation.speedmeter.activities.HistoryDetail
import gps.navigation.speedmeter.ads.SpeedMeterLoadAds
import gps.navigation.speedmeter.ads.SpeedMeterShowAds
import gps.navigation.speedmeter.database.DatabaseBuilder
import gps.navigation.speedmeter.database.DatabaseHelperImpl
import gps.navigation.speedmeter.database.History
import gps.navigation.speedmeter.database.HistoryDatabase
import gps.navigation.speedmeter.database.RoutePoints
import gps.navigation.speedmeter.databinding.FragmentMapsBinding
import gps.navigation.speedmeter.sharedprefrences.SharedPreferenceHelperClass
import gps.navigation.speedmeter.utils.Constants
import gps.navigation.speedmeter.utils.Constants.bitmapFromDrawableRes
import gps.navigation.speedmeter.utils.Constants.directZoom
import gps.navigation.speedmeter.utils.Constants.isSATELLITE
import gps.navigation.speedmeter.utils.Constants.mapLoaded
import gps.navigation.speedmeter.utils.Constants.moveForward
import gps.navigation.speedmeter.utils.LocationManager
import gps.navigation.speedmeter.utils.Marker
import gps.navigation.speedmeter.utils.MarkerManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MapsFragment : Fragment(), OnMapLoadedListener {

    // var mMapView: MapView? = null
    private val binding: FragmentMapsBinding by lazy {
        FragmentMapsBinding.inflate(layoutInflater)
    }
    var historyDatabase: HistoryDatabase? = null
    var databaseHelper: DatabaseHelperImpl? = null

    //  var googleMap: GoogleMap? = null
    var previousLatLng: Point? = null
    private val polygonPoints = mutableListOf<Point>()

    //   private var polylineOptions = PolylineOptions()
    var previousMarker: Marker? = null
    var startMarker: Marker? = null
    var markerManger: MarkerManager? = null

    var sharedPrefrences: SharedPreferenceHelperClass? = null
    var dbID: Int = 0
    val LOCATION_PERMISSION_REQUEST_CODE = 1
    var polylineAnnotationManager: PolylineAnnotationManager? = null
    var points: ArrayList<Point> = ArrayList()
    var currentPolyline: PolylineAnnotation? = null
    var isResume = true
    var isStop = true
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        MapboxOptions.accessToken = Constants.keyMapbox

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            activity?.registerReceiver(
                startUpdateReceiver,
                IntentFilter("ACTION_START_UPDATE"),
                Context.RECEIVER_EXPORTED
            )
            activity?.registerReceiver(
                pauseUpdateReceiver,
                IntentFilter("ACTION_PAUSE_UPDATE"),
                Context.RECEIVER_EXPORTED
            )
            activity?.registerReceiver(
                resetUpdateReceiver,
                IntentFilter("ACTION_RESET_UPDATE"),
                Context.RECEIVER_EXPORTED
            )
        } else {
            activity?.registerReceiver(startUpdateReceiver, IntentFilter("ACTION_SPEED_UPDATE"))
            activity?.registerReceiver(pauseUpdateReceiver, IntentFilter("ACTION_PAUSE_UPDATE"))
            activity?.registerReceiver(resetUpdateReceiver, IntentFilter("ACTION_RESET_UPDATE"))
        }

        sharedPrefrences = SharedPreferenceHelperClass(requireContext())
        markerManger = MarkerManager(binding.mapView)


        binding.mapView.apply {
            mapboxMap.loadStyle(style(Style.SATELLITE) {
                compass.enabled = false
                +projection(ProjectionName.MERCATOR)
                +atmosphere {
                    color(rgb(0, 0, 0)) // Pink fog / lower atmosphere
                    highColor(rgb(0, 0, 0)) // Blue sky / upper atmosphere
                    horizonBlend(0.4)
                }
                +rasterDemSource("raster-dem") {
                    url("mapbox://mapbox.terrain-rgb")
                }
                +terrain("raster-dem")
            }
            )
        }
        Constants.observerLNG.observe(requireActivity()) {
            if (it != 0.0) {
                Log.d("LAT_TAGG", "onCreateView: longitude $it")
                Log.d("LAT_TAGG", "onCreateView: latitude ${Constants.observerLAT.value!!}")
                mapLoaded = true
                Constants.currentLongitude = it
                Constants.currentLatitude = Constants.observerLAT.value!!
                val sydney = Point.fromLngLat(it, Constants.observerLAT.value!!)
                previousMarker = Marker(
                    title = "",
                    snippet = "",
                    position = sydney,
                    icon = requireContext().bitmapFromDrawableRes(R.drawable.source_icon),
                )
                markerManger?.addMarker(previousMarker!!)
                Handler(Looper.getMainLooper()).postDelayed({
                    Constants.zoomInAnimation(
                        Constants.observerLAT.value!!,
                        it,
                        binding.mapView.mapboxMap,
                        true
                    )
                }, 2000)
            }


        }


        binding.mapView.mapboxMap.addOnMapLoadedListener(this)


        binding.mapStyle.setOnClickListener {
            if (!isSATELLITE) {
                binding.mapView.mapboxMap.loadStyle(Style.SATELLITE)
                isSATELLITE = true
            } else {
                binding.mapView.mapboxMap.loadStyle(Style.STANDARD)
                isSATELLITE = false
            }
        }
        binding.currentLoc.setOnClickListener {
            Constants.zoomInAnimation(
                Constants.currentLatitude,
                Constants.currentLongitude,
                binding.mapView.mapboxMap, true
            )
        }
        return binding.root
    }


    private val latlngUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val lat = intent?.getDoubleExtra("Lat", 0.0)
            val lng = intent?.getDoubleExtra("Lng", 0.0)
            val currentLatLng = Point.fromLngLat(lng!!, lat!!)
            val routeTracking = sharedPrefrences?.getBoolean("RouteTracking", true)
            if (routeTracking!!) {
                if (previousMarker != null) {
                    markerManger!!.removeMarker(previousMarker!!)
                }
                if (Constants.initialLat == 0.0 && Constants.initialLng == 0.0) {
                    Constants.initialLat = currentLatLng.latitude()
                    Constants.initialLng = currentLatLng.longitude()
                    if (previousMarker != null) {
                        markerManger!!.removeMarker(previousMarker!!)
                    }
                    if (activity != null) {
                        startMarker = Marker(
                            title = "",
                            snippet = "",
                            position = Point.fromLngLat(
                                currentLatLng.longitude(),
                                currentLatLng.latitude()
                            ),
                            icon = activity!!.bitmapFromDrawableRes(R.drawable.dest_icon)
                        )
                    }
                    markerManger!!.addMarker(startMarker!!)

                    points.add(
                        Point.fromLngLat(
                            currentLatLng.longitude(),
                            currentLatLng.latitude()
                        )
                    )
                }
                Constants.endLat = lat
                Constants.endLng = lng
                if (dbID == 0) {
                    CoroutineScope(Dispatchers.IO).launch {
                        dbID = if (points.isNotEmpty()) {
                            Log.d("TAG_DATA", "if: ${sharedPrefrences!!.getInt("dbID")}")
                            sharedPrefrences!!.getInt("dbID")
                        } else {
                            val id = Constants.getUniqueRandomInt(activity!!)
                            sharedPrefrences!!.putInt("dbID", id)
                            Log.d("TAG_DATA", "else : $id")
                            id

                        }
                    }
                }
                Constants.routePoints.add(RoutePoints(0, dbID, lat, lng))

                if (activity != null) {
                    previousMarker = Marker(
                        title = "",
                        snippet = "",
                        position = Point.fromLngLat(
                            currentLatLng.longitude(),
                            currentLatLng.latitude()
                        ),
                        icon = activity!!.bitmapFromDrawableRes(R.drawable.source_icon),
                    )
                }
                markerManger?.addMarker(previousMarker!!)
                if (previousLatLng != null) {
                    points.add(
                        Point.fromLngLat(
                            currentLatLng.longitude(),
                            currentLatLng.latitude()
                        )
                    )
                    setAddPolyline()
                }
                previousLatLng = currentLatLng
                sharedPrefrences!!.putPoints("mapPoints", points)
            }
        }
    }

    fun setupPolylineManager(mapView: MapView) {
        val annotationApi = mapView.annotations
        polylineAnnotationManager =
            annotationApi.createPolylineAnnotationManager(AnnotationConfig())
    }

    fun setAddPolyline() {
        val polylineOptions =
            PolylineAnnotationOptions().withPoints(points)
                .withLineColor(
                    Color.parseColor(
                        sharedPrefrences?.getString(
                            "AppColor",
                            "#ADFF00"
                        )
                    )
                ).withLineWidth(3.0)
        if (polylineAnnotationManager != null) {
            currentPolyline = polylineAnnotationManager!!.create(polylineOptions)
        }
    }

    private val speedUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val speed = intent?.getFloatExtra("speed", 0.0f)
            val time = intent?.getStringExtra("time")
            val distance = intent?.getStringExtra("distance")
            val avgSpeed = intent?.getStringExtra("avgSpeed")

            if (time != "")
                binding.time.text = time
            when (sharedPrefrences?.getString("Unit", "KMH")) {
                "KMH" -> {
                    binding.distance.text = distance
                    binding.avgSpeed.text = avgSpeed
                    binding.speedTV.text = (speed!!.toInt().toString())
                }

                "MPH" -> {
                    binding.distance.text =
                        Constants.kmTomi(distance!!.toFloat()).toInt().toString()
                    binding.avgSpeed.text =
                        Constants.kmhToMph(avgSpeed!!.toFloat()).toInt().toString()
                    binding.speedTV.text =
                        Constants.kmhToMph(speed!!).toInt().toString()
                }

                "KNOT" -> {
                    binding.distance.text =
                        Constants.kmTonm(distance!!.toFloat()).toInt().toString()
                    binding.avgSpeed.text =
                        Constants.kmhToKnots(avgSpeed!!.toFloat()).toInt().toString()
                    binding.speedTV.text =
                        Constants.kmhToKnots(speed!!).toInt().toString()
                }
            }


            val maxSpeed = binding.maxSpeed.text.toString().toFloat().toInt()
            if (maxSpeed < speed!!.toInt()) {
                when (sharedPrefrences?.getString("Unit", "KMH")) {
                    "KMH" -> {
                        binding.maxSpeed.text = speed.toString()
                    }

                    "MPH" -> {
                        binding.maxSpeed.text =
                            Constants.kmhToMph(maxSpeed.toFloat()).toInt().toString()
                    }

                    "KNOT" -> {
                        binding.maxSpeed.text =
                            Constants.kmhToKnots(maxSpeed.toFloat()).toInt().toString()
                    }
                }
            }
        }
    }
    private val startUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {

            val start = intent?.getStringExtra("isStart")
            val isDestroying = intent?.getBooleanExtra("isDestroying", false) ?: false
            Log.d("TAG__", "bindService Value : $start , $isDestroying")
            if (!isDestroying) {
                if (start == "Start") {
                    isStop = false
                    binding.playTV.text = context?.getString(R.string.stop) ?: "Stop"
                    Constants.viewScaling(0f, 1f, true, binding.pauseBtn)
                    Constants.viewScaling(0f, 1f, true, binding.resetBtn)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        activity?.registerReceiver(
                            speedUpdateReceiver,
                            IntentFilter("ACTION_SPEED_UPDATE"),
                            Context.RECEIVER_EXPORTED
                        )
                        activity?.registerReceiver(
                            latlngUpdateReceiver,
                            IntentFilter("ACTION_CONSTRAINTS_UPDATE"),
                            Context.RECEIVER_EXPORTED
                        )
                    } else {
                        activity?.registerReceiver(
                            speedUpdateReceiver,
                            IntentFilter("ACTION_SPEED_UPDATE")
                        )
                        activity?.registerReceiver(
                            latlngUpdateReceiver,
                            IntentFilter("ACTION_CONSTRAINTS_UPDATE")
                        )
                    }
                } else if (start == "Stop") {
                    isStop = true
                    activity?.unregisterReceiver(speedUpdateReceiver)
                    activity?.unregisterReceiver(latlngUpdateReceiver)

                    sharedPrefrences!!.putPoints("mapPoints", ArrayList())
                    binding.playBtn.isEnabled = false
                    moveForward.value = "No"
                    binding.playTV.text = context?.getString(R.string.saving) ?: "Saving"
                    binding.playProgress.visibility = View.VISIBLE
                    Constants.viewScaling(1f, 0f, false, binding.pauseBtn)
                    Constants.viewScaling(1f, 0f, false, binding.resetBtn)

                    Log.d("TAG_DB", "onSAving: $dbID")
                    when (sharedPrefrences?.getString("Unit", "KMH")) {
                        "KMH" -> {
                            CoroutineScope(Dispatchers.IO).launch {
                                val source = Constants.getCompleteAddress(
                                    Constants.initialLat,
                                    Constants.initialLng,
                                    requireActivity()
                                )
                                val start = source?.completeAddress ?: "Start Point"
                                val destination = Constants.getCompleteAddress(
                                    Constants.endLat,
                                    Constants.endLng,
                                    requireContext()
                                )
                                val end = destination?.completeAddress ?: "End Point"
                                val history = History(
                                    dbID,
                                    start,
                                    end,
                                    binding.distance.text.toString().toFloat(),
                                    binding.speedTV.text.toString().toFloat(),
                                    binding.avgSpeed.text.toString().toFloat(),
                                    binding.maxSpeed.text.toString().toFloat(),
                                    binding.time.text.toString(),
                                    Constants.getDate()
                                )

                                var hist: Long = 0
                                val route = try {
                                    hist = databaseHelper!!.insertHistory(history)
                                    databaseHelper!!.insertRoutePoints(Constants.routePoints)
                                } catch (e: Exception) {
                                    Log.d("TAG_DATA", "onReceive: ${e.localizedMessage} ")
                                    null
                                }
                                Log.d(
                                    "TAG_DATA",
                                    "onReceive: KMH history $hist and Route ${Constants.routePoints}"
                                )
                                if (route.isNullOrEmpty()) {

                                    Log.d("TAG_DATA", "onReceive: KMH Comes Empty")
                                    dbID += 1
                                    withContext(Dispatchers.Main) {
                                        binding.time.text = "00:00:00"
                                        binding.speedTV.text = "0"
                                        binding.distance.text = "0"
                                        binding.maxSpeed.text = "0"
                                        binding.avgSpeed.text = "0"
                                        moveToNext(history)
                                    }
                                } else {
                                    withContext(Dispatchers.Main) {
                                        Log.d("TAG_DATA", "onReceive: KMH Comes Not Empty")
                                        clearPolygon()
                                        previousMarker?.let { markerManger?.removeMarker(it) }

                                        //  markerManger?.removeMarker(previousMarker!!)
                                        startMarker?.let { markerManger?.removeMarker(it) }

                                        //    markerManger?.removeMarker(startMarker!!)
                                        val sydney =
                                            Point.fromLngLat(
                                                Constants.currentLongitude,
                                                Constants.currentLatitude
                                            )
                                        previousMarker = Marker(
                                            title = Constants.currentAddress,
                                            snippet = "",
                                            position = sydney,
                                            icon = requireContext().bitmapFromDrawableRes(R.drawable.source_icon),
                                        )
                                        markerManger!!.addMarker(previousMarker!!)
                                        Constants.zoomInAnimation(
                                            Constants.currentLatitude,
                                            Constants.currentLongitude,
                                            binding.mapView.mapboxMap,
                                            true
                                        )
                                        dbID += 1
                                        binding.time.text = "00:00:00"
                                        binding.speedTV.text = "0"
                                        binding.distance.text = "0"
                                        binding.maxSpeed.text = "0"
                                        binding.avgSpeed.text = "0"
                                        Constants.routePoints = ArrayList()
                                        Constants.initialLat = 0.0
                                        Constants.initialLng = 0.0
                                        //  previousLatLng = null
                                        //  polylineOptions = PolylineOptions()
                                        moveToNext(history)
                                    }

                                }
                            }
                        }

                        "MPH" -> {
                            CoroutineScope(Dispatchers.IO).launch {
                                val source = Constants.getCompleteAddress(
                                    Constants.initialLat,
                                    Constants.initialLng,
                                    requireContext()
                                )
                                val start = source?.completeAddress ?: "Start Point"
                                val destination = Constants.getCompleteAddress(
                                    Constants.endLat,
                                    Constants.endLng,
                                    requireContext()
                                )
                                val end = destination?.completeAddress ?: "End Point"
                                val history = History(
                                    dbID,
                                    start,
                                    end,
                                    Constants.miTokm(binding.distance.text.toString().toFloat()),
                                    Constants.mphToKmh(binding.speedTV.text.toString().toFloat()),
                                    Constants.mphToKmh(binding.avgSpeed.text.toString().toFloat()),
                                    Constants.mphToKmh(binding.maxSpeed.text.toString().toFloat()),
                                    binding.time.text.toString(),
                                    Constants.getDate()
                                )

                                var hist: Long = 0
                                val route = try {
                                    hist = databaseHelper!!.insertHistory(history)
                                    databaseHelper!!.insertRoutePoints(Constants.routePoints)
                                } catch (e: Exception) {
                                    null
                                }
                                Log.d("TAG_DATA", "onReceive:MPH history $hist and Route $route")
                                if (route.isNullOrEmpty()) {

                                    Log.d("TAG_DATA", "onReceive: MPH Comes Empty")

                                    dbID += 1
                                    withContext(Dispatchers.Main) {
                                        binding.time.text = "00:00:00"
                                        binding.speedTV.text = "0"
                                        binding.distance.text = "0"
                                        binding.maxSpeed.text = "0"
                                        binding.avgSpeed.text = "0"
                                        moveToNext(history)
                                    }
                                } else {
                                    withContext(Dispatchers.Main) {

                                        Log.d("TAG_DATA", "onReceive: MPH Comes  not Empty")
                                        clearPolygon()
                                        markerManger?.removeMarker(previousMarker!!)
                                        markerManger?.removeMarker(startMarker!!)
                                        val sydney =
                                            Point.fromLngLat(
                                                Constants.currentLongitude,
                                                Constants.currentLatitude
                                            )
                                        previousMarker = Marker(
                                            title = Constants.currentAddress,
                                            snippet = "",
                                            position = sydney,
                                            icon = requireContext().bitmapFromDrawableRes(R.drawable.source_icon),
                                        )
                                        markerManger!!.addMarker(previousMarker!!)
                                        Constants.zoomInAnimation(
                                            Constants.currentLatitude,
                                            Constants.currentLongitude,
                                            binding.mapView.mapboxMap,
                                            true
                                        )
                                        dbID += 1
                                        binding.time.text = "00:00:00"
                                        binding.speedTV.text = "0"
                                        binding.distance.text = "0"
                                        binding.maxSpeed.text = "0"
                                        binding.avgSpeed.text = "0"
                                        Constants.routePoints = ArrayList()
                                        Constants.initialLat = 0.0
                                        Constants.initialLng = 0.0
                                        //   previousLatLng = null
                                        //  polylineOptions = PolylineOptions()
                                        moveToNext(history)
                                    }
                                }
                            }
                        }

                        "KNOT" -> {
                            CoroutineScope(Dispatchers.IO).launch {
                                val source = Constants.getCompleteAddress(
                                    Constants.initialLat,
                                    Constants.initialLng,
                                    requireContext()
                                )
                                val start = source?.completeAddress ?: "Start Point"
                                val destination = Constants.getCompleteAddress(
                                    Constants.endLat,
                                    Constants.endLng,
                                    requireContext()
                                )
                                val end = destination?.completeAddress ?: "End Point"
                                val history = History(
                                    dbID,
                                    start,
                                    end,
                                    Constants.nmTokm(binding.distance.text.toString().toFloat()),
                                    Constants.knotsToKmh(binding.speedTV.text.toString().toFloat()),
                                    Constants.knotsToKmh(
                                        binding.avgSpeed.text.toString().toFloat()
                                    ),
                                    Constants.knotsToKmh(
                                        binding.maxSpeed.text.toString().toFloat()
                                    ),
                                    binding.time.text.toString(),
                                    Constants.getDate()
                                )

                                var hist: Long = 0
                                val route = try {
                                    hist = databaseHelper!!.insertHistory(history)
                                    databaseHelper!!.insertRoutePoints(Constants.routePoints)
                                } catch (e: Exception) {
                                    null
                                }
                                Log.d("TAG_DATA", "onReceive: KNOT history $hist and Route $route")
                                if (route.isNullOrEmpty()) {

                                    Log.d("TAG_DATA", "onReceive: KNOT Comes Empty")

                                    dbID += 1
                                    withContext(Dispatchers.Main) {
                                        binding.time.text = "00:00:00"
                                        binding.speedTV.text = "0"
                                        binding.distance.text = "0"
                                        binding.maxSpeed.text = "0"
                                        binding.avgSpeed.text = "0"
                                        moveToNext(history)
                                    }
                                } else {

                                    Log.d("TAG_DATA", "onReceive: KNOT Comes NOT Empty")
                                    withContext(Dispatchers.Main) {
                                        polygonPoints.clear()
                                        markerManger?.removeMarker(previousMarker!!)
                                        markerManger?.removeMarker(startMarker!!)
                                        val sydney =
                                            Point.fromLngLat(
                                                Constants.currentLongitude,
                                                Constants.currentLatitude
                                            )
                                        previousMarker = Marker(
                                            title = Constants.currentAddress,
                                            snippet = "",
                                            position = sydney,
                                            icon = requireContext().bitmapFromDrawableRes(R.drawable.source_icon),
                                        )
                                        markerManger!!.addMarker(previousMarker!!)
                                        Constants.zoomInAnimation(
                                            Constants.currentLatitude,
                                            Constants.currentLongitude,
                                            binding.mapView.mapboxMap,
                                            true
                                        )
                                        dbID += 1
                                        binding.time.text = "00:00:00"
                                        binding.speedTV.text = "0"
                                        binding.distance.text = "0"
                                        binding.maxSpeed.text = "0"
                                        binding.avgSpeed.text = "0"
                                        Constants.routePoints = ArrayList()
                                        Constants.initialLat = 0.0
                                        Constants.initialLng = 0.0
                                        //  previousLatLng = null
                                        //  polylineOptions = PolylineOptions()
                                        moveToNext(history)
                                    }
                                }
                            }
                        }
                    }


                }
            }
        }
    }
    private val pauseUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val start = intent?.getBooleanExtra("isPause", false) ?: false
            isResume = !start
            if (start) {
                if (activity != null) {
                    binding.pauseIcon.setImageResource(R.drawable.play_icon)
                    binding.pauseTxt.text = getString(R.string.resume)
                }
            } else {
                if (activity != null) {
                    binding.pauseIcon.setImageResource(R.drawable.resume_icon)
                    binding.pauseTxt.text = getString(R.string.pause)
                }
            }
        }
    }

    private fun clearPolygon() {
        polylineAnnotationManager?.deleteAll()
        points = ArrayList()
    }


    private val resetUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val rotation = ObjectAnimator.ofFloat(binding.resetIcon, View.ROTATION, 0f, 360f)
            rotation.duration = 1000
            rotation.interpolator = AccelerateDecelerateInterpolator()
            rotation.start()
            if (markerManger != null) {
                if (previousMarker != null) {
                    markerManger?.removeMarker(previousMarker!!)
                }
                if (startMarker != null) {
                    markerManger?.removeMarker(startMarker!!)
                }
            }
            // googleMap?.clear()
            Constants.routePoints = ArrayList()
            Constants.initialLat = 0.0
            Constants.initialLng = 0.0
            // previousLatLng = null
            // polylineOptions = PolylineOptions()

        }
    }

    override fun onResume() {
        super.onResume()
        historyDatabase = DatabaseBuilder.getInstance(requireContext())
        databaseHelper = DatabaseHelperImpl(historyDatabase!!)

        val sp = SharedPreferenceHelperClass(requireContext())
        Constants.setLocale(requireActivity(), sp.getString("language", "en"))
        isResume= !sharedPrefrences!!.getBoolean("isPaused", false)
        updatingText()
        settingColors(sp.getString("AppColor", "#FBC100"))
        changeUnit(sp.getString("Unit", "KMH"))
        points = sp.getPoints("mapPoints")
        CoroutineScope(Dispatchers.IO).launch {
            dbID = if (points.isNotEmpty()) {
                Log.d("TAG_DATA", "if: ${sp.getInt("dbID")}")
                sp.getInt("dbID")
            } else {
                val id = Constants.getUniqueRandomInt(requireActivity())
                sp.putInt("dbID", id)
                Log.d("TAG_DATA", "else : $id")
                id

            }
        }
        Log.d("TAG_DB", "onResume: $dbID")
        if (points.isNotEmpty()) {

            for (i in 0..points.size - 1) {
                Constants.routePoints.add(
                    RoutePoints(
                        0,
                        dbID,
                        points[i].latitude(),
                        points[i].longitude()
                    )
                )
            }
            Constants.initialLat = points[0].latitude()
            Constants.initialLng = points[0].longitude()
            if (activity != null) {
                startMarker = Marker(
                    title = "",
                    snippet = "",
                    position = Point.fromLngLat(
                        points[0].longitude(),
                        points[0].latitude()
                    ),
                    icon = requireActivity().bitmapFromDrawableRes(R.drawable.dest_icon)
                )
            }
            markerManger!!.addMarker(startMarker!!)

            Constants.endLat = points[points.size - 1].latitude()
            Constants.endLng = points[points.size - 1].longitude()

            if (activity != null) {
                previousMarker = Marker(
                    title = "",
                    snippet = "",
                    position = Point.fromLngLat(
                        Constants.endLng,
                        Constants.endLat
                    ),
                    icon = requireActivity().bitmapFromDrawableRes(R.drawable.source_icon),
                )
            }
            markerManger?.addMarker(previousMarker!!)

            setAddPolyline()
        }
        else if (previousMarker == null && Constants.currentLongitude != 0.0 && Constants.currentLatitude != 0.0) {
            Log.d("LAT_TAGG", "Resume: latitude ${Constants.currentLongitude}")
            val sydney = Point.fromLngLat(Constants.currentLongitude, Constants.currentLatitude)
            previousMarker = Marker(
                title = "",
                snippet = "",
                position = sydney,
                icon = requireContext().bitmapFromDrawableRes(R.drawable.source_icon),
            )
            markerManger?.addMarker(previousMarker!!)
            Handler(Looper.getMainLooper()).postDelayed({
                Constants.zoomInAnimation(
                    Constants.currentLatitude,
                    Constants.currentLongitude,
                    binding.mapView.mapboxMap,
                    true
                )
                mapLoaded = true
            }, 2000)
        }
        //fetchingLocation()
        /* if (mMapView != null) {
             mMapView?.onResume()
         }*/
    }

    fun updatingText() {
        binding.durationTV.text = getString(R.string.duration)
        binding.distanceTV.text = getString(R.string.distance)
        binding.avgSpeedTV.text = getString(R.string.avg_speed)
        binding.maxSpeedTV.text = getString(R.string.max_speed)
        binding.resetTV.text = getString(R.string.reset)
        if (isResume) {
            binding.pauseTxt.text = getString(R.string.pause)
            binding.pauseIcon.setImageResource(R.drawable.resume_icon)
        } else {
            binding.pauseIcon.setImageResource(R.drawable.play_icon)
            binding.pauseTxt.text = getString(R.string.resume)
        }
        if (isStop) {
            binding.playTV.text = getString(R.string.start)
        } else {
            binding.playTV.text = getString(R.string.stop)
        }
    }

    fun settingColors(color: String) {
        binding.resetIcon.setColorFilter(Color.parseColor(color), PorterDuff.Mode.SRC_IN)
        binding.playBtn.backgroundTintList = ColorStateList.valueOf(Color.parseColor(color))
        binding.pauseIcon.setColorFilter(Color.parseColor(color), PorterDuff.Mode.SRC_IN)
        binding.mapStyle.setColorFilter(Color.parseColor(color), PorterDuff.Mode.SRC_IN)
        binding.currentLoc.setColorFilter(Color.parseColor(color), PorterDuff.Mode.SRC_IN)
    }

    /* private val callback = OnMapReadyCallback { googleMap ->
         this.googleMap = googleMap
         if (Constants.currentLatitude != 0.0 && Constants.currentLongitude != 0.0) {
             if (!mapLoaded) {
                 val sydney = LatLng(Constants.currentLatitude, Constants.currentLongitude)
                 previousMarker = googleMap.addMarker(
                     MarkerOptions().position(sydney).title(Constants.currentAddress)
                 )
                 googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
                 Handler(Looper.getMainLooper()).postDelayed({
                     googleMap.animateCamera(CameraUpdateFactory.zoomTo(17f), 1500, null)
                     mapLoaded = true
                 }, 2000)
             }
         } else {
             fetchingLocation()
         }
     }*/


    fun fetchingLocation() {
        if (checkLocationPermission() && checkCourseLocationPermission() && !mapLoaded) {
            val locationManager = LocationManager(requireContext())
            GlobalScope.launch(Dispatchers.Main) {
                try {
                    setupPolylineManager(binding.mapView)
                    val location = locationManager.getCurrentLocation()
                    Constants.currentLatitude = location.latitude
                    Constants.currentLongitude = location.longitude
                    CoroutineScope(Dispatchers.IO).launch {
                        val addressData = Constants.getCompleteAddress(
                            Constants.currentLatitude,
                            Constants.currentLongitude,
                            requireContext()
                        )
                        Constants.currentAddress =
                            addressData?.completeAddress ?: "Current Location"
                        withContext(Dispatchers.Main) {
                            val sydney = Point.fromLngLat(
                                Constants.currentLongitude,
                                Constants.currentLatitude
                            )

                            previousMarker = Marker(
                                title = "",
                                snippet = "",
                                position = sydney,
                                icon = requireActivity().bitmapFromDrawableRes(R.drawable.source_icon),
                            )
                            markerManger?.addMarker(previousMarker!!)
                            Handler(Looper.getMainLooper()).postDelayed({
                                Constants.zoomInAnimation(
                                    Constants.currentLatitude,
                                    Constants.currentLongitude,
                                    binding.mapView.mapboxMap,
                                    true
                                )
                                mapLoaded = true
                            }, 2000)
                        }
                    }
                } catch (e: SecurityException) {
                    Log.d("LAT_TAG", "Exception: ${e.localizedMessage}")
                }
            }
        } else {
            requestLocationPermission()
        }
    }

    override fun onPause() {
        super.onPause()
        /*if (mMapView != null) {
            mMapView!!.onPause()
        }*/
    }

    override fun onDestroy() {
        super.onDestroy()
        mapLoaded = false
        try {
            activity?.unregisterReceiver(startUpdateReceiver)
            activity?.unregisterReceiver(pauseUpdateReceiver)
            activity?.unregisterReceiver(speedUpdateReceiver)
            Log.w("MapsFragment", "Unregistered Receiver")
        } catch (e: Exception) {
            Log.w("MapsFragment", "Receiver already unregistered or activity is null")
        }
    }

    override fun onLowMemory() {
        super.onLowMemory()
        /* if (mMapView != null) {
             mMapView!!.onLowMemory()
         }*/
    }

    fun changeUnit(unit: String) {
        when (unit) {
            "KMH" -> {
                binding.distanceUnit.text = "km"
                binding.avgSpeedUnit.text = "Km/h"
                binding.maxSpeedUnit.text = "Km/h"
            }

            "MPH" -> {
                binding.distanceUnit.text = "mi"
                binding.avgSpeedUnit.text = "Mph"
                binding.maxSpeedUnit.text = "Mph"
            }

            "KNOT" -> {
                binding.distanceUnit.text = "nm"
                binding.maxSpeedUnit.text = "Knots"
                binding.avgSpeedUnit.text = "Knots"
            }
        }
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(), arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ), LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    private fun checkLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun checkCourseLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onMapLoaded(eventData: MapLoadedEventData) {
        Log.d("TAG_LL", "onMapLoaded: ")
        val array = sharedPrefrences!!.getPoints("mapPoints")
        setupPolylineManager(binding.mapView)
        if (array.isEmpty())
            if (Constants.currentLatitude != 0.0 && Constants.currentLongitude != 0.0) {
                if (!mapLoaded) {
                    Log.d("LAT_TAGG", "onMapLoaded: latitude ${Constants.currentLongitude}")

                    val sydney =
                        Point.fromLngLat(Constants.currentLongitude, Constants.currentLatitude)
                    previousMarker = Marker(
                        title = "",
                        snippet = "",
                        position = sydney,
                        icon = requireContext().bitmapFromDrawableRes(R.drawable.source_icon),
                    )
                    markerManger?.addMarker(previousMarker!!)
                    Handler(Looper.getMainLooper()).postDelayed({
                        Constants.zoomInAnimation(
                            Constants.currentLatitude,
                            Constants.currentLongitude,
                            binding.mapView.mapboxMap,
                            true
                        )
                        mapLoaded = true
                    }, 2000)
                }
            } else {
                fetchingLocation()
            } else {
            directZoom(
                array[0].latitude(),
                array[0].longitude(),
                binding.mapView.mapboxMap,
                true
            )
        }
    }

    fun moveToNext(history: History) {
        SpeedMeterLoadAds.loadVideoAdForPrompt(
            requireActivity(),
            object : SpeedMeterLoadAds.OnVideoLoad {
                override fun onLoaded() {
                    moveForward.value = "Yes"
                    binding.playBtn.isEnabled = true
                    binding.playTV.text = context?.getString(R.string.start) ?: "Start"
                    binding.playProgress.visibility = View.GONE
                    SpeedMeterShowAds.showingVideoAd(requireActivity()) {
                        val bundle = Bundle()
                        bundle.putInt("pointID", history.pointsID)
                        val intent1 =
                            Intent(requireContext(), HistoryDetail::class.java)
                        intent1.putExtras(bundle)
                        startActivity(intent1)
                    }
                }

                override fun onFailed() {
                    moveForward.value = "Yes"
                    binding.playBtn.isEnabled = true
                    binding.playTV.text = context?.getString(R.string.start) ?: "Start"
                    binding.playProgress.visibility = View.GONE
                    val bundle = Bundle()
                    bundle.putInt("pointID", history.pointsID)
                    val intent1 =
                        Intent(requireContext(), HistoryDetail::class.java)
                    intent1.putExtras(bundle)
                    startActivity(intent1)
                }

            })
    }

}