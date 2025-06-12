package gps.navigation.speedmeter.utils

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.multidex.MultiDex
import gps.navigation.speedmeter.ads.SpeedMeterAppOpenSplashAd
import com.google.firebase.FirebaseApp
import gps.navigation.speedmeter.ads.SpeedMeterAppOpenAd


class MyApp : Application() {

    companion object {
        var gpsArea: MyApp? = null
        var isFirstTime = true
        var honeyBeeGPSOpenAdManager: SpeedMeterAppOpenAd? = null
        var sStreetViewTruckCTAppOpenSplashAdManager: SpeedMeterAppOpenSplashAd? = null

        // Store categories from Firebase

        @JvmStatic
        fun getStr(id: Int): String {
            return gpsArea!!.getString(id)
        }


        private fun sendBroadcastToActivity(data: String) {
            val intent = Intent("FACE_SWAP")
            intent.putExtra("data", data)
            gpsArea!!.sendBroadcast(intent)
        }
    }


    override fun onCreate() {
        super.onCreate()

        gpsArea = this


        // Initialize and start GrizzlyMonitor with default settings
        // Initialize Firebase Crashlytics

        // Initialize and start GrizzlyMonitor with custom settings
        /* GrizzlyMonitorBuilder(this)
             .withTicker(200L) // Set ticker interval (1-500ms)
             .withThreshold(4500L) // Set ANR threshold (1000-4500ms)
             .withTitle("App Error") // Set custom crash dialog title
             .withMessage("An error occurred. Please restart.") // Set custom crash dialog message
             .withFirebaseCrashLytics(firebaseCrashlytics) // Integrate with Firebase Crashlytics
             .build()
             .start()*/

        try {
            FirebaseApp.initializeApp(this)
        } catch (e: Exception) {
        }
        sStreetViewTruckCTAppOpenSplashAdManager =
            SpeedMeterAppOpenSplashAd(this)
        // sStreetViewhighTruckCTAppOpenSplashAdManager = HghStreetViewTruckCTAppOpenSplashAdManager(this)
        honeyBeeGPSOpenAdManager = SpeedMeterAppOpenAd(this)
        // HoneyBeeMapNavigationLoadAds.insertADSToFirebase(this)
        // getDataFromFirebase()

    }


    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

}