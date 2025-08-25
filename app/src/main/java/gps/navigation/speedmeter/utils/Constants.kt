package gps.navigation.speedmeter.utils

import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.location.Geocoder
import android.media.MediaPlayer
import android.net.ConnectivityManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.MutableLiveData
import com.mapbox.geojson.Point
import com.mapbox.maps.EdgeInsets
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.dsl.cameraOptions
import com.mapbox.maps.plugin.animation.MapAnimationOptions.Companion.mapAnimationOptions
import com.mapbox.maps.plugin.animation.flyTo
import gps.navigation.speedmeter.R
import gps.navigation.speedmeter.database.RoutePoints
import gps.navigation.speedmeter.models.LanguageModel
import gps.navigation.speedmeter.models.ThemeModel
import gps.navigation.speedmeter.sharedprefrences.SharedPreferenceHelperClass
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


object Constants {
    val APP_UPDATE_REQUEST_CODE = 1111
    var keyMapbox =
        "pk.eyJ1Ijoicm9sZXgyMzEiLCJhIjoiY2x5OGxlMnZ3MGc2azJpcXBteHM4czVsMyJ9.Ge3lVmhKer_ghlwYQpwzQg"
    var currentLatitude: Double = 0.0
    var currentLongitude: Double = 0.0
    var initialLat: Double = 0.0
    var initialLng: Double = 0.0
    var endLat: Double = 0.0
    var endLng: Double = 0.0
    var billingWeekly = "error"
    var billingMonthly = "Rs 1,650.00"
    var billingYearly = "Rs 9,800.00"
    var routePoints: ArrayList<RoutePoints> = ArrayList()
    var currentAddress: String = "Current Location"
    var mapLoaded: Boolean = false
    var isStart: Boolean = false
    var IsAppOnTimer = false
    var isSATELLITE: Boolean = true
    var willIntersShow = false
    var backpressadcontrol = true
    var mediaPlayer: MediaPlayer? = null

    var observerLAT = MutableLiveData<Double>(0.0)
    var observerLNG = MutableLiveData<Double>(0.0)
    var moveForward = MutableLiveData<String>("No")
    var startStop = MutableLiveData<String>("isStart")
    val arrayLang = arrayListOf(
        LanguageModel(R.drawable.english, "English", "en", true),
        LanguageModel(R.drawable.spanish, "Spanish", "es", false),
        LanguageModel(R.drawable.french, "French", "fr", false),
        LanguageModel(R.drawable.russian, "Russian", "ru", false),
        LanguageModel(R.drawable.japanese, "Japanese", "ja", false),
        LanguageModel(R.drawable.portuguese, "Portuguese", "pt", false),
        LanguageModel(R.drawable.german, "German", "de", false),
        LanguageModel(R.drawable.turkey, "Turkey", "tr", false),
        LanguageModel(R.drawable.indonesia, "Indonesian", "in", false),
        LanguageModel(R.drawable.hindi, "Hindi", "hi", false),
    )


    var listTheme = arrayListOf(
        ThemeModel("#0DCF31", true), ThemeModel("#00C0FB"),
        ThemeModel("#E600FB"), ThemeModel("#FB0004"),
        ThemeModel("#43FB00"), ThemeModel("#4B00FB"),
        ThemeModel("#FB008E"), ThemeModel("#FB7500"),
        ThemeModel("#E0115F"), ThemeModel("#50C878"),
        ThemeModel("#4B0082"), ThemeModel("#00FFFF"),
        ThemeModel("#008080"), ThemeModel("#6ABD43"),
        ThemeModel("#FAA318"), ThemeModel("#EE72FF")
    )

    fun getDoubleDigit(value: Int): String {
        return if (value < 10) {
            "0$value"
        } else {
            value.toString()
        }
    }

