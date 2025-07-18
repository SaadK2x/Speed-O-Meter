package gps.navigation.speedmeter.activities

import android.graphics.Color
import android.graphics.Color.rgb
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.mapbox.geojson.Point
import com.mapbox.maps.MapLoaded
import com.mapbox.maps.MapLoadedCallback
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.atmosphere.generated.atmosphere
import com.mapbox.maps.extension.style.layers.properties.generated.ProjectionName
import com.mapbox.maps.extension.style.projection.generated.projection
import com.mapbox.maps.extension.style.sources.generated.rasterDemSource
import com.mapbox.maps.extension.style.style
import com.mapbox.maps.extension.style.terrain.generated.terrain
import com.mapbox.maps.plugin.annotation.AnnotationConfig
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPolylineAnnotationManager
import com.mapbox.maps.plugin.compass.compass
import gps.navigation.speedmeter.R
import gps.navigation.speedmeter.ads.SpeedMeterLoadAds
import gps.navigation.speedmeter.database.DatabaseBuilder
import gps.navigation.speedmeter.database.DatabaseHelperImpl
import gps.navigation.speedmeter.database.HistoryDatabase
import gps.navigation.speedmeter.databinding.ActivityHistoryDetailBinding
import gps.navigation.speedmeter.sharedprefrences.SharedPreferenceHelperClass
import gps.navigation.speedmeter.utils.Constants
import gps.navigation.speedmeter.utils.Constants.bitmapFromDrawableRes
import gps.navigation.speedmeter.utils.Marker
import gps.navigation.speedmeter.utils.MarkerManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HistoryDetail : AppCompatActivity(), MapLoadedCallback {
    private val binding: ActivityHistoryDetailBinding by lazy {
        ActivityHistoryDetailBinding.inflate(layoutInflater)
    }
    var historyDatabase: HistoryDatabase? = null
    var databaseHelper: DatabaseHelperImpl? = null

    var markerManger: MarkerManager? = null
    var previousLatLng: Point? = null
    var toLatLng: Point? = null
    var fromLatLng: Point? = null
    var points: ArrayList<Point> = ArrayList()
    var pid: Int = 0
    var sharedPrefrences: SharedPreferenceHelperClass? = null
    private lateinit var polylineAnnotationManager: PolylineAnnotationManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        squareXGPSBannerAdsSmall()
        pid = intent.extras?.getInt("pointID") ?: 0
        historyDatabase = DatabaseBuilder.getInstance(this)
        databaseHelper = DatabaseHelperImpl(historyDatabase!!)
        sharedPrefrences = SharedPreferenceHelperClass(this)
        markerManger = MarkerManager(binding.map)
        binding.map.apply {
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
        binding.map.mapboxMap.subscribeMapLoaded(this)

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        binding.deleteBtn.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val lit = databaseHelper!!.getDeleteSpecific(pid)
                withContext(Dispatchers.Main) {
                    if (lit > 0) {
                        onBackPressed()
                        Toast.makeText(this@HistoryDetail, "Data Deleted", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
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

    fun settingColors(color: String) {
        binding.duration.setTextColor(Color.parseColor(color))
        binding.distance.setTextColor(Color.parseColor(color))
        binding.distanceUnit.setTextColor(Color.parseColor(color))
        binding.avgSpeed.setTextColor(Color.parseColor(color))
        binding.avgSpeedUnit.setTextColor(Color.parseColor(color))
        binding.maxSpeed.setTextColor(Color.parseColor(color))
        binding.maxSpeedUnit.setTextColor(Color.parseColor(color))
    }

    override fun onResume() {
        super.onResume()
        val sp = SharedPreferenceHelperClass(this)
        settingColors(sp.getString("AppColor", "#0DCF31"))
    }

    override fun onPause() {
        super.onPause()

    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
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
        polylineAnnotationManager.create(polylineOptions)
    }

    fun setupPolylineManager(mapView: MapView) {
        val annotationApi = mapView.annotations
        polylineAnnotationManager =
            annotationApi.createPolylineAnnotationManager(AnnotationConfig())
    }

    override fun run(mapLoaded: MapLoaded) {
        setupPolylineManager(binding.map)
        CoroutineScope(Dispatchers.IO).launch {
            Log.d("TAG_PPP", "run: pid: $pid")
            val item = databaseHelper!!.getSpecificHistoryAndRoute(pid)
            withContext(Dispatchers.Main) {
                Log.d("TAG_PPP", "run: item: $${Gson().toJson(item)}")
                binding.toTV.text = item.history.destination
                binding.fromTV.text = item.history.source
                binding.toTV.isSelected = true
                binding.fromTV.isSelected = true
                binding.distance.text = item.history.distance.toString()
                binding.avgSpeed.text = item.history.avgSpeed.toString()
                binding.maxSpeed.text = item.history.maxSpeed.toString()
                binding.duration.text = item.history.time
                val route = item.routePoints
                for (i in route.indices) {
                    if (i == 0) {
                        fromLatLng = Point.fromLngLat(route[0].lng!!, route[0].lat!!)
                        toLatLng = Point.fromLngLat(
                            route[route.size - 1].lng!!,
                            route[route.size - 1].lat!!
                        )
                        Log.d("TAG_LOG", "to Lat: ${toLatLng!!.latitude()}")
                        markerManger?.addMarker(
                            Marker(
                                title = "",
                                snippet = "",
                                position = fromLatLng!!,
                                icon = bitmapFromDrawableRes(R.drawable.source_icon),
                            )
                        )
                        markerManger?.addMarker(
                            Marker(
                                title = "",
                                snippet = "",
                                position = toLatLng!!,
                                icon = bitmapFromDrawableRes(R.drawable.dest_icon),
                            )
                        )
                    }

                    Log.d("TAG_LOG", " latitude: ${route[i].lat!!}")
                    val currentLatLng = Point.fromLngLat(route[i].lng!!, route[i].lat!!)
                    points.add(currentLatLng)
                    setAddPolyline()
                    previousLatLng = currentLatLng

                }
                Handler(Looper.getMainLooper()).postDelayed({
                    Constants.zoomToPolyline(binding.map, points)
                    /*  Constants.zoomInAnimation(
                          fromLatLng!!.latitude(),
                          fromLatLng!!.longitude(),
                          binding.map.mapboxMap, true
                      )*/
                }, 2000)
            }
        }
    }
}