    suspend fun getCompleteAddress(
        latitude: Double, longitude: Double, context: Context
    ): AddressInfo? {
        return withContext(Dispatchers.IO) {
            try {
                if (isConnectedToNetwork(context)) {
                    val geocoder = Geocoder(context, Locale.getDefault())

                    val addresses = geocoder.getFromLocation(latitude, longitude, 5)


                    if (addresses!!.isNotEmpty()) {
                        val completeAddress = addresses[0].getAddressLine(0)
                        val city: String = addresses[0].locality ?: ""
                        val state: String = addresses[0].adminArea ?: ""
                        val country: String = addresses[0].countryName ?: ""
                        val postalCode: String = addresses[0].postalCode ?: ""
                        val knownName: String = addresses[0].featureName ?: ""

                        // Return the address information
                        AddressInfo(completeAddress, city, state, country, postalCode, knownName)
                    } else {
                        null

                    }
                } else {
                    Log.d("LAT_TAG", "Internet is not connected")
                    null
                }
            } catch (e: Exception) {
                Log.d("LAT_TAG", "Exception: ${e.localizedMessage}")
                null
            }
        }
    }

    fun isConnectedToNetwork(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    fun viewScaling(from: Float, to: Float, isFrom: Boolean, view: View) {
        val scaleAnimation = AlphaAnimation(from, to)
        scaleAnimation.duration = 500 // 1 second
        view.startAnimation(scaleAnimation)
        scaleAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(p0: Animation?) {
                if (isFrom) {
                    view.visibility = View.VISIBLE
                }
            }

            override fun onAnimationEnd(p0: Animation?) {
                if (!isFrom) {
                    view.visibility = View.GONE
                }
            }

            override fun onAnimationRepeat(p0: Animation?) {

            }

        })
    }

    fun getDate(): String {
        val date = Date() // Replace this with your actual Date object
        val sdf = SimpleDateFormat("MMM d, yyyy", Locale.ENGLISH)
        val formattedDate: String = sdf.format(date)
        return formattedDate
    }

    fun kmhToMph(speedKmh: Float): Float {
        return speedKmh * 0.621371f
    }

    fun kmhToKnots(speedKmh: Float): Float {
        return speedKmh * 0.539957f
    }

    fun mphToKmh(speedKmh: Float): Float {
        return speedKmh / 0.621371f
    }

    fun knotsToKmh(speedKmh: Float): Float {
        return speedKmh / 0.539957f
    }

    fun kmTonm(speedKmh: Float): Float {
        return speedKmh / 1.852f
    }

    fun nmTokm(speedKmh: Float): Float {
        return speedKmh * 1.852f
    }

    fun kmTomi(speedKmh: Float): Float {
        return speedKmh / 1.609f
    }

    fun miTokm(speedKmh: Float): Float {
        return speedKmh * 1.609f
    }

    fun vibratePhone(context: Context) {
        val vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createOneShot(400, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(400)
        }
    }

    fun zoomInAnimation(lat: Double, lng: Double, mapboxMap: MapboxMap, is2D: Boolean) {
        val target = if (is2D) {
            cameraOptions {
                center(Point.fromLngLat(lng, lat))
                zoom(12.5)
                pitch(15.0)
                bearing(130.0)
            }
        } else {
            cameraOptions {
                center(Point.fromLngLat(lng, lat))
                zoom(12.5)
                pitch(75.0)
                bearing(130.0)
            }

        }
        mapboxMap.flyTo(
            target,
            mapAnimationOptions {
                duration(4_000)
            }
        )
    }

    fun directZoom(lat: Double, lng: Double, mapboxMap: MapboxMap, is2D: Boolean) {
        val target = if (is2D) {
            cameraOptions {
                center(Point.fromLngLat(lng, lat))
                zoom(12.5)
                pitch(15.0)
                bearing(130.0)
            }
        } else {
            cameraOptions {
                center(Point.fromLngLat(lng, lat))
                zoom(12.5)
                pitch(75.0)
                bearing(130.0)
            }

        }
        mapboxMap.flyTo(
            target,
        )
    }

    fun Context.bitmapFromDrawableRes(@DrawableRes resourceId: Int): Bitmap =
        drawableToBitmap(AppCompatResources.getDrawable(this, resourceId)!!)

    fun drawableToBitmap(
        sourceDrawable: Drawable,
        flipX: Boolean = false,
        flipY: Boolean = false,
        @ColorInt tint: Int? = null,
    ): Bitmap {
        return if (sourceDrawable is BitmapDrawable) {
            sourceDrawable.bitmap
        } else {
            // copying drawable object to not manipulate on the same reference
            val constantState = sourceDrawable.constantState!!
            val drawable = constantState.newDrawable().mutate()
            val bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth, drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            tint?.let(drawable::setTint)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            canvas.scale(
                if (flipX) -1f else 1f,
                if (flipY) -1f else 1f,
                canvas.width / 2f,
                canvas.height / 2f
            )
            drawable.draw(canvas)
            bitmap
        }
    }

    fun getRelativeDateLabel(inputDate: String): String {
        val dateFormat = SimpleDateFormat("MMM d,yyyy", Locale.ENGLISH)
        dateFormat.isLenient = false

        return try {
            val parsedDate = dateFormat.parse(inputDate)
            val calendarInput = Calendar.getInstance().apply { time = parsedDate!! }

            val calendarToday = Calendar.getInstance()
            val calendarYesterday = Calendar.getInstance().apply { add(Calendar.DATE, -1) }

            return when {
                isSameDay(calendarInput, calendarToday) -> "Today"
                isSameDay(calendarInput, calendarYesterday) -> "Yesterday"
                else -> inputDate
            }
        } catch (e: Exception) {
            inputDate // Return original if parsing fails
        }
    }

    private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    fun setLocale(activity: Activity, languageCode: String?) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val resources = activity.resources
        val config: Configuration = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    fun zoomOnIndex(mapView: MapView, points: List<Point>) {
        if (points.isEmpty()) return

        val padding = EdgeInsets(100.0, 100.0, 100.0, 100.0) // top, left, bottom, right

        val cameraOptions = mapView.getMapboxMap().cameraForCoordinates(
            points,
            padding,
            bearing = null,
            pitch = null
        )

        mapView.getMapboxMap().setCamera(cameraOptions)
    }

    fun zoomToPolyline(mapView: MapView, points: List<Point>) {
        if (points.isEmpty()) return

        val padding = EdgeInsets(100.0, 100.0, 100.0, 100.0) // top, left, bottom, right

        val cameraOptions = mapView.getMapboxMap().cameraForCoordinates(
            points,
            padding,
            bearing = null,
            pitch = null
        )
        mapView.getMapboxMap().flyTo(
            cameraOptions,
            mapAnimationOptions {
                duration(2_000)
            }
        )
    }

    fun getUniqueRandomInt(context: Context): Int {
        val sp = SharedPreferenceHelperClass(context)
        val min = 10_001
        val max = Int.MAX_VALUE
        val usedNumbers = sp.getConstraints("Unique")
        if (usedNumbers.size >= (max - min + 1)) {
            throw IllegalStateException("All unique values have been used.")
        }

        while (true) {
            val number = (min..max).random()
            if (usedNumbers.add(number)) {
                sp.putConstraints("Unique", usedNumbers)
                return number
            }
        }
    }

    fun countAnimKMH(
        start: Int, end: Int, duration: Long = 1000L,
        text: TextView
    ) {
        val animator = ValueAnimator.ofInt(start, end)
        animator.duration = duration
        animator.addUpdateListener {
            val currentValue = it.animatedValue as Int
            text.text = getDoubleDigit(currentValue)
        }
        animator.start()
    }

    fun countAnimMPH(
        start: Float, end: Float, duration: Long = 1000L,
        text: TextView
    ) {
        val animator = ValueAnimator.ofFloat(start, end)
        animator.duration = duration
        animator.addUpdateListener {
            val currentValue = it.animatedValue as Float
            text.text = getDoubleDigit(kmhToMph(currentValue).toInt())
        }
        animator.start()
    }

    fun countAnimKNOTS(
        start: Float, end: Float, duration: Long = 1000L,
        text: TextView
    ) {
        val animator = ValueAnimator.ofFloat(start, end)
        animator.duration = duration
        animator.addUpdateListener {
            val currentValue = it.animatedValue as Float
            text.text = getDoubleDigit(kmhToKnots(currentValue).toInt())
        }
        animator.start()
    }
}